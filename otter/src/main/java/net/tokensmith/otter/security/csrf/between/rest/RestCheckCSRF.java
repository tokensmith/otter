package net.tokensmith.otter.security.csrf.between.rest;


import net.tokensmith.otter.controller.entity.Cookie;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.dispatch.entity.RestBtwnRequest;
import net.tokensmith.otter.dispatch.entity.RestBtwnResponse;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.router.exception.CsrfException;
import net.tokensmith.otter.router.exception.HaltException;
import net.tokensmith.otter.security.csrf.DoubleSubmitCSRF;


public class RestCheckCSRF<S, U> implements RestBetween<S, U> {
    private String cookieName;
    private String headerName;
    private DoubleSubmitCSRF doubleSubmitCSRF;
    private static String HALT_MSG = "CSRF failed.";
    private StatusCode failStatusCode;

    public RestCheckCSRF(String cookieName, String headerName, DoubleSubmitCSRF doubleSubmitCSRF, StatusCode failStatusCode) {
        this.cookieName = cookieName;
        this.headerName = headerName;
        this.doubleSubmitCSRF = doubleSubmitCSRF;
        this.failStatusCode = failStatusCode;
    }

    @Override
    public void process(Method method, RestBtwnRequest<S, U> request, RestBtwnResponse response) throws HaltException {
        Boolean ok;
        String headerValue = request.getHeaders().get(headerName);
        Cookie csrfCookie = request.getCookies().get(cookieName);
        if ( csrfCookie != null && headerValue != null) {
            ok = doubleSubmitCSRF.doTokensMatch(csrfCookie.getValue(), headerValue);
        } else {
            ok = false;
        }

        if(!ok) {
            CsrfException haltException = new CsrfException(HALT_MSG);
            onHalt(haltException, response);
            throw haltException;
        }
    }

    protected void onHalt(HaltException e, RestBtwnResponse response) {
        response.setStatusCode(failStatusCode);
    }

    public String getCookieName() {
        return cookieName;
    }

    public String getHeaderName() {
        return headerName;
    }

    public StatusCode getFailStatusCode() {
        return failStatusCode;
    }
}
