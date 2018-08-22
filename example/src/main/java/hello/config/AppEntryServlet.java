package hello.config;


import hello.security.TokenSession;
import org.rootservices.otter.gateway.Configure;
import org.rootservices.otter.servlet.OtterEntryServlet;

import javax.servlet.annotation.WebServlet;


@WebServlet(value="/app/*", name="AppEntryServlet", asyncSupported = true)
public class AppEntryServlet extends OtterEntryServlet<TokenSession> {

    @Override
    public Configure<TokenSession> makeConfigure() {
        return new AppConfig(new AppFactory());
    }
}
