package com.sourcey.materiallogindemo;

import android.app.Application;

public class GlobalVariable extends Application {
    private static boolean LoginToken;
    private static String Authorization;

    public void setAuthorization(String authorization) {
        this.Authorization = authorization;
    }

    public String getAuthorization() {
        return Authorization;
    }

    public void setLoginToken(boolean LoginToken) {
        this.LoginToken = LoginToken;
    }

    public boolean getLoginToken() {
        return LoginToken;
    }
}