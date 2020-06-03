package helper;


import helper.entity.*;
import helper.entity.model.DummyPayload;
import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import helper.fake.FakeResource;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.jwt.entity.jwk.Use;
import net.tokensmith.jwt.entity.jwt.JsonWebToken;
import net.tokensmith.jwt.entity.jwt.header.Algorithm;
import net.tokensmith.jwt.exception.InvalidJWT;
import net.tokensmith.jwt.exception.SignatureException;
import net.tokensmith.jwt.jws.serialization.SecureJwtSerializer;
import net.tokensmith.jwt.serialization.JwtSerde;
import net.tokensmith.jwt.serialization.exception.JsonToJwtException;
import net.tokensmith.jwt.serialization.exception.JwtToJsonException;
import net.tokensmith.otter.config.CookieConfig;
import net.tokensmith.otter.controller.builder.MimeTypeBuilder;
import net.tokensmith.otter.controller.builder.ResponseBuilder;
import net.tokensmith.otter.controller.entity.Cookie;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.request.RestRequest;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.mime.MimeType;
import net.tokensmith.otter.controller.entity.response.RestResponse;
import net.tokensmith.otter.controller.header.Header;
import net.tokensmith.otter.controller.header.HeaderValue;
import net.tokensmith.otter.dispatch.html.RouteRun;
import net.tokensmith.otter.dispatch.RouteRunner;
import net.tokensmith.otter.dispatch.entity.RestBtwnRequest;
import net.tokensmith.otter.dispatch.entity.RestBtwnResponse;
import net.tokensmith.otter.dispatch.entity.RestErrorRequest;
import net.tokensmith.otter.dispatch.entity.RestErrorResponse;
import net.tokensmith.otter.dispatch.translator.AnswerTranslator;
import net.tokensmith.otter.dispatch.translator.RequestTranslator;
import net.tokensmith.otter.gateway.builder.ErrorTargetBuilder;
import net.tokensmith.otter.gateway.builder.RestTargetBuilder;
import net.tokensmith.otter.gateway.builder.ShapeBuilder;
import net.tokensmith.otter.gateway.builder.TargetBuilder;
import net.tokensmith.otter.gateway.entity.*;
import net.tokensmith.otter.gateway.entity.rest.RestErrorTarget;
import net.tokensmith.otter.gateway.entity.rest.RestTarget;
import net.tokensmith.otter.router.builder.AnswerBuilder;
import net.tokensmith.otter.router.builder.AskBuilder;
import net.tokensmith.otter.router.builder.LocationBuilder;
import net.tokensmith.otter.router.builder.RouteBuilder;
import net.tokensmith.otter.router.entity.*;
import net.tokensmith.otter.router.entity.between.Between;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.router.entity.io.Answer;
import net.tokensmith.otter.router.entity.io.Ask;
import net.tokensmith.otter.security.builder.entity.Betweens;
import net.tokensmith.otter.security.builder.entity.RestBetweens;
import net.tokensmith.otter.security.csrf.CsrfClaims;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.regex.Matcher;

public class FixtureFactory {
    private static JwtAppFactory jwtAppFactory = new JwtAppFactory();

    public static Shape makeShape(String encKeyId, String signKeyId) {
        SymmetricKey encKey = FixtureFactory.encKey(encKeyId);
        SymmetricKey signKey = FixtureFactory.signKey(signKeyId);

        return new ShapeBuilder()
                .encKey(encKey)
                .signkey(signKey)
                .build();
    }

    public static CookieConfig csrfCookieConfig() {
        return makeShape("foo", "bar").getCsrfCookie();
    }

    public static CookieConfig sessionCookieConfig() {
        return makeShape("foo", "bar").getSessionCookie();
    }

    public static Optional<MatchedLocation> makeMatch(String url) {
        Location route = makeLocation(url);
        Matcher matcher = route.getPattern().matcher(url);
        return  Optional.of(new MatchedLocation(matcher, route));
    }

    public static Route<DummySession, DummyUser> makeRoute() {
        FakeResource resource = new FakeResource();
        return new RouteBuilder<DummySession, DummyUser>()
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
    }

    public static RestRoute<DummySession, DummyUser, DummyPayload> makeRestRoute() {
        OkRestResource okRestResource = new OkRestResource();
        return new RestRoute<DummySession, DummyUser, DummyPayload>(
                okRestResource, new ArrayList<>(), new ArrayList<>()
        );
    }

    public static Location makeLocation(String regex) {
        FakeResource resource = new FakeResource();
        return new LocationBuilder<DummySession, DummyUser>()
            .path(regex)
            .contentTypes(new ArrayList<MimeType>())
            .resource(resource)
            .before(new ArrayList<>())
            .after(new ArrayList<>())
            .build();
    }

    public static Location makeLocationWithErrorRoutes(String regex) {
        FakeResource resource = new FakeResource();
        FakeResource unSupportedMediaType = new FakeResource();
        FakeResource serverError = new FakeResource();

        return new LocationBuilder<DummySession, DummyUser>()
                .path(regex)
                .contentTypes(new ArrayList<MimeType>())
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .errorRouteRunner(StatusCode.UNSUPPORTED_MEDIA_TYPE, unSupportedMediaType)
                .errorRouteRunner(StatusCode.SERVER_ERROR, unSupportedMediaType)
                .build();
    }

    public static Map<StatusCode, Route<DummySession, DummyUser>> makeErrorRoutes() {
        Route<DummySession, DummyUser> notFound = FixtureFactory.makeRoute();
        Route<DummySession, DummyUser> unSupportedMediaType = FixtureFactory.makeRoute();
        Route<DummySession, DummyUser> serverError = FixtureFactory.makeRoute();

        Map<StatusCode,Route<DummySession, DummyUser>> errorRoutes = new HashMap<>();
        errorRoutes.put(StatusCode.NOT_FOUND, notFound);
        errorRoutes.put(StatusCode.UNSUPPORTED_MEDIA_TYPE, unSupportedMediaType);
        errorRoutes.put(StatusCode.SERVER_ERROR, serverError);

        return errorRoutes;
    }

    public static Map<StatusCode, RouteRunner> makeErrorRouteRunners() {
        Route<DummySession, DummyUser> notFound = FixtureFactory.makeRoute();
        Route<DummySession, DummyUser> unSupportedMediaType = FixtureFactory.makeRoute();
        Route<DummySession, DummyUser> serverError = FixtureFactory.makeRoute();

        Map<StatusCode, RouteRunner> errorRouteRunners = new HashMap<>();

        RequestTranslator<DummySession, DummyUser> requestTranslator = new RequestTranslator<>();
        AnswerTranslator<DummySession> answerTranslator = new AnswerTranslator<>();
        RouteRunner notFoundRunner = new RouteRun<DummySession, DummyUser>(notFound, requestTranslator, answerTranslator, new HashMap<>());
        RouteRunner unSupportedMediaTypeRunner = new RouteRun<DummySession, DummyUser>(unSupportedMediaType, requestTranslator, answerTranslator, new HashMap<>());
        RouteRunner serverErrorRunner = new RouteRun<DummySession, DummyUser>(serverError, requestTranslator, answerTranslator, new HashMap<>());

        errorRouteRunners.put(StatusCode.NOT_FOUND, notFoundRunner);
        errorRouteRunners.put(StatusCode.UNSUPPORTED_MEDIA_TYPE, unSupportedMediaTypeRunner);
        errorRouteRunners.put(StatusCode.SERVER_ERROR, serverErrorRunner);

        return errorRouteRunners;
    }

    public static List<Location> makeLocations(String baseContext) {
        List<Location> locations = new ArrayList<>();
        locations.add(makeLocation(baseContext + Regex.UUID.getRegex()));
        locations.add(makeLocation(baseContext + Regex.UUID.getRegex() + "/bar"));
        return locations;
    }

    public static Target<DummySession, DummyUser> makeTarget() {

        FakeResource notFoundResource = new FakeResource();
        ErrorTarget<DummySession, DummyUser> notFound = new ErrorTargetBuilder<DummySession, DummyUser>()
                .resource(notFoundResource)
                .build();

        FakeResource fakeResource = new FakeResource();
        MimeType html = new MimeTypeBuilder().html().build();

        TargetBuilder<DummySession, DummyUser> targetBuilder = new TargetBuilder<DummySession, DummyUser>();

        return targetBuilder
                .regex("/foo")
                .method(Method.GET)
                .method(Method.POST)
                .contentType(html)
                .accept(html)
                .resource(fakeResource)
                .before(new DummyBetween<>())
                .before(new DummyBetween<>())
                .after(new DummyBetween<>())
                .after(new DummyBetween<>())
                .form()
                .onDispatchError(StatusCode.NOT_FOUND, notFound)
                .build();
    }

    public static Betweens<DummySession, DummyUser> makeBetweens() {
        Between<DummySession, DummyUser> before = new DummyBetween<>();
        Between<DummySession, DummyUser> after = new DummyBetween<>();
        return new Betweens<>(
                Arrays.asList(before), Arrays.asList(after)
        );
    }

    public static RestTarget<DummySession, DummyUser, DummyPayload> makeRestTarget() {

        OkRestResource okRestResource = new OkRestResource();
        OkRestResource notFoundResource = new OkRestResource();
        RestErrorTarget<DummySession, DummyUser, DummyPayload> notFound = new RestErrorTarget<DummySession, DummyUser, DummyPayload>(
                DummyPayload.class, notFoundResource, new ArrayList<>(), new ArrayList<>()
        );

        MimeType json = new MimeTypeBuilder().json().build();

        RestTargetBuilder<DummySession, DummyUser, DummyPayload> builder = new RestTargetBuilder<>();

        return builder
                .regex("/foo")
                .method(Method.GET)
                .method(Method.POST)
                .contentType(json)
                .accept(json)
                .restResource(okRestResource)
                .payload(DummyPayload.class)
                .before(new DummyRestBetween<>())
                .before(new DummyRestBetween<>())
                .after(new DummyRestBetween<>())
                .after(new DummyRestBetween<>())
                .authenticate()
                .onDispatchError(StatusCode.NOT_FOUND, notFound)
                .build();
    }

    public static RestBetweens<DummySession, DummyUser> makeRestBetweens() {
        RestBetween<DummySession, DummyUser> before = new DummyRestBetween<>();
        RestBetween<DummySession, DummyUser> after = new DummyRestBetween<>();
        return new RestBetweens<>(
                Arrays.asList(before), Arrays.asList(after)
        );
    }

    public static Map<String, List<String>> makeEmptyQueryParams() {
        Map<String, List<String>> params = new HashMap<>();
        return params;
    }

    public static Ask makeAsk() {
        MimeType html = new MimeTypeBuilder().html().build();

        List<MimeType> contentTypes = new ArrayList<>();
        contentTypes.add(html);

        Ask ask = new AskBuilder()
            .matcher(Optional.empty())
            .possibleContentTypes(contentTypes)
            .possibleAccepts(contentTypes)
            .method(Method.GET)
            .pathWithParams("")
            .contentType(html)
            .accept(html)
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

    public static Request<DummySession, DummyUser> makeRequest() {
        Request<DummySession, DummyUser> request = new Request<DummySession, DummyUser>();
        request.setFormData(new HashMap<>());
        request.setCookies(makeCookies());
        request.setCsrfChallenge(Optional.empty());
        return request;
    }

    public static RestRequest<DummyUser, DummyPayload> makeRestRequest() {
        RestRequest<DummyUser, DummyPayload> request = new RestRequest<DummyUser, DummyPayload>();
        request.setFormData(new HashMap<>());
        request.setCookies(makeCookies());
        request.setBody(Optional.empty());
        request.setPayload(Optional.empty());
        request.setUser(Optional.empty());
        return request;
    }

    public static RestBtwnRequest<DummySession, DummyUser> makeRestBtwnRequest() {
        RestBtwnRequest<DummySession, DummyUser> request =  new RestBtwnRequest<DummySession, DummyUser>();

        MimeType json = new MimeTypeBuilder().json().build();
        List<MimeType> contentTypes = new ArrayList<>();
        contentTypes.add(json);

        request.setMatcher(Optional.empty());
        request.setPossibleContentTypes(contentTypes);
        request.setPossibleAccepts(contentTypes);
        request.setMethod(Method.GET);
        request.setPathWithParams("");
        request.setContentType(json);
        request.setAccept(json);
        request.setHeaders(new HashMap<>());
        request.setCookies(makeCookies());
        request.setQueryParams(new HashMap<>());
        request.setFormData(new HashMap<>());
        request.setBody(Optional.empty());
        request.setIpAddress("127.0.0.1");
        request.setSession(Optional.empty());

        return request;
    }

    public static RestErrorRequest<DummyUser> makeRestErrorRequest() {
        return new RestErrorRequest<DummyUser>();
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

    public static RestResponse<DummyPayload> makeRestResponse() {
        return new RestResponse<DummyPayload>(
                StatusCode.OK, new HashMap<>(), new HashMap<>(), Optional.empty()
        );
    }

    public static RestBtwnResponse makeRestBtwnResponse() {
        return new RestBtwnResponse(
                StatusCode.OK, new HashMap<>(), new HashMap<>(), Optional.empty(), Optional.empty()
        );
    }

    public static RestErrorResponse makeRestErrorResponse() {
        return new RestErrorResponse(
                StatusCode.OK, new HashMap<>(), new HashMap<>(), Optional.empty()
        );
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
