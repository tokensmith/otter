package org.rootservices.otter.gateway.servlet.translator.request;


import javax.servlet.http.HttpServletRequest;

public class HttpServletRequestUrlTranslator {
    private static String URL_DELIMITER = "://";
    private static String PARAM_DELIMITER = "?";
    private static String PORT_DELIMITER = ":";
    private static String EMPTY = "";

    private static String HTTP = "http";
    private static int HTTP_PORT = 80;

    private static String HTTPS = "https";
    private static int HTTPS_PORT = 443;

    public String from(HttpServletRequest containerRequest) {
        return containerRequest.getScheme() + URL_DELIMITER +
                containerRequest.getServerName() +
                portNumber(containerRequest) +
                containerRequest.getRequestURI() +
                queryStringForUrl(containerRequest.getQueryString());
    }

    protected String portNumber(HttpServletRequest containerRequest) {
        String portNumber;
        if ((HTTP.equals(containerRequest.getScheme()) && HTTP_PORT == containerRequest.getServerPort()) || (HTTPS.equals(containerRequest.getScheme()) && HTTPS_PORT == containerRequest.getServerPort())) {
            portNumber = EMPTY;
        } else {
            portNumber = PORT_DELIMITER + Integer.toString(containerRequest.getServerPort());
        }
        return portNumber;
    }

    protected String queryStringForUrl(String queryString) {
        String queryStringForUrl;
        if (queryString != null) {
            queryStringForUrl = PARAM_DELIMITER + queryString;
        } else {
            queryStringForUrl = EMPTY;
        }
        return queryStringForUrl;
    }
}
