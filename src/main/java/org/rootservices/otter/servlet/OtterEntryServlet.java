package org.rootservices.otter.servlet;


import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.otter.config.AppFactory;
import org.rootservices.otter.gateway.servlet.ServletGateway;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class OtterEntryServlet extends HttpServlet {
    protected AppFactory appFactory;
    protected ServletGateway servletGateway;

    @Override
    public void init() throws ServletException {
        appFactory = new AppFactory();
        servletGateway = appFactory.servletGateway();

    }


    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        servletGateway.processRequest(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        servletGateway.processRequest(req, resp);
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        servletGateway.processRequest(req, resp);
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        servletGateway.processRequest(req, resp);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        servletGateway.processRequest(req, resp);
    }

}

