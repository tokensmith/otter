package net.tokensmith.otter.security.cookie;

import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.jwt.entity.jwt.Claims;
import net.tokensmith.otter.config.CookieConfig;
import net.tokensmith.otter.controller.entity.Cookie;
import net.tokensmith.otter.security.cookie.either.ReadEither;

public interface CookieSecurity {
    <T extends Claims> Cookie make(CookieConfig cookieConfig, T claim) throws CookieJwtException;
    <T extends Claims> ReadEither<T> read(String value, Class<T> claimClazz);
    SymmetricKey getKey(String keyId);
}
