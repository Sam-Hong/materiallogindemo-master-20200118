package com.sourcey.materiallogindemo;

import android.app.Application;

public class GlobalVariable extends Application {
    private static boolean LoginToken;
    private static String Authorization;
    private static String Identification;

    public void setAuthorization(String authorization) {
        Authorization = authorization;
    }

    public String getAuthorization() {
        return Authorization;
    }

    public void setIdentification(String identification) {
        Identification = identification;
    }

    public String getIdentification() {
        return Identification;
    }

    public void setLoginToken(boolean Logintoken) {
        LoginToken = Logintoken;
    }

    public boolean getLoginToken() {
        return LoginToken;
    }
}