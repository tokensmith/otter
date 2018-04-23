package org.rootservices.otter;

import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


public class QueryStringToMapTest {

    private QueryStringToMap subject;

    @Before
    public void setUp() {
        subject = new QueryStringToMap();
    }

    @Test
    public void testParamsToMap() throws Exception {
        String decodedQueryString = "param1=value1&param2=value2";
        String encodedQueryString = URLEncoder.encode(decodedQueryString,"UTF-8");
        Optional<String> optionalQueryString = Optional.of(encodedQueryString);

        Map<String, List<String>> params = subject.run(optionalQueryString);
        assertThat(params.get("param1").size(), is(1));
        assertThat(params.get("param1").get(0), is("value1"));
        assertThat(params.get("param2").size(), is(1));
        assertThat(params.get("param2").get(0), is("value2"));
    }

    @Test
    public void noQueryParameters() throws UnsupportedEncodingException {
        Optional<String> optionalQueryString = Optional.of("");
        Map<String, List<String>> params = subject.run(optionalQueryString);

        assertThat(params.size(), is(0));
    }

    @Test
    public void paramValueIsEmptyShouldBeEmptyList() throws UnsupportedEncodingException {
        String decodedQueryString = "param1=";
        String encodedQueryString = URLEncoder.encode(decodedQueryString, "UTF-8");
        Optional<String> optionalQueryString = Optional.of(encodedQueryString);

        Map<String, List<String>> params = subject.run(optionalQueryString);
        assertThat(params.get("param1").size(), is(0));
    }
}