package org.rootservices.otter.authentication;

import org.rootservices.otter.authentication.exception.HttpBasicException;

/**
 * Created by tommackenzie on 6/4/15.
 */
public interface ParseHttpBasic {
    HttpBasicEntity run(String header) throws HttpBasicException;
}
