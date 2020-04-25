package com.appsnipp.profiledesigns.AdaptorClasses;

import java.io.Serializable;

public class OrderAddress implements Serializable {


    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPostalCode() {
        return PostalCode;
    }

    public void setPostalCode(String postalCode) {
        PostalCode = postalCode;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    private String  Address;
    private String PostalCode;
    private String Phone;
}
