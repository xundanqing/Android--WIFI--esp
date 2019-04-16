package com.example.esp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SpalshActivity extends AppCompatActivity {

    private  int    PERMISSON_ACKSUCESS  =  0X01;
    private  int    ACTIVITY_START       =  105;

    private  String  []   neededPermissions    = {

             Manifest.permission.INTERNET,
             Manifest.permission.READ_PHONE_STATE,
             Manifest.permission.READ_EXTERNAL_STORAGE,
             Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    /*处理线程数据*/
    private    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == ACTIVITY_START) {
                Log.d("SpalshActivity","Start  Main  activity");
                startActivity(new Intent(SpalshActivity.this,MainActivity.class));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spalsh_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(PermisssonRequest()) {
                Log.d("SpalshActivity", "permmison  ok");
                handler.sendEmptyMessageDelayed(ACTIVITY_START, 3000);
            }
        }else{
            handler.sendEmptyMessageDelayed(ACTIVITY_START, 3000);
        }
    }
    /*
     * @Titl:
     * @Param
     * @Return:
     * @Description:申请权限若不成功
     * @author xundanqing
     * @CreateDate: 2019/3/2 11:32
     */
    public    boolean   PermisssonRequest(){
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this.getApplicationContext(), neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }
    /*
     * @Titl:
     * @Param
     * @Return:
     * @Description:运行时权限查看
     * @author xundanqing
     * @CreateDate: 2019/3/2 11:33
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        boolean isAllGranted = true;
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSON_ACKSUCESS) {
            for (int grantResult : grantResults) {
                isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
            if(isAllGranted){
                handler.sendEmptyMessageDelayed(ACTIVITY_START, 3000);
            }else{
                Log.d("SpalshActivity","Permisson  error");
            }
        }
    }
}
