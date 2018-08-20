package helper.entity;


import hello.security.TokenSession;
import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.translator.JsonTranslator;

public class FakeRestResource extends RestResource<DummyPayload, TokenSession> {
    public FakeRestResource(JsonTranslator<DummyPayload> translator) {
        super(translator);
    }
}
