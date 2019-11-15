package com.example.chen.ls4000.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.bean.BlueDevice;

import java.util.ArrayList;


public class BlueListAdapter extends BaseAdapter {
    private static final String TAG = "BlueListAdapter";
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<BlueDevice> mBlueList;                //<BlueDevice>
    public static int CONNECTED = 3;

    public BlueListAdapter(Context context, ArrayList<BlueDevice> blue_list) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mBlueList = blue_list;
    }

    @Override
    public int getCount() {
        return mBlueList.size();
    }

    @Override
    public Object getItem(int position) {
        return mBlueList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String[] mStateArray = {mContext.getString(R.string.string101), mContext.getString(R.string.string102),
                mContext.getString(R.string.string103), mContext.getString(R.string.string104)};

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_bluetooth, null);
            holder.tv_blue_name = (TextView) convertView.findViewById(R.id.tv_blue_name);
            holder.tv_blue_address = (TextView) convertView.findViewById(R.id.tv_blue_address);
            holder.tv_blue_state = (TextView) convertView.findViewById(R.id.tv_blue_state);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final BlueDevice device = mBlueList.get(position);
//        holder.tv_blue_name.setText(device.name);
//        holder.tv_blue_address.setText(device.address);
        String dName = device.getName() == null ? mContext.getString(R.string.string336) : device.getName();
        if (TextUtils.isEmpty(dName)) {
            dName = mContext.getString(R.string.string336);
        }
        holder.tv_blue_name.setText(dName);
        String dAddress = device.getAddress() == null ? "未知地址" : device.getAddress();
        if (TextUtils.isEmpty(dAddress)) {
            dAddress = "未知地址";
        }
        holder.tv_blue_address.setText(dAddress);
        holder.tv_blue_state.setText(mStateArray[device.state]);

        return convertView;
    }

    public final class ViewHolder {
        public TextView tv_blue_name;
        public TextView tv_blue_address;
        public TextView tv_blue_state;


    }

}
