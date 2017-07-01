package integration.app.hello.config;


import integration.app.hello.controller.HelloRestResource;
import org.rootservices.otter.config.AppFactory;
import org.rootservices.otter.translator.JsonTranslator;

public class AppConfig {
    public AppFactory appFactory() {
        return new AppFactory();
    }

    public JsonTranslator jsonTranslator() {
        return appFactory().jsonTranslator();
    }

    public HelloRestResource helloRestResource() {
        return new HelloRestResource(jsonTranslator());
    }

}
