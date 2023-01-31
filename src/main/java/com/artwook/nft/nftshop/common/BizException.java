package com.artwook.nft.nftshop.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class BizException extends RuntimeException {

    private int code;
    private String msg;

    public BizException(BaseResponseCode rc) {
        super(rc.getValue());
        this.code = rc.getCode();
        this.msg = rc.getValue();
    }

    public BizException(BaseResponseCode rc, String msg) {
        super(msg);
        this.code = rc.getCode();
        this.msg = msg;
    }

    public BizException(BaseResponseCode rc, Throwable e) {
        super(e);
        this.code = rc.getCode();
        this.msg = e.getMessage();
    }
}
