package net.tokensmith.otter.gateway.builder;

import helper.FixtureFactory;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.otter.config.CookieConfig;
import net.tokensmith.otter.gateway.entity.Shape;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
                .writeChunkSize(1024)
                .readChunkSize(1024)
                .build();

        assertThat(actual.getEncKey(), is(encKey));
        assertThat(actual.getSignkey(), is(signKey));
        assertThat(actual.getRotationEncKeys(), is(rotationEncKeys));
        assertThat(actual.getRotationSignKeys(), is(rotationSignKeys));
        assertThat(actual.getWriteChunkSize(), is(1024));
        assertThat(actual.getReadChunkSize(), is(1024));
    }

    @Test
    public void buildWithDefaults() {
        SymmetricKey encKey = FixtureFactory.encKey("test-enc-key-0");
        SymmetricKey signKey = FixtureFactory.signKey("test-sign-key-0");
        Map<String, SymmetricKey> rotationEncKeys = FixtureFactory.rotationEncKeys("test-enc-key", 1);
        Map<String, SymmetricKey> rotationSignKeys = FixtureFactory.rotationSignKeys("test-sign-key", 1);

        Shape actual = new ShapeBuilder()
                .encKey(encKey)
                .signkey(signKey)
                .rotationEncKeys(rotationEncKeys)
                .rotationSignKeys(rotationSignKeys)
                .writeChunkSize(1024)
                .readChunkSize(1024)
                .build();

        assertThat(actual.getEncKey(), is(encKey));
        assertThat(actual.getSignkey(), is(signKey));
        assertThat(actual.getRotationEncKeys(), is(rotationEncKeys));
        assertThat(actual.getRotationSignKeys(), is(rotationSignKeys));
        assertThat(actual.getWriteChunkSize(), is(1024));
        assertThat(actual.getReadChunkSize(), is(1024));
    }

    @Test
    public void buildWithCookieConfigs() {
        SymmetricKey encKey = FixtureFactory.encKey("test-enc-key-0");
        SymmetricKey signKey = FixtureFactory.signKey("test-sign-key-0");
        Map<String, SymmetricKey> rotationEncKeys = FixtureFactory.rotationEncKeys("test-enc-key", 1);
        Map<String, SymmetricKey> rotationSignKeys = FixtureFactory.rotationSignKeys("test-sign-key", 1);

        CookieConfig cookieConfig = new CookieConfig(
                "test", false, -1, true
        );

        Shape actual = new ShapeBuilder()
                .encKey(encKey)
                .signkey(signKey)
                .rotationEncKeys(rotationEncKeys)
                .rotationSignKeys(rotationSignKeys)
                .writeChunkSize(1024)
                .readChunkSize(1024)
                .csrfCookieConfig(cookieConfig)
                .build();

        assertThat(actual.getEncKey(), is(encKey));
        assertThat(actual.getSignkey(), is(signKey));
        assertThat(actual.getRotationEncKeys(), is(rotationEncKeys));
        assertThat(actual.getRotationSignKeys(), is(rotationSignKeys));
        assertThat(actual.getWriteChunkSize(), is(1024));
        assertThat(actual.getReadChunkSize(), is(1024));
    }
}