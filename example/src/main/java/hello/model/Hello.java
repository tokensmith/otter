package hello.model;


import org.rootservices.otter.translatable.Translatable;

public class Hello implements Translatable {
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
