package com.artwook.nft.nftshop.common;

public class LoginUserContext {

    private static ThreadLocal<UserSession> currentUser = new ThreadLocal<UserSession>();

    public static UserSession getCurrentUser() {
        return currentUser.get();
    }

    public static void setCurrentUser(UserSession aUser) {
        currentUser.set(aUser);
    }

    public static void clean() {
        currentUser.remove();
    }
}
