package io.quill.paper.util.bukkit.task;

import com.google.common.collect.Lists;
import io.quill.paper.util.bukkit.Logger;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 순차적으로 실행되는 태스크 체인을 정의하는 추상 클래스.
 * 동기/비동기 작업과 지연을 조합하여 콜백 지옥 없이 순차 작업을 표현할 수 있다.
 *
 * <pre>{@code
 * new TaskChain() {
 *     @Override
 *     protected void define() {
 *         run(ctx -> {
 *             int result = doA();
 *             ctx.set("result", result);
 *         });
 *
 *         delay(20L);
 *
 *         run(ctx -> {
 *             int result = ctx.get("result");
 *             doB(result);
 *         });
 *     }
 * }.start();
 * }</pre>
 */
public abstract class TaskChain {
    private List<TaskStep> steps = Lists.newArrayList();
    private final ConcurrentHashMap<String, Object> sharedContext = new ConcurrentHashMap<>();
    private final AtomicBoolean defined = new AtomicBoolean(false);
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private final AtomicReference<CancellableTask> currentTask = new AtomicReference<>();

    protected TaskChain() { }

    /**
     * 태스크 체인을 정의한다.
     * run(), runAsync(), delay() 메서드를 사용하여 순차적인 작업을 구성한다.
     */
    protected abstract void define();

    /**
     * 메인 스레드에서 동기적으로 실행되는 작업을 추가한다.
     */
    protected void run(Runnable action) {
        checkNotNull(action, "action");
        steps.add(new TaskStep.Sync(action));
    }

    /**
     * 메인 스레드에서 동기적으로 실행되는 작업을 추가한다.
     * Context를 통해 데이터를 공유하고 체인을 제어할 수 있다.
     */
    protected void run(Consumer<ChainContext> action) {
        checkNotNull(action, "action");
        steps.add(new TaskStep.Sync(() -> action.accept(new ContextImpl())));
    }

    /**
     * 비동기로 실행되는 작업을 추가한다.
     * 비동기 작업은 완료를 기다리지 않고 즉시 다음 스텝으로 진행한다.
     * <p>
     * 주의: 비동기 작업의 완료 전에 다음 스텝이 실행되므로,
     * 비동기 작업에서 설정한 context 값은 다음 스텝에서 즉시 사용할 수 없다.
     * <p>
     * 사용 예:
     * <pre>{@code
     * // 좋은 예: 독립적인 비동기 작업
     * runAsync(() -> saveDataToFile());  // 완료를 기다릴 필요 없음
     * run(() -> broadcastMessage());     // 병렬로 진행
     *
     * // 나쁜 예: 다음 스텝이 비동기 결과에 의존
     * runAsync(ctx -> {
     *     ctx.set("data", fetchFromDatabase());
     * });
     * run(ctx -> {
     *     Object data = ctx.get("data");  // 실패 가능!
     * });
     * }</pre>
     */
    protected void runAsync(Runnable action) {
        checkNotNull(action, "action");
        steps.add(new TaskStep.Async(action));
    }

    protected void runAsync(Consumer<ChainContext> action) {
        checkNotNull(action, "action");
        steps.add(new TaskStep.Async(() -> action.accept(new ContextImpl())));
    }

    /**
     * 지정된 틱만큼 대기한다.
     * @param ticks 대기할 틱 수 (1틱 = 50ms)
     */
    protected void delay(long ticks) {
        checkArgument(ticks > 0, "ticks must be positive");
        steps.add(new TaskStep.Delay(ticks));
    }

    /**
     * 체인 실행을 시작한다.
     * 이 메서드는 한 번만 호출할 수 있다. 재호출 시 예외가 발생한다.
     *
     * @return 체인 전체를 제어할 수 있는 CancellableTask
     * @throws IllegalStateException 이미 시작된 경우
     */
    public CancellableTask start() {
        if (!defined.compareAndSet(false, true)) {
            throw new IllegalStateException("TaskChain can only be started once");
        }

        define();

        steps = List.copyOf(steps);

        if (steps.isEmpty()) throw new IllegalStateException("No steps defined in chain");

        runNextStep(0);
        return new ChainController();
    }

    private void runNextStep(int index) {
        if (cancelled.get() || index >= steps.size()) {
            currentTask.set(null);
            return;
        }

        TaskStep step = steps.get(index);

        switch (step) {
            case TaskStep.Sync(Runnable action) -> {
                if (!cancelled.get()) {
                    try {
                        action.run();
                    } catch (Exception e) {
                        Logger.error("Exception in TaskChain sync step", e);
                        if (!onError(e, index)) {
                            cancelled.set(true);
                            return;
                        }
                    }
                }
                runNextStep(index + 1);
            }
            case TaskStep.Async(Runnable action) -> {
                if (!cancelled.get()) {
                    Tasks.runAsync(() -> {
                        try {
                            action.run();
                        } catch (Exception e) {
                            Logger.error("Exception in TaskChain async step", e);
                            onAsyncError(e, index);
                        }
                    });
                }
                runNextStep(index + 1);
            }
            case TaskStep.Delay(long ticks) -> {
                if (!cancelled.get()) {
                    CancellableTask task = Tasks.later(ticks, () -> runNextStep(index + 1));
                    currentTask.set(task);
                } else {
                    currentTask.set(null);
                }
            }
        }
    }

    /**
     * 동기 스텝 실행 중 발생한 에러를 처리한다.
     * 오버라이드하여 커스텀 에러 핸들링을 구현할 수 있다.
     *
     * @param error 발생한 예외
     * @param stepIndex 에러가 발생한 스텝의 인덱스
     * @return true면 다음 스텝 계속 진행, false면 체인 중단 (기본값: false)
     */
    protected boolean onError(Exception error, int stepIndex) {
        return false;
    }

    /**
     * 비동기 스텝 실행 중 발생한 에러를 처리한다.
     * 오버라이드하여 커스텀 에러 핸들링을 구현할 수 있다.
     * <p>
     * 비동기 에러는 체인 실행 흐름을 중단시키지 않는다.
     *
     * @param error 발생한 예외
     * @param stepIndex 에러가 발생한 스텝의 인덱스
     */
    protected void onAsyncError(Exception error, int stepIndex) { }

    /**
     * 체인 실행 컨텍스트.
     * 스텝 간 데이터 공유 및 체인 제어 기능을 제공한다.
     * Thread-safe하며 여러 스레드에서 동시에 접근할 수 있다.
     */
    public interface ChainContext {
        /**
         * 컨텍스트에 값을 저장한다.
         */
        void set(String key, Object value);

        /**
         * 컨텍스트에서 값을 가져온다.
         * @throws IllegalArgumentException key에 해당하는 값이 없을 경우
         */
        <T> T get(String key);

        /**
         * 컨텍스트에서 값을 가져온다. 값이 없으면 기본값을 반환한다.
         */
        <T> T getOrDefault(String key, T defaultValue);

        /**
         * 체인을 중단한다.
         * 현재 스텝 완료 후 더 이상 진행하지 않으며, 대기 중인 delay도 취소된다.
         */
        void cancel();

        /**
         * 체인이 취소되었는지 확인한다.
         */
        boolean isCancelled();
    }

    private class ContextImpl implements ChainContext {
        @Override
        public void set(String key, Object value) {
            checkNotNull(key, "key");
            checkNotNull(value, "value cannot be null");
            sharedContext.put(key, value);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T get(String key) {
            checkNotNull(key, "key");
            T value = (T) sharedContext.get(key);
            if (value == null) {
                throw new IllegalArgumentException("No value found for key: " + key);
            }
            return value;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getOrDefault(String key, T defaultValue) {
            checkNotNull(key, "key");
            return (T) sharedContext.getOrDefault(key, defaultValue);
        }

        @Override
        public void cancel() {
            if (cancelled.compareAndSet(false, true)) {
                CancellableTask task = currentTask.get();
                if (task != null) {
                    task.cancel();
                }
            }
        }

        @Override
        public boolean isCancelled() {
            return cancelled.get();
        }
    }

    private class ChainController implements CancellableTask {
        @Override
        public boolean cancel() {
            if (cancelled.compareAndSet(false, true)) {
                CancellableTask task = currentTask.get();
                if (task != null) {
                    return task.cancel();
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean isCancelled() {
            return cancelled.get();
        }
    }
}