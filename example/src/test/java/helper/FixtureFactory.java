package helper;


import io.netty.handler.codec.http.cookie.DefaultCookie;
import org.rootservices.jwt.config.JwtAppFactory;


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
}
