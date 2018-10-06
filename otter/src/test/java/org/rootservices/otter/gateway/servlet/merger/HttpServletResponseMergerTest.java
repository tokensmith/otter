package org.rootservices.otter.gateway.servlet.merger;

import helper.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.otter.gateway.servlet.translator.HttpServletRequestCookieTranslator;
import org.rootservices.otter.router.entity.io.Answer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.util.Optional;
import java.util.function.Function;

import static org.mockito.Mockito.*;


public class HttpServletResponseMergerTest {
    private HttpServletResponseMerger subject;
    @Mock
    private HttpServletRequestCookieTranslator mockHttpServletRequestCookieTranslator;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new HttpServletResponseMerger(mockHttpServletRequestCookieTranslator);
    }
    
    @Test
    public void mergeHasHeaders() throws Exception {

        HttpServletResponse mockContainerResponse = mock(HttpServletResponse.class);
        Cookie[] containerCookies = new Cookie[0];
        Answer answer = FixtureFactory.makeAnswer();

        answer.getHeaders().put("some-header", "some-value");

        subject.merge(mockContainerResponse, containerCookies, answer);

        verify(mockContainerResponse).setHeader("some-header", "some-value");

    }

    @Test
    public void mergeDeleteCookies() throws Exception {

        HttpServletResponse mockContainerResponse = mock(HttpServletResponse.class);
        Cookie[] containerCookies = new Cookie[1];
        Cookie mockContainerCookie = mock(Cookie.class);
        when(mockContainerCookie.getName()).thenReturn("container-cookie");
        containerCookies[0] = mockContainerCookie;

        Answer answer = FixtureFactory.makeAnswer();

        subject.merge(mockContainerResponse, containerCookies, answer);

        // indicates deletion.
        verify(mockContainerCookie).setMaxAge(0);
        verify(mockContainerResponse).addCookie(mockContainerCookie);

    }

    @Test
    @SuppressWarnings("unchecked")
    public void mergeUpdateCookies() throws Exception {
        String cookieName = "cookie-to-update";

        HttpServletResponse mockContainerResponse = mock(HttpServletResponse.class);
        Cookie[] containerCookies = new Cookie[1];
        Cookie mockContainerCookieToUpdate = mock(Cookie.class);
        when(mockContainerCookieToUpdate.getName()).thenReturn(cookieName);
        containerCookies[0] = mockContainerCookieToUpdate;

        Answer answer = FixtureFactory.makeAnswer();
        answer.getCookies().put(cookieName, FixtureFactory.makeCookie(cookieName));

        Function mockTo = mock(Function.class);
        mockHttpServletRequestCookieTranslator.to = mockTo;
        when(mockTo.apply(answer.getCookies().get(cookieName)))
                .thenReturn(mockContainerCookieToUpdate);

        subject.merge(mockContainerResponse, containerCookies, answer);

        // indicates cookie was updated.
        verify(mockHttpServletRequestCookieTranslator.to).apply(answer.getCookies().get(cookieName));
        verify(mockContainerResponse).addCookie(mockContainerCookieToUpdate);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mergeCreateCookies() throws Exception {
        String cookieName = "cookie-to-create";

        HttpServletResponse mockContainerResponse = mock(HttpServletResponse.class);
        Cookie[] containerCookies = new Cookie[0];

        Answer answer = FixtureFactory.makeAnswer();
        answer.getCookies().put(cookieName, FixtureFactory.makeCookie(cookieName));

        Cookie mockContainerCookieToCreate = mock(Cookie.class);
        Function mockTo = mock(Function.class);
        mockHttpServletRequestCookieTranslator.to = mockTo;
        when(mockTo.apply(answer.getCookies().get(cookieName)))
                .thenReturn(mockContainerCookieToCreate);

        subject.merge(mockContainerResponse, containerCookies, answer);

        // indicates cookie was added
        verify(mockHttpServletRequestCookieTranslator.to).apply(answer.getCookies().get(cookieName));
        verify(mockContainerResponse).addCookie(mockContainerCookieToCreate);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void mergeWhenPayloadShouldNotWritePayload() throws Exception {
        HttpServletResponse mockContainerResponse = mock(HttpServletResponse.class);
        Cookie[] containerCookies = new Cookie[0];
        Answer answer = FixtureFactory.makeAnswer();

        Optional<ByteArrayOutputStream> payload = Optional.of(new ByteArrayOutputStream());
        answer.setPayload(payload);

        ServletOutputStream mockServletOutputStream = mock(ServletOutputStream.class);
        when(mockContainerResponse.getOutputStream()).thenReturn(mockServletOutputStream);

        subject.merge(mockContainerResponse, containerCookies, answer);

        // indicates json was not set in response.
        verify(mockServletOutputStream, never()).write(payload.get().toByteArray());
    }
}