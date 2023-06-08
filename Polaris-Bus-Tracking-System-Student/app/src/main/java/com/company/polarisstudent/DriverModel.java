package com.company.polarisstudent;

public class DriverModel {
    String busId,driverID,email,imagurl,name,phone;

    DriverModel(){ }

    public DriverModel(String busId, String driverID, String email, String imagurl, String name, String phone) {
        this.busId = busId;
        this.driverID = driverID;
        this.email = email;
        this.imagurl = imagurl;
        this.name = name;
        this.phone = phone;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImagurl() {
        return imagurl;
    }

    public void setImagurl(String imagurl) {
        this.imagurl = imagurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
