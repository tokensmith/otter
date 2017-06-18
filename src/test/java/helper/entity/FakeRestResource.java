package helper.entity;


import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.translator.JsonTranslator;

public class FakeRestResource extends RestResource<Dummy> {
    public FakeRestResource(JsonTranslator translator) {
        super(translator);
    }
}
