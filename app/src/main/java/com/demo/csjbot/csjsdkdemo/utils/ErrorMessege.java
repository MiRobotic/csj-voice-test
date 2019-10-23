package com.mirobotic.voiceapp.utils;

public class ErrorMessege {


    public static String getErrorMsg(int errorCode) {


        switch (errorCode) {

            case -1:
                return "Abnormal, generally error, current json called failure";
            case 10001:
                return "No voice service enabled";
            case 10002:
                return "Voice service enabled now";
            case 10003:
                return "Microphone is recording ";
            case 10004:
                return "TTS synthetic failed ";
            case 10005:
                return "No microphone device detected available ";
            case 10006:
                return "No speaker detected ";
            case 10111:
                return "Voice not initialized ";
            case 10112:
                return "Voice initialization failed ";
            case 10113:
                return "Voice memory overflow ";
            case 10114:
                return "Voice network timeout ";
            case 10115:
                return "Open voice file failed ";
            case 10116:
                return "No voice speaker model found ";
            case 10117:
                return "Insufficient voice memory request ";
            case 10118:
                return "Voice data read failed ";
            case 10119:
                return "No semantics found ";
            case 10120:
                return "There is abnormality in the voice function";
            case 20001:
                return "Navigation board connection failed ";
            case 20002:
                return "Navigation request timed out ";
            case 20003:
                return "Navigate to destination turned to fail ";
            case 20004:
                return "Target destination unreachable ";
            case 20005:
                return "No saved maps found ";
            case 20006:
                return "Already navigating ";
            case 20007:
                return "The robot is surrounded by many people ";
            case 21000:
                return "Navigation exception, please check if the target point is reachable ";
            case 30001:
                return "Server json parsing error ";
            case 30002:
                return "Server status is abnormal ";
            case 30003:
                return "Server request timed out ";
            case 40001:
                return "Face saved error; fetching feature values failed, etc. ";
            case 40002:
                return "The Face has already been registered ";
            case 40003:
                return "The name format of the Face error ";
            case 50001:
                return "Upper computer SN number does not exist ";
            case 50002:
                return "Robot not in storage ";
            case 60001:
                return "Failed to get the version ";
            case 60002:
                return "Already the latest version ";
            case 70001:
                return "Power On Self Test (POST) not yet finished ";
            case 80001:
                return "Upper control board unconnected ";
            case 80002:
                return "Temp sensor unconnected/ connection failed ";
            case 80003:
                return "Call machine unconnected/ connection failed ";
            default:
                return "Invalid Error Code";

        }

    }

}
