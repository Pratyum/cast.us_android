package com.example.prjagannath.castus.CustomEnum;

/**
 * Created by prjagannath on 9/2/2016.
 */
public enum Language {
    NONE("Undefine"),
    EN("English"),
    CN("华语");

    private final String text;

    private Language(String text) {
        this.text = text;
    }

    public String toString() {
        return this.text;
    }

    public static Language fromString(String text) {
        if(text != null) {
            Language[] arr$ = values();
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                Language c = arr$[i$];
                if(text.equalsIgnoreCase(c.text)) {
                    return c;
                }
            }
        }

        return EN;
    }
}