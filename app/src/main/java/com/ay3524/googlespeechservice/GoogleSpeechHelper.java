package com.ay3524.googlespeechservice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.List;

public class GoogleSpeechHelper implements RecognitionListener {

    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private static final String TAG = GoogleSpeechHelper.class.getSimpleName();
    private SpeechCallbacks speechCallbacks;

    /**
     * Method to set the callback, if we want any data
     */
    public void setSpeechCallbacks(SpeechCallbacks speechCallbacks) {
        this.speechCallbacks = speechCallbacks;
    }

    /**
     * Initializes the SpeechRecognizer.
     * Checks whether google app or voice search is installed or not.
     *
     * @param context Context from where this method is called.
     */
    public void initSpeechRecognizer(Context context) {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            mSpeechRecognizer.setRecognitionListener(this);
            mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        } else {
            //TODO Google app or google voice is not installed
            Log.e(TAG, "Please install the google app.");
        }
    }

    /**
     * Start the SpeechRecognizer's listening.
     */
    public void startSpeechToText() {
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.e(TAG, "onReadyForSpeech()");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.e(TAG, "onBeginningOfSpeech()");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.e(TAG, "onRmsChanged()");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.e(TAG, "onBufferReceived()");
    }

    @Override
    public void onEndOfSpeech() {
        Log.e(TAG, "onEndOfSpeech()");
    }

    @Override
    public void onError(int error) {
        Log.e(TAG, "onError()");
        switch (error) {
            case SpeechRecognizer.ERROR_CLIENT:
                Log.e(TAG, "ERROR_CLIENT");
                break;

            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                Log.e(TAG, "ERROR_INSUFFICIENT_PERMISSIONS");
                break;

            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                Log.e(TAG, "ERROR_RECOGNIZER_BUSY");
                break;

            case SpeechRecognizer.ERROR_AUDIO:
                Log.e(TAG, "ERROR_AUDIO");
                break;

            case SpeechRecognizer.ERROR_NETWORK:
                Log.e(TAG, "ERROR_NETWORK");
                break;

            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                Log.e(TAG, "ERROR_NETWORK_TIMEOUT");
                //
                break;

            case SpeechRecognizer.ERROR_SERVER:
                Log.e(TAG, "ERROR_SERVER");
                //
                break;

            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                Log.e(TAG, "ERROR_SPEECH_TIMEOUT");
                //
                break;

            case SpeechRecognizer.ERROR_NO_MATCH:
                Log.e(TAG, "ERROR_NO_MATCH");
                //
                break;
        }
    }

    @Override
    public void onResults(Bundle results) {
        Log.e(TAG, "onResults()");

        mSpeechRecognizer.stopListening();

        List<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        if (matches != null && !matches.isEmpty()) {
            Log.e(TAG, matches.get(0));
            speechCallbacks.result(matches.get(0));
//            for (String result : matches) {
//                Log.e(TAG, result);
//            }
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.e(TAG, "onPartialResults()");
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.e(TAG, "onEvent() : type : " + eventType);
    }

    /**
     * Free up the memory to make it enable for GC and prevent memory leak.
     */
    public void speechCleanUp() {
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.cancel();
            mSpeechRecognizer.destroy();
        }
    }

    /**
     * Interface to give the corresponding result to the receiver
     */
    public interface SpeechCallbacks{
        void result(String result);
    }
}
