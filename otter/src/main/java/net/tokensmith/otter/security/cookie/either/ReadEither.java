package net.tokensmith.otter.security.cookie.either;

import net.tokensmith.jwt.entity.jwt.Claims;

import java.util.Objects;
import java.util.Optional;

public class ReadEither<T extends Claims> {
    private Optional<ReadError<T>> left;
    private Optional<T> right;

    public ReadEither(Optional<ReadError<T>> left, Optional<T> right) {
        this.left = left;
        this.right = right;
    }

    public Optional<T> getRight() {
        return right;
    }

    public Optional<ReadError<T>> getLeft() {
        return left;
    }

    public static class Builder<T extends Claims> {
        private T right;
        private ReadError<T> left;

        public Builder<T> left(ReadError<T> left) {
            this.left = left;
            return this;
        }

        public Builder<T> right(T right) {
            this.right = right;
            return this;
        }

        public ReadEither<T> build() {
            Optional<T> right = Objects.isNull(this.right) ? Optional.empty() : Optional.of(this.right);
            Optional<ReadError<T>> left = Objects.isNull(this.left) ? Optional.empty() : Optional.of(this.left);
            return new ReadEither<>(left, right);
        }
    }
}
