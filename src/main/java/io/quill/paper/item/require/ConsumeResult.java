package io.quill.paper.item.require;

import javax.annotation.concurrent.Immutable;

@Immutable
public sealed interface ConsumeResult permits ConsumeResult.Success, ConsumeResult.Failure {

    boolean isSuccess();

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