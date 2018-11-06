package org.rootservices.otter.security.builder;

import helper.FixtureFactory;
import helper.entity.DummySession;
import helper.entity.DummyUser;
import org.junit.Test;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.otter.config.OtterAppFactory;
import org.rootservices.otter.controller.entity.EmptyPayload;
import org.rootservices.otter.security.builder.entity.Betweens;
import org.rootservices.otter.security.csrf.between.CheckCSRF;
import org.rootservices.otter.security.csrf.between.PrepareCSRF;
import org.rootservices.otter.security.session.between.DecryptSession;
import org.rootservices.otter.security.session.between.EncryptSession;


import java.util.Map;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;


public class BetweenBuilderTest {
    private static OtterAppFactory otterAppFactory = new OtterAppFactory();

    @Test
    public void buildShouldBeEmptyLists() {
        BetweenBuilder<DummySession, DummyUser, EmptyPayload> subject = new BetweenBuilder<DummySession, DummyUser, EmptyPayload>();

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject
                .otterFactory(otterAppFactory)
                .build();

        assertThat(actual.getBefore().size(), is(0));
        assertThat(actual.getAfter().size(), is(0));
    }

    @Test
    public void buildUnSecureCsrfPrepareShouldBeOk() {
        BetweenBuilder<DummySession, DummyUser, EmptyPayload> subject = new BetweenBuilder<DummySession, DummyUser, EmptyPayload>();

        SymmetricKey preferredSignKey = FixtureFactory.signKey("preferred-key");
        Map<String, SymmetricKey> rotationSignKeys = FixtureFactory.rotationSignKeys("rotation-key-", 2);

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject
                .otterFactory(otterAppFactory)
                .secure(false)
                .signKey(preferredSignKey)
                .rotationSignKeys(rotationSignKeys)
                .csrfPrepare()
                .build();

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getBefore().get(0), is(instanceOf(PrepareCSRF.class)));
        PrepareCSRF<DummySession, DummyUser, EmptyPayload> actualPrepare = (PrepareCSRF<DummySession, DummyUser, EmptyPayload>) actual.getBefore().get(0);
        assertThat(actualPrepare.getCookieConfig().getSecure(), is(false));
        assertThat(actualPrepare.getCookieConfig().getAge(), is(-1));
        assertThat(actualPrepare.getCookieConfig().getName(), is("csrfToken"));

        assertThat(actual.getAfter().size(), is(0));
    }

    @Test
    public void buildSecureCsrfPrepareShouldBeOk() {
        BetweenBuilder<DummySession, DummyUser, EmptyPayload> subject = new BetweenBuilder<DummySession, DummyUser, EmptyPayload>();
        subject.otterFactory(otterAppFactory);

        SymmetricKey preferredSignKey = FixtureFactory.signKey("preferred-key");
        Map<String, SymmetricKey> rotationSignKeys = FixtureFactory.rotationSignKeys("rotation-key-", 2);

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject
                .otterFactory(otterAppFactory)
                .secure(true)
                .signKey(preferredSignKey)
                .rotationSignKeys(rotationSignKeys)
                .csrfPrepare()
                .build();

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getBefore().get(0), is(instanceOf(PrepareCSRF.class)));
        PrepareCSRF<DummySession, DummyUser, EmptyPayload> actualPrepare = (PrepareCSRF<DummySession, DummyUser, EmptyPayload>) actual.getBefore().get(0);
        assertThat(actualPrepare.getCookieConfig().getSecure(), is(true));
        assertThat(actualPrepare.getCookieConfig().getAge(), is(-1));
        assertThat(actualPrepare.getCookieConfig().getName(), is("csrfToken"));

        assertThat(actual.getAfter().size(), is(0));
    }


    @Test
    public void buildSecureCsrfProtectShouldBeOk() {
        BetweenBuilder<DummySession, DummyUser, EmptyPayload> subject = new BetweenBuilder<DummySession, DummyUser, EmptyPayload>();
        subject.otterFactory(otterAppFactory);

        SymmetricKey preferredSignKey = FixtureFactory.signKey("preferred-key");
        Map<String, SymmetricKey> rotationSignKeys = FixtureFactory.rotationSignKeys("rotation-key-", 2);

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject
                .otterFactory(otterAppFactory)
                .secure(true)
                .signKey(preferredSignKey)
                .rotationSignKeys(rotationSignKeys)
                .csrfProtect()
                .build();

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getBefore().get(0), is(instanceOf(CheckCSRF.class)));
        CheckCSRF<DummySession, DummyUser, EmptyPayload> actualCheck = (CheckCSRF<DummySession, DummyUser, EmptyPayload>) actual.getBefore().get(0);
        assertThat(actualCheck.getCookieName(), is("csrfToken"));
        assertThat(actualCheck.getFormFieldName(), is("csrfToken"));

        assertThat(actual.getAfter().size(), is(0));
    }


    @Test
    public void buildUnSecureSessionShouldBeOk() throws Exception {
        BetweenBuilder<DummySession, DummyUser, EmptyPayload> subject = new BetweenBuilder<DummySession, DummyUser, EmptyPayload>();
        subject.otterFactory(otterAppFactory);

        SymmetricKey preferredEncKey = FixtureFactory.encKey("preferred-key");
        Map<String, SymmetricKey> rotationEncKeys = FixtureFactory.rotationEncKeys("rotation-key-", 2);

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject
                .otterFactory(otterAppFactory)
                .secure(false)
                .encKey(preferredEncKey)
                .rotationEncKey(rotationEncKeys)
                .sessionClass(DummySession.class)
                .session()
                .build();


        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getBefore().get(0), is(instanceOf(DecryptSession.class)));

        DecryptSession<DummySession, DummyUser, EmptyPayload> actualDecrypt = (DecryptSession<DummySession, DummyUser, EmptyPayload>) actual.getBefore().get(0);
        assertThat(actualDecrypt.getRequired(), is(true));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(instanceOf(EncryptSession.class)));

        EncryptSession<DummySession, DummyUser, EmptyPayload> actualEncrypt = (EncryptSession<DummySession, DummyUser, EmptyPayload>) actual.getAfter().get(0);
        assertThat(actualEncrypt.getCookieConfig().getSecure(), is(false));
        assertThat(actualEncrypt.getCookieConfig().getAge(), is(-1));
        assertThat(actualEncrypt.getCookieConfig().getName(), is("session"));
    }

    @Test
    public void buildSecureSessionShouldBeOk() throws Exception {
        BetweenBuilder<DummySession, DummyUser, EmptyPayload> subject = new BetweenBuilder<DummySession, DummyUser, EmptyPayload>();
        subject.otterFactory(otterAppFactory);

        SymmetricKey preferredEncKey = FixtureFactory.encKey("preferred-key");
        Map<String, SymmetricKey> rotationEncKeys = FixtureFactory.rotationEncKeys("rotation-key-", 2);

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject
                .otterFactory(otterAppFactory)
                .secure(true)
                .encKey(preferredEncKey)
                .rotationEncKey(rotationEncKeys)
                .sessionClass(DummySession.class)
                .session()
                .build();

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getBefore().get(0), is(instanceOf(DecryptSession.class)));

        DecryptSession<DummySession, DummyUser, EmptyPayload> actualDecrypt = (DecryptSession<DummySession, DummyUser, EmptyPayload>) actual.getBefore().get(0);
        assertThat(actualDecrypt.getRequired(), is(true));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(instanceOf(EncryptSession.class)));

        EncryptSession<DummySession, DummyUser, EmptyPayload> actualEncrypt = (EncryptSession<DummySession, DummyUser, EmptyPayload>) actual.getAfter().get(0);
        assertThat(actualEncrypt.getCookieConfig().getSecure(), is(true));
        assertThat(actualEncrypt.getCookieConfig().getAge(), is(-1));
        assertThat(actualEncrypt.getCookieConfig().getName(), is("session"));
    }

    @Test
    public void buildUnSecureOptionalSessionShouldBeOk() throws Exception {
        BetweenBuilder<DummySession, DummyUser, EmptyPayload> subject = new BetweenBuilder<DummySession, DummyUser, EmptyPayload>();
        subject.otterFactory(otterAppFactory);

        SymmetricKey preferredEncKey = FixtureFactory.encKey("preferred-key");
        Map<String, SymmetricKey> rotationEncKeys = FixtureFactory.rotationEncKeys("rotation-key-", 2);

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject
                .otterFactory(otterAppFactory)
                .secure(false)
                .encKey(preferredEncKey)
                .rotationEncKey(rotationEncKeys)
                .sessionClass(DummySession.class)
                .optionalSession()
                .build();


        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getBefore().get(0), is(instanceOf(DecryptSession.class)));

        DecryptSession<DummySession, DummyUser, EmptyPayload> actualDecrypt = (DecryptSession<DummySession, DummyUser, EmptyPayload>) actual.getBefore().get(0);
        assertThat(actualDecrypt.getRequired(), is(false));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(instanceOf(EncryptSession.class)));

        EncryptSession<DummySession, DummyUser, EmptyPayload> actualEncrypt = (EncryptSession<DummySession, DummyUser, EmptyPayload>) actual.getAfter().get(0);
        assertThat(actualEncrypt.getCookieConfig().getSecure(), is(false));
        assertThat(actualEncrypt.getCookieConfig().getAge(), is(-1));
        assertThat(actualEncrypt.getCookieConfig().getName(), is("session"));
    }

    @Test
    public void buildSecureOptionalSessionShouldBeOk() throws Exception {
        BetweenBuilder<DummySession, DummyUser, EmptyPayload> subject = new BetweenBuilder<DummySession, DummyUser, EmptyPayload>();
        subject.otterFactory(otterAppFactory);

        SymmetricKey preferredEncKey = FixtureFactory.encKey("preferred-key");
        Map<String, SymmetricKey> rotationEncKeys = FixtureFactory.rotationEncKeys("rotation-key-", 2);

        Betweens<DummySession, DummyUser, EmptyPayload> actual = subject
                .otterFactory(otterAppFactory)
                .secure(true)
                .encKey(preferredEncKey)
                .rotationEncKey(rotationEncKeys)
                .sessionClass(DummySession.class)
                .optionalSession()
                .build();

        assertThat(actual.getBefore().size(), is(1));
        assertThat(actual.getBefore().get(0), is(instanceOf(DecryptSession.class)));

        DecryptSession<DummySession, DummyUser, EmptyPayload> actualDecrypt = (DecryptSession<DummySession, DummyUser, EmptyPayload>) actual.getBefore().get(0);
        assertThat(actualDecrypt.getRequired(), is(false));

        assertThat(actual.getAfter().size(), is(1));
        assertThat(actual.getAfter().get(0), is(instanceOf(EncryptSession.class)));

        EncryptSession<DummySession, DummyUser, EmptyPayload> actualEncrypt = (EncryptSession<DummySession, DummyUser, EmptyPayload>) actual.getAfter().get(0);
        assertThat(actualEncrypt.getCookieConfig().getSecure(), is(true));
        assertThat(actualEncrypt.getCookieConfig().getAge(), is(-1));
        assertThat(actualEncrypt.getCookieConfig().getName(), is("session"));
    }
}