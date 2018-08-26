package hello.config;


import hello.security.TokenSession;
import hello.security.User;
import org.rootservices.otter.gateway.Configure;
import org.rootservices.otter.servlet.OtterEntryServlet;

import javax.servlet.annotation.WebServlet;


@WebServlet(value="/app/*", name="AppEntryServlet", asyncSupported = true)
public class AppEntryServlet extends OtterEntryServlet<TokenSession, User> {

    @Override
    public Configure<TokenSession, User> makeConfigure() {
        return new AppConfig(new AppFactory<TokenSession, User>());
    }
}
