package net.tokensmith.otter.gateway.servlet.merger;

import helper.FixtureFactory;
import net.tokensmith.otter.gateway.servlet.translator.HttpServletRequestCookieTranslator;
import net.tokensmith.otter.router.entity.io.Answer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


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
        Cookie containerCookie = new Cookie(cookieName, "old value");
        containerCookies[0] = containerCookie;

        Answer answer = FixtureFactory.makeAnswer();
        answer.getCookies().put(cookieName, FixtureFactory.makeCookie(cookieName));

        Cookie cookieToUpdate = new Cookie(cookieName, "new value");
        when(mockHttpServletRequestCookieTranslator.to(answer.getCookies().get(cookieName)))
                .thenReturn(cookieToUpdate);

        subject.merge(mockContainerResponse, containerCookies, answer);

        // indicates cookie was updated.

        verify(mockHttpServletRequestCookieTranslator).to(eq(answer.getCookies().get(cookieName)));
        verify(mockContainerResponse).addCookie(cookieToUpdate);
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
        when(mockHttpServletRequestCookieTranslator.to(answer.getCookies().get(cookieName)))
                .thenReturn(mockContainerCookieToCreate);

        subject.merge(mockContainerResponse, containerCookies, answer);

        // indicates cookie was added
        verify(mockHttpServletRequestCookieTranslator).to(eq(answer.getCookies().get(cookieName)));
        verify(mockContainerResponse).addCookie(mockContainerCookieToCreate);
    }
}