package net.tokensmith.otter.gateway.builder;

import helper.FixtureFactory;
import net.tokensmith.otter.controller.entity.StatusCode;
import org.junit.Test;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.otter.gateway.entity.Shape;

import java.util.Map;
import java.util.Optional;

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
                .sessionFailStatusCode(StatusCode.OK)
                .sessionFailTemplate(Optional.of("/WEB-INF/jsp/session.jsp"))
                .signkey(signKey)
                .csrfFailStatusCode(StatusCode.NOT_FOUND)
                .csrfFailTemplate(Optional.of("/WEB-INF/jsp/csrf.jsp"))
                .rotationEncKeys(rotationEncKeys)
                .rotationSignKeys(rotationSignKeys)
                .secure(true)
                .writeChunkSize(1024)
                .readChunkSize(1024)
                .build();

        assertThat(actual.getEncKey(), is(encKey));
        assertThat(actual.getSessionFailStatusCode(), is(StatusCode.OK));
        assertThat(actual.getSessionFailTemplate().isPresent(), is(true));
        assertThat(actual.getSessionFailTemplate().get(), is("/WEB-INF/jsp/session.jsp"));
        assertThat(actual.getSignkey(), is(signKey));
        assertThat(actual.getCsrfFailStatusCode(), is(StatusCode.NOT_FOUND));
        assertThat(actual.getCsrfFailTemplate().isPresent(), is(true));
        assertThat(actual.getCsrfFailTemplate().get(), is("/WEB-INF/jsp/csrf.jsp"));
        assertThat(actual.getRotationEncKeys(), is(rotationEncKeys));
        assertThat(actual.getRotationSignKeys(), is(rotationSignKeys));
        assertThat(actual.getSecure(), is(true));
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
                .secure(true)
                .writeChunkSize(1024)
                .readChunkSize(1024)
                .build();

        assertThat(actual.getEncKey(), is(encKey));
        assertThat(actual.getSessionFailStatusCode(), is(StatusCode.UNAUTHORIZED));
        assertThat(actual.getSessionFailTemplate().isPresent(), is(false));
        assertThat(actual.getSignkey(), is(signKey));
        assertThat(actual.getCsrfFailStatusCode(), is(StatusCode.FORBIDDEN));
        assertThat(actual.getCsrfFailTemplate().isPresent(), is(false));
        assertThat(actual.getRotationEncKeys(), is(rotationEncKeys));
        assertThat(actual.getRotationSignKeys(), is(rotationSignKeys));
        assertThat(actual.getSecure(), is(true));
        assertThat(actual.getWriteChunkSize(), is(1024));
        assertThat(actual.getReadChunkSize(), is(1024));
    }
}