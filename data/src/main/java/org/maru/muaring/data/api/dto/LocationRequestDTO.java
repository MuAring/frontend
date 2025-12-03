package org.maru.muaring.data.api.dto;

public class LocationRequestDTO {
    private double lat;
    private double lng;

    public LocationRequestDTO(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }
}
