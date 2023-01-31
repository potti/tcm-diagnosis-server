package com.artwook.nft.nftshop.vo;

import com.artwook.nft.nftshop.common.BaseResponseCode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResponseMessage<T> {

    private int statusCode;
    private T data;
    private String message;

    public BaseResponseMessage(T data) {
        this.statusCode = 1;
        this.data = data;
    }

    public BaseResponseMessage(int code, String msg) {
        this.statusCode = code;
        this.message = msg;
    }

    public BaseResponseMessage(BaseResponseCode responseCode) {
        this.statusCode = responseCode.getCode();
        this.message = responseCode.getValue();
    }
}
