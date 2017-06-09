package helper.entity;


import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.translator.JsonTranslator;

import java.util.Map;

public class FakeRestResourceMap extends RestResource<Map<String,String>> {

    public FakeRestResourceMap(JsonTranslator translator) {
        super(translator);
    }
}
