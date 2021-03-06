package net.tokensmith.otter.authentication;

import net.tokensmith.otter.authentication.exception.BearerException;

import java.util.Objects;

/**
 * Created by tommackenzie on 12/13/16.
 */
public class ParseBearer {
    private static String HEADER_EMPTY = "header is null or empty";
    private static String BEARER = "Bearer ";
    private static String NOT_BEARER = "header is not Bearer authentication scheme";

    public String parse(String header) throws BearerException {
        if (Objects.isNull(header) || header.isEmpty()) {
            throw new BearerException(HEADER_EMPTY);
        }

        String[] credentials = header.split(BEARER);

        if ( credentials.length != 2  || credentials[1].isEmpty()) {
            throw new BearerException(NOT_BEARER);
        }

        return credentials[1];
    }
}
