package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.adapter.ProAdapter;
import com.example.chen.ls4000.bean.Project;
import com.example.chen.ls4000.bean.Sample;
import com.example.chen.ls4000.event.BleMethodCode;
import com.example.chen.ls4000.event.BleReadEvent;
import com.example.chen.ls4000.utils.DBUtils;
import com.example.chen.ls4000.utils.MyApp;
import com.example.chen.ls4000.utils.SharedHelper;
import com.example.chen.ls4000.utils.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PromanaActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener{

    private ImageView back;
    private TextView screen;
    private ListView lv;
    private Button scan,delete,clear;
    private DBUtils dbUtils;
    private List<Project> data;
    private ProAdapter proAdapter;
    private SharedHelper sp;
    private CheckBox allChoose;
    private Sample sample;
    private List<Project> addData;
    private  MyApp myApp;

    private int flap ;

   private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(flap == 1){
                return;
            }else if(flap ==0) {
                BleMethodCode methodCode;
                methodCode = new BleMethodCode(3);
                methodCode.setMes("");
                methodCode.setComm((byte) 0x0A);
                EventBus.getDefault().post(methodCode);
                showToast(PromanaActivity.this, "E107:"+getString(R.string.string246), 1500);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promana);

        initviews();
    }


    public void showToast(final Activity activity, final String word, final long time){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Toast toast = Toast.makeText(activity,word,Toast.LENGTH_SHORT);
                toast.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                },time);
            }
        });
    }

    private void initviews(){
        back = (ImageView)findViewById(R.id.id_promana_back);
        //screen = (TextView)findViewById(R.id.id_promana_screen);
        lv = (ListView)findViewById(R.id.id_promana_lv);
        scan = (Button)findViewById(R.id.id_promana_scan);
        delete = (Button)findViewById(R.id.id_promana_delete);
        clear = (Button)findViewById(R.id.id_promana_clear);
        allChoose = (CheckBox)findViewById(R.id.id_promana_allchoose);

        hideBottomUIMenu();

        back.setOnClickListener(this);
//        screen.setOnClickListener(this);
        scan.setOnClickListener(this);
        delete.setOnClickListener(this);
        clear.setOnClickListener(this);
        allChoose.setOnClickListener(this);

        EventBus.getDefault().register(this);
        sp = new SharedHelper(getApplicationContext());
        dbUtils = new DBUtils(this);

        myApp = (MyApp) getApplication();
        sample = new Sample();

        data = new ArrayList<Project>();
        addData = new ArrayList<Project>();

        //data.addAll(dbUtils.search20Projects());//显示20条
        data.addAll(dbUtils.searchProjects());
//        addData.addAll(dbUtils.search20Projects());
        proAdapter = new ProAdapter(PromanaActivity.this,data);

        lv.setAdapter(proAdapter);
        proAdapter.notifyDataSetChanged();

        proAdapter.setOnItemState(new ProAdapter.OnItemState() {
            @Override
            public void init(int position) {
                // System.out.println("数据走到这里");
                //  System.out.println("数据是"+MyApp.selectMap.size());
                for (int i = 0; i < MyApp.selectMapPro.size(); i++) {
                    //  System.out.println("数据是"+MyApp.selectMap.get(i));
                    if (MyApp.selectMapPro.containsValue(false)) {
                        allChoose.setChecked(false);
                        //  System.out.println("数据不是全选");
                    } else {
                        allChoose.setChecked(true);
                        //   System.out.println("数据是全选");
                    }
                }

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BleReadEvent response){
        System.out.println("收到数据" + response.getMes());

        int comm = response.getComm();
        String datames = response.getMes();
        byte[] bys = response.getBys();

        Project project = new Project();



        String ste=datames.substring(0,2);
        System.out.println("数据21654"+ste);

        if(!datames.contains("LS")){
            flap = 1;
            showToast(this,"E109:"+getString(R.string.string273),2500);
            BleMethodCode methodCode;
            methodCode = new BleMethodCode(3);
            methodCode.setMes("");
            methodCode.setComm((byte) 0x0A);
            EventBus.getDefault().post(methodCode);
            return;
        }


        if(comm == 0x0E){
            handler.removeCallbacks(runnable);
            String ss = response.getMes();
            System.out.println("数据"+ss);
            int flag = ss.indexOf(';');
            int start = ss.indexOf("L");
            String proStr = ss.substring(start,flag);
            String[] pros = proStr.split(",");
//            Project project = new Project();
            project.setAudit(pros[0]+"");
            project.setArea(pros[1]+"");
            project.setBatch(pros[2]+"");
            project.setProName(pros[3]+"");
            project.setBornTime(pros[4]+"");
            project.setShelfLife(pros[5]+"");
            project.setIncuTime(pros[6]+"");
            project.setAddSample(pros[7]+"");
            project.setCt1(pros[8]+"");
            project.setCt2(pros[9]+"");
            project.setCt3(pros[10]+"");
            project.setCline(pros[11]+"");   //C线位置
            project.setBackHigh(pros[12]+"");
            project.setcMin(pros[13]+"");
            project.setAmount(pros[14]+"");

            if("1".equals(project.getAmount())){
                project.setcRefer(pros[15]+"--"+pros[16]+"");   //参考值
                switch (pros[19]){
                    case "0":
                        project.setUnit("ug/ml");
                        break;
                    case "1":
                        project.setUnit("ng/ml");
                        break;
                    case "2":
                        project.setUnit("%");
                        break;
                    case "3":
                        project.setUnit("nmol/L");
                        break;
                    case "4":
                        project.setUnit("u IU/ml");
                        break;
                    case "5":
                        project.setUnit("pg/ml");
                        break;
                    case "6":
                        project.setUnit("mIU/ml");
                        break;
                }
            }else if("2".equals(project.getAmount())){
                project.setcRefer(pros[15]+"--"+pros[16]+"");   //参考值
                project.setcRefer2(pros[35]+"--"+pros[36]+"");   //参考值2
                switch (pros[19]){
                    case "0":
                        project.setUnit("ug/ml");
                        break;
                    case "1":
                        project.setUnit("ng/ml");
                        break;
                    case "2":
                        project.setUnit("%");
                        break;
                    case "3":
                        project.setUnit("nmol/L");
                        break;
                    case "4":
                        project.setUnit("u IU/ml");
                        break;
                    case "5":
                        project.setUnit("pg/ml");
                        break;
                    case "6":
                        project.setUnit("mIU/ml");
                        break;
                }

                switch (pros[39]){
                    case "0":
                        project.setUnit2("ug/ml");
                        break;
                    case "1":
                        project.setUnit2("ng/ml");
                        break;
                    case "2":
                        project.setUnit2("%");
                        break;
                    case "3":
                        project.setUnit2("nmol/L");
                        break;
                    case "4":
                        project.setUnit2("u IU/ml");
                        break;
                    case "5":
                        project.setUnit2("pg/ml");
                        break;
                    case "6":
                        project.setUnit2("mIU/ml");
                        break;
                }
            }else if("3".equals(project.getAmount())){
                project.setcRefer(pros[15]+"--"+pros[16]+"");   //参考值
                project.setcRefer2(pros[35]+"--"+pros[36]+"");   //参考值2
                project.setcRefer3(pros[55]+"--"+pros[56]+"");   //参考值3
                switch (pros[19]){
                    case "0":
                        project.setUnit("ug/ml");
                        break;
                    case "1":
                        project.setUnit("ng/ml");
                        break;
                    case "2":
                        project.setUnit("%");
                        break;
                    case "3":
                        project.setUnit("nmol/L");
                        break;
                    case "4":
                        project.setUnit("u IU/ml");
                        break;
                    case "5":
                        project.setUnit("pg/ml");
                        break;
                    case "6":
                        project.setUnit("mIU/ml");
                        break;
                }

                switch (pros[39]){
                    case "0":
                        project.setUnit2("ug/ml");
                        break;
                    case "1":
                        project.setUnit2("ng/ml");
                        break;
                    case "2":
                        project.setUnit2("%");
                        break;
                    case "3":
                        project.setUnit2("nmol/L");
                        break;
                    case "4":
                        project.setUnit2("u IU/ml");
                        break;
                    case "5":
                        project.setUnit2("pg/ml");
                        break;
                    case "6":
                        project.setUnit2("mIU/ml");
                        break;
                }

                switch (pros[59]){
                    case "0":
                        project.setUnit3("ug/ml");
                        break;
                    case "1":
                        project.setUnit3("ng/ml");
                        break;
                    case "2":
                        project.setUnit3("%");
                        break;
                    case "3":
                        project.setUnit3("nmol/L");
                        break;
                    case "4":
                        project.setUnit3("u IU/ml");
                        break;
                    case "5":
                        project.setUnit3("pg/ml");
                        break;
                    case "6":
                        project.setUnit3("mIU/ml");
                        break;
                }
            }

            //校正因子
            //全血pro[29]
            project.setWholeBlood(pros[29]);
            //血清
            project.setSerum(pros[30]);
            //血浆
            project.setPlasma(pros[31]);
            //尿液
            project.setUrine(pros[32]);
            //其他
            project.setOther(pros[33]);

            project.setData("");
            for (int i = 15; i < pros.length-1; i++) {
                project.setData(project.getData()+pros[i]+",");
            }
             project.setAddTime(TimeUtils.getCurTime()+"");
            //project.setAddTime(TimeUtils.getCur2Time()+"");
            System.out.println("数据31321654"+datames);
            System.out.println("数据31321654"+bys);

            if(!TimeUtils.overdueBool(project.getBornTime())){
                showToast(this,getString(R.string.string274),1500);
                return;
            }


            if(dbUtils.saveProjects(project)){
                if(!sp.readBatch().contains(project.getBatch())) {
                    sp.saveBatch(sp.readBatch() + project.getBatch() + ",");
                    data.add(0,project);                        //项目添加成功
                }else{
                    showToast(this,"项目重复添加",1500);
                }

            }else if(dbUtils.deleteResult(project)){
                data.clear();
                data.addAll(dbUtils.searchProjects());
            }



            dbUtils.searchProjectNumBatch();
            proAdapter.notifyDataSetChanged();
            if(!sp.readPro().contains(project.getProName())) {
                sp.savePro(sp.readPro() + project.getProName() + ",");
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_promana_back://返回
                BleMethodCode methodCode;
                 methodCode = new BleMethodCode(3);
                methodCode.setMes("");
                methodCode.setComm((byte) 0x0A);
                EventBus.getDefault().post(methodCode);
                finish();

                break;
//            case R.id.id_promana_screen://筛选
//                break;
            case R.id.id_promana_scan://扫码添加
                methodCode = new BleMethodCode(3);
                methodCode.setMes("");
                methodCode.setComm((byte) 0x01);
                EventBus.getDefault().post(methodCode);
                flap=0;
                handler.postDelayed(runnable,15000);
                break;
            case R.id.id_promana_allchoose:
                for (int i = 0; i < data.size(); i++) {
                    //if(isChecked) {
                    data.get(i).setClickFlag(allChoose.isChecked());
                    proAdapter.notifyDataSetChanged();
                    // }
                }
                break;
            case R.id.id_promana_delete://删除
                if(myApp.getTestPeoples().size()>0){
                    showToast(this,getString(R.string.string322),2000);
                    return;
                }
                popTip(1);
                break;
            case R.id.id_promana_clear://清空
                popTip(2);
                break;
        }
    }


    private void popDialog(String str) {
        new AlertDialog.Builder(this).setTitle(getString(R.string.string196))//设置对话框标题
                .setMessage(str)//设置显示的内容
                .setCancelable(false)
                .setPositiveButton(getString(R.string.string89), new DialogInterface.OnClickListener() { //添加确定按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                        dialog.dismiss();
                    }
                }).setPositiveButton(getString(R.string.string90),null).show();

    }



    private void popTip(final int n) {
        final View view;

//        if("English".equals(lan)){
//            view = View.inflate(this,R.layout.mydialog_eng,null);
//        }else{
        view = View.inflate(this, R.layout.promptlayout, null);
        //  }

        final TextView tip;
        Button ensure,cancel;

        tip = (TextView) view.findViewById(R.id.id_prompt_tip);
        ensure = (Button) view.findViewById(R.id.id_prompt_ensure);
        cancel = (Button)view.findViewById(R.id.id_prompt_cancel);

        if(n== 1){
            tip.setText(getString(R.string.string32));
        }else if(n== 2){
            tip.setText(getString(R.string.string33));
        }
        //tip.setText(str);

        final AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialog).create();
        dialog.setView(view);
        dialog.setCancelable(false);
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        params.height = getWindow().getWindowManager().getDefaultDisplay().getHeight();
        dialog.getWindow().setAttributes(params);

        ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(n== 1){

                    for (int i = 0; i < data.size(); i++) {
                        if(data.get(i).isClickFlag()){
                            dbUtils.deleteProject(data.get(i));
                            Object result= deleteSubString(sp.readBatch(), data.get(i).getBatch());
                            sp.saveBatch(result + "");
                        }
                    }
                    data.clear();
                    data.addAll(dbUtils.searchProjects());
                    proAdapter.notifyDataSetChanged();
                }else if(n == 2){
                    sp.saveBatch("");
                    for (int i = 0; i < data.size(); i++) {
                        dbUtils.deleteProject(data.get(i));
                    }
                    data.clear();
                    proAdapter.notifyDataSetChanged();
                }
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    /**
     * 删除方法一
     * @return
     */
    public Object[] deleteSubString(String str1,String str2) {
        StringBuffer sb = new StringBuffer(str1);
        int delCount = 0;
        Object[] obj = new Object[2];

        while (true) {
            int index = sb.indexOf(str2);
            if(index == -1) {
                break;
            }
            sb.delete(index, index+str2.length());
            delCount++;

        }
        if(delCount!=0) {
            obj[0] = sb.toString();
            obj[1] = delCount;
        }else {
            //不存在返回-1
            obj[0] = -1;
            obj[1] = -1;
        }

        return obj;
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
