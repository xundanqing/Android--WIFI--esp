package com.example.esp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.esp.Util.ListviewClick;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizEventType;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MainActivity extends AppCompatActivity implements ListviewClick {

    private QMUITopBar TopBar;
    private Context    contex;
    private String     Uid   = null;
    private String     Token = null;
    private SwipeRefreshLayout  SwipeRefresh;
    private ListView             DeviceListView;
    private List<GizWifiDevice>  DeviceList = null;
    private DeviceListView       adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainlayout);
        contex = this;
        CreateListView();
        CreateTopBar();
        CreateScowView();
        InitSdk();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    /*
     * @Titl:
     * @Param
     * @Return:
     * @Description:按键按下listview中的按键触发订阅这个设备并且跳转控制界面,并在订阅回调中查看订阅结果
     * @author xundanqing
     * @CreateDate: 2019/4/12 9:52
     */
    @Override
    public void Click(final int ipostion) {

        Log.d("Click"," " +  ipostion);
        final GizWifiDevice  device = DeviceList.get(ipostion);
        final String[] items = new String[]{"重命名设备", "删除设备"};
        QMUIDialog dilaog = new QMUIDialog.MenuDialogBuilder(contex)
                .addItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                CreateRenameDialog(device);
                                break;
                            case 1:
                                RemoveDevice(device);
                                break;
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /*
     * @Titl:
     * @Param
     * @Return:
     * @Description:删除设备
     * @author xundanqing
     * @CreateDate: 2019/4/16 14:28
     */
    private   void  RemoveDevice(GizWifiDevice device){

        if(device != null){
            UnBindDevice(device);
        }

    }
    /*
     * @Titl:
     * @Param
     * @Return:
     * @Description:重命名信息显示
     * @author xundanqing
     * @CreateDate: 2019/4/12 10:22
     */
    private   void   CreateRenameDialog(final GizWifiDevice device){

        device.setListener(gizWifiDeviceListener);
        final  QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(this);
        builder.setTitle("重命名设备？")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确认", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        if (builder.getEditText().getText().toString().isEmpty()) {
                            dialog.dismiss();
                            return;
                        }
                        device.setCustomInfo("xundanqing", builder.getEditText().getText().toString());
                        dialog.dismiss();
                    }
                })
                .show();
    }
    /*
     * @Titl:
     * @Param
     * @Return:
     * @Description:listview设置与长按删除与修改备注名信息
     * @author xundanqing
     * @CreateDate: 2019/4/10 15:29
     */
    private   void  CreateListView(){

        DeviceList = new  ArrayList<>();
        adapter   =  new  DeviceListView(DeviceList ,this,this);
        DeviceListView =  findViewById(R.id.ListDevice);
        DeviceListView.setAdapter(adapter);
        DeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                if (DeviceList.get(position).getNetStatus() ==
                        GizWifiDeviceNetStatus.GizDeviceOffline) {
                        return;
                }

                if(DeviceList.get(position).isBind()) {
                    DeviceList.get(position).setListener(gizWifiDeviceListener);
                    DeviceList.get(position).setSubscribe(Constant.PET_PS, true);
                }
            }
        });
    }
    /*
     * @Titl:
     * @Param
     * @Return:
     * @Description:界面下拉刷新并显示查找到的设备
     * @author xundanqing
     * @CreateDate: 2019/4/10 13:54
     */
    private   void   CreateScowView(){

        SwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.SwipeRefresh);
        SwipeRefresh.setProgressBackgroundColorSchemeResource(android.R.color.white);
        SwipeRefresh.setOnRefreshListener(new  SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Uid == null  || Token == null){
                    Toast.makeText(contex,"LOGIN  ERROR,CHECK  NET  LINK",Toast.LENGTH_SHORT).show();
                }
                GetLocalWlanDevices();
                SwipeRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SwipeRefresh.setRefreshing(false);
                    }
                },3000);
            }
        });
    }
    /*
     * @Titl:
     * @Param
     * @Return:
     * @Description:生成TopBar
     * @author xundanqing
     * @CreateDate: 2019/4/4 11:44
     */
    private    void  CreateTopBar(){

        TopBar  = findViewById(R.id.topBar);
        TopBar.setTitle(R.string.devicename);
        TopBar.addRightImageButton(R.mipmap.ic_add, R.id.TopBarMainRight).setOnClickListener(new View.OnClickListener() {

               @Override
                public void onClick(View view) {
                   startActivityForResult(new Intent(MainActivity.this,WifiConfig.class),105);
                }
            });
    }
    /*
 * @Titl:
 * @Param
 * @Return:
 * @Description:机智云操作回调函数
 * @author xundanqing
 * @CreateDate: 2019/4/4 11:43
 */
    GizWifiSDKListener gizWifiSDKListener = new GizWifiSDKListener() {
        @Override
        public void didBindDevice(GizWifiErrorCode result, String did) {
            super.didBindDevice(result, did);
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                adapter.notifyDataSetChanged();
            }
        }

        /*发现设备回调函数*/
        @Override
        public void didDiscovered(GizWifiErrorCode result, List<GizWifiDevice> deviceList) {
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                for(int  i=0; i<  deviceList.size();i++){
                    if(!deviceList.get(i).isBind())
                    BindDeviceWithApp(deviceList.get(i));
                }
                DeviceList.clear();
                DeviceList.addAll(deviceList);
            }else{
                DeviceList.clear();
            }
            adapter.notifyDataSetChanged();
        }

        /** 用于设备解绑 */
        public void didUnbindDevice(GizWifiErrorCode result, java.lang.String did) {

            if(result ==  GizWifiErrorCode.GIZ_SDK_SUCCESS){
                adapter.notifyDataSetChanged();
            }
        }


        /*设备Sdk  激活操作结果*/
        @Override
        public void didNotifyEvent(GizEventType eventType, Object eventSource, GizWifiErrorCode eventID, String eventMessage) {
            super.didNotifyEvent(eventType, eventSource, eventID, eventMessage);
            if (eventType == GizEventType.GizEventSDK) {
                GizWifiSDK.sharedInstance().userLoginAnonymous();
            }
        }

        /*设备Sdk 匿名登录结果*/
        @Override
        public void didUserLogin(GizWifiErrorCode result, String uid, String token) {
            super.didUserLogin(result, uid, token);
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                Toast.makeText(contex,"LOGIN  OK",Toast.LENGTH_SHORT).show();
                Uid   =  uid;
                Token =  token;
            }
        }
    };



    /*设备操作回调函数*/
    private GizWifiDeviceListener gizWifiDeviceListener = new GizWifiDeviceListener() {


        /*订阅回调*/
        @Override
        public void didSetSubscribe(GizWifiErrorCode result, GizWifiDevice device, boolean isSubscribed){

            Log.d("GizWifiDeviceListener","  " + result);
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                Intent intent = new Intent();
                intent.putExtra("device", device);
                intent.setClass(contex,DeviceControl.class);
                startActivity(intent);
            }else{
                Log.d("GizWifiDeviceListener","SUBSCRIBE  ERROR");
            }
        }


        /*重命名回调*/
        @Override
        public void didSetCustomInfo(GizWifiErrorCode result, GizWifiDevice device) {
            super.didSetCustomInfo(result, device);
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {

                if (GizWifiSDK.sharedInstance().getDeviceList().size() != 0) {
                    DeviceList.clear();
                    DeviceList.addAll(GizWifiSDK.sharedInstance().getDeviceList());
                    adapter.notifyDataSetChanged();
                }
            } else {
                Log.d("GizWifiDeviceListener","device   rename  err");
            }
        }

        @Override
        public void didUpdateNetStatus(GizWifiDevice device, GizWifiDeviceNetStatus netStatus) {
            adapter.notifyDataSetChanged();
        }

    };

    /*
     * @Titl:
     * @Param
     * @Return:
     * @Description:初始化Sdk
     * @author xundanqing
     * @CreateDate: 2019/4/4 11:33
     */
    private   void   InitSdk(){

        ConcurrentHashMap<String, String> appInfo = new ConcurrentHashMap<>();
        appInfo.put("appId", Constant.APP_ID);
        appInfo.put("appSecret", Constant.APP_SECRET);

        List<ConcurrentHashMap<String, String>> productInfo = new ArrayList<>();

        ConcurrentHashMap<String, String> product = new ConcurrentHashMap<>();
        product.put("productKey", Constant.PET_PK);
        product.put("productSecret", Constant.PET_PS);
        productInfo.add(product);

        GizWifiSDK.sharedInstance().setListener(gizWifiSDKListener);
        GizWifiSDK.sharedInstance().startWithAppInfo(contex, appInfo, productInfo, null, false);
    }

    /*
     * @Titl:
     * @Param
     * @Return:
     * @Description:
     * @author xundanqing
     * @CreateDate: 2019/4/10 13:44
     */
    private void GetLocalWlanDevices() {
        if (Uid != null && Token != null) {
            GizWifiSDK.sharedInstance().getBoundDevices(Uid,Token);
        }
    }
    /*
     * @Titl:
     * @Param
     * @Return:
     * @Description:判断绑定设备是不是能与现有的APP 进行绑定
     * @author xundanqing
     * @CreateDate: 2019/4/11 11:05
     */
    private  void  BindDeviceWithApp(GizWifiDevice  device){

        if (Uid != null && Token != null) {
            GizWifiSDK.sharedInstance().bindRemoteDevice(Uid, Token, device.getMacAddress(),
                    Constant.PET_PK, Constant.PET_PS);
        }
    }
    /*
     * @Titl:
     * @Param
     * @Return:
     * @Description:解绑远端设备
     * @author xundanqing
     * @CreateDate: 2019/4/16 13:57
     */
    private  void   UnBindDevice(GizWifiDevice  device){

        if (Uid != null && Token != null) {
            GizWifiSDK.sharedInstance().unbindDevice(Uid, Token, device.getDid());
        }
    }
}


