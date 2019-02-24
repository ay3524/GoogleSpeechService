package com.ay3524.googlespeechservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class SpeechService extends Service implements GoogleSpeechHelper.SpeechCallbacks {

    private final IBinder mSpeechBinder = new SpeechBinder();
    private static final String TAG = SpeechService.class.getSimpleName();
    private GoogleSpeechHelper googleSpeechHelper;
    private ServiceCallbacks serviceCallbacks;

    /**
     * Method to set the callback, if we want any data
     */
    public void setServiceCallbacks(ServiceCallbacks serviceCallbacks) {
        this.serviceCallbacks = serviceCallbacks;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "OnCreate() : ");
        initializeSpeechService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand() : ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mSpeechBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        googleSpeechHelper.speechCleanUp();
    }

    /**
     * Method to initialize Google Speech Recognizer listening
     */
    public void initializeSpeechService() {
        //TODO initializeSpeechService
        googleSpeechHelper = new GoogleSpeechHelper();
        googleSpeechHelper.initSpeechRecognizer(getApplicationContext());
        googleSpeechHelper.setSpeechCallbacks(this);
    }

    /**
     * Method to start Google Speech Recognizer listening
     */
    public void startSpeechListening() {
        //TODO startSpeechListening
        googleSpeechHelper.startSpeechToText();
    }

    /**
     * Method to stop Google Speech Recognizer listening
     */
    public void stopSpeechListening() {
        //TODO stopSpeechListening

    }

    public void cleanUps(){
        googleSpeechHelper.speechCleanUp();
    }

    /**
     * Callback method provided by the Google Speech Recognizer to get the result.
     */
    @Override
    public void result(String result) {
        serviceCallbacks.onSpeechResults(result);
    }

    public class SpeechBinder extends Binder {
        public SpeechService getService() {
            return SpeechService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    /**
     * Interface to give the corresponding result to the receiver
     */
    public interface ServiceCallbacks {
        void onSpeechResults(String result);
    }
}
