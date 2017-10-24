package com.example.administrator.jgpush;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;

import static cn.jpush.android.api.JPushInterface.a.v;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button mInit;
    private Button mSetting;
    private Button mStopPush;
    private Button mResumePush;
    private Button mGetRid;
    private TextView mRegId;
    private EditText msgText;
    public static boolean isForeground;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initView();
        registerMessageReceiver();  // used for receive msg
        ShowWarning.activity=this;

    }
    private void initView(){
        TextView mImei = (TextView) findViewById(R.id.tv_imei);
        String udid =  ExampleUtil.getImei(getApplicationContext(), "");
        if (null != udid)
        {
            mImei.setText("IMEI: " + udid);
        }

        TextView mAppKey = (TextView) findViewById(R.id.tv_appkey);
        String appKey = ExampleUtil.getAppKey(getApplicationContext());
        if (null == appKey)
        {
            appKey = "AppKey异常";
        }
        mAppKey.setText("AppKey: " + appKey);

        mRegId = (TextView) findViewById(R.id.tv_regId);
        mRegId.setText("RegId:");

        String packageName =  getPackageName();
        TextView mPackage = (TextView) findViewById(R.id.tv_package);
        mPackage.setText("PackageName: " + packageName);

        String deviceId = ExampleUtil.getDeviceId(getApplicationContext());
        TextView mDeviceId = (TextView) findViewById(R.id.tv_device_id);
        mDeviceId.setText("deviceId:" + deviceId);

        String versionName =  ExampleUtil.GetVersion(getApplicationContext());
        TextView mVersion = (TextView) findViewById(R.id.tv_version);
        mVersion.setText("Version: " + versionName);

        mInit = (Button)findViewById(R.id.init);
        mInit.setOnClickListener(this);

        mStopPush = (Button)findViewById(R.id.stopPush);
        mStopPush.setOnClickListener(this);

        mResumePush = (Button)findViewById(R.id.resumePush);
        mResumePush.setOnClickListener(this);

        mGetRid = (Button) findViewById(R.id.getRegistrationId);
        mGetRid.setOnClickListener(this);

        mSetting = (Button)findViewById(R.id.setting);
        mSetting.setOnClickListener(this);

        msgText = (EditText)findViewById(R.id.msg_rec);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.init:
                init();
                break;
            case R.id.setting:
                Intent intent = new Intent(MainActivity.this, PushSetActivity.class);
                startActivity(intent);
                break;
            case R.id.stopPush:
                JPushInterface.stopPush(getApplicationContext());
                break;
            case R.id.resumePush:
                JPushInterface.resumePush(getApplicationContext());
                break;
            case R.id.getRegistrationId:
                String rid = JPushInterface.getRegistrationID(getApplicationContext());
                if (!rid.isEmpty()) {
                    mRegId.setText("RegId:" + rid);
                } else {
                    Toast.makeText(this, "Get registration fail, JPush init failed!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    private void init(){

    }


    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }


    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }


    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
                    setCostomMsg(showMsg.toString());
                }
            } catch (Exception e){
            }
        }
    }

    private void setCostomMsg(String msg){
        if (null != msgText) {
            msgText.setText(msg);
            msgText.setVisibility(android.view.View.VISIBLE);
        }
    }
    public static class ShowWarning extends BroadcastReceiver
    {
        private static Context activity;



        public ShowWarning() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String warning="warning";
            Log.e("LBH","context="+context);
            if (warning.equals(intent.getAction()))
            {
                Log.e("LBH","收到报警消息了");
                Log.e("LBH",activity+"=activity");
                AlertDialog.Builder warnDialog =
                        new AlertDialog.Builder(activity);
                warnDialog.setIcon(R.drawable.ic_launcher);
                warnDialog.setTitle("有报警音");
                warnDialog.setMessage("某设备报警了");
                warnDialog.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intentService=new Intent(activity,MyJPushService.class);
                        intentService.putExtra("result",false);
                        activity.stopService(intentService);
                       dialogInterface.dismiss();

                    }
                });
                warnDialog.create().show();
            }
        }
    }
}
