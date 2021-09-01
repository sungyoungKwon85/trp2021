package com.kkwonsy.trp.model;

import lombok.Getter;

@Getter
public class IdResponse {

    private Long id;

    public IdResponse(Long id) {
        this.id = id;
    }
}
