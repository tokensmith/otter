package org.rootservices.otter;


import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class ParseHeaders {

    public void parseAllHeaders(HttpServletRequest request) {
        Map<String, Set<String>> headers = new HashMap<String, Set<String>>();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            List<String> values = new ArrayList<>();

            String key = headerNames.nextElement();
            String value = request.getHeader(key);

            headers.put(key, parseHeaderValues(value));
        }
    }

    public Set<String> parseHeaderValues(String headerValue) {
        Set<String> headerValues = new HashSet<>();
        if (headerValue != null) {
            String[] parsedHeaderValues = headerValue.split(";");
            for (String parsedHeaderValue : parsedHeaderValues) {
                headerValues.add(parsedHeaderValue);
            }
        }
        return headerValues;
    }
}
