package com.sourcey.materiallogindemo;

import android.app.Application;

public class GlobalVariable extends Application {
    private static boolean LoginToken;
    private static String Authorization;

    public void setAuthorization(String authorization) {
        Authorization = authorization;
    }

    public String getAuthorization() {
        return Authorization;
    }

    public void setLoginToken(boolean Logintoken) {
        LoginToken = Logintoken;
    }

    public boolean getLoginToken() {
        return LoginToken;
    }
}