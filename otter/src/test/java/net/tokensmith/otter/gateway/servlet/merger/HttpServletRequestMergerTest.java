package net.tokensmith.otter.gateway.servlet.merger;

import helper.FixtureFactory;
import helper.fake.FakePresenter;
import net.tokensmith.otter.router.entity.io.Answer;
import org.junit.Before;
import org.junit.Test;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


public class HttpServletRequestMergerTest {
    private HttpServletRequestMerger subject;

    @Before
    public void setUp() {
        subject = new HttpServletRequestMerger();
    }

    @Test
    public void mergePresenterAndTemplateArePresent() throws Exception {
        HttpServletRequest mockContainerRequest = mock(HttpServletRequest.class);
        Answer answer = FixtureFactory.makeAnswer();

        answer.setPresenter(Optional.of(new FakePresenter()));
        answer.setTemplate(Optional.of("path/to/template.jsp"));

        subject.merge(mockContainerRequest, answer);

        verify(mockContainerRequest).setAttribute(subject.getPresenterAttr(), answer.getPresenter().get());
    }



    @Test
    public void mergePresenterAndTemplateAreNotPresent() throws Exception {
        HttpServletRequest mockContainerRequest = mock(HttpServletRequest.class);
        Answer answer = FixtureFactory.makeAnswer();

        answer.setPresenter(Optional.empty());
        answer.setTemplate(Optional.empty());

        subject.merge(mockContainerRequest, answer);

        verify(mockContainerRequest, never()).setAttribute(eq(subject.getPresenterAttr()), any(String.class));

    }
}