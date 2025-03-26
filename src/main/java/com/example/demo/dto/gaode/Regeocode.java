package com.example.demo.dto.gaode;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

// Getters and Setters
@AllArgsConstructor
@NoArgsConstructor
public  class Regeocode implements Serializable {
    private List<Road> roads;
    private List<Object> roadinters; // 根据数据，这里可能是空数组
    @JsonProperty("formatted_address")
    private String formatted_address;
    @JsonProperty("addressComponent")
    private AddressComponent addressComponent;
    private List<Object> aois; // 根据数据，这里可能是空数组
    private List<Poi> pois;


    public List<Road> getRoads() {
        return roads;
    }

    public void setRoads(List<Road> roads) {
        this.roads = roads;
    }

    public List<Object> getRoadinters() {
        return roadinters;
    }

    public void setRoadinters(List<Object> roadinters) {
        this.roadinters = roadinters;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public AddressComponent getAddressComponent() {
        return addressComponent;
    }

    public void setAddressComponent(AddressComponent addressComponent) {
        this.addressComponent = addressComponent;
    }

    public List<Object> getAois() {
        return aois;
    }

    public void setAois(List<Object> aois) {
        this.aois = aois;
    }

    public List<Poi> getPois() {
        return pois;
    }

    public void setPois(List<Poi> pois) {
        this.pois = pois;
    }
}
