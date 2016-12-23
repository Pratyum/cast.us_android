package com.example.prjagannath.castus;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.prjagannath.castus.CustomEnum.Token;

/**
 * Created by prjagannath on 9/2/2016.
 */
public class SharedPreference {
    private static SharedPreferences.Editor editor;

    private SharedPreference(){}

    public static void setToken(final Context context, String tokenInput, Token token){
        editor = getSharedPreferences(context).edit();
        editor.putString(token.toString(), tokenInput);
        editor.apply();
    }

    public static String getToken(Context context, Token token){
        return getSharedPreferences(context).getString(token.toString(),"");
    }

    /**
     * remove a specific token
     * @param context applicationContext
     * @param token target token
     */
    public static void removeToken(final Context context, Token token){
        editor = getSharedPreferences(context).edit();
        editor.remove(token.toString());
        editor.apply();
    }

    /**
     * @deprecated ALERT! This will remove all sharedPreferences data!
     * @param context uiContext
     */
    public static void clearToken(Context context){
        editor = getSharedPreferences(context).edit();
        for(Token tokenName: Token.values())
            editor.remove(tokenName.toString());
        editor.apply();
    }

    private static SharedPreferences getSharedPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
