package org.rootservices.otter.router;

import javax.servlet.annotation.WebServlet;

/**
 * Created by tommackenzie on 5/2/15.
 */
public class GetServletURI {

    public String run(String baseURI, Class clazz) {
        WebServlet webServlet = (WebServlet) clazz.getAnnotation(WebServlet.class);

        // prevent duplicate "/"
        if (baseURI.endsWith("/") && webServlet.value()[0].startsWith("/")) {
            baseURI = baseURI.substring(0, baseURI.length()-1);
        }
        return baseURI + webServlet.value()[0];

    }
}
