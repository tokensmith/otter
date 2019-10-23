package net.tokensmith.otter.dispatch.entity.either;

import net.tokensmith.otter.controller.entity.response.Response;

import java.util.Optional;

public class ResponseEither<S, U> {
    private Optional<Response<S>> left;
    private Optional<ResponseError<S, U>> right;

    public Optional<Response<S>> getLeft() {
        return left;
    }

    public void setLeft(Optional<Response<S>> left) {
        this.left = left;
    }

    public Optional<ResponseError<S, U>> getRight() {
        return right;
    }

    public void setRight(Optional<ResponseError<S, U>> right) {
        this.right = right;
    }
}
