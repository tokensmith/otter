package helper.entity;


import org.rootservices.hello.security.TokenSession;
import org.rootservices.hello.security.User;
import org.rootservices.otter.controller.LegacyRestResource;
import org.rootservices.otter.translator.JsonTranslator;

public class FakeLegacyRestResource extends LegacyRestResource<DummyPayload, TokenSession, User> {
    public FakeLegacyRestResource(JsonTranslator<DummyPayload> translator) {
        super(translator);
    }
}
