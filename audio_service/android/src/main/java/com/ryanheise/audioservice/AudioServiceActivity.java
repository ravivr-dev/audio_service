package com.ryanheise.audioservice;

import android.content.Context;

import androidx.annotation.NonNull;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

import android.media.AudioManager;
import android.util.Log;
import android.view.KeyEvent;


public class AudioServiceActivity extends FlutterActivity {

    private static final String CHANNEL = "com.utsah/volume";
    private static final String VOLUME_CHANNEL = "com.utsah/volume_control";

    // Declare MethodChannel as a class-level variable
    private MethodChannel volumeControlChannel;
    @Override
    public FlutterEngine provideFlutterEngine(@NonNull Context context) {
        return AudioServicePlugin.getFlutterEngine(context);

    }

    @Override
    public void configureFlutterEngine(FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        // Initialize MethodChannel once during engine setup
        volumeControlChannel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), VOLUME_CHANNEL);


        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL).setMethodCallHandler((call, result) -> {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            switch (call.method) {
                case "increaseCallVolume":
                    audioManager.adjustStreamVolume(
                            AudioManager.STREAM_VOICE_CALL,
                            AudioManager.ADJUST_RAISE,
                            AudioManager.FLAG_SHOW_UI
                    );
                    result.success(null);
                    break;
                case "decreaseCallVolume":
                    audioManager.adjustStreamVolume(
                            AudioManager.STREAM_VOICE_CALL,
                            AudioManager.ADJUST_LOWER,
                            AudioManager.FLAG_SHOW_UI
                    );
                    result.success(null);
                    break;
                default:
                    result.notImplemented();
                    break;
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.d("dispatchKeyEvent", "dispatchKeyEvent: " + event.getAction());
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    volumeControlChannel.invokeMethod("volumeUp", null);
                    return true; // Event consumed
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    volumeControlChannel.invokeMethod("volumeDown", null);
                    return true; // Event consumed
            }
        }
        return super.dispatchKeyEvent(event);
    }


}
