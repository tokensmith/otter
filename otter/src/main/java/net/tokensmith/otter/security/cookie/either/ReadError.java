package net.tokensmith.otter.security.cookie.either;

import net.tokensmith.jwt.entity.jwt.Claims;

import java.util.Optional;

public class ReadError<T extends Claims> {
    private Optional<T> claims;
    private CookieError cookieError;
    private Throwable cause;

    public ReadError(Optional<T> claims, CookieError cookieError, Throwable cause) {
        this.claims = claims;
        this.cookieError = cookieError;
        this.cause = cause;
    }

    public Optional<T> getClaims() {
        return claims;
    }

    public void setClaims(Optional<T> claims) {
        this.claims = claims;
    }

    public CookieError getCookieError() {
        return cookieError;
    }

    public void setCookieError(CookieError cookieError) {
        this.cookieError = cookieError;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public static class Builder<T extends Claims> {
        private Optional<T> claims = Optional.empty();
        private CookieError cookieError;
        private Throwable cause;

        public Builder<T> claims(Optional<T> claims) {
            this.claims = claims;
            return this;
        }

        public Builder<T> cookieError(CookieError cookieError) {
            this.cookieError = cookieError;
            return this;
        }

        public Builder<T> cause(Throwable cause) {
            this.cause = cause;
            return this;
        }

        public ReadError<T> build() {
            return new ReadError<>(claims, cookieError, cause);
        }
    }
}
