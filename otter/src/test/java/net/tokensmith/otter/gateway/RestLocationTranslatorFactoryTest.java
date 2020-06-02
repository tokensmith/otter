package net.tokensmith.otter.gateway;

import helper.FixtureFactory;
import helper.entity.model.DummyPayload;
import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import net.tokensmith.otter.config.OtterAppFactory;
import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.dispatch.json.validator.Validate;
import net.tokensmith.otter.gateway.config.RestTranslatorConfig;
import net.tokensmith.otter.gateway.entity.ErrorTarget;
import net.tokensmith.otter.gateway.entity.Label;
import net.tokensmith.otter.gateway.entity.Shape;
import net.tokensmith.otter.gateway.entity.rest.RestError;
import net.tokensmith.otter.gateway.entity.rest.RestErrorTarget;
import net.tokensmith.otter.gateway.translator.LocationTranslator;
import net.tokensmith.otter.gateway.translator.RestLocationTranslator;
import net.tokensmith.otter.router.entity.between.Between;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.translatable.Translatable;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class RestLocationTranslatorFactoryTest {
    private OtterAppFactory otterAppFactory;
    private RestLocationTranslatorFactory subject;

    @Before
    public void setUp() throws Exception {
        otterAppFactory = new OtterAppFactory();
        Shape shape = FixtureFactory.makeShape("test-enc-key", "test-sign-key");
        subject = new RestLocationTranslatorFactory(shape);
    }

    @Test
    public void makeShouldNotThrowNPE() {

        Validate validate = otterAppFactory.restValidate();

        RestTranslatorConfig<DummySession, DummyUser> config = new RestTranslatorConfig.Builder<DummySession, DummyUser>()
                .sessionClazz(DummySession.class)
                .before(new HashMap<>())
                .after(new HashMap<>())
                .restErrors(new HashMap<>())
                .defaultErrors(new HashMap<>())
                .dispatchErrors(new HashMap<>())
                .defaultDispatchErrors(new HashMap<>())
                .validate(validate)
                .onHalts(otterAppFactory.defaultRestOnHalts())
                .build();

        RestLocationTranslator<DummySession, DummyUser, DummyPayload> actual = subject.make(config);

        assertThat(actual, is(notNullValue()));
    }
}