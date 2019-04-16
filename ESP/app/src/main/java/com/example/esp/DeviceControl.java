package com.example.esp;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DeviceControl extends AppCompatActivity {


    private   QMUITopBar     TopBar;
    private   QMUITipDialog  dialog;
    private   Boolean        Sw1Sta;
    private   Boolean        Sw2Sta;
    private   Boolean        Sw3Sta;
    private   Boolean        Sw4Sta;
    private   Switch         SwitchFood;
    private   Switch         SwitchO2_1;
    private   Switch         SwitchO2_2;
    private   Switch         SwitchO2_3;
    private   GizWifiDevice  gizWifiDevice;
    private static final int CODE_HANDLER_UI = 105;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.devicecontrol);
        gizWifiDevice = this.getIntent().getParcelableExtra("device");
        gizWifiDevice.setListener(gizWifiDeviceListener);
        CreateTopBar();
        GetDeviceStatus();

    }
    /*
     * @Titl:
     * @Param
     * @Return:
     * @Description:重新更新数据更新开关状态
     * @author xundanqing
     * @CreateDate: 2019/4/16 10:41
     */
    private Handler  handler  =  new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CODE_HANDLER_UI: {
                    SwitchFood.setChecked(Sw1Sta);
                    SwitchO2_1.setChecked(Sw2Sta);
                    SwitchO2_2.setChecked(Sw3Sta);
                    SwitchO2_3.setChecked(Sw4Sta);
                    break;
                }
            }
        }
    };
    /*
     * @Titl:
     * @Param
     * @Return:
     * @Description:设备控制开关
     * @author xundanqing
     * @CreateDate: 2019/4/15 17:11
     */
    private  void    SwitchUserDeal(){

        SwitchFood =  findViewById(R.id.Switchfood);
        SwitchO2_1 =  findViewById(R.id.SwitchO2_2);
        SwitchO2_2 =  findViewById(R.id.SwitchO2_3);
        SwitchO2_3 =  findViewById(R.id.SwitchO2_4);

        /*投食机开关*/
        SwitchFood.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            ConcurrentHashMap Request =  new ConcurrentHashMap<String,Boolean>() ;

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Request.put("switch_1",true);
                    gizWifiDevice.write(Request,1);
                }else{
                    Request.put("switch_1",false);
                    gizWifiDevice.write(Request,1);
                }
            }
        });

        /*增氧机开关1*/
        SwitchO2_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            ConcurrentHashMap Request =  new ConcurrentHashMap<String,Boolean>() ;

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Request.put("switch2",true);
                    gizWifiDevice.write(Request,1);
                }else{
                    Request.put("switch2",false);
                    gizWifiDevice.write(Request,1);
                }
            }
        });

        /*增氧机开关2*/
        SwitchO2_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            ConcurrentHashMap Request =  new ConcurrentHashMap<String,Boolean>() ;
                if(isChecked){
                    Request.put("switch3",true);
                    gizWifiDevice.write(Request,1);
                }else{
                    Request.put("switch3",false);
                    gizWifiDevice.write(Request,1);
                }
            }
        });

        /*增氧机开关3*/
        SwitchO2_3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
             ConcurrentHashMap Request =  new ConcurrentHashMap<String,Boolean>() ;
                if(isChecked){
                    Request.put("switch4",true);
                    gizWifiDevice.write(Request,1);
                }else{
                    Request.put("switch4",false);
                    gizWifiDevice.write(Request,1);
                }
            }
        });
    }
    /*
     * @Titl:
     * @Param
     * @Return:
     * @Description:请求模块的状态
     * @author xundanqing
     * @CreateDate: 2019/4/16 10:12
     */
    private   void    RequestStatus(){

        List<String>  Request =  new ArrayList<>() ;
        Request.add("switch_1");
        Request.add("switch2");
        Request.add("switch4");
        Request.add("switch3");
        gizWifiDevice.getDeviceStatus(Request);

    }
    /*
     *@Titl:
     * @Param
     * @Return:
     * @Description:获得设备状态
     * @author xundanqing
     * @CreateDate: 2019/4/12 15:45
     */
    private   void  GetDeviceStatus(){

        dialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("数据同步...")
                .create();
        dialog.show();

        RequestStatus();
    }
    /*
     * @Titl:
     * @Param
     * @Return:
     * @Description:创建上部抬头
     * @author xundanqing
     * @CreateDate: 2019/4/12 15:21
     */
    private   void  CreateTopBar(){

        TopBar  = findViewById(R.id.devicecontrolbar);
        TopBar.setTitle(gizWifiDevice.getAlias());
        TopBar.addLeftImageButton(R.mipmap.ic_back, R.id.TopBarDeviceControl).setOnClickListener
                (new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    /*
     * @Titl: 
     * @Param
     * @Return: 
     * @Description:同步云端信息
     * @author xundanqing
     * @CreateDate: 2019/4/12 17:10
     */
    private void SyncDeviceStatus(ConcurrentHashMap<String, Object> dataMap ) {

        Log.d("SyncDeviceStatus", "云端下发数据：" + dataMap);

        if (dataMap.get("data") != null) {
            ConcurrentHashMap<String, Object> map
                           = (ConcurrentHashMap<String, Object>) dataMap.get("data");

            for (String dataKey : map.keySet()) {

                if (dataKey.equals("switch_1")) {
                    Sw1Sta = (Boolean) map.get(dataKey);
                }
                if (dataKey.equals("switch2")) {
                    Sw2Sta = (Boolean) map.get(dataKey);
                }
                if (dataKey.equals("switch3")) {
                    Sw3Sta = (Boolean) map.get(dataKey);
                }
                if (dataKey.equals("switch4")) {
                    Sw4Sta = (Boolean) map.get(dataKey);
                }
            }
        }
    }
    /*
     * @Titl:
     * @Param
     * @Return:
     * @Description:设备云端控制回调
     * @author xundanqing
     * @CreateDate: 2019/4/12 15:38
     */
    private GizWifiDeviceListener gizWifiDeviceListener = new GizWifiDeviceListener() {


        /** 用于获取设备状态 */
        public void didReceiveData(GizWifiErrorCode result, GizWifiDevice device,
                                   ConcurrentHashMap<String, Object> dataMap, int sn) {

            if(result == GizWifiErrorCode.GIZ_SDK_SUCCESS ){
                if(sn == 0){
                    SyncDeviceStatus(dataMap);
                    SwitchUserDeal();
                }else{
                    GetDeviceStatus();
                }
                dialog.dismiss();
            }
        }
    };
}
