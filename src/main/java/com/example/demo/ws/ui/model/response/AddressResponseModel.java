package com.example.demo.ws.ui.model.response;

public class AddressResponseModel {

    private String addressId;
    private String city;
    private String country;
    private String streetName;
    private String postalCode;
    private String type;

    public AddressResponseModel() {
    }

    public AddressResponseModel(String city, String country, String streetName, String postalCode, String type) {
        this.city = city;
        this.country = country;
        this.streetName = streetName;
        this.postalCode = postalCode;
        this.type = type;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }
}
