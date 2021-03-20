package net.tokensmith.otter.security.builder;

import helper.FixtureFactory;
import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.otter.config.CookieConfig;
import net.tokensmith.otter.config.OtterAppFactory;
import net.tokensmith.otter.gateway.entity.Shape;
import net.tokensmith.otter.security.builder.entity.RestBetweens;
import net.tokensmith.otter.security.csrf.between.rest.RestCheckCSRF;
import net.tokensmith.otter.security.session.between.rest.RestReadSession;
import net.tokensmith.otter.translator.config.TranslatorAppFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;


public class RestBetweenBuilderTest {
    private static OtterAppFactory otterAppFactory = new OtterAppFactory();
    private static TranslatorAppFactory appFactory = new TranslatorAppFactory();
    private Shape shape;
    
    @Before
    public void setUp() {
        shape = FixtureFactory.makeShape("1234", "5678");
    }

    @Test
    public void buildShouldBeEmptyLists() {
        RestBetweenBuilder<DummySession, DummyUser> subject = new RestBetweenBuilder<DummySession, DummyUser>();

        RestBetweens<DummySession, DummyUser> actual = subject
                .routerAppFactory(appFactory)
                .onHalts(otterAppFactory.defaultRestOnHalts(shape))
                .build();

        assertThat(actual.getBefore().size(), is(0));
        assertThat(actual.getAfter().size(), is(0));
    }

    @Test
    public void buildUnSecureSessionShouldBeOk() throws Exception {
        RestBetweenBuilder<DummySession, DummyUser> subject = new RestBetweenBuilder<DummySession, DummyUser>();
        subject.routerAppFactory(appFactory);

        SymmetricKey preferredEncKey = FixtureFactory.encKey("preferred-key");
        Map<String, SymmetricKey> rotationEncKeys = FixtureFactory.rotationEncKeys("rotation-key-", 2);
        CookieConfig sessionCookieConfig = FixtureFactory.sessionCookieConfig();

        RestBetweens<DummySession, DummyUser> actual = subject
                .routerAppFactory(appFactory)
                .encKey(preferredEncKey)
                .rotationEncKeys(rotationEncKeys)
                .sessionClazz(DummySession.class)
                .sessionCookieConfig(sessionCookieConfig)
                .onHalts(otterAppFactory.defaultRestOnHalts(shape))
                .session()
                .build();


        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getBefore().get(0), is(instanceOf(RestReadSession.class)));

        RestReadSession<DummySession, DummyUser> actualDecrypt = (RestReadSession<DummySession, DummyUser>) actual.getBefore().get(0);
        assertThat(actualDecrypt.getRequired(), is(true));
        assertThat(actualDecrypt.getSessionCookieName(), is(sessionCookieConfig.getName()));

        assertThat(actual.getAfter().size(), is(0));
    }

    @Test
    public void buildSecureSessionShouldBeOk() throws Exception {
        RestBetweenBuilder<DummySession, DummyUser> subject = new RestBetweenBuilder<DummySession, DummyUser>();
        subject.routerAppFactory(appFactory);

        SymmetricKey preferredEncKey = FixtureFactory.encKey("preferred-key");
        Map<String, SymmetricKey> rotationEncKeys = FixtureFactory.rotationEncKeys("rotation-key-", 2);
        CookieConfig sessionCookieConfig = FixtureFactory.sessionCookieConfig();

        RestBetweens<DummySession, DummyUser> actual = subject
                .routerAppFactory(appFactory)
                .encKey(preferredEncKey)
                .rotationEncKeys(rotationEncKeys)
                .sessionClazz(DummySession.class)
                .onHalts(otterAppFactory.defaultRestOnHalts(shape))
                .sessionCookieConfig(sessionCookieConfig)
                .session()
                .build();

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getBefore().get(0), is(instanceOf(RestReadSession.class)));

        RestReadSession<DummySession, DummyUser> actualDecrypt = (RestReadSession<DummySession, DummyUser>) actual.getBefore().get(0);
        assertThat(actualDecrypt.getRequired(), is(true));
        assertThat(actualDecrypt.getSessionCookieName(), is(sessionCookieConfig.getName()));

        assertThat(actual.getAfter().size(), is(0));
    }

    @Test
    public void buildUnSecureOptionalSessionShouldBeOk() throws Exception {
        RestBetweenBuilder<DummySession, DummyUser> subject = new RestBetweenBuilder<DummySession, DummyUser>();
        subject.routerAppFactory(appFactory);

        SymmetricKey preferredEncKey = FixtureFactory.encKey("preferred-key");
        Map<String, SymmetricKey> rotationEncKeys = FixtureFactory.rotationEncKeys("rotation-key-", 2);
        CookieConfig sessionCookieConfig = FixtureFactory.sessionCookieConfig();

        RestBetweens<DummySession, DummyUser> actual = subject
                .routerAppFactory(appFactory)
                .encKey(preferredEncKey)
                .rotationEncKeys(rotationEncKeys)
                .sessionClazz(DummySession.class)
                .onHalts(otterAppFactory.defaultRestOnHalts(shape))
                .sessionCookieConfig(sessionCookieConfig)
                .optionalSession()
                .build();


        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getBefore().get(0), is(instanceOf(RestReadSession.class)));

        RestReadSession<DummySession, DummyUser> actualDecrypt = (RestReadSession<DummySession, DummyUser>) actual.getBefore().get(0);
        assertThat(actualDecrypt.getRequired(), is(false));
        assertThat(actualDecrypt.getSessionCookieName(), is(sessionCookieConfig.getName()));
    }

    @Test
    public void buildSecureOptionalSessionShouldBeOk() throws Exception {
        RestBetweenBuilder<DummySession, DummyUser> subject = new RestBetweenBuilder<DummySession, DummyUser>();
        subject.routerAppFactory(appFactory);

        SymmetricKey preferredEncKey = FixtureFactory.encKey("preferred-key");
        Map<String, SymmetricKey> rotationEncKeys = FixtureFactory.rotationEncKeys("rotation-key-", 2);
        CookieConfig sessionCookieConfig = FixtureFactory.sessionCookieConfig();

        RestBetweens<DummySession, DummyUser> actual = subject
                .routerAppFactory(appFactory)
                .encKey(preferredEncKey)
                .rotationEncKeys(rotationEncKeys)
                .sessionClazz(DummySession.class)
                .onHalts(otterAppFactory.defaultRestOnHalts(shape))
                .sessionCookieConfig(sessionCookieConfig)
                .optionalSession()
                .build();

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getBefore().get(0), is(instanceOf(RestReadSession.class)));

        RestReadSession<DummySession, DummyUser> actualDecrypt = (RestReadSession<DummySession, DummyUser>) actual.getBefore().get(0);
        assertThat(actualDecrypt.getRequired(), is(false));
        assertThat(actualDecrypt.getSessionCookieName(), is(sessionCookieConfig.getName()));

        assertThat(actual.getAfter().size(), is(0));
    }

    @Test
    public void buildSecureCsrfProtectShouldBeOk() {
        RestBetweenBuilder<DummySession, DummyUser> subject = new RestBetweenBuilder<DummySession, DummyUser>();
        subject.routerAppFactory(appFactory);

        SymmetricKey preferredSignKey = FixtureFactory.signKey("preferred-key");
        Map<String, SymmetricKey> rotationSignKeys = FixtureFactory.rotationSignKeys("rotation-key-", 2);
        CookieConfig csrfCookieConfig = FixtureFactory.csrfCookieConfig();

        RestBetweens<DummySession, DummyUser> actual = subject
                .routerAppFactory(appFactory)
                .signKey(preferredSignKey)
                .rotationSignKeys(rotationSignKeys)
                .onHalts(otterAppFactory.defaultRestOnHalts(shape))
                .csrfCookieConfig(csrfCookieConfig)
                .csrfProtect()
                .build();

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getBefore().get(0), is(instanceOf(RestCheckCSRF.class)));
        RestCheckCSRF<DummySession, DummyUser> actualCheck = (RestCheckCSRF<DummySession, DummyUser>) actual.getBefore().get(0);
        assertThat(actualCheck.getCookieName(), is(csrfCookieConfig.getName()));
        assertThat(actualCheck.getHeaderName(), is("X-CSRF"));

        assertThat(actual.getAfter().size(), is(0));
    }
}