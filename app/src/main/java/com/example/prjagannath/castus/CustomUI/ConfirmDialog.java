package com.example.prjagannath.castus.CustomUI;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import com.example.prjagannath.castus.CustomEnum.DialogType;

/**
 * Created by prjagannath on 9/2/2016.
 */
public class ConfirmDialog {

    private ConfirmDialog(){}

    public static android.app.AlertDialog.Builder build(final Context activity, String title, String msg, DialogType type){
        return new android.app.AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(msg)
                .setIcon(getType(type));
    }

    public static void showOK(final Activity activity, final String title, final String msg, final DialogType type){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                build(activity, title, msg, type)
                        .setPositiveButton(android.R.string.ok, dismissOnClick())
                        .show();
            }
        });
    }

    public static void showOK(final Context context, final String title, final String msg, final DialogType type){
        build(context, title, msg, type)
                .setPositiveButton(android.R.string.ok, dismissOnClick())
                .show();
    }

    public static DialogInterface.OnClickListener dismissOnClick(){
        return new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };
    }

    static int getType(DialogType type){
        switch (type){
            case ALERT:
                return android.R.drawable.ic_dialog_alert;
            case INFO:
            default:
                return android.R.drawable.ic_dialog_info;
        }
    }
}
