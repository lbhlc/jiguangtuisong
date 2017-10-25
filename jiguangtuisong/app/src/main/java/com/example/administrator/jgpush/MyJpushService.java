package com.example.administrator.jgpush;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

/**
 * created on 2017/10/24.
 * 邮箱:76681287@qq.com
 * @author libohan
 */

public class MyJpushService extends Service implements MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener {
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

        ad=this.getResources().openRawResourceFd(R.raw.camera_click);
        onPreaere();

    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {

        boolean result=intent.getBooleanExtra("result",false);
        if (result)
        {
            if (mediaPlayer!=null&&!mediaPlayer.isPlaying()) {

                try {
                    this.mediaPlayer.setOnPreparedListener(this);
                    this.mediaPlayer.prepareAsync();
                } catch (Exception e) {
                    Log.e("LBH", "出现异常了");
                }
            }

        }else
        {
            if (mediaPlayer!=null&&mediaPlayer.isPlaying()) {

                mediaPlayer.pause();
            }

        }


        return super.onStartCommand(intent, flags, startId);
    }


    private void onPreaere() {
        mediaPlayer=new MediaPlayer();
        Log.e("LBH",mediaPlayer.hashCode()+"这里的");
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
        Log.e("LBH","you are can not kill me ");
        Intent intent=new Intent(this,MyJpushService.class);
        intent.putExtra("result",false);
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

    }
}
