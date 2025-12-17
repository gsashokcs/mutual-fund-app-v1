package com.mutualfund.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class AdminUserCreationRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "USER|ADMIN", message = "Role must be either USER or ADMIN", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String role;

    public AdminUserCreationRequest() {
    }

    public AdminUserCreationRequest(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdminUserCreationRequest that = (AdminUserCreationRequest) o;
        return Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, role);
    }

    @Override
    public String toString() {
        return "AdminUserCreationRequest{" + "username='" + username + '\'' + ", password='" + password + '\'' + ", role='" + role + '\'' + '}';
    }

    public static AdminUserCreationRequestBuilder builder() {
        return new AdminUserCreationRequestBuilder();
    }

    public static class AdminUserCreationRequestBuilder {
        private String username;
        private String password;
        private String role;

        AdminUserCreationRequestBuilder() {
        }

        public AdminUserCreationRequestBuilder username(String username) {
            this.username = username;
            return this;
        }

        public AdminUserCreationRequestBuilder password(String password) {
            this.password = password;
            return this;
        }

        public AdminUserCreationRequestBuilder role(String role) {
            this.role = role;
            return this;
        }

        public AdminUserCreationRequest build() {
            return new AdminUserCreationRequest(username, password, role);
        }
    }
}
