package com.mservicetech.campsite.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Client  {

    private Long id;
    private String name;
    private String email;

    public Client () {
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Client Client = (Client) o;

        return Objects.equals(id, Client.id) &&
               Objects.equals(name, Client.name) &&
               Objects.equals(email, Client.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Client {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");        sb.append("    name: ").append(toIndentedString(name)).append("\n");        sb.append("    email: ").append(toIndentedString(email)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
