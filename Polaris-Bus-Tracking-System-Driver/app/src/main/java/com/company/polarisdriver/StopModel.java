package com.company.polarisdriver;

public class StopModel {
    double latitude, longitude;
    String stopName;

    StopModel(){}

    public StopModel(double latitude, double longitude, String stopName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.stopName = stopName;
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

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }
}
