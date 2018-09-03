package helper.entity;


import hello.security.TokenSession;
import hello.security.User;
import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.translator.JsonTranslator;

public class FakeRestResource extends RestResource<DummyPayload, TokenSession, User> {
    public FakeRestResource(JsonTranslator<DummyPayload> translator) {
        super(translator);
    }
}
