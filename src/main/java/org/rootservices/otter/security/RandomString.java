package org.rootservices.otter.security;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by tommackenzie on 8/5/15.
 */
public class RandomString {

    private SecureRandom secureRandom = new SecureRandom();

    public RandomString() {}

    public RandomString(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }

    public String run() {
        return new BigInteger(130, secureRandom).toString(32);
    }
}