package org.rootservices.otter.authentication;

import org.rootservices.otter.authentication.exception.HttpBasicException;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Created by tommackenzie on 6/4/15.
 */
public class ParseHttpBasic {

    private static String HEADER_EMPTY = "header is null or empty";
    private static String NOT_BASIC = "header is not Basic authentication scheme";
    private static String PARSE_ERROR = "Could not parse header";
    private static String BASIC = "Basic ";
    private static String DELIMITTER = ":";

    public HttpBasicEntity run(String header) throws HttpBasicException {

        if (header == null || header.isEmpty()) {
            throw new HttpBasicException(HEADER_EMPTY);
        }

        String[] encodedCredentials = header.split(BASIC);

        if ( encodedCredentials.length != 2  || encodedCredentials[1].isEmpty()) {
            throw new HttpBasicException(NOT_BASIC);
        }

        byte[] decodedBasicCredentialsBytes;
        decodedBasicCredentialsBytes = Base64.getDecoder().decode(encodedCredentials[1].getBytes());

        String decodedBasicCredentials = new String(decodedBasicCredentialsBytes, StandardCharsets.UTF_8);

        String[] parsedCredentials = decodedBasicCredentials.split(DELIMITTER);

        if ( parsedCredentials.length != 2 || parsedCredentials[0].isEmpty() || parsedCredentials[1].isEmpty()) {
            throw new HttpBasicException(PARSE_ERROR);
        }
        HttpBasicEntity entity = new HttpBasicEntity(
                parsedCredentials[0], parsedCredentials[1]
        );
        return entity;
    }
}
