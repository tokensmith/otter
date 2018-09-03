package helper;


import helper.entity.DummySession;
import helper.entity.DummyUser;
import helper.entity.FakeResource;
import org.rootservices.jwt.config.JwtAppFactory;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.jwt.entity.jwk.Use;
import org.rootservices.jwt.entity.jwt.JsonWebToken;
import org.rootservices.jwt.entity.jwt.header.Algorithm;
import org.rootservices.jwt.exception.InvalidJWT;
import org.rootservices.jwt.exception.SignatureException;
import org.rootservices.jwt.jws.serialization.SecureJwtSerializer;
import org.rootservices.jwt.serialization.JwtSerde;
import org.rootservices.jwt.serialization.exception.JsonToJwtException;
import org.rootservices.jwt.serialization.exception.JwtToJsonException;
import org.rootservices.otter.controller.builder.ResponseBuilder;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.controller.header.Header;
import org.rootservices.otter.controller.header.HeaderValue;
import org.rootservices.otter.router.builder.LocationBuilder;
import org.rootservices.otter.router.builder.RouteBuilder;
import org.rootservices.otter.router.entity.MatchedLocation;
import org.rootservices.otter.router.entity.Regex;
import org.rootservices.otter.router.entity.Location;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.security.csrf.CsrfClaims;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.regex.Matcher;

public class FixtureFactory {
    private static JwtAppFactory jwtAppFactory = new JwtAppFactory();

    public static Optional<MatchedLocation<DummySession, DummyUser>> makeMatch(String url) {
        Location<DummySession, DummyUser> route = makeLocation(url);
        Matcher matcher = route.getPattern().matcher(url);
        return  Optional.of(new MatchedLocation<DummySession, DummyUser>(matcher, route));
    }

    public static Route<DummySession, DummyUser> makeRoute() {
        FakeResource resource = new FakeResource();
        return new RouteBuilder<DummySession, DummyUser>()
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
    }

    public static Location<DummySession, DummyUser> makeLocation(String regex) {
        FakeResource resource = new FakeResource();
        return new LocationBuilder<DummySession, DummyUser>()
            .path(regex)
            .contentTypes(new ArrayList<MimeType>())
            .resource(resource)
            .before(new ArrayList<>())
            .after(new ArrayList<>())
            .build();
    }

    public static Location<DummySession, DummyUser> makeLocationWithErrorRoutes(String regex) {
        FakeResource resource = new FakeResource();
        FakeResource unSupportedMediaType = new FakeResource();
        FakeResource serverError = new FakeResource();

        return new LocationBuilder<DummySession, DummyUser>()
                .path(regex)
                .contentTypes(new ArrayList<MimeType>())
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .errorResource(StatusCode.UNSUPPORTED_MEDIA_TYPE, unSupportedMediaType)
                .errorResource(StatusCode.SERVER_ERROR, serverError)
                .build();
    }

    public static Map<StatusCode, Route<DummySession, DummyUser>> makeErrorRoutes() {
        Route<DummySession, DummyUser> notFound = FixtureFactory.makeRoute();
        Route<DummySession, DummyUser> unSupportedMediaType = FixtureFactory.makeRoute();
        Route<DummySession, DummyUser> serverError = FixtureFactory.makeRoute();

        Map<StatusCode, Route<DummySession, DummyUser>> errorRoutes = new HashMap<>();
        errorRoutes.put(StatusCode.NOT_FOUND, notFound);
        errorRoutes.put(StatusCode.UNSUPPORTED_MEDIA_TYPE, unSupportedMediaType);
        errorRoutes.put(StatusCode.SERVER_ERROR, serverError);

        return errorRoutes;
    }

    public static List<Location<DummySession, DummyUser>> makeLocations(String baseContext) {
        List<Location<DummySession, DummyUser>> locations = new ArrayList<>();
        locations.add(makeLocation(baseContext + Regex.UUID.getRegex()));
        locations.add(makeLocation(baseContext + Regex.UUID.getRegex() + "/bar"));
        return locations;
    }

    public static Map<String, List<String>> makeEmptyQueryParams() {
        Map<String, List<String>> params = new HashMap<>();
        return params;
    }

    public static Request<DummySession, DummyUser> makeRequest() {
        Request<DummySession, DummyUser> request = new Request<DummySession, DummyUser>();
        request.setFormData(new HashMap<>());
        request.setCookies(makeCookies());
        request.setCsrfChallenge(Optional.empty());
        return request;
    }

    public static Map<String, String> makeHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(Header.CACHE_CONTROL.getValue(), HeaderValue.NO_CACHE.getValue());
        return headers;
    }

    public static Response<DummySession> makeResponse() {
        Response<DummySession> response = new ResponseBuilder<DummySession>()
                .headers(new HashMap<>())
                .cookies(new HashMap<>())
                .payload(Optional.empty())
                .template(Optional.empty())
                .presenter(Optional.empty())
                .ok()
                .build();

        return response;
    }

    public static Map<String, Cookie> makeCookies() {
        List<Cookie> cookieList = new ArrayList<>();
        cookieList.add(makeCookie());

        Map<String, Cookie> cookies = new HashMap<>();
        cookies.put(cookieList.get(0).getName(), cookieList.get(0));
        return cookies;
    }

    public static Cookie makeCookie() {
        return makeCookie("test");
    }

    public static Cookie makeCookie(String name) {
        Cookie cookie = new Cookie();
        cookie.setName(name);
        cookie.setValue("test-value");
        return cookie;
    }

    public static SymmetricKey signKey(String keyId) {
        return new SymmetricKey(
            Optional.of(keyId),
            "AyM1SysPpbyDfgZld3umj1qzKObwVMkoqQ-EstJQLr_T-1qS0gZH75aKtMN3Yj0iPS4hcgUuTwjAzZr1Z9CAow",
            Use.SIGNATURE
        );
    }

    public static SymmetricKey encKey(String keyId) {
        return new SymmetricKey(
                Optional.of(keyId),
                "MMNj8rE5m7NIDhwKYDmHSnlU1wfKuVvW6G--GKPYkRA",
                Use.ENCRYPTION
        );
    }

    public static Map<String, SymmetricKey> encRotationKey(String keyId) {
        Map<String, SymmetricKey> encRotationKey = new HashMap<>();
        SymmetricKey key = new SymmetricKey(
                Optional.of(keyId),
                "MMNj8rE5m7NIDhwKYDmHSnlU1wfKuVvW6G--GKPYkRA",
                Use.ENCRYPTION
        );
        encRotationKey.put(keyId, key);
        return encRotationKey;
    }

    public static String compactJwtForCSRF(SymmetricKey signKey, String challengeToken) {
        CsrfClaims csrfClaims = new CsrfClaims();
        csrfClaims.setChallengeToken(challengeToken);
        csrfClaims.setIssuedAt(Optional.of(OffsetDateTime.now().toEpochSecond()));

        SecureJwtSerializer secureJwtSerializer = null;
        try {
            secureJwtSerializer = jwtAppFactory.secureJwtSerializer(Algorithm.HS256, signKey);
        } catch (SignatureException e) {
            e.printStackTrace();
        }

        String compactJwt = null;
        try {
            compactJwt = secureJwtSerializer.compactJwtToString(csrfClaims);
        } catch (JwtToJsonException e) {
            e.printStackTrace();
        }

        return compactJwt;
    }

    public static JsonWebToken csrfJwt(String encodedCsrfJwt) {
        JwtSerde jwtSerde = jwtAppFactory.jwtSerde();

        JsonWebToken jsonWebToken = null;
        try {
            jsonWebToken = jwtSerde.stringToJwt(encodedCsrfJwt, CsrfClaims.class);
        } catch (JsonToJwtException e) {
            e.printStackTrace();
        } catch (InvalidJWT e) {
            e.printStackTrace();
        }

        return jsonWebToken;
    }
}
