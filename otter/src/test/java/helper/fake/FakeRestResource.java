package helper.fake;


import helper.entity.DummyPayload;
import helper.entity.DummySession;
import helper.entity.DummyUser;
import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.translator.JsonTranslator;

public class FakeRestResource extends RestResource<DummySession, DummyUser, DummyPayload> {
    public FakeRestResource() {
    }

    public FakeRestResource(JsonTranslator<DummyPayload> translator) {
        super(translator);
    }
}
