package com.artwook.nft.nftshop.common;

import com.artwook.nft.nftshop.db.model.User;

public class UserSession extends User {
    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

}
