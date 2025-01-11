package com.onlinebetting.web.enums;

public enum MethodType {

    POST("POST"),
    GET("GET");

    final String text;

    MethodType(String text) {
        this.text = text;
    }

    public static MethodType getMethodType(String text) {
        for (MethodType type : MethodType.values()) {
            if(type.text.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("could not find type value...");
    }
}
