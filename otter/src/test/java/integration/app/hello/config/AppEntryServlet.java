package integration.app.hello.config;


import org.rootservices.otter.gateway.Configure;
import org.rootservices.otter.servlet.OtterEntryServlet;
import javax.servlet.annotation.WebServlet;


@WebServlet(value="/app/*", name="EntryServlet", asyncSupported = true)
public class AppEntryServlet extends OtterEntryServlet {

    @Override
    public Configure makeConfigure() {
        return new AppConfig(new AppFactory());
    }
}
