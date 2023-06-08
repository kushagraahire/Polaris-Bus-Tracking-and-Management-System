package com.company.polarisdriver;

public class BusModel {
    public String busno, busroute, driverid, stops, routeKey;

    public BusModel(){

    }

    public BusModel(String busno, String busroute, String driverid, String stops, String routeKey) {
        this.busno = busno;
        this.busroute = busroute;
        this.driverid = driverid;
        this.stops = stops;
        this.routeKey = routeKey;
    }
}
