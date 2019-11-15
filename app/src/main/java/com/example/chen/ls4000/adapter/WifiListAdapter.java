package com.example.chen.ls4000.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chen.ls4000.R;

public class WifiListAdapter extends ArrayAdapter<ScanResult> {

    private final LayoutInflater mInflater;
    private int mResource;

    public WifiListAdapter(Context context, int resource) {
        super(context, resource);
        mInflater = LayoutInflater.from(context);
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(mResource, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.wifi_name);
        TextView signl = (TextView) convertView.findViewById(R.id.wifi_signal);
        ImageView image = (ImageView) convertView.findViewById(R.id.wifi_image);

        ScanResult scanResult = getItem(position);
        name.setText(scanResult.SSID);

        int level = scanResult.level;
        if (level <= 0 && level >= -50) {
            signl.setText("信号很好");
            image.setImageResource(R.mipmap.ic_wifi_1);
        } else if (level < -50 && level >= -70) {
            signl.setText("信号较好");
            image.setImageResource(R.mipmap.ic_wifi_2);
        } else if (level < -70 && level >= -80) {
            signl.setText("信号一般");
            image.setImageResource(R.mipmap.ic_wifi_3);
        } else if (level < -80 && level >= -100) {
            signl.setText("信号较差");
            image.setImageResource(R.mipmap.ic_wifi_4);
        } else {
            signl.setText("信号很差");
            image.setImageResource(R.mipmap.ic_wifi_5);
        }

        return convertView;
    }

}
