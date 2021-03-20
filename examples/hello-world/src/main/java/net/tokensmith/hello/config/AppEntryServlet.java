package net.tokensmith.hello.config;


import jakarta.servlet.annotation.WebServlet;
import net.tokensmith.otter.gateway.Configure;
import net.tokensmith.otter.servlet.OtterEntryServlet;


@WebServlet(value="/app/*", name="AppEntryServlet", asyncSupported = true, loadOnStartup = 1)
public class AppEntryServlet extends OtterEntryServlet {

    @Override
    public Configure makeConfigure() {
        return new AppConfig(new AppFactory());
    }
}
