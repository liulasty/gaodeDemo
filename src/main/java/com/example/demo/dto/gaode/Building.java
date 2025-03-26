package com.example.demo.dto.gaode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
public  class Building {
    private List<Object> name;
    private List<Object> type;

    public List<Object> getName() {
        return name;
    }

    public void setName(List<Object> name) {
        this.name = name;
    }

    public List<Object> getType() {
        return type;
    }

    public void setType(List<Object> type) {
        this.type = type;
    }

    // Getters and Setters
}
