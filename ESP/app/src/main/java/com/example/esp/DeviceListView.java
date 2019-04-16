package com.example.esp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.esp.Util.ListviewClick;
import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;

import java.util.List;

public class DeviceListView extends BaseAdapter implements   View.OnClickListener {

    private Context  context;
    private LayoutInflater       inflater;
    private List<GizWifiDevice>  DeviceList;
    private ListviewClick        ClickInterface;

    public DeviceListView(List<GizWifiDevice> deviceList , Context context ,ListviewClick clickInterface) {
        DeviceList      =  deviceList;
        this.context    =  context;
        this.ClickInterface =  clickInterface;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return DeviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return DeviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onClick(View v) {
      if(ClickInterface != null){
            int  ipostion  = (int) v.findViewById(R.id.Button).getTag();
            ClickInterface.Click(ipostion);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View  view = null;
        ViewHolerListView holder = null;
        GizWifiDevice     device = DeviceList.get(position);

        if (convertView == null) {
            view = inflater.inflate(R.layout.listdevice, null);
            holder = new ViewHolerListView();
            holder.DeviceName =    (TextView)  view.findViewById(R.id.DeviceName);
            holder.DeviceState =   (TextView)  view.findViewById(R.id.DeviceState);
            holder.DeviceImage =   (ImageView) view.findViewById(R.id.imageDevice);
            holder.DeviceBind  =   (TextView)  view.findViewById(R.id.DeviceBind);
            holder.DeviceControl = (ImageView) view.findViewById(R.id.Button);
            holder.DeviceControl.setTag(position);
            view.setTag(holder);

        }else {
            view = convertView;
            holder = (ViewHolerListView) view.getTag();
        }
        holder.DeviceControl.setOnClickListener(this);

        if (!device.getAlias().isEmpty()) {
            holder.DeviceName.setText(device.getAlias());
        } else {
            holder.DeviceName.setText(device.getProductName());
        }
        if(device.isLAN()){
            holder.DeviceState.setText("局域网在线");
        }
        if (device.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceOffline) {
            holder.DeviceState.setText("离线");

        }else{
            holder.DeviceState.setText("在线");
        }

        if(device.isBind()){
            holder.DeviceBind.setText("绑定成功");
        }else{
            holder.DeviceBind.setText("绑定失败");
        }

        if(!device.isBind() || device.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceOffline){
            holder.DeviceName.setTextColor(Color.GRAY);
            holder.DeviceState.setTextColor(Color.GRAY);
            holder.DeviceBind.setTextColor(Color.GRAY);

        }else{
            holder.DeviceName.setTextColor(Color.BLACK);
            holder.DeviceState.setTextColor(Color.BLACK);
            holder.DeviceBind.setTextColor(Color.BLACK);
        }
        return   view;
    }

    private class ViewHolerListView {
        TextView  DeviceState;
        TextView  DeviceName;
        TextView  DeviceBind;
        ImageView DeviceImage;
        ImageView DeviceControl;
    }
}
