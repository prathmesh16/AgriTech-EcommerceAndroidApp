package com.appsnipp.profiledesigns.AdaptorClasses;

public class User {
    private String Name;
    private String Email;
    private String Phone;
    private String Address;
    private String DOB;
    private String Image;
    private String Status;


    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
    User()
    {
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone =  phone;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String addr) {
        Address = addr;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
