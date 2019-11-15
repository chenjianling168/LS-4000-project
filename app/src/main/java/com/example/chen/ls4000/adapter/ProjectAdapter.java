package com.example.chen.ls4000.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.bean.ProString;

import java.util.List;

/**
 * Created by Administrator on 2017/5/5 0005.
 */

public class ProjectAdapter extends BaseAdapter {
    Context context;
    List<ProString> data;
    public ProjectAdapter(Context context, List<ProString> data){
        this.context = context;
        this.data = data;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder6 holder;
        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.project_lv_layout,null,false);
            holder = new ViewHolder6();
            holder.layout = (RelativeLayout)convertView.findViewById(R.id.id_protip_layout);
            holder.text = (TextView)convertView.findViewById(R.id.id_protip_text);
            holder.choose = (ImageView) convertView.findViewById(R.id.id_protip_choose);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder6) convertView.getTag();
        }

        holder.text.setText(data.get(position).getProName()+"");
        if(data.get(position).isChoose()){
            holder.layout.setBackgroundResource(R.drawable.samtype_shape_corner);
            holder.choose.setVisibility(View.VISIBLE);
            holder.text.setTextColor(context.getResources().getColor(R.color.colorwhite));
        }else{
            holder.layout.setBackgroundResource(R.drawable.unsamtype_shape_corner);
            holder.choose.setVisibility(View.INVISIBLE);
            holder.text.setTextColor(context.getResources().getColor(R.color.colorlightblue));
        }

//        holder.pro.setText(data.get(position).getProName());
//        holder.batch.setText(data.get(position).getBatch());
//        holder.bornTime.setText(data.get(position).getBornTime());
//        holder.shelfLife.setText(data.get(position).getShelfLife());
        //holder.end.setText(data.get(position).getUntilTime());
//        if(data.get(position).isClickFlag()){
//            convertView.setBackgroundResource(R.color.gray);
//        }else{
//            convertView.setBackgroundResource(R.color.white);
//        }
        return convertView;
    }


}

class ViewHolder6{
    RelativeLayout layout;
    ImageView choose;
    TextView text;
}

