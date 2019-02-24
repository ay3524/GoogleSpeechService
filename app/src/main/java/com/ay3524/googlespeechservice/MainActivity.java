package com.ay3524.googlespeechservice;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SpeechService.ServiceCallbacks, View.OnClickListener {

    private TextView textView;
    private static final int RECORD_AUDIO_PERM = 100;
    private static final int STORAGE_PERM = 101;
    private static final int CAMERA_RESULT_CODE = 200;
    private boolean mServiceBound;
    private SpeechService mSpeechService;
    private static final String TAG = MainActivity.class.getSimpleName();
    private StringBuilder googleSpeechString = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //For printing the result
        textView = findViewById(R.id.text);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(this);

        initComponents();
    }

    /**
     * Initializes the app components like permissions and other necessary checks
     */
    private void initComponents() {
        if (!Utils.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        } else {
            requestPermissionPrompt(new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_PERM);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case RECORD_AUDIO_PERM: {

                // If request is cancelled, the result arrays are empty.
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(MainActivity.this, "Permission needed for Record Audio", Toast.LENGTH_SHORT).show();
                } else {
                    startSpeechService();
                }
            }
            break;

            case STORAGE_PERM: {

                // If request is cancelled, the result arrays are empty.
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(MainActivity.this, "Permission needed for Record Audio", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Storage Permission already granted!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * It starts the SpeechService.
     */
    private void startSpeechService() {
        textView.setText("Loading...");
        Intent intent = new Intent(this, SpeechService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * It makes an instance of ServiceConnection required for bound services.
     * The callbacks makes sure that the service is connected or disconnected.
     * We will get the service instance here.
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SpeechService.SpeechBinder myBinder = (SpeechService.SpeechBinder) service;
            mSpeechService = myBinder.getService();
            mSpeechService.setServiceCallbacks(MainActivity.this);
            mSpeechService.startSpeechListening();
            textView.setText("Start Speaking...");
            mServiceBound = true;
        }
    };

    @Override
    protected void onRestart() {
        Log.e(TAG, "onRestart() ");
        super.onRestart();
        startSpeechService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSpeechService != null) {
            mSpeechService.cleanUps();
        }
    }

    /**
     * Callback method provided by the service to get the result.
     */
    @Override
    public void onSpeechResults(String result) {
        googleSpeechString.append(result);
        Log.e(TAG, googleSpeechString.toString());
        textView.append(result);
        textView.append("\n=====Listening done=====\n");

        handleIntents(result.toLowerCase().trim());
    }

    /**
     * Method for handling the intent received as text
     */
    private void handleIntents(String result) {
        if (Utils.checkForWord(result, Utils.ARRAY_STORAGE_WORDS)) {
            requestPermissionPrompt(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERM);
        } else if (Utils.checkForWord(result, Utils.ARRAY_CAMERA_WORDS)) {
            openCamera();
        } else {
            mSpeechService.startSpeechListening();
        }
    }

    /**
     * Method for opening camera via Explicit Intent
     */
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Utils.checkForExplicitIntent(intent, getApplicationContext())) {
            startActivityForResult(intent, CAMERA_RESULT_CODE);
        } else {
            Toast.makeText(mSpeechService, "No camera found! :(", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        switch (requestCode) {
            case CAMERA_RESULT_CODE:
                //TODO save picture if needed
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (mServiceBound) {
            if (mSpeechService != null) {
                mSpeechService.startSpeechListening();
            }
        }
    }

    private void requestPermissionPrompt(String[] permissionArray, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissionArray, requestCode);
        }
    }
}
