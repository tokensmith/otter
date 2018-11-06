package helper;


import helper.entity.DummyBetween;
import helper.entity.DummyPayload;
import helper.entity.DummySession;
import helper.entity.DummyUser;
import helper.fake.FakeResource;
import helper.fake.FakeRestResource;
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
import org.rootservices.otter.controller.builder.MimeTypeBuilder;
import org.rootservices.otter.controller.builder.ResponseBuilder;
import org.rootservices.otter.controller.entity.*;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.controller.header.Header;
import org.rootservices.otter.controller.header.HeaderValue;
import org.rootservices.otter.dispatch.HtmlRouteRun;
import org.rootservices.otter.dispatch.RouteRunner;
import org.rootservices.otter.dispatch.translator.AnswerTranslator;
import org.rootservices.otter.dispatch.translator.RequestTranslator;
import org.rootservices.otter.gateway.builder.ErrorTargetBuilder;
import org.rootservices.otter.gateway.builder.ShapeBuilder;
import org.rootservices.otter.gateway.builder.target.TargetBuilder;
import org.rootservices.otter.gateway.entity.ErrorTarget;
import org.rootservices.otter.gateway.entity.Label;
import org.rootservices.otter.gateway.entity.Shape;
import org.rootservices.otter.gateway.entity.target.Target;
import org.rootservices.otter.router.builder.AnswerBuilder;
import org.rootservices.otter.router.builder.AskBuilder;
import org.rootservices.otter.router.builder.LocationBuilder;
import org.rootservices.otter.router.builder.RouteBuilder;
import org.rootservices.otter.router.entity.*;
import org.rootservices.otter.router.entity.io.Answer;
import org.rootservices.otter.router.entity.io.Ask;
import org.rootservices.otter.security.builder.entity.Betweens;
import org.rootservices.otter.security.csrf.CsrfClaims;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.regex.Matcher;

public class FixtureFactory {
    private static JwtAppFactory jwtAppFactory = new JwtAppFactory();

    public static Shape makeShape(String encKeyId, String signKeyId) {
        SymmetricKey encKey = FixtureFactory.encKey(encKeyId);
        SymmetricKey signKey = FixtureFactory.signKey(signKeyId);

        return new ShapeBuilder()
                .secure(false)
                .encKey(encKey)
                .signkey(signKey)
                .build();
    }

    public static Optional<MatchedLocation> makeRestMatch(String url) {
        Location route = makeRestLocation(url);
        Matcher matcher = route.getPattern().matcher(url);
        return  Optional.of(new MatchedLocation(matcher, route));
    }

    public static Route<DummySession, DummyUser, EmptyPayload> makeRoute() {
        FakeResource resource = new FakeResource();
        return new RouteBuilder<DummySession, DummyUser, EmptyPayload>()
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
    }

    public static Route<DummySession, DummyUser, DummyPayload> makeRestRoute() {
        FakeRestResource resource = new FakeRestResource();
        return new RouteBuilder<DummySession, DummyUser, DummyPayload>()
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
    }

    public static Location makeRestLocation(String regex) {
        FakeRestResource resource = new FakeRestResource();
        return new LocationBuilder<DummySession, DummyUser, DummyPayload>()
            .path(regex)
            .contentTypes(new ArrayList<MimeType>())
            .resource(resource)
            .before(new ArrayList<>())
            .after(new ArrayList<>())
            .build();
    }

    public static Location makeRestLocationWithErrorRoutes(String regex) {
        FakeRestResource resource = new FakeRestResource();
        FakeRestResource unSupportedMediaType = new FakeRestResource();
        FakeRestResource serverError = new FakeRestResource();

        return new LocationBuilder<DummySession, DummyUser, DummyPayload>()
                .path(regex)
                .contentTypes(new ArrayList<MimeType>())
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .errorRouteRunner(StatusCode.UNSUPPORTED_MEDIA_TYPE, unSupportedMediaType)
                .errorRouteRunner(StatusCode.SERVER_ERROR, unSupportedMediaType)
                .build();
    }

    public static Map<StatusCode, Route<DummySession, DummyUser, DummyPayload>> makeRestErrorRoutes() {
        Route<DummySession, DummyUser, DummyPayload> notFound = FixtureFactory.makeRestRoute();
        Route<DummySession, DummyUser, DummyPayload> unSupportedMediaType = FixtureFactory.makeRestRoute();
        Route<DummySession, DummyUser, DummyPayload> serverError = FixtureFactory.makeRestRoute();

        Map<StatusCode,Route<DummySession, DummyUser, DummyPayload>> errorRoutes = new HashMap<>();
        errorRoutes.put(StatusCode.NOT_FOUND, notFound);
        errorRoutes.put(StatusCode.UNSUPPORTED_MEDIA_TYPE, unSupportedMediaType);
        errorRoutes.put(StatusCode.SERVER_ERROR, serverError);

        return errorRoutes;
    }

    public static Map<StatusCode, RouteRunner> makeRestErrorRouteRunners() {
        Route<DummySession, DummyUser, DummyPayload> notFound = FixtureFactory.makeRestRoute();
        Route<DummySession, DummyUser, DummyPayload> unSupportedMediaType = FixtureFactory.makeRestRoute();
        Route<DummySession, DummyUser, DummyPayload> serverError = FixtureFactory.makeRestRoute();

        Map<StatusCode, RouteRunner> errorRouteRunners = new HashMap<>();

        RequestTranslator<DummySession, DummyUser, DummyPayload> requestTranslator = new RequestTranslator<>();
        AnswerTranslator<DummySession> answerTranslator = new AnswerTranslator<>();

        RouteRunner notFoundRunner = new HtmlRouteRun<DummySession, DummyUser, DummyPayload>(notFound, requestTranslator, answerTranslator);
        RouteRunner unSupportedMediaTypeRunner = new HtmlRouteRun<DummySession, DummyUser, DummyPayload>(unSupportedMediaType, requestTranslator, answerTranslator);
        RouteRunner serverErrorRunner = new HtmlRouteRun<DummySession, DummyUser, DummyPayload>(serverError, requestTranslator, answerTranslator);

        errorRouteRunners.put(StatusCode.NOT_FOUND, notFoundRunner);
        errorRouteRunners.put(StatusCode.UNSUPPORTED_MEDIA_TYPE, unSupportedMediaTypeRunner);
        errorRouteRunners.put(StatusCode.SERVER_ERROR, serverErrorRunner);

        return errorRouteRunners;
    }

    public static Map<StatusCode, Route<DummySession, DummyUser, EmptyPayload>> makeErrorRoutes() {
        Route<DummySession, DummyUser, EmptyPayload> notFound = FixtureFactory.makeRoute();
        Route<DummySession, DummyUser, EmptyPayload> unSupportedMediaType = FixtureFactory.makeRoute();
        Route<DummySession, DummyUser, EmptyPayload> serverError = FixtureFactory.makeRoute();

        Map<StatusCode,Route<DummySession, DummyUser, EmptyPayload>> errorRoutes = new HashMap<>();
        errorRoutes.put(StatusCode.NOT_FOUND, notFound);
        errorRoutes.put(StatusCode.UNSUPPORTED_MEDIA_TYPE, unSupportedMediaType);
        errorRoutes.put(StatusCode.SERVER_ERROR, serverError);

        return errorRoutes;
    }

    public static List<Location> makeLocations(String baseContext) {
        List<Location> locations = new ArrayList<>();
        locations.add(makeRestLocation(baseContext + Regex.UUID.getRegex()));
        locations.add(makeRestLocation(baseContext + Regex.UUID.getRegex() + "/bar"));
        return locations;
    }

    public static Target<DummySession, DummyUser, EmptyPayload> makeTarget() {

        FakeResource notFoundResource = new FakeResource();
        ErrorTarget<DummySession, DummyUser, EmptyPayload> notFound = new ErrorTargetBuilder<DummySession, DummyUser, EmptyPayload>()
                .resource(notFoundResource)
                .build();

        FakeResource fakeResource = new FakeResource();
        MimeType html = new MimeTypeBuilder().html().build();

        TargetBuilder<DummySession, DummyUser, EmptyPayload> targetBuilder = new TargetBuilder<DummySession, DummyUser, EmptyPayload>();

        return targetBuilder
                .regex("/foo")
                .method(Method.GET)
                .method(Method.POST)
                .resource(fakeResource)
                .contentType(html)
                .before(new DummyBetween<>())
                .before(new DummyBetween<>())
                .after(new DummyBetween<>())
                .after(new DummyBetween<>())
                .label(Label.CSRF)
                .label(Label.SESSION_REQUIRED)
                .errorTarget(StatusCode.NOT_FOUND, notFound)
                .build();
    }

    public static Target<DummySession, DummyUser, DummyPayload> makeRestTarget() {

        FakeRestResource notFoundResource = new FakeRestResource();
        ErrorTarget<DummySession, DummyUser, DummyPayload> notFound = new ErrorTargetBuilder<DummySession, DummyUser, DummyPayload>()
                .resource(notFoundResource)
                .build();

        FakeRestResource fakeResource = new FakeRestResource();
        MimeType json = new MimeTypeBuilder().json().build();

        TargetBuilder<DummySession, DummyUser, DummyPayload> targetBuilder = new TargetBuilder<DummySession, DummyUser, DummyPayload>();

        return targetBuilder
                .regex("/foo")
                .method(Method.GET)
                .method(Method.POST)
                .contentType(json)
                .resource(fakeResource)
                .before(new DummyBetween<>())
                .before(new DummyBetween<>())
                .after(new DummyBetween<>())
                .after(new DummyBetween<>())
                .label(Label.CSRF)
                .label(Label.SESSION_REQUIRED)
                .errorTarget(StatusCode.NOT_FOUND, notFound)
                .build();
    }

    public static Betweens<DummySession, DummyUser, EmptyPayload> makeBetweens() {
        Between<DummySession, DummyUser, EmptyPayload> before = new DummyBetween<>();
        Between<DummySession, DummyUser, EmptyPayload> after = new DummyBetween<>();
        return new Betweens<>(
                Arrays.asList(before), Arrays.asList(after)
        );
    }

    public static Map<String, List<String>> makeEmptyQueryParams() {
        Map<String, List<String>> params = new HashMap<>();
        return params;
    }

    public static Ask makeAsk() {
        Ask ask = new AskBuilder()
            .matcher(Optional.empty())
            .method(Method.GET)
            .pathWithParams("")
            .contentType(new MimeTypeBuilder().html().build())
            .headers(new HashMap<>())
            .cookies(new HashMap<>())
            .queryParams(new HashMap<>())
            .formData(new HashMap<>())
            .body(Optional.empty())
            .csrfChallenge(Optional.empty())
            .ipAddress("127.0.0.1")
            .build();

        return ask;
    }

    public static Answer makeAnswer() {
        return new AnswerBuilder()
                .headers(new HashMap<>())
                .cookies(new HashMap<>())
                .payload(Optional.empty())
                .template(Optional.of("template"))
                .presenter(Optional.empty())
                .ok()
                .build();
    }

    public static Request<DummySession, DummyUser, EmptyPayload> makeRequest() {
        Request<DummySession, DummyUser, EmptyPayload> request = new Request<DummySession, DummyUser, EmptyPayload>();
        request.setFormData(new HashMap<>());
        request.setCookies(makeCookies());
        request.setCsrfChallenge(Optional.empty());
        return request;
    }

    public static Request<DummySession, DummyUser, DummyPayload> makeRestRequest() {
        Request<DummySession, DummyUser, DummyPayload> request = new Request<DummySession, DummyUser, DummyPayload>();
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

    public static Map<String, SymmetricKey> rotationSignKeys(String baseKeyId, Integer qty) {
        Map<String, SymmetricKey> keys = new HashMap<>();
        for(int i = 0; i<qty; i++) {
            String keyId = baseKeyId + i;
            keys.put(keyId, FixtureFactory.signKey(keyId));
        }

        return keys;
    }

    public static SymmetricKey encKey(String keyId) {
        return new SymmetricKey(
                Optional.of(keyId),
                "MMNj8rE5m7NIDhwKYDmHSnlU1wfKuVvW6G--GKPYkRA",
                Use.ENCRYPTION
        );
    }

    public static Map<String, SymmetricKey> rotationEncKeys(String baseKeyId, Integer qty) {
        Map<String, SymmetricKey> keys = new HashMap<>();
        for(int i = 0; i<qty; i++) {
            String keyId = baseKeyId + i;
            keys.put(keyId, FixtureFactory.encKey(keyId));
        }

        return keys;
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
