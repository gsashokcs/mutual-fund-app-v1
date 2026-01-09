package com.mutualfund.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class UserRegistrationRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    public UserRegistrationRequest() {
    }

    public UserRegistrationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRegistrationRequest that = (UserRegistrationRequest) o;
        return Objects.equals(username, that.username) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }

    @Override
    public String toString() {
        return "UserRegistrationRequest{" + "username='" + username + '\'' + ", password='" + password + '\'' + '}';
    }

    public static UserRegistrationRequestBuilder builder() {
        return new UserRegistrationRequestBuilder();
    }

    public static class UserRegistrationRequestBuilder {
        private String username;
        private String password;

        UserRegistrationRequestBuilder() {
        }

        public UserRegistrationRequestBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserRegistrationRequestBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserRegistrationRequest build() {
            return new UserRegistrationRequest(username, password);
        }
    }
}
