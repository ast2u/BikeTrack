package com.example.biketrackcba;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class TimerService implements Runnable {
    private boolean timerStarted = false;
    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;
    private Handler handler;
    private TextView text_Time;

    public TimerService(Handler handler, TextView text_Time) {
        this.handler = handler;
        this.text_Time = text_Time;
        timer = new Timer();
    }
    @Override
    public void run() {
        timerStarted = true;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                time++;
                handler.post(() -> text_Time.setText(getTimerText()));
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    public void resetTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            time = 0.0;
            timerStarted = false;
            handler.post(() -> text_Time.setText(formatTime(0,0)));
        }
    }

    private String getTimerText(){
        int rounded = (int) Math.round(time);
        int seconds = ((rounded%86400)%3600)%60;
        int minutes = ((rounded%86400)%3600)/60;
        return formatTime(seconds,minutes);
    }

    private String formatTime(int seconds, int minutes){
        return String.format("%02d",minutes)+" : "+String.format("%02d",seconds);
    }
}