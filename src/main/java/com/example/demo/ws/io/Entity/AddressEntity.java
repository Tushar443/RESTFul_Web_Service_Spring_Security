package com.example.demo.ws.io.Entity;

import com.example.demo.ws.shared.dto.UserDto;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
@Entity
@Table(name = "addresses")
public class AddressEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -543406475814093625L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(length = 30,nullable = false)
    private String addressId;
    @Column(length = 15,nullable = false)
    private String city;
    @Column(length = 15,nullable = false)
    private String country;
    @Column(length = 100,nullable = false)
    private String streetName;
    @Column(length = 7,nullable = false)
    private String postalCode;
    @Column(length = 10,nullable = false)
    private String type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userDetails;

    public AddressEntity() {
    }

    public AddressEntity(String addressId, String city, String country, String streetName, String postalCode, String type, UserEntity userDetails) {
        this.addressId = addressId;
        this.city = city;
        this.country = country;
        this.streetName = streetName;
        this.postalCode = postalCode;
        this.type = type;
        this.userDetails = userDetails;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
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

    public UserEntity getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserEntity userDetails) {
        this.userDetails = userDetails;
    }
}
