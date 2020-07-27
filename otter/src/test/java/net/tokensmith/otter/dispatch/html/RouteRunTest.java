package net.tokensmith.otter.dispatch.html;

import helper.FixtureFactory;
import helper.entity.HaltBetween;
import helper.entity.OkResource;
import helper.entity.RuntimeExceptionResource;
import helper.entity.ServerErrorResource;
import helper.entity.model.DummyPayload;
import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.otter.dispatch.translator.AnswerTranslator;
import net.tokensmith.otter.dispatch.translator.RequestTranslator;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.Route;
import net.tokensmith.otter.router.entity.io.Answer;
import net.tokensmith.otter.router.entity.io.Ask;
import net.tokensmith.otter.router.exception.HaltException;
import net.tokensmith.otter.translator.config.TranslatorAppFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class RouteRunTest {
    private static TranslatorAppFactory appFactory = new TranslatorAppFactory();
    private RouteRun<DummySession, DummyUser> subject;

    @Before
    public void setUp(){
        OkResource okResource = new OkResource();
        subject = subject(okResource);
    }

    public RouteRun<DummySession, DummyUser> subject(Resource<DummySession, DummyUser> resource) {
        Route<DummySession, DummyUser> route = FixtureFactory.makeRoute();
        route.setResource(resource);

        RequestTranslator<DummySession, DummyUser> requestTranslator = new RequestTranslator<DummySession, DummyUser>();
        AnswerTranslator<DummySession> answerTranslator = new AnswerTranslator<DummySession>();


        Map<StatusCode, Resource<DummySession, DummyUser>> errorResources = new HashMap<>();
        errorResources.put(StatusCode.SERVER_ERROR, new ServerErrorResource());

        RouteRun<DummySession, DummyUser> subject = new RouteRun<>(
                route, requestTranslator, answerTranslator, errorResources
        );
        return subject;
    }

    @Test
    public void whenGetShouldReturnOk() throws Exception {
        testRun(Method.GET, StatusCode.OK, Optional.empty());
    }

    @Test
    public void whenPostShouldReturnOk() throws Exception {
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        testRun(Method.POST, StatusCode.CREATED, Optional.of(body));
    }

    @Test
    public void whenPutShouldReturnOk() throws Exception {
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        testRun(Method.PUT, StatusCode.OK, Optional.of(body));
    }

    @Test
    public void whenPatchShouldReturnOk() throws Exception {
        DummyPayload dummyPayload = new DummyPayload();
        dummyPayload.setInteger(123);
        byte[] body = appFactory.objectWriter().writeValueAsBytes(dummyPayload);

        testRun(Method.PATCH, StatusCode.OK, Optional.of(body));
    }

    @Test
    public void whenDeletedShouldReturnOk() throws Exception {
        testRun(Method.DELETE, StatusCode.OK, Optional.empty());
    }

    @Test
    public void whenConnectShouldReturnOk() throws Exception {
        testRun(Method.CONNECT, StatusCode.OK, Optional.empty());
    }

    @Test
    public void whenOptionsShouldReturnOk() throws Exception {
        testRun(Method.OPTIONS, StatusCode.OK, Optional.empty());
    }

    @Test
    public void whenTraceShouldReturnOk() throws Exception {
        testRun(Method.TRACE, StatusCode.OK, Optional.empty());
    }

    @Test
    public void whenHeadShouldReturnOk() throws Exception {
        testRun(Method.HEAD, StatusCode.OK, Optional.empty());
    }

    public void testRun(Method method, StatusCode statusCode, Optional<byte[]> body) throws Exception {
        Ask ask = FixtureFactory.makeAsk();
        ask.setMethod(method);
        ask.setBody(body);
        Answer answer = FixtureFactory.makeAnswer();

        Answer actual = subject.run(ask, answer);

        assertThat(actual.getStatusCode(), is(statusCode));
    }

    @Test
    public void whenHaltExceptionShouldReturn403() throws Exception {

        Ask ask = FixtureFactory.makeAsk();
        Answer answer = FixtureFactory.makeAnswer();

        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setMethod(Method.GET);

        // add a before that will throw halt.
        HaltBetween haltBetween = new HaltBetween();
        subject.getRoute().getBefore().add(haltBetween);

        HaltException actual = null;
        try {
            subject.run(ask, answer);
        } catch (HaltException e) {
            actual = e;
        }

        assertThat(actual, is(notNullValue()));
        assertThat(answer.getStatusCode(), is(StatusCode.UNAUTHORIZED));
    }

    @Test
    public void whenRuntimeExceptionThenShouldHandleAnd500() throws Exception {

        RuntimeExceptionResource resource = new RuntimeExceptionResource();
        RouteRun<DummySession, DummyUser> subject = subject(resource);

        Ask ask = FixtureFactory.makeAsk();
        Answer answer = FixtureFactory.makeAnswer();

        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setMethod(Method.GET);

        Answer actual = subject.run(ask, answer);
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getStatusCode(), is(StatusCode.SERVER_ERROR));
    }

    public RouteRun<DummySession, DummyUser> subjectWithMocks(Resource<DummySession, DummyUser> resource) {
        Route<DummySession, DummyUser> route = FixtureFactory.makeRoute();
        route.setResource(resource);

        RequestTranslator<DummySession, DummyUser> mockRequestTranslator = mock(RequestTranslator.class);
        AnswerTranslator<DummySession> mockAnswerTranslator = mock(AnswerTranslator.class);

        Map<StatusCode, Resource<DummySession, DummyUser>> errorResources = new HashMap<>();
        Resource<DummySession, DummyUser> errorResource = mock(ServerErrorResource.class);


        errorResources.put(StatusCode.SERVER_ERROR, errorResource);

        return new RouteRun<>(
                route, mockRequestTranslator, mockAnswerTranslator, errorResources
        );
    }

    @Test
    public void HandleWhenCauseShouldAssignCause() throws Exception {

        RuntimeExceptionResource resource = new RuntimeExceptionResource();
        RouteRun<DummySession, DummyUser> subject = subjectWithMocks(resource);

        Ask ask = FixtureFactory.makeAsk();
        Answer answer = FixtureFactory.makeAnswer();

        Answer errorAnswer = FixtureFactory.makeAnswer();
        errorAnswer.setStatusCode(StatusCode.SERVER_ERROR);

        Request<DummySession, DummyUser> request = FixtureFactory.makeRequest();
        request.setMethod(Method.GET);

        Response<DummySession> response = FixtureFactory.makeResponse();
        response.setStatusCode(StatusCode.SERVER_ERROR);

        when(
            subject.getRequestTranslator().to(ask)
        ).thenReturn(request);

        when(
            subject.getAnswerTranslator().from(answer)
        ).thenReturn(response);

        // error handler should return the response.
        when(
            subject.getErrorResources().get(StatusCode.SERVER_ERROR)
                .get(any(Request.class), any(Response.class))
        ).thenReturn(response);

        when(
            subject.getAnswerTranslator().to(answer, response)
        ).thenReturn(errorAnswer);

        Throwable cause = new RuntimeException();
        Optional<Answer> actual = subject.handle(StatusCode.SERVER_ERROR, cause, ask, answer);

        assertThat(actual, is(notNullValue()));
        assertTrue(actual.isPresent());
        actual.ifPresent(a -> {
            assertThat(a.getStatusCode(), is(StatusCode.SERVER_ERROR));
        });

        // the only thing that really matters in this test..
        assertTrue(request.getCause().isPresent());
        assertThat(request.getCause().get(), is(cause));
    }
}