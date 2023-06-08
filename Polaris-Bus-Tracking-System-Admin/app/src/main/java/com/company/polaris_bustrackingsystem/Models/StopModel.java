package com.company.polaris_bustrackingsystem.Models;

public class StopModel {
    private String stopName;
    private double latitude;
    private double longitude;

    public StopModel(String stopName, double latitude, double longitude) {
        this.stopName = stopName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public StopModel() {}

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }
}
