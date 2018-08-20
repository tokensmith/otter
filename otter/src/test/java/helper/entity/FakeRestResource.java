package helper.entity;


import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.translator.JsonTranslator;

public class FakeRestResource extends RestResource<DummyPayload, DummySession> {
    public FakeRestResource(JsonTranslator<DummyPayload> translator) {
        super(translator);
    }
}
