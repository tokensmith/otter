package org.rootservices.otter.gateway.servlet.merger;

import helper.FixtureFactory;
import helper.fake.FakePresenter;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.router.entity.io.Answer;

import javax.servlet.http.HttpServletRequest;


import java.util.Optional;

import static org.mockito.Mockito.*;


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