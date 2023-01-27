package com.zignsec.identification.enums;

public enum ZignSecEnvironment {
    DEV,
    TEST,
    PROD;

    public static String getBaseUrl(ZignSecEnvironment environment) {
        if (environment == ZignSecEnvironment.DEV) {
            return "https://dev-gateway.zignsec.com";
        }
        else if (environment == ZignSecEnvironment.TEST) {
            return "https://test-gateway.zignsec.com";
        } else {
            return "https://gateway.zignsec.com";
        }
    }
}
