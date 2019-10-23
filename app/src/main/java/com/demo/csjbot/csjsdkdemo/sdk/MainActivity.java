package com.demo.csjbot.csjsdkdemo;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.csjbot.coshandler.core.Action;
import com.csjbot.coshandler.core.CsjRobot;
import com.csjbot.coshandler.core.Expression;
import com.csjbot.coshandler.core.Extra;
import com.csjbot.coshandler.core.Face;
import com.csjbot.coshandler.core.Speech;
import com.csjbot.coshandler.core.State;
import com.csjbot.coshandler.core.Version;
import com.csjbot.coshandler.global.REQConstants;
import com.csjbot.coshandler.listener.OnCameraListener;
import com.csjbot.coshandler.listener.OnDetectPersonListener;
import com.csjbot.coshandler.listener.OnDeviceInfoListener;
import com.csjbot.coshandler.listener.OnExpressionListener;
import com.csjbot.coshandler.listener.OnFaceListener;
import com.csjbot.coshandler.listener.OnFaceSaveListener;
import com.csjbot.coshandler.listener.OnGetAllFaceListener;
import com.csjbot.coshandler.listener.OnGetVersionListener;
import com.csjbot.coshandler.listener.OnGoRotationListener;
import com.csjbot.coshandler.listener.OnHeadTouchListener;
import com.csjbot.coshandler.listener.OnHotWordsListener;
import com.csjbot.coshandler.listener.OnNaviListener;
import com.csjbot.coshandler.listener.OnNaviSearchListener;
import com.csjbot.coshandler.listener.OnPositionListener;
import com.csjbot.coshandler.listener.OnRobotStateListener;
import com.csjbot.coshandler.listener.OnSNListener;
import com.csjbot.coshandler.listener.OnSnapshotoListener;
import com.csjbot.coshandler.listener.OnSpeakListener;
import com.csjbot.coshandler.listener.OnSpeechGetResultListener;
import com.csjbot.coshandler.listener.OnSpeechListener;
import com.csjbot.coshandler.listener.OnUpgradeListener;
import com.csjbot.coshandler.listener.OnWakeupListener;
import com.csjbot.coshandler.listener.OnWarningCheckSelfListener;
import com.csjbot.coshandler.log.CsjlogProxy;
import com.csjbot.coshandler.tts.ISpeechSpeak;
import com.google.gson.Gson;
import com.iflytek.cloud.SpeechError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    CsjRobot mCsjBot;

    RobotPose robotPose1;

    RobotPose robotPose2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCsjBot = CsjRobot.getInstance();

        // 每次开机连接机器人之后要加载地图
//        mCsjBot.getAction().loadMap();

        // 唤醒
        mCsjBot.registerWakeupListener(new OnWakeupListener() {
            @Override
            public void response(int i) {
                Log.d("TAG","registerWakeupListener:i:"+i);
                mCsjBot.getTts().startSpeaking("我在呢!",null);
                mCsjBot.getAction().moveAngle(i, new OnGoRotationListener() {
                    @Override
                    public void response(int i) {
                        if (i > 0 && i < 360) {
                            if (i <= 180) {
                                CsjlogProxy.getInstance().debug("向左转:+" + i);
                                if (mCsjBot.getState().getChargeState() == State.NOT_CHARGING) {
                                    mCsjBot.getAction().moveAngle(i,null);
                                }
                            } else {
                                CsjlogProxy.getInstance().debug("向右转:-" + (360 - i));
                                if (mCsjBot.getState().getChargeState() == State.NOT_CHARGING) {
                                    mCsjBot.getAction().moveAngle(-(360 - i),null);
                                }
                            }
                        }
                    }
                });
            }
        });

        // 语音识别
        mCsjBot.registerSpeechListener(new OnSpeechListener() {
            @Override
            public void speechInfo(String s, int i) {

                // 简单解析示例
                Log.d("TAG","registerSpeechListener:s:"+s);
                if(Speech.SPEECH_RECOGNITION_RESULT == i){ // 识别到的信息
                    try {
                        String text = new JSONObject(s).getString("text");
                        Toast.makeText(MainActivity.this, "识别到的文本:"+text, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if(Speech.SPEECH_RECOGNITION_AND_ANSWER_RESULT == i){// 识别到的信息与的回答
                    try {
                        String say = new JSONObject(s).getJSONObject("result").getJSONObject("data").getString("say");
                        Toast.makeText(MainActivity.this, "获取的答案信息:"+say, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // 摄像头实时图片传输
        mCsjBot.registerCameraListener(new OnCameraListener() {
            @Override
            public void response(Bitmap bitmap) {

            }
        });

        // 综合人体检测
        mCsjBot.registerDetectPersonListener(new OnDetectPersonListener() {
            @Override
            public void response(int i) {
                if(i == 0){ // 无人
                    Log.d("TAG","registerDetectPersonListener:无人");
                }else if(i == 1){ // 有人
                    Log.d("TAG","registerDetectPersonListener:有人");
                }
            }
        });

        // 人脸信息
        mCsjBot.registerFaceListener(new OnFaceListener() {
            @Override
            public void personInfo(String s) {
                // 人脸识别到的会员信息(人脸一直在就会持续触发)
                Log.d("TAG","registerFaceListener:s:"+s);
            }

            @Override
            public void personNear(boolean b) {

                if(b){// 有人脸靠近
                    Log.d("TAG","registerFaceListener:有人脸靠近");
                }else{// 人脸消失
                    Log.d("TAG","registerFaceListener:人脸消失");
                }
            }
        });

        // 机器人头部传感器(小雪)
        mCsjBot.registerHeadTouchListener(new OnHeadTouchListener() {
            @Override
            public void response() {
                Log.d("TAG","registerHeadTouchListener:头部传感器触发");
            }
        });

        findViewById(R.id.bt_alice_arm_right_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCsjBot.getAction().AliceRightArmUp();
            }
        });

        findViewById(R.id.bt_alice_arm_right_down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCsjBot.getAction().AliceRightArmDown();
            }
        });

        findViewById(R.id.bt_happy_expression).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCsjBot.getExpression().happy();
            }
        });

        findViewById(R.id.bt_save_point1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCsjBot.getAction().getPosition(new OnPositionListener() {
                    @Override
                    public void positionInfo(String s) {
                        Log.d("TAG","OnPositionListener:s:"+s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            String rotation = jsonObject.getString("rotation");
                            String x = jsonObject.getString("x");
                            String y = jsonObject.getString("y");
                            String z = jsonObject.getString("z");
                            RobotPose robotPose = new RobotPose();
                            robotPose.setPoseName("位置1");
                            RobotPose.PosBean posBean = new RobotPose.PosBean();
                            posBean.setRotation(Float.valueOf(rotation));
                            posBean.setX(Float.valueOf(x));
                            posBean.setY(Float.valueOf(y));
                            posBean.setZ(Float.valueOf(z));
                            robotPose.setPos(posBean);
                            robotPose1 = robotPose;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        findViewById(R.id.bt_save_point2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCsjBot.getAction().getPosition(new OnPositionListener() {
                    @Override
                    public void positionInfo(String s) {
                        Log.d("TAG","OnPositionListener:s:"+s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            String rotation = jsonObject.getString("rotation");
                            String x = jsonObject.getString("x");
                            String y = jsonObject.getString("y");
                            String z = jsonObject.getString("z");
                            RobotPose robotPose = new RobotPose();
                            robotPose.setPoseName("位置2");
                            RobotPose.PosBean posBean = new RobotPose.PosBean();
                            posBean.setRotation(Float.valueOf(rotation));
                            posBean.setX(Float.valueOf(x));
                            posBean.setY(Float.valueOf(y));
                            posBean.setZ(Float.valueOf(z));
                            robotPose.setPos(posBean);
                            robotPose2 = robotPose;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        findViewById(R.id.bt_navi_point1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(robotPose1 == null){
                    return;
                }
                mCsjBot.getAction().navi(new Gson().toJson(robotPose1.getPos()), new OnNaviListener() {
                    @Override
                    public void moveResult(String s) {
                        mCsjBot.getTts().startSpeaking(robotPose1.getPoseName()+"已经到啦!",null);
                    }

                    @Override
                    public void messageSendResult(String s) {
                        Log.d("TAG","导航消息下发成功");
                    }

                    @Override
                    public void cancelResult(String s) {

                    }

                    @Override
                    public void goHome() {

                    }
                });
            }
        });

        findViewById(R.id.bt_navi_point2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(robotPose2 == null){
                    return;
                }
                mCsjBot.getAction().navi(new Gson().toJson(robotPose2.getPos()), new OnNaviListener() {
                    @Override
                    public void moveResult(String s) {
                        mCsjBot.getTts().startSpeaking(robotPose2.getPoseName()+"已经到啦!",null);
                    }

                    @Override
                    public void messageSendResult(String s) {
                        Log.d("TAG","导航消息下发成功");
                    }


                    @Override
                    public void cancelResult(String s) {

                    }

                    @Override
                    public void goHome() {

                    }
                });
            }
        });

        findViewById(R.id.bt_save_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCsjBot.getAction().saveMap();
            }
        });

        // 开启语音识别(多次)
        mCsjBot.getSpeech().startIsr();

    }

    private void actionTest(){

        // 机器人动作
        Action action = CsjRobot.getInstance().getAction();

        // 动作重置
        action.reset();

        // 肢体动作(bodyPart:肢体部位,action:对应动作)
        action.action(2,6);// 低头

        // 开始左右摆手(intervalTime:间隔时间)
        action.startWaveHands(1000);

        // 停止左右摆手
        action.stopWaveHands();

        // 开始跳舞
        action.startDance();

        // 停止跳舞
        action.stopDance();

        // 获取当前机器人的位置信息
        action.getPosition(new OnPositionListener() {
            @Override
            public void positionInfo(String s) {
                // 返回json
                /*{
                    "msg_id":"NAVI_GET_CURPOS_RSP",
                        "x":”0”,
                    "y":”0”,
                    "z":”0”,
                    "rotation":”0”,
                    "error_code":0
                  }*/
            }
        });

        // 移动方法(direction:方向 0:前 1:后 2:左 3:右)
        // 移动距离较短
        action.move(0);

        // 导航方法(机器人导航到某个点)
        String json = "";
        /*{
            "msg_id":"NAVI_ROBOT_MOVE_TO_REQ",
                "pos": {
                    "x":2,
                    "y":1,
                    "z":0,
                    "rotation":30
                 }
          }*/
        action.navi(json, new OnNaviListener() {
            @Override
            public void moveResult(String s) {
                // 到达后通知
            }

            @Override
            public void messageSendResult(String s) {
                // 导航消息发送成功后的通知
            }

            @Override
            public void cancelResult(String s) {

            }

            @Override
            public void goHome() {

            }
        });

        // 机器人取消当前导航
        action.cancelNavi(new OnNaviListener() {
            @Override
            public void moveResult(String s) {

            }

            @Override
            public void messageSendResult(String s) {

            }

            @Override
            public void cancelResult(String s) {
                // 机器人取消导航通知
            }

            @Override
            public void goHome() {

            }
        });

        // 转至特定角度()
        action.goAngle(180);

        // 步进角度(Rotation>0:向左转，Rotation<0:向右转)
        action.moveAngle(180, new OnGoRotationListener() {
            @Override
            public void response(int i) {
                // 到达角度通知
            }
        });

        // 回去充电
        action.goHome(new OnNaviListener() {
            @Override
            public void moveResult(String s) {

            }

            @Override
            public void messageSendResult(String s) {

            }

            @Override
            public void cancelResult(String s) {

            }

            @Override
            public void goHome() {
                // 充电通知
            }
        });

        // 保存当前机器人的地图信息
        action.saveMap();

        // 加载保存的机器人地图信息
        action.loadMap();

        // 机器人速度设置(0.1-0.7,默认0.5)
        action.setSpeed(0.6f);

        // 导航状态查询
        action.search(new OnNaviSearchListener() {
            @Override
            public void searchResult(String s) {
                /*{
                    "msg_id":"NAVI_GET_STATUS_RSP",
                     "state":0,
                     "error_code":0
                  }*/
                // state:0空闲,1:正在导航
            }
        });

        // 点头动作
        action.nodAction();

        // 小雪右臂摆动
        action.snowRightArm();

        // 小雪左臂摆动
        action.snowLeftArm();

        // 小雪双臂摆动
        action.snowDoubleArm();

        // 爱丽丝抬头
        action.AliceHeadUp();

        // 爱丽丝头低头
        action.AliceHeadDown();

        // 爱丽丝头部重置
        action.AliceHeadHReset();

        // 爱丽丝左手臂抬起
        action.AliceLeftArmUp();

        // 爱丽丝左手臂放下
        action.AliceLeftArmDown();

        // 爱丽丝右手臂抬起
        action.AliceRightArmUp();

        // 爱丽丝右手臂放下
        action.AliceRightArmDown();

        // 小雪左臂摆动次数
        action.SnowLeftArmSwing(20);

        // 小雪右臂摆动次数
        action.SnowRightArmSwing(20);

        // 小雪双臂摆动次数
        action.SnowDoubleArmSwing(20);

        // 向左转
        action.turnLeft(new OnGoRotationListener() {
            @Override
            public void response(int i) {
                // 完成通知
            }
        });

        // 向右转
        action.turnRight(new OnGoRotationListener() {
            @Override
            public void response(int i) {
                // 完成通知
            }
        });

        // 向左移动
        action.moveLeft();

        // 向右移动
        action.moveRight();

        // 前进
        action.moveForward();

        // 后退
        action.moveBack();
    }

    public void ttsTest(){
        // 机器人TTS
        ISpeechSpeak speak = CsjRobot.getInstance().getTts();

        // 开始说话
        speak.startSpeaking("你好呀！", new OnSpeakListener() {
            @Override
            public void onSpeakBegin() {
                // 说话前
            }

            @Override
            public void onCompleted(SpeechError speechError) {
                // 说话完成
            }
        });

        // 停止说话
        speak.stopSpeaking();

        // 暂停说话
        speak.pauseSpeaking();

        // 重新说话
        speak.resumeSpeaking();

        // 是否正在说话
        speak.isSpeaking();

        // 也可以使用自己实现的TTS(继承自ISpeechSpeak接口)
        CsjRobot.getInstance().setTts(null);
    }

    public void speechTest(){
        // 机器人语音
        Speech speech = CsjRobot.getInstance().getSpeech();

        // 开启讯飞语音服务(默认开启，无需操作)
        speech.startSpeechService();

        // 关闭讯飞语音服务
        speech.closeSpeechService();

        // 开启多次识别
        speech.startIsr();

        // 关闭多次识别
        speech.stopIsr();

        // 开启单次识别
        speech.startOnceIsr();

        // 关闭单次识别
        speech.stopOnceIsr();

        // 手动唤醒机器人
        speech.openMicro();

        // 手动去获取问题的答案
        speech.getResult("你叫什么名字？", new OnSpeechGetResultListener() {
            @Override
            public void response(String s) {

            }
        });
    }

    private void faceTest(){
        // 机器人人脸识别
        Face face = CsjRobot.getInstance().getFace();

        // 打开摄像头(打开视频流传输 默认开启)
        face.openVideo();

        // 关闭摄像头
        face.closeVideo();

        // 启动人脸识别服务(默认开启)
        face.startFaceService();

        // 关闭人脸识别服务
        face.closeFaceService();


        // 摄像头拍照
        face.snapshot(new OnSnapshotoListener() {
            @Override
            public void response(String s) {
                /*
                * {
                    "error_code": 0,
                    "face_position": 0,
                    "msg_id":”FACE_SNAPSHOT_RESULT_RSP"
                    } */
                // erro_code : 0表示有人脸 其他表示无人脸
            }
        });

        // 人脸注册(保存当前拍照的人脸)
        face.saveFace("张三", new OnFaceSaveListener() {
            @Override
            public void response(String s) {
                /*
                * {
                    "msg_id":"FACE_SAVE_RSP",
                    “person_id”:”personx20170107161021mRJOVw”,
                       “error_code":0
                   }*/

                // error_cdoe : 0 成功 , 40002 人脸已经注册,40003 人脸姓名格式错误
            }
        });

        // 人脸信息删除
        face.faceDel("faceId");

        // 人脸信息批量删除
        face.faceDelList("faceIdsJson");

        // 获取所有人脸数据库
        face.getFaceDatabase(new OnGetAllFaceListener() {
            @Override
            public void personList(String s) {
                // s
            }
        });
    }

    private void stateTest(){
        // 机器人状态
        State state = CsjRobot.getInstance().getState();

        // 机器人的连接状态
        state.isConnect();

        // 机器人的电量值
        state.getElectricity();

        // 机器人当前充电状态
        state.getChargeState();

        // 机器人关机
        state.shutdown();

        // 机器人重启
        state.reboot();

        // 获取机器人电量信息
        state.getBattery(new OnRobotStateListener() {
            @Override
            public void getBattery(int i) {
                // 返回的电量值
            }

            @Override
            public void getCharge(int i) {

            }
        });

        // 获取机器人的充电状态
        state.getCharge(new OnRobotStateListener() {
            @Override
            public void getBattery(int i) {

            }

            @Override
            public void getCharge(int i) {
                // 返回的充电状态
            }
        });


        // 机器人检查信息
        state.checkSelf(new OnWarningCheckSelfListener() {
            @Override
            public void response(String s) {
                // 返回机器人的检查信息
            }
        });

        // 手动获取综合人体检测的信息
        state.getPerson(new OnDetectPersonListener() {
            @Override
            public void response(int i) {
                // 返回的状态
                // i==0 无人 i==1 有人
            }
        });

        // 获取机器人的SN信息
        state.getSN(new OnSNListener() {
            @Override
            public void response(String s) {
                // 返回的SN信息
            }
        });
    }

    private void expressionTest(){
        // 机器人表情
        Expression expression = CsjRobot.getInstance().getExpression();

        // 获取机器人表情
        expression.getExpression(new OnExpressionListener() {
            @Override
            public void response(int i) {
                // 返回的表情
            }
        });

        // 开心
        expression.happy();

        // 悲伤
        expression.sadness();

        // 惊讶
        expression.surprised();

        // 微笑
        expression.smile();

        // 正常
        expression.normal();

        // 愤怒
        expression.angry();

        // 闪电
        expression.lightning();

        // 困倦
        expression.sleepiness();
    }

    private void versionTest(){
        // 机器人版本信息
        Version version = CsjRobot.getInstance().getVersion();

        // 获取机器人版本信息
        version.getVersion(new OnGetVersionListener() {
            @Override
            public void response(String s) {
                // 返回的版本信息
            }
        });

        // 底层软件检查
        version.softwareCheck(new OnUpgradeListener() {
            @Override
            public void checkRsp(int i) {
                if (i == 60002) {//已是最新版本

                } else if (i == 60001) {//没有获取到版本信息，请检查网络

                } else if (i == 0) {//正常更新

                }
            }

            @Override
            public void upgradeRsp(int i) {

            }

            @Override
            public void upgradeProgress(int i) {

            }
        });


        // 底层软件更新
        version.softwareUpgrade(new OnUpgradeListener() {
            @Override
            public void checkRsp(int i) {

            }

            @Override
            public void upgradeRsp(int i) {

            }

            @Override
            public void upgradeProgress(int i) {
                // 更新进度
            }
        });
    }

    private void extraTest(){
        // 其他功能
        Extra extra = CsjRobot.getInstance().getExtra();

        // 获取机器人的热词列表
        extra.getHotWords(new OnHotWordsListener() {
            @Override
            public void hotWords(List<String> list) {
                // 返回的热词列表
            }
        });

        // 设置热词功能
        extra.setHotWords(null);

        // 恢复出厂设置
        extra.resetRobot();
    }
}
