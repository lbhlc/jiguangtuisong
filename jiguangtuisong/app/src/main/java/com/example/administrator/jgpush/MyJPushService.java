package com.example.administrator.jgpush;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import cn.jpush.android.api.JPushInterface;

/**
 *  on 2017/10/24.
 * 邮箱:76681287@qq.com
 * @author libohan
 */

public class MyJPushService extends Service implements MediaPlayer.OnCompletionListener {
    private MediaPlayer mediaPlayer;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("LBH","报警了");
        onPreaere();
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
       // mediaPlayer.start();
        boolean result=intent.getBooleanExtra("result",false);
        if (result)
        {
            mediaPlayer.start();
            Log.e("LBH","音乐播放了");
        }else
        {
            mediaPlayer.pause();
            Log.e("LBH","stop///////////////////////////////////////////////////////////////////");
        }


        return super.onStartCommand(intent, flags, startId);
    }

    private void onPreaere() {
        mediaPlayer = MediaPlayer.create(this, R.raw.camera_click);
        mediaPlayer.setLooping(true);
        mediaPlayer.setOnCompletionListener(this);

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Intent intent=new Intent(this,MyJPushService.class);
        startService(intent);
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.seekTo(0);
        mediaPlayer.stop();
    }
}
