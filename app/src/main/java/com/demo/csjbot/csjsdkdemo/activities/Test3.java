package com.demo.csjbot.csjsdkdemo.activities;


import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.csjbot.cosclient.constant.ClientConstant;
import com.csjbot.cosclient.entity.CommonPacket;
import com.csjbot.cosclient.entity.MessagePacket;
import com.csjbot.cosclient.listener.ClientEvent;
import com.csjbot.cosclient.listener.EventListener;
import com.demo.csjbot.csjsdkdemo.R;
import com.demo.csjbot.csjsdkdemo.services.RobotService;
import com.demo.csjbot.csjsdkdemo.utils.ErrorMessege;
import com.demo.csjbot.csjsdkdemo.utils.Request;

import org.json.JSONException;
import org.json.JSONObject;

public class Test2 extends AppCompatActivity {

    private static final String TAG = "Test1";
    private Context context;
    private RobotService robotService;
    private EventListener eventListener;
    private RobotService.OnEventFailedListener failedListener;
    private boolean isVoiceStarted = false;
    private TextView tvResult;

    private ServiceConnection robotConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            RobotService.RobotServiceBinder robotServiceBinder = (RobotService.RobotServiceBinder) service;
            robotService = robotServiceBinder.getService();
            robotService.connectRobot(eventListener, failedListener, 60002);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);

        context = Test2.this;

        init();

    }

    private void init() {

        tvResult = (TextView) findViewById(R.id.tvResult);

        final Button btnVoice = (Button) findViewById(R.id.btnVoiceRecognition);

        btnVoice.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (isVoiceStarted){
                    btnVoice.setText("Start Voice Recognition");
                    isVoiceStarted = false;
                    stopSpeechRecog();

                    return;
                }
                btnVoice.setText("Stop Voice Recognition");
                isVoiceStarted = true;
                startSpeechRecog();

            }
        });

        eventListener = new EventListener() {
            @Override
            public void onEvent(final ClientEvent event) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (event.eventType) {
                            case ClientConstant.EVENT_RECONNECTED:
                                Log.d(TAG, " EVENT_RECONNECTED");
                                break;
                            case ClientConstant.EVENT_CONNECT_SUCCESS:
                                Log.d(TAG, " EVENT_CONNECT_SUCCESS");
                                setLanguage();
                                break;
                            case ClientConstant.EVENT_CONNECT_FAILD:
                                Log.d(TAG, "EVENT_CONNECT_FAILD" + event.data);
                                break;
                            case ClientConstant.EVENT_CONNECT_TIME_OUT:
                                Log.d(TAG, "EVENT_CONNECT_TIME_OUT  " + event.data);
                                break;
                            case ClientConstant.SEND_FAILED:
                                showMessage(true,"Send Failed");
                                Log.d(TAG, "SEND_FAILED");
                                break;
                            case ClientConstant.EVENT_DISCONNET:
                                Log.d(TAG, "EVENT_DISCONNECT");
                                break;
                            case ClientConstant.EVENT_PACKET:
                                MessagePacket packet = (MessagePacket) event.data;
                                String json = ((CommonPacket) packet).getContentJson();
                                handleResponse(json);
                                break;
                            default:
                                break;
                        }
                    }
                });

            }
        };

        failedListener = new RobotService.OnEventFailedListener() {
            @Override
            public void onEventFailed(final String cause) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showMessage(true,cause);
                    }
                });
            }
        };


        Intent intent = new Intent(this, RobotService.class);
        bindService(intent, robotConnection, Context.BIND_AUTO_CREATE);


    }

    private void startSpeechRecog() {
        JSONObject object = new JSONObject();
        try {
            object.put("msg_id", Request.SPEECH_START_MULTI_RECOG_REQ);
            robotService.sendCommand(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void stopSpeechRecog() {
        JSONObject object = new JSONObject();
        try {
            object.put("msg_id", Request.SPEECH_STOP_MULTI_RECOG_REQ);
            robotService.sendCommand(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void setLanguage(){

        Log.e(TAG,"SET LANGUAGE >>>>>> ");

        if (robotService == null) {
            return;
        }
        JSONObject object = new JSONObject();
        try {
            object.put("msg_id", Request.SPEECH_SETTING_LANG_REQ);
            object.put("local_type", "en_us");
            robotService.sendCommand(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void handleResponse(String json) {
        try {
            JSONObject object = new JSONObject(json);

            int errorCode = 0;

            if (object.has("error_code")) {
                errorCode = object.getInt("error_code");
            }

            String event = object.getString("msg_id");

            Log.d(TAG,"RES >> "+object.toString());

            if (errorCode == 0) {

                switch (event) {

                    case Request.DANCE_START_RSP:
                        showMessage(false,"Dance Start!");

                        break;
                    case Request.DANCE_STOP_RSP:
                        showMessage(false,"Dance Stop!");
                        break;

                    case Request.SPEECH_START_MULTI_RECOG_RSP:
                        showMessage(false,"Speech Start Recognition");
                        break;

                    case Request.SPEECH_STOP_MULTI_RECOG_RSP:
                        showMessage(false,"Speech Stop Recognition");
                        break;
                    case Request.FACE_RECOG_NOTIFICATION:
//                        if (voiceRecogStart){
//                            Log.e(TAG,"SEND VOICE RECOG");
//                            startSpeechRecog();
//                            voiceRecogStart = false;
//                        }
                        break;

                    case Request.SPEECH_TO_TEXT_NOTIFICATION:
                        Log.d(TAG, json);
                        try {
                            String text = object.getString("text");
                            Log.e(TAG,"SPEECH_TO_TEXT_NOTIFICATION >> "+text);
                            showMessage(false,text);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }

            } else {
                showMessage(true,event + "\n" + ErrorMessege.getErrorMsg(errorCode));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMessage(boolean isError,final String msg) {

        if (isError){
            Log.e(TAG, "error: >> " + msg);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvResult.setText(msg);
            }
        });
    }



}
