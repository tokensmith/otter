package org.rootservices.otter.authentication;

import org.rootservices.otter.authentication.exception.HttpBasicException;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * Created by tommackenzie on 6/4/15.
 */
public class ParseHttpBasicImpl implements ParseHttpBasic {

    @Override
    public HttpBasicEntity run(String header) throws HttpBasicException {

        if (header == null || header.isEmpty()) {
            throw new HttpBasicException("header is null or empty");
        }

        String[] encodedCredentials = header.split("Basic ");

        if ( encodedCredentials.length != 2  || encodedCredentials[1].isEmpty()) {
            throw new HttpBasicException("header is not Basic authentication scheme");
        }

        byte[] decodedBasicCredentialsBytes = null;
        decodedBasicCredentialsBytes = Base64.getDecoder().decode(encodedCredentials[1].getBytes());

        String decodedBasicCredentials = null;
        try {
            decodedBasicCredentials = new String(decodedBasicCredentialsBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new HttpBasicException("Could not convert bytes to UTF-8 string");
        }

        String[] parsedCredentials = decodedBasicCredentials.split(":");

        if ( parsedCredentials.length != 2 || parsedCredentials[0].isEmpty() || parsedCredentials[1].isEmpty()) {
            throw new HttpBasicException("Could not parse header");
        }
        HttpBasicEntity entity = new HttpBasicEntity(
                parsedCredentials[0], parsedCredentials[1]
        );
        return entity;
    }
}
