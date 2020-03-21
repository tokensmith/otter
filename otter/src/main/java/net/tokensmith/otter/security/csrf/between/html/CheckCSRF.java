package net.tokensmith.otter.security.csrf.between.html;


import net.tokensmith.otter.controller.entity.Cookie;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.router.entity.between.Between;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.exception.CsrfException;
import net.tokensmith.otter.router.exception.HaltException;
import net.tokensmith.otter.security.csrf.DoubleSubmitCSRF;

import java.util.List;
import java.util.Optional;

public class CheckCSRF<S, U> implements Between<S, U> {
    private String cookieName;
    private String formFieldName;
    private DoubleSubmitCSRF doubleSubmitCSRF;
    private static String HALT_MSG = "CSRF failed.";
    private StatusCode failStatusCode;
    private Optional<String> failTemplate;

    public CheckCSRF(DoubleSubmitCSRF doubleSubmitCSRF) {
        this.doubleSubmitCSRF = doubleSubmitCSRF;
    }

    public CheckCSRF(String cookieName, String formFieldName, DoubleSubmitCSRF doubleSubmitCSRF, StatusCode failStatusCode, Optional<String> failTemplate) {
        this.cookieName = cookieName;
        this.formFieldName = formFieldName;
        this.doubleSubmitCSRF = doubleSubmitCSRF;
        this.failStatusCode = failStatusCode;
        this.failTemplate = failTemplate;
    }

    @Override
    public void process(Method method, Request<S, U> request, Response<S> response) throws HaltException {
        Boolean ok;
        Cookie csrfCookie = request.getCookies().get(cookieName);
        List<String> formValue = request.getFormData().get(formFieldName);
        if ( csrfCookie != null && formValue != null && formValue.size() == 1) {
            ok = doubleSubmitCSRF.doTokensMatch(csrfCookie.getValue(), formValue.get(0));
        } else {
            ok = false;
        }

        if(!ok) {
            CsrfException haltException = new CsrfException(HALT_MSG);
            onHalt(haltException, response);
            throw haltException;
        } else {
            request.setCsrfChallenge(Optional.of(formValue.get(0)));
        }
    }

    /**
     * This method will be called before a Halt Exception is thrown.
     * Override this method if you wish to change the behavior on the
     * response right before a Halt Exception is going to be thrown.
     * An Example would be, you may want to redirect the user to a login page.
     *
     * @param e a HaltException
     * @param response a Response
     */
    protected void onHalt(HaltException e, Response response) {
        response.setStatusCode(failStatusCode);
        response.setTemplate(failTemplate);
    }

    // used for tests to make sure it gets built correctly.
    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public String getFormFieldName() {
        return formFieldName;
    }

    public void setFormFieldName(String formFieldName) {
        this.formFieldName = formFieldName;
    }

    public DoubleSubmitCSRF getDoubleSubmitCSRF() {
        return doubleSubmitCSRF;
    }

    public void setDoubleSubmitCSRF(DoubleSubmitCSRF doubleSubmitCSRF) {
        this.doubleSubmitCSRF = doubleSubmitCSRF;
    }
}
