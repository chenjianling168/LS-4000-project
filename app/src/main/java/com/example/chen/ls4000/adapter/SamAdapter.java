package com.example.chen.ls4000.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.bean.Sample;
import com.example.chen.ls4000.utils.MyApp;
import com.example.chen.ls4000.utils.SharedHelper;


import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/5/5 0005.
 */

public class SamAdapter extends BaseAdapter {
    Context context;
    List<Sample> data ;
    private SharedHelper sp;
    private Activity testActivity;
    private String lan;
    private int checkedPosition = -1;
    private boolean onBind;

    public SamAdapter(Context context, List<Sample> data){
        this.context = context;
        this.data = data;
        MyApp.selectMap = new HashMap<Integer,Boolean>();
        sp = new SharedHelper(context);
        lan = sp.readLan();
    }

    public ViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.test_item_lv,parent,false);
        return  new ViewHolder1();
    }




    @Override
    public int getCount() {
        return data.size() ;

    }

    @Override
    public Object getItem(int position) {
        return data.get(position);    //data.get(position)
    }

    @Override
    public long getItemId(int position) {
        return position;
    }




    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder1 holder;
        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.test_item_lv,null,false);
            holder = new ViewHolder1();
//            holder.ID = (TextView)convertView.findViewById(R.id.id_test_item_id);
//            holder.sample = (TextView)convertView.findViewById(R.id.id_test_item_sample);
            holder.samnum = (TextView)convertView.findViewById(R.id.id_test_item_samnum);
            holder.proName = (TextView)convertView.findViewById(R.id.id_test_item_proname);
            holder.name = (TextView)convertView.findViewById(R.id.id_test_item_name);
            holder.age = (TextView)convertView.findViewById(R.id.id_test_item_age);
            holder.gender = (TextView)convertView.findViewById(R.id.id_test_item_gender);
            holder.choose = (CheckBox) convertView.findViewById(R.id.id_test_item_choose);
            holder.state = (TextView)convertView.findViewById(R.id.id_test_item_state);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder1) convertView.getTag();
        }
//        holder.ID.setText((position+1)+"");
//        holder.sample.setText(data.get(position).getSamNum());
        holder.proName.setText(data.get(position).getProName());
        holder.name.setText(data.get(position).getName());
        holder.age.setText(data.get(position).getAge());
        holder.gender.setText(data.get(position).getGender());
        holder.samnum.setText(data.get(position).getSamNum());
        holder.state.setText("");
        if("1".equals(sp.readFlap())){
            holder.state.setText(data.get(position).getState());    //"正在即使检测"
        }else if("2".equals(sp.readFlap())){
            holder.state.setText(data.get(position).getState()) ;    //"正在孵育检测"
        }else if("0".equals(sp.readFlap())){
            holder.state.setText("  ");
        }
        holder.choose.setChecked(data.get(position).isFlagClick());
        //holder.concl.setText("阴性");
        MyApp.selectMap.put(position,holder.choose.isChecked());
//        if(position == 0){
//            holder.choose.setChecked(true);
//        }

        holder.choose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try{
                    MyApp.selectMap.put(position, holder.choose.isChecked());
                    data.get(position).setFlagClick(holder.choose.isChecked());
                    System.out.println("数据" + holder.choose.isChecked());


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });



        return convertView;
    }

    ///返回当前CheckBox选中的位置,便于获取值.
    public int getCheckedPosition(){
        return checkedPosition;
    }

    public static  class ViewHolder1{
        public TextView ID,samnum,proName,name,age,gender,state;
        public CheckBox choose;


    }

}


