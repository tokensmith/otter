package org.rootservices.otter;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by tommackenzie on 4/22/15.
 */
public interface QueryStringToMap {
    Map<String, List<String>> run(Optional<String> queryString) throws UnsupportedEncodingException;
}
