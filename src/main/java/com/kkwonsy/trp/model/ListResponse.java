package com.kkwonsy.trp.model;

import java.util.List;

import lombok.Getter;

@Getter
public class ListResponse<T> extends ApiResponse {

    private List<T> payload;

    public ListResponse(List<T> payload) {
        this.payload = payload;
        this.success();
    }
}
