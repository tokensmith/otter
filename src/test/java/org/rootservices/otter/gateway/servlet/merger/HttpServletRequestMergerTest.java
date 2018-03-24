package org.rootservices.otter.gateway.servlet.merger;

import helper.FixtureFactory;
import helper.entity.FakePresenter;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.rootservices.otter.controller.entity.Response;
import suite.UnitTest;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


@Category(UnitTest.class)
public class HttpServletRequestMergerTest {
    private HttpServletRequestMerger subject;

    @Before
    public void setUp() {
        subject = new HttpServletRequestMerger();
    }

    @Test
    public void mergePresenterAndTemplateArePresent() throws Exception {
        HttpServletRequest mockContainerRequest = mock(HttpServletRequest.class);
        Response response = FixtureFactory.makeResponse();

        response.setPresenter(Optional.of(new FakePresenter()));
        response.setTemplate(Optional.of("path/to/template.jsp"));

        subject.merge(mockContainerRequest, response);

        verify(mockContainerRequest).setAttribute(subject.getPresenterAttr(), response.getPresenter().get());
    }



    @Test
    public void mergePresenterAndTemplateAreNotPresent() throws Exception {
        HttpServletRequest mockContainerRequest = mock(HttpServletRequest.class);
        Response response = FixtureFactory.makeResponse();

        response.setPresenter(Optional.empty());
        response.setTemplate(Optional.empty());

        subject.merge(mockContainerRequest, response);

        verify(mockContainerRequest, never()).setAttribute(eq(subject.getPresenterAttr()), any(String.class));

    }
}