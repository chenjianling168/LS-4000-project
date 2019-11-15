package com.example.chen.ls4000.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.bean.Sample;
import com.example.chen.ls4000.utils.MyApp;
import com.example.chen.ls4000.utils.SharedHelper;
import com.example.chen.ls4000.utils.TimeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/5/5 0005.
 */

public class SampleAdapter extends BaseAdapter {
    Context context;
    List<Sample> data;
    private Sample sample;
    private SharedHelper sp;
    private String lan;
    public SampleAdapter(Context context, List<Sample> data){
        this.context = context;
        this.data = data;
        MyApp.selectMap = new HashMap<Integer,Boolean>();
        sp = new SharedHelper(context);
        lan = sp.readLan();
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
        final ViewHolder2 holder;
        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.query_item_lv,null,false);
            holder = new ViewHolder2();
            holder.ID = (TextView)convertView.findViewById(R.id.id_query_item_id);
            holder.samNum = (TextView)convertView.findViewById(R.id.id_query_item_samnum);
            holder.name = (TextView)convertView.findViewById(R.id.id_query_item_name);
            holder.proName = (TextView)convertView.findViewById(R.id.id_query_item_proname);
            holder.time = (TextView)convertView.findViewById(R.id.id_query_item_time);
            holder.choose = (CheckBox) convertView.findViewById(R.id.id_query_item_choose);
            holder.layout = (LinearLayout) convertView.findViewById(R.id.id_query_item_layout);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder2) convertView.getTag();
        }
        holder.ID.setText((position+1)+"");
        holder.samNum.setText(data.get(position).getSamNum());
        holder.name.setText(data.get(position).getName());
        holder.proName.setText(data.get(position).getProName());

        String textTime =data.get(position).getTestTime();
        String minTime="yy.MM.dd";
        String resultTime=TimeUtils.getWantDate(textTime,minTime);
        holder.time.setText(resultTime);

//        if(data.get(position).getTestTime() !=null && data.get(position).getTestTime().length()>10){
//            holder.time.setText(data.get(position).getTestTime().substring(0,8));
//        }

        //holder.time.setText(data.get(position).getTestTime());
        holder.choose.setChecked(data.get(position).isFlagClick());
//        holder.layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MyApp.selectMap.put(position,holder.choose.isChecked());
//                //data.get(position).setFlag(holder.choose.isChecked());
//                System.out.println("数据"+holder.choose.isChecked());
//                onItemState.init(position);
//                holder.choose.setChecked(!holder.choose.isChecked());
//            }
//        });
        MyApp.selectMap.put(position,holder.choose.isChecked());
        holder.choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.selectMap.put(position,holder.choose.isChecked());
                data.get(position).setFlagClick(holder.choose.isChecked());
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

class ViewHolder2{
    LinearLayout layout;
    TextView ID,samNum,name,proName,time;
    CheckBox choose;
}

