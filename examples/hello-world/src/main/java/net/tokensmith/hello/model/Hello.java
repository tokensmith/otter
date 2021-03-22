package net.tokensmith.hello.model;


import jakarta.validation.constraints.NotNull;

public class Hello {
    @NotNull
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
