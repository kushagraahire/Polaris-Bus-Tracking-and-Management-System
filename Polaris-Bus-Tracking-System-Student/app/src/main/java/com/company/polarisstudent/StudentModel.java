package com.company.polarisstudent;

public class StudentModel {

    public String name,email,enroll,imgurl,busstop;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEnroll() {
        return enroll;
    }

    public void setEnroll(String enroll) {
        this.enroll = enroll;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getBusstop() {
        return busstop;
    }

    public void setBusstop(String busstop) {
        this.busstop = busstop;
    }

    public StudentModel(String name, String email, String enroll, String imgurl, String busstop) {
        this.name = name;
        this.email = email;
        this.enroll = enroll;
        this.imgurl = imgurl;
        this.busstop = busstop;
    }

    public StudentModel(){}


}
