package helper;


import helper.entity.FakeResource;
import org.rootservices.jwt.SecureJwtEncoder;
import org.rootservices.jwt.config.AppFactory;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.jwt.entity.jwk.Use;
import org.rootservices.jwt.entity.jwt.JsonWebToken;
import org.rootservices.jwt.entity.jwt.header.Algorithm;
import org.rootservices.jwt.serializer.JWTSerializer;
import org.rootservices.jwt.serializer.exception.JsonToJwtException;
import org.rootservices.jwt.serializer.exception.JwtToJsonException;
import org.rootservices.jwt.signature.signer.factory.exception.InvalidAlgorithmException;
import org.rootservices.jwt.signature.signer.factory.exception.InvalidJsonWebKeyException;
import org.rootservices.otter.controller.builder.ResponseBuilder;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.header.Header;
import org.rootservices.otter.controller.header.HeaderValue;
import org.rootservices.otter.router.entity.MatchedRoute;
import org.rootservices.otter.router.entity.Regex;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.security.csrf.CsrfClaims;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FixtureFactory {
    private static AppFactory jwtFactory = new AppFactory();

    public static Optional<MatchedRoute> makeMatch(String url) {
        Route route = makeRoute(url);
        Matcher matcher = route.getPattern().matcher(url);
        return  Optional.of(new MatchedRoute(matcher, route));
    }

    public static Route makeRoute(String regex) {
        Pattern p = Pattern.compile(regex);
        FakeResource resource = new FakeResource();
        return new Route(p, resource);
    }

    public static List<Route> makeRoutes() {
        return makeRoutes("/api/v1/foo/");
    }

    public static List<Route> makeRoutes(String baseContext) {
        List<Route> routes = new ArrayList<>();
        routes.add(makeRoute(baseContext + Regex.UUID.getRegex()));
        routes.add(makeRoute(baseContext + Regex.UUID.getRegex() + "/bar"));
        return routes;
    }

    public static Map<String, List<String>> makeEmptyQueryParams() {
        Map<String, List<String>> params = new HashMap<>();
        return params;
    }

    public static Request makeRequest() {
        Request request = new Request();
        request.setCookies(makeCookies());
        return request;
    }

    public static Map<String, String> makeHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(Header.CACHE_CONTROL.getValue(), HeaderValue.NO_CACHE.getValue());
        return headers;
    }

    public static Response makeResponse() {
        return new ResponseBuilder()
                .headers(new HashMap<>())
                .cookies(new HashMap<>())
                .payload(Optional.empty())
                .template(Optional.empty())
                .ok()
                .build();
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

    public static String encodedCsrfJwt(SymmetricKey signKey, String challengeToken) {
        CsrfClaims csrfClaims = new CsrfClaims();
        csrfClaims.setChallengeToken(challengeToken);
        csrfClaims.setIssuedAt(Optional.of(OffsetDateTime.now().toEpochSecond()));

        SecureJwtEncoder secureJwtEncoder = null;
        try {
            secureJwtEncoder = jwtFactory.secureJwtEncoder(Algorithm.HS256, signKey);
        } catch (InvalidAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidJsonWebKeyException e) {
            e.printStackTrace();
        }

        String encodedJwt = null;
        try {
            encodedJwt = secureJwtEncoder.encode(csrfClaims);
        } catch (JwtToJsonException e) {
            e.printStackTrace();
        }

        return encodedJwt;
    }

    public static JsonWebToken csrfJwt(String encodedCsrfJwt) {
        JWTSerializer jwtSerializer = jwtFactory.jwtSerializer();

        JsonWebToken jsonWebToken = null;
        try {
            jsonWebToken = jwtSerializer.stringToJwt(encodedCsrfJwt, CsrfClaims.class);
        } catch (JsonToJwtException e) {
            // could not create a JsonWebToken from the jwt json.
        }

        return jsonWebToken;

    }
}
