package com.example.demo.dto.gaode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author lz
 */
@AllArgsConstructor
@NoArgsConstructor
public  class AddressComponent {
    private String city;
    private String province;
    private String adcode;
    private String district;
    private String towncode;
    private StreetNumber streetNumber;
    private String country;
    private String township;
    private List<BusinessArea> businessAreas;
    private Building building;
    private Neighborhood neighborhood;
    private String citycode;


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTowncode() {
        return towncode;
    }

    public void setTowncode(String towncode) {
        this.towncode = towncode;
    }

    public StreetNumber getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(StreetNumber streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTownship() {
        return township;
    }

    public void setTownship(String township) {
        this.township = township;
    }

    public List<BusinessArea> getBusinessAreas() {
        return businessAreas;
    }

    public void setBusinessAreas(List<BusinessArea> businessAreas) {
        this.businessAreas = businessAreas;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public Neighborhood getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(Neighborhood neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }
}