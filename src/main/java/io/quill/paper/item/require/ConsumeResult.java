package io.quill.paper.item.require;

import javax.annotation.concurrent.Immutable;
import java.util.function.Consumer;

@Immutable
public sealed interface ConsumeResult permits ConsumeResult.Success, ConsumeResult.Failure {

    boolean isSuccess();

    default void ifSuccess(Runnable action) {
        if (isSuccess()) action.run();
    }

    default void ifFailure(Consumer<String> action) {
        if (this instanceof Failure(String reason)) {
            action.accept(reason);
        }
    }

    default void handle(Runnable onSuccess, Consumer<String> onFailure) {
        if (isSuccess()) {
            onSuccess.run();
        } else if (this instanceof Failure(String reason)) {
            onFailure.accept(reason);
        }
    }

    default String reason() {
        return this instanceof Failure(String reason) ? reason : "";
    }

    static ConsumeResult success() {
        return Success.INSTANCE;
    }

    static ConsumeResult failure(String reason) {
        return new Failure(reason);
    }

    record Success() implements ConsumeResult {
        static final Success INSTANCE = new Success();

        @Override
        public boolean isSuccess() {
            return true;
        }
    }

    record Failure(String reason) implements ConsumeResult {
        @Override
        public boolean isSuccess() {
            return false;
        }
    }
}