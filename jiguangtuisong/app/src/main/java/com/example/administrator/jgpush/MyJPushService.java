package com.example.administrator.jgpush;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import cn.jpush.android.api.JPushInterface;

/**
 *   created on 2017/10/24.
 * 邮箱:76681287@qq.com
 * @author libohan
 */

public class MyJPushService extends Service implements MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener {
    private MediaPlayer mediaPlayer;

    private AssetFileDescriptor ad;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("LBH","报警了");
        ad=this.getResources().openRawResourceFd(R.raw.camera_click);
        onPreaere();
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        Log.e("LBH","intent="+intent);
        boolean result=intent.getBooleanExtra("result",false);
        if (result)
        {
            try {
                mediaPlayer.setOnPreparedListener(this);
                this.mediaPlayer.prepareAsync();
            }catch (Exception e)
            {
                Log.e("LBH","出现异常了");
            }

            Log.e("LBH","音乐播放了");
        }else
        {
            Log.e("LBH",mediaPlayer.isPlaying()+"=playing");
            if (mediaPlayer.isPlaying()) {
                Log.e("LBH","播放中");
                mediaPlayer.pause();
            }
            Log.e("LBH","stop///////////////////////////////////////////////////////////////////");
        }


        return super.onStartCommand(intent, flags, startId);
    }

    private void onPreaere() {
       // mediaPlayer = MediaPlayer.create(this, R.raw.camera_click);
        mediaPlayer=new MediaPlayer();
        if (ad==null)
        {
            return;
        }
        try {
            mediaPlayer.setDataSource(ad.getFileDescriptor(),ad.getStartOffset(),ad.getLength());
        } catch (IOException e) {
            Log.e("LBH","报错了="+e.getMessage());
        }
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

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
       this.mediaPlayer.start();
        Log.e("LBH",this.mediaPlayer.isPlaying()+"=playing");
        Log.e("LBH",this.mediaPlayer.isLooping()+"=looping");

    }
}
