package helper.entity;


import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.translator.JsonTranslator;

public class FakeResource extends RestResource<DummyPayload, DummySession, DummyUser> {
    public FakeResource() {

    }
    public FakeResource(JsonTranslator<DummyPayload> translator) {
        super(translator);
    }
}
