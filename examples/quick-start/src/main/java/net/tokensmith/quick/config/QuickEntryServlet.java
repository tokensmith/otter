package net.tokensmith.quick.config;

import net.tokensmith.otter.gateway.Configure;
import net.tokensmith.otter.servlet.OtterEntryServlet;

import javax.servlet.annotation.WebServlet;

@WebServlet(value="/app/*", name="AppEntryServlet", asyncSupported = true, loadOnStartup = 1)
public class QuickEntryServlet extends OtterEntryServlet {

    @Override
    public Configure makeConfigure() {
        return new QuickConfig();
    }
}
