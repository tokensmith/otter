package org.rootservices.otter.security.csrf.between;


import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.security.csrf.DoubleSubmitCSRF;

public class CheckCSRF implements Between {
    private String cookieName;
    private String formFieldName;
    private DoubleSubmitCSRF doubleSubmitCSRF;

    public CheckCSRF(DoubleSubmitCSRF doubleSubmitCSRF) {
        this.doubleSubmitCSRF = doubleSubmitCSRF;
    }

    public CheckCSRF(String cookieName, String formFieldName, DoubleSubmitCSRF doubleSubmitCSRF) {
        this.cookieName = cookieName;
        this.formFieldName = formFieldName;
        this.doubleSubmitCSRF = doubleSubmitCSRF;
    }

    @Override
    public Boolean process(Method method, Request request, Response response) {
        Boolean ok;
        Cookie csrfCookie = request.getCookies().get(cookieName);
        String formValue = request.getFormData().get(formFieldName);
        if ( csrfCookie != null && formValue != null) {
            ok = doubleSubmitCSRF.doTokensMatch(csrfCookie.getValue(), formValue);
        } else {
            ok = false;
        }

        if(!ok) {
            response.setStatusCode(StatusCode.FORBIDDEN);
        }
        return ok;
    }

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