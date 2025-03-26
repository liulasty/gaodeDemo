package com.example.demo.dto.gaode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public  class StreetNumber {
    private List<Object> number;
    private List<Object> direction;
    private List<Object> distance;
    private List<Object> street;


    public List<Object> getNumber() {
        return number;
    }

    public void setNumber(List<Object> number) {
        this.number = number;
    }

    public List<Object> getDirection() {
        return direction;
    }

    public void setDirection(List<Object> direction) {
        this.direction = direction;
    }

    public List<Object> getDistance() {
        return distance;
    }

    public void setDistance(List<Object> distance) {
        this.distance = distance;
    }

    public List<Object> getStreet() {
        return street;
    }

    public void setStreet(List<Object> street) {
        this.street = street;
    }
}
