package helper.entity;


import hello.security.TokenSession;
import hello.security.User;
import org.rootservices.otter.controller.LegacyRestResource;
import org.rootservices.otter.translator.JsonTranslator;

public class FakeLegacyRestResource extends LegacyRestResource<DummyPayload, TokenSession, User> {
    public FakeLegacyRestResource(JsonTranslator<DummyPayload> translator) {
        super(translator);
    }
}
