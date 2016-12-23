package com.example.prjagannath.castus.CustomEnum;

/**
 * Created by prjagannath on 9/2/2016.
 */

public enum Token {
    AIM("aim_token"),
    GCM("gcm_token"),
    PARSE("parse_token"),
    FB("fb_token"),
    LANGUAGE("language"),
    COUNTRY_CODE("country_code");

    private final String text;

    Token(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
