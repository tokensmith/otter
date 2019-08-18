package org.rootservices.quick.config;

import org.rootservices.otter.gateway.Configure;
import org.rootservices.otter.servlet.OtterEntryServlet;

import javax.servlet.annotation.WebServlet;

@WebServlet(value="/app/*", name="AppEntryServlet", asyncSupported = true, loadOnStartup = 1)
public class QuickEntryServlet extends OtterEntryServlet {

    @Override
    public Configure makeConfigure() {
        return new QuickConfig();
    }
}
