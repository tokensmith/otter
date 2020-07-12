package net.tokensmith.otter.dispatch.entity.either;

import net.tokensmith.otter.controller.entity.response.Response;

import java.util.Optional;

public class ResponseEither<S, U> {
    private Optional<ResponseError<S, U>> left;
    private Optional<Response<S>> right;

    public Optional<Response<S>> getRight() {
        return right;
    }

    public void setRight(Optional<Response<S>> right) {
        this.right = right;
    }

    public Optional<ResponseError<S, U>> getLeft() {
        return left;
    }

    public void setLeft(Optional<ResponseError<S, U>> left) {
        this.left = left;
    }
}
