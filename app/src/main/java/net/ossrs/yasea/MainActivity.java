package net.ossrs.yasea;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.prjagannath.castus.API.APICall;
import com.example.prjagannath.castus.CustomEnum.API;

import net.ossrs.yasea.rtmp.RtmpPublisher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends Activity {
    private static final String TAG = "Yasea";

    Button btnPublish = null;
    Button btnSwitchCamera = null;
    Button btnRecord = null;
    Button btnSwitchEncoder = null;

    private String mNotifyMsg;
    private SharedPreferences sp;
    private String rtmpUrl = "rtmp://rtmp-api.facebook.com:80/rtmp/10209743123796108?ds=1&a=AabSxRkvN3Pcit-G";
    private String recPath = Environment.getExternalStorageDirectory().getPath() + "/test.mp4";
    private String stream_url,secure_stream_url,videoId,query,fb_id,access_token;

    private SrsPublisher mPublisher = new SrsPublisher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String friends="";
        Log.d(TAG, "onCreate: ");
        if(getIntent().getStringExtra("friends")!=null)
            friends = getIntent().getStringExtra("friends");
        JSONObject json_friends;
        ArrayList<Pair<String,String>> friends_list_array = new ArrayList<>();
        try {
            json_friends = new JSONObject(friends);
            JSONArray friends_list = json_friends.getJSONArray("data");
            for (int i=0;i<friends_list.length();++i){
                friends_list_array.add(new Pair<String, String>(friends_list.getJSONObject(i).getString("name"),friends_list.getJSONObject(i).getString("id")));
            }
//            requestee_query = "requestee_fb_id=" + friends_list_array.get(0).second + "&fb_id=" + fb_id;

            Log.d("TAG","Friends found");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        fb_id = getIntent().getStringExtra("fb_id");
        access_token = getIntent().getStringExtra("access_token");
        Log.d("TAG", "onCreate: "+ friends);
        query = "fb_id="+fb_id+"&access_token="+access_token;

        if(getIntent().getIntExtra("notificationID", 1) == 0) {
            //TODO send to server of accepting
            Log.d("TAG","notificationId==0");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(getIntent().getIntExtra("notificationID", 0));
            //send to server of accepting
            String[] data = getIntent().getStringExtra("data").split("|");
            query ="live_video_id="+data[2]+"&fb_id="+fb_id;
            //TODO API CALL FOR ACCEPT REQUEST
            new AcceptRequest().execute();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        // response screen rotation event
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        // restore data.
//        sp = getSharedPreferences("Yasea", MODE_PRIVATE);
//        rtmpUrl = sp.getString("rtmpUrl", rtmpUrl);

//        // initialize url.
//        final EditText efu = (EditText) findViewById(R.id.url);
//        efu.setText(rtmpUrl);

        btnPublish = (Button) findViewById(R.id.publish);
        btnSwitchCamera = (Button) findViewById(R.id.swCam);
        btnRecord = (Button) findViewById(R.id.record);
        btnSwitchEncoder = (Button) findViewById(R.id.swEnc);

        mPublisher.setSurfaceView((SurfaceView) findViewById(R.id.preview));
//        mPublisher.setPreviewResolution(1280, 720);
//        mPublisher.setOutputResolution(384, 640);
//        mPublisher.setVideoHDMode();
//        mPublisher.startCamera();
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnPublish.getText().toString().contentEquals("publish")) {
//                    rtmpUrl = efu.getText().toString();
                    Log.i(TAG, String.format("RTMP URL changed to %s", rtmpUrl));
//                    SharedPreferences.Editor editor = sp.edit();
//                    editor.putString("rtmpUrl", rtmpUrl);
//                    editor.commit();

                    mPublisher.setPreviewResolution(1280, 720);
                    mPublisher.setOutputResolution(384, 640);
                    mPublisher.setVideoHDMode();
                    mPublisher.startPublish(rtmpUrl);

                    if (btnSwitchEncoder.getText().toString().contentEquals("soft enc")) {
                        Toast.makeText(getApplicationContext(), "Use hard encoder", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Use soft encoder", Toast.LENGTH_SHORT).show();
                    }
                    btnPublish.setText("stop");
                    btnSwitchEncoder.setEnabled(false);
                } else if (btnPublish.getText().toString().contentEquals("stop")) {
                    mPublisher.stopPublish();

                    btnPublish.setText("publish");
                    btnRecord.setText("record");
                    btnSwitchEncoder.setEnabled(true);
                }
            }
        });

        btnSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPublisher.getNumberOfCameras() > 0) {
                    mPublisher.switchCameraFace((mPublisher.getCamraId() + 1) % mPublisher.getNumberOfCameras());
                }
            }
        });

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnRecord.getText().toString().contentEquals("record")) {
                    mPublisher.startRecord(recPath);

                    btnRecord.setText("pause");
                } else if (btnRecord.getText().toString().contentEquals("pause")) {
                    mPublisher.pauseRecord();
                    btnRecord.setText("resume");
                } else if (btnRecord.getText().toString().contentEquals("resume")) {
                    mPublisher.resumeRecord();
                    btnRecord.setText("pause");
                }
            }
        });

        btnSwitchEncoder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnSwitchEncoder.getText().toString().contentEquals("soft enc")) {
                    mPublisher.swithToSoftEncoder();
                    btnSwitchEncoder.setText("hard enc");
                } else if (btnSwitchEncoder.getText().toString().contentEquals("hard enc")) {
                    mPublisher.swithToHardEncoder();
                    btnSwitchEncoder.setText("soft enc");
                }
            }
        });

        mPublisher.setPublishEventHandler(new RtmpPublisher.EventHandler() {
            @Override
            public void onRtmpConnecting(String msg) {
                mNotifyMsg = msg;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), mNotifyMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onRtmpConnected(String msg) {
                mNotifyMsg = msg;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), mNotifyMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onRtmpVideoStreaming(String msg) {
            }

            @Override
            public void onRtmpAudioStreaming(String msg) {
            }

            @Override
            public void onRtmpStopped(String msg) {
                mNotifyMsg = msg;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), mNotifyMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onRtmpDisconnected(String msg) {
                mNotifyMsg = msg;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), mNotifyMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onRtmpOutputFps(final double fps) {
                Log.i(TAG, String.format("Output Fps: %f", fps));
            }
        });

        mPublisher.setRecordEventHandler(new SrsMp4Muxer.EventHandler() {
            @Override
            public void onRecordPause(String msg) {
                mNotifyMsg = msg;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), mNotifyMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onRecordResume(String msg) {
                mNotifyMsg = msg;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), mNotifyMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onRecordStarted(String msg) {
                mNotifyMsg = "Recording file: " + msg;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), mNotifyMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onRecordFinished(String msg) {
                mNotifyMsg = "MP4 file saved: " + msg;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), mNotifyMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mPublisher.setNetworkEventHandler(new SrsEncoder.EventHandler() {
            @Override
            public void onNetworkResume(final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onNetworkWeak(final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                mNotifyMsg = ex.getMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), mNotifyMsg, Toast.LENGTH_LONG).show();
                        mPublisher.stopPublish();
                        mPublisher.stopRecord();
                        btnPublish.setText("publish");
                        btnRecord.setText("record");
                        btnSwitchEncoder.setEnabled(true);
                    }
                });
            }
        });
        new CreateLiveVideoTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        final Button btn = (Button) findViewById(R.id.publish);
        btn.setEnabled(true);
        mPublisher.resumeRecord();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPublisher.pauseRecord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPublisher.stopPublish();
        mPublisher.stopRecord();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mPublisher.setPreviewRotation(90);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mPublisher.setPreviewRotation(0);
        }
        mPublisher.stopEncode();
        mPublisher.stopRecord();
        btnRecord.setText("record");
        mPublisher.setScreenOrientation(newConfig.orientation);
        if (btnPublish.getText().toString().contentEquals("stop")) {
            mPublisher.startEncode();
        }
    }

    private static String getRandomAlphaString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    private static String getRandomAlphaDigitString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    class CreateLiveVideoTask extends AsyncTask<Void, Void, String> {

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
                videoId = json.getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("CREATE_VIDEO", "onPostExecute: "+ stream_url);
            Log.d("CREATE_VIDEO", "onPostExecute: " + secure_stream_url);
            Log.d("CREATE_VIDEO", "onPostExecute: " + videoId);

            rtmpUrl = stream_url;
            Log.d("TAG", "Chaging url to "+ rtmpUrl);
//            new RequestSwitchStreamTask().execute();

        }

        @Override
        protected String doInBackground(Void... params) {
            Log.d("TAG","Before GET Functions");
            return apiCall.request(API.GET,"create?"+query, null, null);
        }
    }


//    class RequestSwitchStreamTask extends AsyncTask<Void, Void, String> {
//        APICall apiCall = new APICall(getBaseContext());
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            Log.d(getClass().getSimpleName(), "onPostExecute: ");
//            apiCall.logDebug("String is " + s);
//            Log.d("request_switch", s);
//            Toast.makeText(ctx, "executing api call", Toast.LENGTH_SHORT);
//            firebaseDb.child("liveVideos/" + videoId + "/currentStreamer").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    Log.d("CHANGE_STREAM", "event triggered");
//                    if (!dataSnapshot.getValue().equals(fb_id)){
//                        // TODO: put code here to stop streaming when needed
//                        Log.d("CHANGE_STREAM", "changing stream to " + dataSnapshot.getValue());
//                    }
//                }
//
//                @Override
//                public void onCancelled(FirebaseError firebaseError) {
//
//                }
//            });
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//
//            return apiCall.request(API.GET,"request_switch?"+query, null, null);
//        }
//    }

    class AcceptRequest extends AsyncTask<Void, Void, String> {

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

            return apiCall.request(API.GET,"accept_request?"+query, null, null);
        }
    }
    
}