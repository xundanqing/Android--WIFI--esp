package com.example.esp.Util;

import android.content.Context;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class WifiInfo {

    private  Context context;
    private  static  WifiInfo  wifiInfo;

    private   WifiInfo(Context  context){
       this.context =  context;
    }

    public   static   WifiInfo getinstance(Context context){
        if(wifiInfo == null){
            wifiInfo =  new  WifiInfo(context);
        }
        return  wifiInfo;
    }

    /*
     * @Titl:
     * @Param
     * @Return: 返回当前的wifi 连接SSid
     * @Description:
     * @author xundanqing
     * @CreateDate: 2019/4/4 14:12
     */
    public   String  GetWifiSSid(){

        android.net.wifi.WifiInfo mWifiInfo = getConnectionInfo();
        String ssid = null;
        if (mWifiInfo != null && isWifiConnected()) {
            int len = mWifiInfo.getSSID().length();
            if (mWifiInfo.getSSID().startsWith("\"")
                    && mWifiInfo.getSSID().endsWith("\"")) {
                ssid = mWifiInfo.getSSID().substring(1, len - 1);
            } else {
                ssid = mWifiInfo.getSSID();
            }
        }
        return ssid;
    }
    /*
     * @Titl:
     * @Param
     * @Return:
     * @Description:Wifi 信息
     * @author xundanqing
     * @CreateDate: 2019/4/4 14:11
     */
    private android.net.wifi.WifiInfo getConnectionInfo() {
        WifiManager mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        android.net.wifi.WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        return wifiInfo;
    }
    /*
     * @Titl:
     * @Param
     * @Return:
     * @Description:当前是否Wifi在线
     * @author xundanqing
     * @CreateDate: 2019/4/4 14:09
     */
    private  boolean isWifiConnected() {
        NetworkInfo mWiFiNetworkInfo = getWifiNetworkInfo();
        boolean isWifiConnected = false;
        if (mWiFiNetworkInfo != null) {
            isWifiConnected = mWiFiNetworkInfo.isConnected();
        }
        return isWifiConnected;
    }

    /*
     * @Titl:
     * @Param
     * @Return:
     * @Description:获得网络连接信息
     * @author xundanqing
     * @CreateDate: 2019/4/4 14:08
     */
    private NetworkInfo getWifiNetworkInfo() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWiFiNetworkInfo;
    }
}
