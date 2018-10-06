package org.rootservices.otter.gateway.builder;

import helper.FixtureFactory;
import helper.entity.DummySession;
import org.junit.Test;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.otter.gateway.entity.Shape;

import java.util.Map;

import static java.util.function.Predicate.isEqual;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class ShapeBuilderTest {

    @Test
    public void build() {
        SymmetricKey encKey = FixtureFactory.encKey("test-enc-key-0");
        SymmetricKey signKey = FixtureFactory.signKey("test-sign-key-0");
        Map<String, SymmetricKey> rotationEncKeys = FixtureFactory.rotationEncKeys("test-enc-key", 1);
        Map<String, SymmetricKey> rotationSignKeys = FixtureFactory.rotationSignKeys("test-sign-key", 1);

        Shape actual = new ShapeBuilder()
                .encKey(encKey)
                .signkey(signKey)
                .rotationEncKeys(rotationEncKeys)
                .rotationSignKeys(rotationSignKeys)
                .secure(true)
                .build();

        assertThat(actual.getEncKey(), is(encKey));
        assertThat(actual.getSignkey(), is(signKey));
        assertThat(actual.getRotationEncKeys(), is(rotationEncKeys));
        assertThat(actual.getRotationSignKeys(), is(rotationSignKeys));
        assertThat(actual.getSecure(), is(true));
    }
}