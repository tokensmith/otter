package org.rootservices.otter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class QueryStringToMap {
    private static final String DELIMITER = "&";
    private static final String ASSIGNMENT = "=";

    public Map<String, List<String>> run(Optional<String> queryString) throws UnsupportedEncodingException {
        Map<String, List<String>> parameters = new HashMap<String, List<String>>();
        if ( queryString.isPresent() && !queryString.get().isEmpty()) {
            String decoded = URLDecoder.decode(queryString.get(), StandardCharsets.UTF_8.name());
            String[] parts = decoded.split(DELIMITER);

            for (String part : parts) {
                String[] nameAndValue = part.split(ASSIGNMENT);
                List<String> items;
                if (parameters.containsKey(nameAndValue[0])) {
                    items = parameters.get(nameAndValue[0]);
                } else {
                    items = new ArrayList<>();

                }
                if (nameAndValue.length == 2) {
                    items.add(nameAndValue[1]);
                }
                parameters.put(nameAndValue[0], items);
            }
        }
        return parameters;
    }
}
