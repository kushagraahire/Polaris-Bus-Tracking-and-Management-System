package com.company.polaris_bustrackingsystem.Models;

public class RouteModel {
    String routeName, routeNo;

    public RouteModel() {}
    public RouteModel(String routeName, String routeNo) {
        this.routeName = routeName;
        this.routeNo = routeNo;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getRouteNo() {
        return routeNo;
    }

    public void setRouteNo(String routeNo) {
        this.routeNo = routeNo;
    }
}
