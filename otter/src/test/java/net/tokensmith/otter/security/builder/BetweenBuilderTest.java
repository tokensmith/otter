package net.tokensmith.otter.security.builder;

import helper.FixtureFactory;
import helper.entity.model.DummySession;
import helper.entity.model.DummyUser;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.otter.config.CookieConfig;
import net.tokensmith.otter.config.OtterAppFactory;
import net.tokensmith.otter.gateway.entity.Shape;
import net.tokensmith.otter.security.builder.entity.Betweens;
import net.tokensmith.otter.security.csrf.between.html.CheckCSRF;
import net.tokensmith.otter.security.csrf.between.html.PrepareCSRF;
import net.tokensmith.otter.security.session.between.html.DecryptSession;
import net.tokensmith.otter.security.session.between.html.EncryptSession;
import net.tokensmith.otter.translator.config.TranslatorAppFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;


public class BetweenBuilderTest {
    private static OtterAppFactory otterAppFactory = new OtterAppFactory();
    private static TranslatorAppFactory appFactory = new TranslatorAppFactory();
    private Shape shape;

    @Before
    public void setUp() {
        shape = FixtureFactory.makeShape("1234", "5678");
    }

    @Test
    public void buildShouldBeEmptyLists() {
        BetweenBuilder<DummySession, DummyUser> subject = new BetweenBuilder<DummySession, DummyUser>();

        Betweens<DummySession, DummyUser> actual = subject
                .routerAppFactory(appFactory)
                .onHalts(otterAppFactory.defaultOnHalts(shape))
                .build();

        assertThat(actual.getBefore().size(), is(0));
        assertThat(actual.getAfter().size(), is(0));
    }

    @Test
    public void buildUnSecureCsrfPrepareShouldBeOk() {
        BetweenBuilder<DummySession, DummyUser> subject = new BetweenBuilder<DummySession, DummyUser>();

        SymmetricKey preferredSignKey = FixtureFactory.signKey("preferred-key");
        Map<String, SymmetricKey> rotationSignKeys = FixtureFactory.rotationSignKeys("rotation-key-", 2);
        CookieConfig csrfCookieConfig = FixtureFactory.csrfCookieConfig();

        Betweens<DummySession, DummyUser> actual = subject
                .routerAppFactory(appFactory)
                .signKey(preferredSignKey)
                .rotationSignKeys(rotationSignKeys)
                .csrfCookieConfig(csrfCookieConfig)
                .onHalts(otterAppFactory.defaultOnHalts(shape))
                .csrfPrepare()
                .build();

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getBefore().get(0), is(instanceOf(PrepareCSRF.class)));
        PrepareCSRF<DummySession, DummyUser> actualPrepare = (PrepareCSRF<DummySession, DummyUser>) actual.getBefore().get(0);
        assertThat(actualPrepare.getCookieConfig().getSecure(), is(false));
        assertThat(actualPrepare.getCookieConfig().getAge(), is(-1));
        assertThat(actualPrepare.getCookieConfig().getName(), is("csrfToken"));

        assertThat(actual.getAfter().size(), is(0));
    }

    @Test
    public void buildSecureCsrfPrepareShouldBeOk() {
        BetweenBuilder<DummySession, DummyUser> subject = new BetweenBuilder<DummySession, DummyUser>();
        subject.routerAppFactory(appFactory);

        SymmetricKey preferredSignKey = FixtureFactory.signKey("preferred-key");
        Map<String, SymmetricKey> rotationSignKeys = FixtureFactory.rotationSignKeys("rotation-key-", 2);
        CookieConfig csrfCookieConfig = FixtureFactory.csrfCookieConfig();
        csrfCookieConfig.setSecure(true);

        Betweens<DummySession, DummyUser> actual = subject
                .routerAppFactory(appFactory)
                .signKey(preferredSignKey)
                .rotationSignKeys(rotationSignKeys)
                .csrfCookieConfig(csrfCookieConfig)
                .onHalts(otterAppFactory.defaultOnHalts(shape))
                .csrfPrepare()
                .build();

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getBefore().get(0), is(instanceOf(PrepareCSRF.class)));
        PrepareCSRF<DummySession, DummyUser> actualPrepare = (PrepareCSRF<DummySession, DummyUser>) actual.getBefore().get(0);
        assertThat(actualPrepare.getCookieConfig().getSecure(), is(true));
        assertThat(actualPrepare.getCookieConfig().getAge(), is(-1));
        assertThat(actualPrepare.getCookieConfig().getName(), is("csrfToken"));

        assertThat(actual.getAfter().size(), is(0));
    }


    @Test
    public void buildSecureCsrfProtectShouldBeOk() {
        BetweenBuilder<DummySession, DummyUser> subject = new BetweenBuilder<DummySession, DummyUser>();
        subject.routerAppFactory(appFactory);

        SymmetricKey preferredSignKey = FixtureFactory.signKey("preferred-key");
        Map<String, SymmetricKey> rotationSignKeys = FixtureFactory.rotationSignKeys("rotation-key-", 2);
        CookieConfig csrfCookieConfig = FixtureFactory.csrfCookieConfig();

        Betweens<DummySession, DummyUser> actual = subject
                .routerAppFactory(appFactory)
                .signKey(preferredSignKey)
                .rotationSignKeys(rotationSignKeys)
                .csrfCookieConfig(csrfCookieConfig)
                .onHalts(otterAppFactory.defaultOnHalts(shape))
                .csrfProtect()
                .build();

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getBefore().get(0), is(instanceOf(CheckCSRF.class)));
        CheckCSRF<DummySession, DummyUser> actualCheck = (CheckCSRF<DummySession, DummyUser>) actual.getBefore().get(0);
        assertThat(actualCheck.getCookieName(), is("csrfToken"));
        assertThat(actualCheck.getFormFieldName(), is("csrfToken"));

        assertThat(actual.getAfter().size(), is(0));
    }


    @Test
    public void buildUnSecureSessionShouldBeOk() throws Exception {
        BetweenBuilder<DummySession, DummyUser> subject = new BetweenBuilder<DummySession, DummyUser>();
        subject.routerAppFactory(appFactory);

        SymmetricKey preferredEncKey = FixtureFactory.encKey("preferred-key");
        Map<String, SymmetricKey> rotationEncKeys = FixtureFactory.rotationEncKeys("rotation-key-", 2);
        CookieConfig sessionCookieConfig = FixtureFactory.sessionCookieConfig();

        Betweens<DummySession, DummyUser> actual = subject
                .routerAppFactory(appFactory)
                .encKey(preferredEncKey)
                .rotationEncKey(rotationEncKeys)
                .sessionClass(DummySession.class)
                .sessionCookieConfig(sessionCookieConfig)
                .onHalts(otterAppFactory.defaultOnHalts(shape))
                .session()
                .build();


        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getBefore().get(0), is(instanceOf(DecryptSession.class)));

        DecryptSession<DummySession, DummyUser> actualDecrypt = (DecryptSession<DummySession, DummyUser>) actual.getBefore().get(0);
        assertThat(actualDecrypt.getRequired(), is(true));
        assertThat(actualDecrypt.getSessionCookieName(), is(sessionCookieConfig.getName()));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(instanceOf(EncryptSession.class)));

        EncryptSession<DummySession, DummyUser> actualEncrypt = (EncryptSession<DummySession, DummyUser>) actual.getAfter().get(0);
        assertThat(actualEncrypt.getCookieConfig().getSecure(), is(false));
        assertThat(actualEncrypt.getCookieConfig().getAge(), is(-1));
        assertThat(actualEncrypt.getCookieConfig().getName(), is("session"));
    }

    @Test
    public void buildSecureSessionShouldBeOk() throws Exception {
        BetweenBuilder<DummySession, DummyUser> subject = new BetweenBuilder<DummySession, DummyUser>();
        subject.routerAppFactory(appFactory);

        SymmetricKey preferredEncKey = FixtureFactory.encKey("preferred-key");
        Map<String, SymmetricKey> rotationEncKeys = FixtureFactory.rotationEncKeys("rotation-key-", 2);
        CookieConfig sessionCookieConfig = FixtureFactory.sessionCookieConfig();
        sessionCookieConfig.setSecure(true);

        Betweens<DummySession, DummyUser> actual = subject
                .routerAppFactory(appFactory)
                .encKey(preferredEncKey)
                .rotationEncKey(rotationEncKeys)
                .sessionClass(DummySession.class)
                .sessionCookieConfig(sessionCookieConfig)
                .onHalts(otterAppFactory.defaultOnHalts(shape))
                .session()
                .build();

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getBefore().get(0), is(instanceOf(DecryptSession.class)));

        DecryptSession<DummySession, DummyUser> actualDecrypt = (DecryptSession<DummySession, DummyUser>) actual.getBefore().get(0);
        assertThat(actualDecrypt.getRequired(), is(true));
        assertThat(actualDecrypt.getSessionCookieName(), is(sessionCookieConfig.getName()));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(instanceOf(EncryptSession.class)));

        EncryptSession<DummySession, DummyUser> actualEncrypt = (EncryptSession<DummySession, DummyUser>) actual.getAfter().get(0);
        assertThat(actualEncrypt.getCookieConfig().getSecure(), is(true));
        assertThat(actualEncrypt.getCookieConfig().getAge(), is(-1));
        assertThat(actualEncrypt.getCookieConfig().getName(), is("session"));
    }

    @Test
    public void buildUnSecureOptionalSessionShouldBeOk() throws Exception {
        BetweenBuilder<DummySession, DummyUser> subject = new BetweenBuilder<DummySession, DummyUser>();
        subject.routerAppFactory(appFactory);

        SymmetricKey preferredEncKey = FixtureFactory.encKey("preferred-key");
        Map<String, SymmetricKey> rotationEncKeys = FixtureFactory.rotationEncKeys("rotation-key-", 2);
        CookieConfig sessionCookieConfig = FixtureFactory.sessionCookieConfig();

        Betweens<DummySession, DummyUser> actual = subject
                .routerAppFactory(appFactory)
                .encKey(preferredEncKey)
                .rotationEncKey(rotationEncKeys)
                .sessionClass(DummySession.class)
                .sessionCookieConfig(sessionCookieConfig)
                .onHalts(otterAppFactory.defaultOnHalts(shape))
                .optionalSession()
                .build();


        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getBefore().get(0), is(instanceOf(DecryptSession.class)));

        DecryptSession<DummySession, DummyUser> actualDecrypt = (DecryptSession<DummySession, DummyUser>) actual.getBefore().get(0);
        assertThat(actualDecrypt.getRequired(), is(false));
        assertThat(actualDecrypt.getSessionCookieName(), is(sessionCookieConfig.getName()));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(instanceOf(EncryptSession.class)));

        EncryptSession<DummySession, DummyUser> actualEncrypt = (EncryptSession<DummySession, DummyUser>) actual.getAfter().get(0);
        assertThat(actualEncrypt.getCookieConfig().getSecure(), is(false));
        assertThat(actualEncrypt.getCookieConfig().getAge(), is(-1));
        assertThat(actualEncrypt.getCookieConfig().getName(), is("session"));
    }

    @Test
    public void buildSecureOptionalSessionShouldBeOk() throws Exception {
        BetweenBuilder<DummySession, DummyUser> subject = new BetweenBuilder<DummySession, DummyUser>();
        subject.routerAppFactory(appFactory);

        SymmetricKey preferredEncKey = FixtureFactory.encKey("preferred-key");
        Map<String, SymmetricKey> rotationEncKeys = FixtureFactory.rotationEncKeys("rotation-key-", 2);
        CookieConfig sessionCookieConfig = FixtureFactory.sessionCookieConfig();

        Betweens<DummySession, DummyUser> actual = subject
                .routerAppFactory(appFactory)
                .encKey(preferredEncKey)
                .rotationEncKey(rotationEncKeys)
                .sessionClass(DummySession.class)
                .sessionCookieConfig(sessionCookieConfig)
                .onHalts(otterAppFactory.defaultOnHalts(shape))
                .optionalSession()
                .build();

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getBefore().get(0), is(instanceOf(DecryptSession.class)));

        DecryptSession<DummySession, DummyUser> actualDecrypt = (DecryptSession<DummySession, DummyUser>) actual.getBefore().get(0);
        assertThat(actualDecrypt.getRequired(), is(false));
        assertThat(actualDecrypt.getSessionCookieName(), is(sessionCookieConfig.getName()));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(instanceOf(EncryptSession.class)));

        EncryptSession<DummySession, DummyUser> actualEncrypt = (EncryptSession<DummySession, DummyUser>) actual.getAfter().get(0);
        assertThat(actualEncrypt.getCookieConfig().getSecure(), is(false));
        assertThat(actualEncrypt.getCookieConfig().getAge(), is(-1));
        assertThat(actualEncrypt.getCookieConfig().getName(), is("session"));
    }
}