package net.tokensmith.otter.dispatch.entity.either;

import net.tokensmith.otter.controller.entity.response.RestResponse;

import java.util.Optional;

public class RestReponseEither<S, U, P> {
    private Optional<RestResponse<P>> left = Optional.empty();
    private Optional<RestResponseError<S, U, P>> right = Optional.empty();

    public RestReponseEither() {
    }

    public RestReponseEither(Optional<RestResponse<P>> left, Optional<RestResponseError<S, U, P>> right) {
        this.left = left;
        this.right = right;
    }

    public Optional<RestResponse<P>> getLeft() {
        return left;
    }

    public void setLeft(Optional<RestResponse<P>> left) {
        this.left = left;
    }

    public Optional<RestResponseError<S, U, P>> getRight() {
        return right;
    }

    public void setRight(Optional<RestResponseError<S, U, P>> right) {
        this.right = right;
    }
}
