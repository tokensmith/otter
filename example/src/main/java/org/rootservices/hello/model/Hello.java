package org.rootservices.hello.model;



public class Hello {
    private String message;

    public Hello() {}

    public Hello(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
