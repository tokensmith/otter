package org.rootservices.hello.config;


import org.rootservices.otter.gateway.Configure;
import org.rootservices.otter.servlet.OtterEntryServlet;

import javax.servlet.annotation.WebServlet;


@WebServlet(value="/app/*", name="AppEntryServlet", asyncSupported = true, loadOnStartup = 1)
public class AppEntryServlet extends OtterEntryServlet {

    @Override
    public Configure makeConfigure() {
        return new AppConfig(new AppFactory());
    }
}
