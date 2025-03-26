package com.example.demo.dto;

import com.example.demo.dto.gaode.Regeocode;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GeocodeResponse implements Serializable {
    @JsonProperty("status")
    private int status;
    @JsonProperty("regeocode")
    private Regeocode regeocode;
    @JsonProperty("info")
    private String info;
    @JsonProperty("infocode")
    private String infocode;


}