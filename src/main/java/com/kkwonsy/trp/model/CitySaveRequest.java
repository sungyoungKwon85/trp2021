package com.kkwonsy.trp.model;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;

@Getter
public class CitySaveRequest {

    @NotEmpty
    private String name;

    public CitySaveRequest(String name) {
        this.name = name;
    }
}
