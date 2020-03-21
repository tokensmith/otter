package helper;


import io.netty.handler.codec.http.cookie.DefaultCookie;
import net.tokensmith.jwt.config.JwtAppFactory;


public class FixtureFactory {
    private static JwtAppFactory jwtAppFactory = new JwtAppFactory();

    /**
     * should domain be non null?
     * should http only be true?
     *
     * @return
     */
    public static io.netty.handler.codec.http.cookie.Cookie sessionCookie() {
        io.netty.handler.codec.http.cookie.Cookie sessionCookie = new DefaultCookie("session", "eyJhbGciOiJkaXIiLCJraWQiOiJrZXktMiIsImVuYyI6IkEyNTZHQ00ifQ..tk4SPH6W5Y9Vs-4CfhcMaBgrcfk8UrH67Hwfpq7qDgewDqd07k-d9ApCpuuNqFgkKMK_5KR06BNO3tfDo2lGg_kVB2-S1C-SvawOZB1Xrn0bRdU_oMDePJp-gMV_yDct.sYM50OOWl5-4VSCyLlsWWdi5kGszuPfF8El5EUSCywWUy67miDknFoVKVwVxCu3EijXCmGk1Ig.qIRxD-uPapS2Qhoh_fMCuA");
        sessionCookie.setHttpOnly(false);
        sessionCookie.setMaxAge(-9223372036854775808L);
        return sessionCookie;
    }

    public static io.netty.handler.codec.http.cookie.Cookie csrfCookie() {
        io.netty.handler.codec.http.cookie.Cookie sessionCookie = new DefaultCookie("csrfToken", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImtpZCI6ImtleS0xIn0.eyJjaGFsbGVuZ2VfdG9rZW4iOiJ2ZmprMHZxMzlwNGNiMTBnYWk5dmdhbDRsZSIsIm5vaXNlIjoidGJmNGNnczFkMGtsZjRnNHQ4cDZxdW0wZDYiLCJpYXQiOjE1ODQ4MjEyOTh9.HYSk5BBakR76LJiS69_rGb8uBPEmEPr0ZZwKOD3UAq4");
        sessionCookie.setHttpOnly(false);
        sessionCookie.setMaxAge(-9223372036854775808L);
        return sessionCookie;
    }

    public static String csrfHeader() {
        return "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImtpZCI6ImtleS0xIn0.eyJjaGFsbGVuZ2VfdG9rZW4iOiJ2ZmprMHZxMzlwNGNiMTBnYWk5dmdhbDRsZSIsIm5vaXNlIjoia3E3cTA0OHAwMDU0dnE0bDUzZTBuZ3RnMWgiLCJpYXQiOjE1ODQ4MjEyOTh9.Hgj_7stI3cVNedC8D8hlW9NPfNU0uEqZ9BVHtC5SqGg";
    }
}
