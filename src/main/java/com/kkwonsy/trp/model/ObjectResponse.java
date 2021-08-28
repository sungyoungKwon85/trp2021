package com.kkwonsy.trp.model;

public class ObjectResponse<T> extends ApiResponse {

    private T payload;

    public ObjectResponse(T payload) {
        this.payload = payload;
        this.success();
    }
}
