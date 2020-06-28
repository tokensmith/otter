package net.tokensmith.otter.security.cookie.either;

import net.tokensmith.jwt.entity.jwt.Claims;

import java.util.Objects;
import java.util.Optional;

public class ReadEither<T extends Claims> {
    private Optional<T> left;
    private Optional<ReadError<T>> right;

    public ReadEither(Optional<T> left, Optional<ReadError<T>> right) {
        this.left = left;
        this.right = right;
    }

    public Optional<T> getLeft() {
        return left;
    }

    public Optional<ReadError<T>> getRight() {
        return right;
    }

    public static class Builder<T extends Claims> {
        private T left;
        private ReadError<T> right;

        public Builder<T> left(T left) {
            this.left = left;
            return this;
        }

        public Builder<T> right(ReadError<T> right) {
            this.right = right;
            return this;
        }

        public ReadEither<T> build() {
            Optional<T> left = Objects.isNull(this.left) ? Optional.empty() : Optional.of(this.left);
            Optional<ReadError<T>> right = Objects.isNull(this.right) ? Optional.empty() : Optional.of(this.right);
            return new ReadEither<T>(left, right);
        }
    }
}
