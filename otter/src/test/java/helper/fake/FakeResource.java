package helper.fake;



import helper.entity.DummySession;
import helper.entity.DummyUser;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.EmptyPayload;


public class FakeResource extends Resource<DummySession, DummyUser, EmptyPayload> {
    public FakeResource() {
    }
}
