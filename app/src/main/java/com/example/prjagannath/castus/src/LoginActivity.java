package com.example.prjagannath.castus.src;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.prjagannath.castus.API.APICall;
import com.example.prjagannath.castus.API.MyFirebaseInstanceIDService;
import com.example.prjagannath.castus.CustomEnum.API;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.iid.FirebaseInstanceId;

import net.ossrs.yasea.MainActivity;
import net.ossrs.yasea.R;

import java.util.Arrays;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private Intent mIntent;
    private String query="";
    private String data;
    private int notificationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().getIntExtra("notificationId",1)==0 ) {
            notificationId =getIntent().getIntExtra("notificationId",1);
            data = getIntent().getStringExtra("data");
        }
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("user_videos", "user_friends"));
        loginButton.clearPermissions();
        loginButton.setPublishPermissions(Arrays.asList("publish_actions"));
        Log.d("TAG", "onCreate: ");
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                loginButton.setReadPermissions(Arrays.asList("user_videos"));
                MyFirebaseInstanceIDService myFirebaseInstanceIDService = new MyFirebaseInstanceIDService();
                myFirebaseInstanceIDService.onTokenRefresh();
//                Log.d("Token", FirebaseInstanceId.getInstance().getToken());

         }
        });


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                mIntent = new Intent(LoginActivity.this, MainActivity.class);
                mIntent.putExtra("userID", loginResult.getAccessToken().getUserId());
                mIntent.putExtra("token", loginResult.getAccessToken().getToken());
                Log.d("Facebook ID",loginResult.getAccessToken().getUserId());
                if(data !=null)
                {Log.d("data", "onSuccess: "+ data);
                    mIntent.putExtra("data",data);
                    mIntent.putExtra("notificationId",notificationId);
                }
                MyFirebaseInstanceIDService myFirebaseInstanceIDService = new MyFirebaseInstanceIDService();
                myFirebaseInstanceIDService.onTokenRefresh();
                query= "fb_id="+loginResult.getAccessToken().getUserId()+"&name="+"pratyum"+"&device_id="+FirebaseInstanceId.getInstance().getToken();
                Log.d("TAG", "onSuccess voila: " + query);

                new HealthCheckTask().execute(query);
                Log.d("Facebook Token",loginResult.getAccessToken().getToken());
                final String[] friendList = new String[1];
                GraphRequestAsyncTask graphRequestAsyncTask = new GraphRequest(
                        loginResult.getAccessToken(),
                        "/me/friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                try {
                                    Log.d("Your Mom!",response.getRawResponse());
                                    mIntent.putExtra("friends",response.getRawResponse());
                                    mIntent.putExtra("fb_id",loginResult.getAccessToken().getUserId());
                                    mIntent.putExtra("access_token", loginResult.getAccessToken().getToken());
                                    startActivity(mIntent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).executeAsync();

//                new GraphRequest(
//                        AccessToken.getCurrentAccessToken(),
//                        "/{friend-list-id}",
//                        null,
//                        HttpMethod.GET,
//                        new GraphRequest.Callback() {
//                            public void onCompleted(GraphResponse response) {
//                                System.out.println(response.getJSONObject().toString());
//                            }
//                        }
//                ).executeAsync();

//                info.setText(
//                        "User ID: "
//                                + loginResult.getAccessToken().getUserId()
//                                + "\n" +
//                                "Auth Token: "
//                                + loginResult.getAccessToken().getToken()
//                );
            }

            @Override
            public void onCancel() {
                //Do nothing
            }

            @Override
            public void onError(FacebookException e) {
//                info.setText("Login attempt failed.");
                AlertDialog.Builder alertBox = new AlertDialog.Builder(getApplicationContext());
                alertBox.setTitle("Login attempt failed");
                alertBox.setMessage("Please try again!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = alertBox.create();
                alertDialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    class HealthCheckTask extends AsyncTask<String, Void, String> {

        APICall apiCall = new APICall(getBaseContext());

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (apiCall.isRequestSuccess(s, true)) {
                Log.d(getClass().getSimpleName(), "onPostExecute: ");
                apiCall.logDebug("String is " + s);




            }
        }

        @Override
        protected String doInBackground(String... params) {
            for (int i=0;i<params.length;++i){
              Log.d("resigster ",params[i]+" + "+i);
            };
            Log.d("register " , query);
            return apiCall.request(API.GET,"register?"+query, null, null);
        }
    }



}

