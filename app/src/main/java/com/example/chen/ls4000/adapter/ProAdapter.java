package com.example.chen.ls4000.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.bean.Project;
import com.example.chen.ls4000.utils.MyApp;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/5/5 0005.
 */

public class ProAdapter extends BaseAdapter {
    Context context;
    List<Project> data;
    public ProAdapter(Context context, List<Project> data){
        this.context = context;
        this.data = data;
        MyApp.selectMapPro = new HashMap<Integer,Boolean>();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder5 holder;
        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.pro_lv_layout,null,false);
            holder = new ViewHolder5();
            holder.pro = (TextView)convertView.findViewById(R.id.id_prolv_pro);
            holder.batch = (TextView)convertView.findViewById(R.id.id_prolv_batch);
            holder.bornTime = (TextView)convertView.findViewById(R.id.id_prolv_borntime);
            holder.shelfLife = (TextView)convertView.findViewById(R.id.id_prolv_shelflife);
            holder.choose = (CheckBox)convertView.findViewById(R.id.id_prolv_choose);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder5) convertView.getTag();
        }
        holder.pro.setText(data.get(position).getProName());
        holder.batch.setText(data.get(position).getBatch());
        holder.bornTime.setText(data.get(position).getBornTime());
        holder.shelfLife.setText(data.get(position).getShelfLife());
        holder.choose.setChecked(data.get(position).isClickFlag());
        //holder.end.setText(data.get(position).getUntilTime());
//        if(data.get(position).isClickFlag()){
//            convertView.setBackgroundResource(R.color.gray);
//        }else{
//            convertView.setBackgroundResource(R.color.white);
//        }
        MyApp.selectMapPro.put(position,holder.choose.isChecked());
        holder.choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.selectMapPro.put(position,holder.choose.isChecked());
                data.get(position).setClickFlag(holder.choose.isChecked());
                System.out.println("数据"+holder.choose.isChecked());
                onItemState.init(position);
            }
        });
        return convertView;
    }

    public interface OnItemState{
        void init(int position);
    }

    private OnItemState onItemState;

    public void setOnItemState(OnItemState onItemState){
        this.onItemState = onItemState;
    }

}

class ViewHolder5{
    TextView pro,batch,bornTime,shelfLife;
    CheckBox choose;
}

