package org.rootservices.hello.controller.api.model;

import org.rootservices.otter.controller.entity.DefaultUser;

import java.util.UUID;

public class ApiUser extends DefaultUser {
    private UUID id;
    private String firstName;
    private String lastName;

    public ApiUser(UUID id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
