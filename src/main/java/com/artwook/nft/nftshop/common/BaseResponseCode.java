package com.artwook.nft.nftshop.common;

public enum BaseResponseCode {

    ERROR(-1, "System exception, please operate later"),
    SUCCESS_CODE(1, "success"),
    ERROR_USER_NOTFOUND(990, "user not found"),
    ERROR_TOKENFAILURE(999, "Token verification failed"),
    ERROR_INVALIDREQUEST(1001, "Invalid request"),
    ERROR_PARAMEMPTY(1002, "Exception, parameter {0} is empty"),
    ERROR_PARAMILLEGAL(1003, "Exception, parameter {0} is illegal"),
    ERROR_UPDATEFAIL(1004, "Data modification failed, resubmit[{0}]"),
    ERROR_INSERTFAIL(1005, "Data insertion failed, resubmit[{0}]"),
    ERROR_RECORDNOTFOUND(1008, "Record does not exist[{0}]"),
    ERROR_RECORDEXISTED(1009, "Record already exists[{0}]"),
    ERROR_TRYAGAIN(1010, "request try again"),
    ERROR_DATANOTFOUND(1011, "file not found[{0}]"),
    ERROR_CALLREMOTESERVICE(1012, "Remote call failed,Service[{0}]"),
    ERROR_CONFIGNOTEXISTED(1013, "config is not exist {0}"),
    ERROR_DATANOTONCHAIN(1014, "data saving {0}"),

    ERROR_NOT_ENOUGH_STOCK(1100, "not enough stock"),

    ERROR_NOT_LOGIN(301, "not login"),
    ;

    private int code;
    private String value;

    private BaseResponseCode(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public static BaseResponseCode getErrorCode(int code) {
        for (BaseResponseCode errorCode : values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
