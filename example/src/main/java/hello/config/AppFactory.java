package hello.config;



import hello.controller.HelloRestResource;
import hello.model.Hello;
import hello.security.SessionBefore;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.jwt.entity.jwk.Use;
import org.rootservices.otter.config.CookieConfig;
import org.rootservices.otter.config.OtterAppFactory;
import org.rootservices.otter.security.session.Session;
import org.rootservices.otter.security.session.between.EncryptSession;
import org.rootservices.otter.translator.JsonTranslator;

import java.util.Map;
import java.util.Optional;

public class AppFactory<T extends Session> {
    public OtterAppFactory<T> otterAppFactory() {
        return new OtterAppFactory<T>();
    }

    public HelloRestResource helloRestResource() {
        JsonTranslator<Hello> jsonTranslator = otterAppFactory().jsonTranslator(Hello.class);
        return new HelloRestResource(jsonTranslator);
    }

    /**
     * Definitely do not have your keys in code :)
     * This key should be vaulted. It's in plaintext here for ease of use.
     *
     * @return A Symmetric Key used to encrypt the session.
     */
    public SymmetricKey encKey() {
        return new SymmetricKey(
                Optional.of("key-2"),
                "MMNj8rE5m7NIDhwKYDmHSnlU1wfKuVvW6G--GKPYkRA",
                Use.ENCRYPTION
        );
    }

    /**
     * Definitely do not have your keys in code :)
     * This key should be vaulted. It's in plaintext here for ease of use.
     *
     * @return A Symmetric Key used to sign CSRF tokens.
     */
    public SymmetricKey signKey() {
        return new SymmetricKey(
                Optional.of("key-1"),
                "AyM1SysPpbyDfgZld3umj1qzKObwVMkoqQ-EstJQLr_T-1qS0gZH75aKtMN3Yj0iPS4hcgUuTwjAzZr1Z9CAow",
                Use.SIGNATURE
        );
    }

    public SessionBefore sessionBefore(String cookieName, SymmetricKey preferredKey, Map<String, SymmetricKey> rotationKeys) {
        return new SessionBefore(cookieName, otterAppFactory().jwtAppFactory(), preferredKey, rotationKeys, otterAppFactory().objectReader());
    }

    public EncryptSession<T> encryptSession(CookieConfig sessionCookieConfig, SymmetricKey encKey) {
        return new EncryptSession<T>(sessionCookieConfig, encKey, otterAppFactory().objectWriter());
    }
}
