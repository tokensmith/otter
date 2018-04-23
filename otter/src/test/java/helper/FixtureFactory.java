package helper;


import helper.entity.FakeResource;
import io.netty.handler.codec.http.cookie.DefaultCookie;
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
    private static JwtAppFactory jwtAppFactory = new JwtAppFactory();

    public static Optional<MatchedRoute> makeMatch(String url) {
        Route route = makeRoute(url);
        Matcher matcher = route.getPattern().matcher(url);
        return  Optional.of(new MatchedRoute(matcher, route));
    }

    public static Route makeRoute(String regex) {
        Pattern p = Pattern.compile(regex);
        FakeResource resource = new FakeResource();
        return new Route(p, resource, new ArrayList<>(), new ArrayList<>());
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

    public static Response makeResponse() {
        return new ResponseBuilder()
                .headers(new HashMap<>())
                .cookies(new HashMap<>())
                .payload(Optional.empty())
                .template(Optional.empty())
                .presenter(Optional.empty())
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

    /**
     * should domain be non null?
     * should http only be true?
     *
     * @return
     */
    public static io.netty.handler.codec.http.cookie.Cookie sessionCookie() {
        io.netty.handler.codec.http.cookie.Cookie sessionCookie = new DefaultCookie("session", "eyJhbGciOiJkaXIiLCJraWQiOiJrZXktMiIsImVuYyI6IkEyNTZHQ00ifQ..tk4SPH6W5Y9Vs-4CfhcMaBgrcfk8UrH67Hwfpq7qDgewDqd07k-d9ApCpuuNqFgkKMK_5KR06BNO3tfDo2lGg_kVB2-S1C-SvawOZB1Xrn0bRdU_oMDePJp-gMV_yDct.sYM50OOWl5-4VSCyLlsWWdi5kGszuPfF8El5EUSCywWUy67miDknFoVKVwVxCu3EijXCmGk1Ig.qIRxD-uPapS2Qhoh_fMCuA");
        sessionCookie.setHttpOnly(false);
        sessionCookie.setMaxAge(-9223372036854775808L);
        return sessionCookie;
    }
}
