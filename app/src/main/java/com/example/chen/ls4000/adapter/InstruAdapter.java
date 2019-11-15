package com.example.chen.ls4000.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.bean.Instrument;
import com.example.chen.ls4000.utils.MyApp;
import com.example.chen.ls4000.utils.SharedHelper;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/5/5 0005.
 */

public class InstruAdapter extends BaseAdapter {
    Context context;
    List<Instrument> data;
    private SharedHelper sp;
    private String lan;
    public InstruAdapter(Context context, List<Instrument> data){
        this.context = context;
        this.data = data;
        MyApp.selectMapInstr = new HashMap<Integer,Boolean>();
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
        final ViewHolder4 holder;
        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.instru_lv_layout,null,false);
            holder = new ViewHolder4();
            holder.pro = (TextView)convertView.findViewById(R.id.id_instrulv_pro);
            holder.time = (TextView)convertView.findViewById(R.id.id_instrulv_time);
            holder.result = (TextView)convertView.findViewById(R.id.id_instrulv_result);
            holder.choose = (CheckBox)convertView.findViewById(R.id.id_instrulv_choose);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder4) convertView.getTag();
        }
        holder.pro.setText((position+1)+"");
        holder.time.setText(data.get(position).getTime());
//        if(data.get(position).getResult().contains("0")){
//
//            if("English".equals(lan)){
//                holder.result.setText("Unqualified");
//            }else{
//                holder.result.setText("不合格");
//            }
//        }else{

//            if("English".equals(lan)){
//                holder.result.setText("Qualified");
//            }else{
        holder.result.setText(data.get(position).getResult());
        holder.choose.setChecked(data.get(position).isClickFlag());
         //   }
      //  }

//        if(data.get(position).isClickFlag()){
//            convertView.setBackgroundResource(R.color.gray);
//        }else{
//            convertView.setBackgroundResource(R.color.white);
//        }

        MyApp.selectMapInstr.put(position,holder.choose.isChecked());
        holder.choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.selectMapInstr.put(position,holder.choose.isChecked());
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

class ViewHolder4{
    TextView pro,time,result;
    CheckBox choose;
}

