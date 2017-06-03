package org.rootservices.otter.gateway.servlet.translator.request;


import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class HttpServletRequestHeaderTranslator {

    public Map<String, String> from(HttpServletRequest containerRequest) {
        Map<String, String> headers = new HashMap<>();
        Enumeration headerNames = containerRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = containerRequest.getHeader(key);
            headers.put(key, value);
        }
        return headers;
    }
}
