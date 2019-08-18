package org.rootservices.otter.translator;

import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.controller.entity.mime.ParamKey;
import org.rootservices.otter.controller.entity.mime.ParamValue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * https://tools.ietf.org/html/rfc7231#section-3.1.1.1
 * https://tools.ietf.org/html/rfc2046
 */
public class MimeTypeTranslator {
    public static final String TYPE = "TYPE";
    public static final String SUBTYPE = "SUBTYPE";
    public static final String PARAMETERS = "PARAMETERS";
    private static Pattern mimeTypeRegex = Pattern.compile("(?<" + TYPE + ">(\\w+))/(?<" + SUBTYPE + ">([a-zA-Z_0-9\\-:]+));?(?<" + PARAMETERS + ">(.*))");
    private static Pattern parameterRegex = Pattern.compile("\\G\\s?(\\w+)=\"?([a-zA-Z_0-9\\-:]+)\"?;?");

    public MimeType to(String from) {
        MimeType to = new MimeType();
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        to.setParameters(parameters);

        if (from == null) {
            return to;
        }

        Matcher matcher = mimeTypeRegex.matcher(from);
        if (matcher.matches()) {
            to.setType(matcher.group(TYPE));
            to.setSubType(matcher.group(SUBTYPE));

            String fromParamters = matcher.group(PARAMETERS);
            to.setParameters(toParameters(fromParamters));

        }
        return to;
    }

    protected Map<String, String> toParameters(String from) {
        Matcher parameterMatcher = parameterRegex.matcher(from);
        Map<String, String> parameters = new LinkedHashMap<String, String>();

        while (parameterMatcher.find()) {
            String key = parameterMatcher.group(1);
            String value = parameterMatcher.group(2);
            if(ParamKey.CHARSET.toString().equalsIgnoreCase(key)) {
                value = value.toLowerCase();
            }
            parameters.put(key, value);
        }

        // default to US-ASCII charset.
        if (parameters.get(ParamKey.CHARSET.toString()) == null) {
            parameters.put(ParamKey.CHARSET.toString(), ParamValue.US_ASCII.toString());
        }

        return parameters;
    }
}
