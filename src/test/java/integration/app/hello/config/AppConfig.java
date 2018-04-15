package integration.app.hello.config;


import integration.app.hello.controller.HelloRestResource;
import org.rootservices.otter.config.OtterAppFactory;
import org.rootservices.otter.translator.JsonTranslator;

public class AppConfig {
    public OtterAppFactory otterAppFactory() {
        return new OtterAppFactory();
    }

    public JsonTranslator jsonTranslator() {
        return otterAppFactory().jsonTranslator();
    }

    public HelloRestResource helloRestResource() {
        return new HelloRestResource(jsonTranslator());
    }

}
