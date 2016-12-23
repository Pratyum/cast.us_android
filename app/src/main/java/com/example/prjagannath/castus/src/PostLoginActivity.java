package com.example.prjagannath.castus.src;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.widget.TextView;

import com.example.prjagannath.castus.API.APICall;
import com.example.prjagannath.castus.CustomEnum.API;

import net.ossrs.yasea.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PostLoginActivity extends AppCompatActivity {

    private TextView info;
    private String query;
    private String stream_url , secure_stream_url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_login);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2VideoFragment.newInstance())
                    .commit();
        }

        String friends = getIntent().getStringExtra("friends");
        JSONObject json_friends;
        ArrayList<Pair<String,String>> friends_list_array = new ArrayList<>();
        try {
             json_friends = new JSONObject(friends);
            JSONArray friends_list = json_friends.getJSONArray("data");
            for (int i=0;i<friends_list.length();++i){
                friends_list_array.add(new Pair<String, String>(friends_list.getJSONObject(i).getString("name"),friends_list.getJSONObject(i).getString("id")));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String fb_id = getIntent().getStringExtra("fb_id");
        String access_token = getIntent().getStringExtra("access_token");
        Log.d("TAG", "onCreate: "+ friends);
        query = "fb_id="+fb_id+"&access_token="+access_token;


        new HealthCheckTask().execute();
    }



    class HealthCheckTask extends AsyncTask<Void, Void, String> {

        APICall apiCall = new APICall(getBaseContext());

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
                Log.d(getClass().getSimpleName(), "onPostExecute: ");
                apiCall.logDebug("String is " + s);
                JSONObject json = apiCall.convertToJsonObject(s);
                try {
                    stream_url = json.getString("stream_url");
                    secure_stream_url = json.getString("secure_stream_url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            Log.d(getClass().getSimpleName(), "onPostExecute: "+ stream_url);
            Log.d(getClass().getSimpleName(), "onPostExecute: "+ secure_stream_url);
        }

        @Override
        protected String doInBackground(Void... params) {

            return apiCall.request(API.GET,"create?"+query, null, null);
        }
    }
}
