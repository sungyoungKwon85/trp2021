package com.kkwonsy.trp.model;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;

@Getter
public class MemberSaveRequest {

    @NotEmpty
    private String name;

    public MemberSaveRequest() {
    }

    public MemberSaveRequest(String name) {
        this.name = name;
    }
}
