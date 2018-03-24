package suite;



import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.rootservices.otter.QueryStringToMapTest;
import org.rootservices.otter.authentication.ParseBearer;
import org.rootservices.otter.authentication.ParseHttpBasicTest;
import org.rootservices.otter.controller.ResourceTest;
import org.rootservices.otter.controller.RestResourceTest;
import org.rootservices.otter.controller.builder.RequestBuilderTest;
import org.rootservices.otter.controller.builder.ResponseBuilderTest;
import org.rootservices.otter.gateway.servlet.ServletGatewayTest;
import org.rootservices.otter.gateway.servlet.merger.HttpServletRequestMergerTest;
import org.rootservices.otter.gateway.servlet.merger.HttpServletResponseMergerTest;
import org.rootservices.otter.gateway.servlet.translator.HttpServletRequestCookieTranslatorTest;
import org.rootservices.otter.gateway.servlet.translator.HttpServletRequestHeaderTranslatorTest;
import org.rootservices.otter.gateway.servlet.translator.HttpServletRequestTranslatorTest;
import org.rootservices.otter.router.DispatcherTest;
import org.rootservices.otter.router.EngineTest;
import org.rootservices.otter.router.GetServletURITest;
import org.rootservices.otter.router.RouteBuilderTest;
import org.rootservices.otter.security.csrf.DoubleSubmitCSRFTest;
import org.rootservices.otter.security.csrf.SynchronizerTokenTest;
import org.rootservices.otter.security.csrf.between.CheckCSRFTest;
import org.rootservices.otter.security.csrf.between.PrepareCSRF;
import org.rootservices.otter.security.csrf.between.PrepareCSRFTest;
import org.rootservices.otter.security.csrf.filter.CsrfPreventionFilter;
import org.rootservices.otter.security.csrf.filter.CsrfPreventionFilterTest;
import org.rootservices.otter.server.container.ServletContainer;
import org.rootservices.otter.server.path.CompiledClassPathTest;
import org.rootservices.otter.server.path.WebAppPathTest;
import org.rootservices.otter.translator.JsonTranslatorTest;

@RunWith(Categories.class)
@Categories.IncludeCategory(UnitTest.class)
@Categories.ExcludeCategory(ServletContainer.class)
@Suite.SuiteClasses({
        ParseHttpBasicTest.class,
        ParseBearer.class,
        RequestBuilderTest.class,
        ResponseBuilderTest.class,
        ResourceTest.class,
        RestResourceTest.class,
        HttpServletRequestMergerTest.class,
        HttpServletResponseMergerTest.class,
        HttpServletRequestCookieTranslatorTest.class,
        HttpServletRequestHeaderTranslatorTest.class,
        HttpServletRequestTranslatorTest.class,
        ServletGatewayTest.class,
        DispatcherTest.class,
        EngineTest.class,
        GetServletURITest.class,
        RouteBuilderTest.class,
        CheckCSRFTest.class,
        PrepareCSRFTest.class,
        CsrfPreventionFilterTest.class,
        DoubleSubmitCSRFTest.class,
        SynchronizerTokenTest.class,
        CompiledClassPathTest.class,
        WebAppPathTest.class,
        JsonTranslatorTest.class,
        QueryStringToMapTest.class
})
public class UnitTestSuite {
}
