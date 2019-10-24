package net.tokensmith.otter.security.csrf;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.tokensmith.otter.security.RandomString;
import net.tokensmith.otter.security.csrf.exception.CsrfException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class SynchronizerTokenTest {

    @Mock
    private RandomString mockRandomString;
    private SynchronizerToken subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new SynchronizerToken(mockRandomString);
    }


    @Test
    public void checkTokensShouldBeOk() throws Exception{
        String token = "token";

        HttpSession mockHttpSession = mock(HttpSession.class);
        when(mockHttpSession.getAttribute(SynchronizerToken.CHALLENGE_TOKEN_SESSION_NAME))
                .thenReturn(token);

        HttpServletRequest mockServletRequest = mock(HttpServletRequest.class);
        when(mockServletRequest.getParameter(SynchronizerToken.CHALLENGE_TOKEN_FORM_NAME))
                .thenReturn(token);

        when(mockServletRequest.getMethod()).thenReturn("POST");

        when(mockServletRequest.getSession()).thenReturn(mockHttpSession);

        subject.checkTokens(mockServletRequest);
    }

    @Test(expected = CsrfException.class)
    public void checkTokensShouldThrowCsrfException() throws CsrfException{

        HttpSession mockHttpSession = mock(HttpSession.class);
        when(mockHttpSession.getAttribute(SynchronizerToken.CHALLENGE_TOKEN_SESSION_NAME))
                .thenReturn("token1");

        HttpServletRequest mockServletRequest = mock(HttpServletRequest.class);
        when(mockServletRequest.getParameter(SynchronizerToken.CHALLENGE_TOKEN_FORM_NAME))
                .thenReturn("token2");

        when(mockServletRequest.getMethod()).thenReturn("POST");

        when(mockServletRequest.getSession()).thenReturn(mockHttpSession);

        subject.checkTokens(mockServletRequest);
    }

    @Test
    public void checkTokensShouldInsertTokenToSession() throws CsrfException {
        HttpSession mockHttpSession = mock(HttpSession.class);

        HttpServletRequest mockServletRequest = mock(HttpServletRequest.class);
        when(mockServletRequest.getMethod()).thenReturn("GET");
        when(mockServletRequest.getSession()).thenReturn(mockHttpSession);


        String randomString = "randomString";
        when(mockRandomString.run()).thenReturn(randomString);

        subject.checkTokens(mockServletRequest);

        verify(mockHttpSession, times(1)).setAttribute(SynchronizerToken.CHALLENGE_TOKEN_SESSION_NAME, "cmFuZG9tU3RyaW5n");
    }

    @Test
    public void requestMethodRequiresChallengeTokenWhenPostShouldBeTrue() {
        Boolean actual = subject.requestMethodRequiresChallengeToken("POST");

        assertThat(actual, is(true));
    }

    @Test
    public void requestMethodRequiresChallengeTokenWhenPutShouldBeTrue() {
        Boolean actual = subject.requestMethodRequiresChallengeToken("PUT");

        assertThat(actual, is(true));
    }

    @Test
    public void requestMethodRequiresChallengeTokenWhenDeleteShouldBeTrue() {
        Boolean actual = subject.requestMethodRequiresChallengeToken("DELETE");

        assertThat(actual, is(true));
    }

    @Test
    public void requestMethodRequiresChallengeTokenWhenGetShouldBeFalse() {
        Boolean actual = subject.requestMethodRequiresChallengeToken("GET");

        assertThat(actual, is(false));
    }

    @Test
    public void challengeTokensMatchShouldBeTrue() throws Exception {
        Optional<String> sessionChallengeToken = Optional.of("token");
        Optional<String> formChallengeToken = Optional.of("token");

        Boolean actual = subject.doTokensMatch(sessionChallengeToken, formChallengeToken);
        assertThat(actual, is(true));
    }

    @Test
    public void challengeTokensMatchWhenDifferentShouldBeFalse() throws Exception {
        Optional<String> sessionChallengeToken = Optional.of("token1");
        Optional<String> formChallengeToken = Optional.of("token2");

        Boolean actual = subject.doTokensMatch(sessionChallengeToken, formChallengeToken);
        assertThat(actual, is(false));
    }

    @Test
    public void challengeTokensMatchWhenSessionIsNotPresentShouldBeFalse() throws Exception {
        Optional<String> sessionChallengeToken = Optional.empty();
        Optional<String> formChallengeToken = Optional.of("token");

        Boolean actual = subject.doTokensMatch(sessionChallengeToken, formChallengeToken);
        assertThat(actual, is(false));
    }

    @Test
    public void challengeTokensMatchWhenFormIsNotPresentShouldBeFalse() throws Exception {
        Optional<String> sessionChallengeToken = Optional.of("token");
        Optional<String> formChallengeToken = Optional.empty();

        Boolean actual = subject.doTokensMatch(sessionChallengeToken, formChallengeToken);
        assertThat(actual, is(false));
    }

    @Test
    public void getChallengeTokenFromSessionShouldReturnToken() {

        HttpSession mockHttpSession = mock(HttpSession.class);
        when(mockHttpSession.getAttribute(SynchronizerToken.CHALLENGE_TOKEN_SESSION_NAME))
                .thenReturn("token");

        HttpServletRequest mockServletRequest = mock(HttpServletRequest.class);
        when(mockServletRequest.getSession()).thenReturn(mockHttpSession);

        Optional<String> actual = subject.getChallengeTokenFromSession(mockServletRequest);

        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is("token"));
    }

    @Test
    public void getChallengeTokenFromSessionShouldReturnEmpty() {

        HttpSession mockHttpSession = mock(HttpSession.class);
        when(mockHttpSession.getAttribute(SynchronizerToken.CHALLENGE_TOKEN_SESSION_NAME))
                .thenReturn(null);

        HttpServletRequest mockServletRequest = mock(HttpServletRequest.class);
        when(mockServletRequest.getSession()).thenReturn(mockHttpSession);

        Optional<String> actual = subject.getChallengeTokenFromSession(mockServletRequest);

        assertThat(actual.isPresent(), is(false));
    }

    @Test
    public void insertChallengeTokenIntoSessionShouldInsert() throws Exception{

        String randomString = "randomString";
        String encodedChallengeToken = "cmFuZG9tU3RyaW5n";
        HttpSession mockHttpSession = mock(HttpSession.class);
        HttpServletRequest mockServletRequest = mock(HttpServletRequest.class);
        when(mockServletRequest.getSession()).thenReturn(mockHttpSession);

        when(mockRandomString.run()).thenReturn(randomString);
        subject.insertChallengeTokenIntoSession(mockServletRequest);

        verify(mockHttpSession, times(1)).setAttribute(SynchronizerToken.CHALLENGE_TOKEN_SESSION_NAME, encodedChallengeToken);

    }

    @Test
    public void getChallengeTokenFromFormShouldReturnToken() {
        HttpServletRequest mockServletRequest = mock(HttpServletRequest.class);
        when(mockServletRequest.getParameter(SynchronizerToken.CHALLENGE_TOKEN_FORM_NAME))
                .thenReturn("token");

        Optional<String> actual = subject.getChallengeTokenFromForm(mockServletRequest);
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is("token"));
    }

    @Test
    public void getChallengeTokenFromFormShouldReturnEmpty() {
        HttpServletRequest mockServletRequest = mock(HttpServletRequest.class);
        when(mockServletRequest.getParameter(SynchronizerToken.CHALLENGE_TOKEN_FORM_NAME))
                .thenReturn(null);

        Optional<String> actual = subject.getChallengeTokenFromForm(mockServletRequest);
        assertThat(actual.isPresent(), is(false));
    }
}