package com.mservicetech.campsite.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Reservation  {

    private String id;
    private Client client;
    private java.time.LocalDate arrival;
    private java.time.LocalDate departure;

    public Reservation () {
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("client")
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @JsonProperty("arrival")
    public java.time.LocalDate getArrival() {
        return arrival;
    }

    public void setArrival(java.time.LocalDate arrival) {
        this.arrival = arrival;
    }

    @JsonProperty("departure")
    public java.time.LocalDate getDeparture() {
        return departure;
    }

    public void setDeparture(java.time.LocalDate departure) {
        this.departure = departure;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Reservation Reservation = (Reservation) o;

        return Objects.equals(id, Reservation.id) &&
               Objects.equals(client, Reservation.client) &&
               Objects.equals(arrival, Reservation.arrival) &&
               Objects.equals(departure, Reservation.departure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, client, arrival, departure);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Reservation {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");        sb.append("    client: ").append(toIndentedString(client)).append("\n");        sb.append("    arrival: ").append(toIndentedString(arrival)).append("\n");        sb.append("    departure: ").append(toIndentedString(departure)).append("\n");
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
