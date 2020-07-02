package net.tokensmith.otter.dispatch.entity.either;

import net.tokensmith.otter.controller.entity.response.RestResponse;

import java.util.Optional;

public class RestReponseEither<S, U, P> {
    private Optional<RestResponseError<S, U, P>> left = Optional.empty();
    private Optional<RestResponse<P>> right = Optional.empty();

    public RestReponseEither() {
    }

    public RestReponseEither(Optional<RestResponse<P>> right, Optional<RestResponseError<S, U, P>> left) {
        this.left = left;
        this.right = right;
    }

    public Optional<RestResponse<P>> getRight() {
        return right;
    }

    public void setRight(Optional<RestResponse<P>> right) {
        this.right = right;
    }

    public Optional<RestResponseError<S, U, P>> getLeft() {
        return left;
    }

    public void setLeft(Optional<RestResponseError<S, U, P>> left) {
        this.left = left;
    }
}
