package org.rootservices.otter.controller;

import org.rootservices.otter.QueryStringToMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;


public class OtterController extends HttpServlet {
    private QueryStringToMap queryStringToMap;

    @Override
    public void init() throws ServletException {
        this.queryStringToMap = new QueryStringToMap();
    }

}
