package com.example.prjagannath.castus.API;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.example.prjagannath.castus.Core;
import com.example.prjagannath.castus.CustomEnum.API;
import com.example.prjagannath.castus.CustomEnum.DialogType;
import com.example.prjagannath.castus.CustomEnum.Token;
import com.example.prjagannath.castus.CustomUI.ConfirmDialog;
import com.example.prjagannath.castus.SharedPreference;
import com.example.prjagannath.castus.src.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by prjagannath on 9/2/2016.
 */
public class APICall {
    private final String ERROR_TOAST = "ERROR_TOAST";
    private final String ERROR_DIALOG = "ERROR_DIALOG";
    private final String DUPLICATE_LOGIN = "403_";
    private final String SEPARATOR = "<@>";
    private Activity activity;
    private Context context;
    private static final String BaseURL = "http://castus.herokuapp.com/";

    public APICall(Activity activity) {
        activity = activity;
    }

    public APICall(Context context) {
        context = context;
    }

    public String processString(String respond) {
        return noError(respond)?respond:"";
    }

    public boolean isRequestSuccess(String respond, boolean showError) {
        logDebug(respond);
        return !showError?respond.equalsIgnoreCase("ok"):noError(respond) && !respond.isEmpty();
    }

    public boolean isRequestSuccessSilent(String respond) {
        return !respond.isEmpty();
    }

    public JSONObject convertToJsonObject(String respond) {
        try {
                return new JSONObject(respond);
        } catch (JSONException var3) {
            var3.printStackTrace();
        }

        return new JSONObject();
    }

    public JSONArray convertToJsonArray(String respond) {
        try {
            if(noError(respond)) {
                return new JSONArray(respond);
            }
        } catch (JSONException var3) {
            var3.printStackTrace();
        }

        return new JSONArray();
    }

    public String request(API action, String api_id, JSONObject object_to_send) {
        return request(action, api_id, (String)null, object_to_send);
    }

    public String request(API action, String api_id, String target) {
        return request(action, api_id, target, (JSONObject)null);
    }

    public String request(API action, String api_id, String target, JSONObject object_to_send) {
        String inputLine = "";

            String url = BaseURL + api_id;
            Log.d("TEST_URL",url );
            try {
                URL e = new URL(url);
                HttpURLConnection connection = (HttpURLConnection)e.openConnection();
                connection.setRequestMethod(action.toString());
                connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                connection.setReadTimeout(5000);
                connection.setConnectTimeout(5000);
                String responseCode;

                if(action != API.GET && action != API.DELETE) {
                    responseCode = object_to_send == null?"":object_to_send.toString();
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    BufferedOutputStream reader1 = new BufferedOutputStream(connection.getOutputStream());
                    reader1.write(responseCode.getBytes());
                    reader1.flush();
                    reader1.close();
                }

                int responseCode1 = connection.getResponseCode();
                logError(action.toString() + " URL : " + url + "\nResponse Code : " + responseCode1);
                switch(responseCode1) {
                    case 200:
                        BufferedReader reader2 = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                        do {
                            inputLine = reader2.readLine();
                            if(inputLine == null) {
                                return "OK";
                            }
                        } while(inputLine.isEmpty());

                        reader2.close();
                        break;
                    case 403:
                        Core.getAppInfo().setAccessAllow(false);
                        inputLine = ERROR_DIALOG + DUPLICATE_LOGIN+ "Duplicate Login" + connection.getResponseMessage();
                        break;
                    default:
                        inputLine = "ERROR_TOAST" + connection.getResponseMessage();
                }
            } catch (ProtocolException | MalformedURLException var15) {
                var15.printStackTrace();
            } catch (IOException var16) {
                var16.printStackTrace();
                inputLine = ERROR_TOAST + "Internet Access is unavilable. Pease retry or switch ...";
            }

            logError("inputLine \n" + inputLine);
            return inputLine;

    }

    public String Call(API action, int api_id, String parameters) {
        String url = getString(api_id);
        String inputLine = "";

        try {
            URL e = new URL(url);
            HttpURLConnection connection = (HttpURLConnection)e.openConnection();
            connection.setRequestMethod(action.toString());
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            dos.writeBytes(parameters);
            dos.flush();
            dos.close();
            int responseCode = connection.getResponseCode();
            if(responseCode == connection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                inputLine = reader.readLine();
                reader.close();
            } else {
                showToast(connection.getResponseMessage());
            }
        } catch (Exception var11) {
            var11.printStackTrace();
        }

        return inputLine;
    }

    public void logInfo(String message) {
        Log.i(getTAG(), message);
    }

    public void logDebug(String message) {
        Log.d(getTAG(), message);
    }

    public void logError(String message) {
        Log.e(getTAG(), message);
    }

    String getTAG() {
        return "Your Mom";
    }

    private String getFinalUrl(int api, String target) {
        String base_url = AppInfo.getBaseURL();
        if(api != 0){
            if(target == null)
                return base_url + getString(api);
            else
                return base_url + getString(api) + target;
        }
        return target;
    }
    private boolean noError(String respond) {
        if(!respond.isEmpty()) {
            if(respond.contains(ERROR_TOAST)) {
                showToast(respond.replace(ERROR_TOAST, ""));
            } else {
                if(!respond.contains(ERROR_DIALOG)) {
                    return true;
                }

                checkError(respond.replace(ERROR_DIALOG, ""));
            }
        }

        return false;
    }

    private void checkError(String result) {
        String[] msg = result.split("<@>");
        String title = msg[0];
        String content = msg[1];
        if(title.contains("403_")) {
            final AppInfo appInfo = Core.getAppInfo();
            title = title.replace("403_", "");
            if(!SharedPreference.getToken(activity, Token.FB).isEmpty()) {
               //TODO Log out
            }

            SharedPreference.clearToken(activity);
            logError("msg: " + content);
            ConfirmDialog.build(activity, title, content, DialogType.ALERT).setNegativeButton("Report", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    appInfo.setAccessAllow(true);
                    String bodyHead = "Your account has been logged in from another device.\\nTo retrieve your account, please tell us your:\\n\\nNickname:\\nPhone number:\\nBirthday:</string>";
                    String body = bodyHead + "\\n\\nAdditional information required by the support team." + appInfo.getDeviceManufacturer() + '-' + AppInfo.getDeviceModel() + ',' + "System Version:" + appInfo.getAndroidVersion();
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("plain/text");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"pratyum96@gmail.com"});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Retriving lost Info");
                    intent.putExtra(Intent.EXTRA_TEXT, body);
                    intent = Intent.createChooser(intent, "");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(intent);
                    activity.finish();
                }
            }).setPositiveButton("Login Again", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    appInfo.setAccessAllow(true);
                    dialog.dismiss();
                    activity.startActivity((new Intent(activity, LoginActivity.class)).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    activity.finish();
                }
            }).setCancelable(false).show();
        } else {
            showToast(result);
        }

    }

    private void showToast(final String msg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast customToast = Toast.makeText(activity, msg, Toast.LENGTH_LONG);
                customToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                customToast.show();
            }
        });
    }

    private String getString(int msgId, Object... args) {
        return activity == null?context.getString(msgId, args):activity.getString(msgId, args);
    }


}
