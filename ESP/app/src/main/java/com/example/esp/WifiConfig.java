package com.example.esp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.example.esp.Util.WifiInfo;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizWifiConfigureMode;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.enumration.GizWifiGAgentType;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;
import com.qmuiteam.qmui.widget.QMUITopBar;

import java.util.ArrayList;
import java.util.List;

public class WifiConfig extends AppCompatActivity {

    private   Button     button;
    private   CheckBox   checkbox;
    private   EditText   EditPass;
    private   TextView   TextviewSSid;
    private   QMUITopBar TopBar;
    private   Context    context;

    private   ProgressDialog  DiscorveDialog;
    private   int      DeviceBoardok  =  105;
    private   int      DeviceBoarderr =  106;
    private   int      WifiSsid       =  107;
    /*
     * @Titl:
     * @Param
     * @Return:
     * @Description:操作结果Dialog 显示
     * @author xundanqing
     * @CreateDate: 2019/4/4 13:54
     */
    private   Handler   handler  =  new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == DeviceBoardok) {
                DiscorveDialog.setMessage("配网成功");
            }else  if(msg.what==DeviceBoarderr){
                DiscorveDialog.setMessage("配网失败");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wificonfig_layout);
        context = this;
        TopBar = findViewById(R.id.topBar);
        TopBar.setTitle("添加设备");
        TopBar.addLeftImageButton(R.mipmap.ic_back, R.id.TopBarWifiReturn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        UserDeal();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String apSsid =  WifiInfo.getinstance(context).GetWifiSSid();

        if (apSsid != null) {
           TextviewSSid.setText(apSsid);
        }
    }
    /*
     * @Titl:
     * @Param
     * @Return:
     * @Description:按键触发流程
     * @author xundanqing
     * @CreateDate: 2019/4/4 13:47
     */
    private   void  UserDeal(){

        checkbox     = findViewById(R.id.cbPaw);
        button       = findViewById(R.id.btAdd);
        EditPass     = findViewById(R.id.edApPassword);
        TextviewSSid = findViewById(R.id.tvApSsid);
        String SSid =  WifiInfo.getinstance(context).GetWifiSSid();
        if(SSid!=null  &&  SSid.length() > 0){
            TextviewSSid.setText(SSid);
        }

        /*密码显示操作*/
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    EditPass.setInputType(0x90);
                } else {
                    EditPass.setInputType(0x81);
                }
            }
        });

        /*搜索设备操作*/
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String SSid =  WifiInfo.getinstance(context).GetWifiSSid();
                String Pass =  EditPass.getText().toString().intern();

                if(SSid == null || Pass == null){
                     return;
                }

                DiscorveDialog = new ProgressDialog(context);
                DiscorveDialog.setMessage("设备配网中，请稍后...");
                DiscorveDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                DiscorveDialog.setCancelable(false);
                DiscorveDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DiscorveDialog.dismiss();

                    }
                });

                DiscorveDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DiscorveDialog.dismiss();
                        finish();
                    }
                });
                DiscorveDialog.show();
                StartAirlink(SSid,Pass);
            }
        });
    }
    /*
     * @Titl:
     * @Param
     * @Return:
     * @Description:配置wifi发送并配置设备
     * @author xundanqing
     * @CreateDate: 2019/4/4 13:35
     */
    private void StartAirlink(String   SSid,String   Pass) {

        if(SSid == null || Pass==null){
            return;
        }

        if(SSid.isEmpty() ||SSid.isEmpty()){
            return;
        }

        List<GizWifiGAgentType> DeviceSupportList = new ArrayList<>();
        DeviceSupportList.add(GizWifiGAgentType.GizGAgentESP);
        GizWifiSDK.sharedInstance().setListener(WifiDeviceListener);
        GizWifiSDK.sharedInstance().setDeviceOnboarding(SSid,Pass,
                       GizWifiConfigureMode.GizWifiAirLink, null, 60, DeviceSupportList);

    }
    /*
     * @Titl:
     * @Param
     * @Return:
     * @Description:配置设备回调函数
     * @author xundanqing
     * @CreateDate: 2019/4/4 13:36
     */
    private GizWifiSDKListener  WifiDeviceListener = new  GizWifiSDKListener() {

        @Override
        public void didSetDeviceOnboarding(GizWifiErrorCode result, GizWifiDevice device) {
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                handler.sendEmptyMessage(DeviceBoardok);
            } else {
                handler.sendEmptyMessage(DeviceBoarderr);
            }
        }
    };
}
