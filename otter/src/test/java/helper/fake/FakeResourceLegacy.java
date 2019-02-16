package helper.fake;


import helper.entity.DummyPayload;
import helper.entity.DummySession;
import helper.entity.DummyUser;
import org.rootservices.otter.controller.LegacyRestResource;
import org.rootservices.otter.translator.JsonTranslator;

public class FakeResourceLegacy extends LegacyRestResource<DummyPayload, DummySession, DummyUser> {
    public FakeResourceLegacy() {

    }
    public FakeResourceLegacy(JsonTranslator<DummyPayload> translator) {
        super(translator);
    }
}
