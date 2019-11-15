package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.adapter.SampleAdapter;
import com.example.chen.ls4000.bean.Pro;
import com.example.chen.ls4000.bean.Project;
import com.example.chen.ls4000.bean.Sample;
import com.example.chen.ls4000.event.BleEvent;
import com.example.chen.ls4000.event.LisEvent;
import com.example.chen.ls4000.utils.BluetoothUtil;
import com.example.chen.ls4000.utils.DBUtils;
import com.example.chen.ls4000.utils.MyApp;
import com.example.chen.ls4000.utils.SharedHelper;
import com.example.chen.ls4000.utils.TimeUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class OperActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener {

    private DBUtils dbUtils;
    private PullToRefreshListView lv_oper;
    private Button choose,delete,upload,print;
    private ImageView back;
    private CheckBox allchoose;
    private MyApp myApp;
    private SharedHelper sp;
    private List<Sample> samples;
    private SampleAdapter adapter;
    private ArrayAdapter projectAdapter;

    private int onclickTimes = 0;
    private long mLastTime = 0;




    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter.notifyDataSetChanged();
            lv_oper.onRefreshComplete();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oper);
        initview();
        hideBottomUIMenu();
        setProgressBarIndeterminateVisibility(true);
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

    private void initview(){
        dbUtils = new DBUtils(this);

        back=(ImageView)findViewById(R.id.id_oper_back);             //返回
        lv_oper=(PullToRefreshListView)findViewById(R.id.id_oper_lv);        //listview
        choose=(Button)findViewById(R.id.id_oper_choose);       //全选
        delete=(Button)findViewById(R.id.id_oper_delete);       //删除
        upload=(Button)findViewById(R.id.id_oper_upload);       //上传
        print=(Button)findViewById(R.id.id_oper_print);       //打印
        allchoose=(CheckBox)findViewById(R.id.id_oper_allchoose);       //全选按钮

        back.setOnClickListener(this);
        choose.setOnClickListener(this);
        delete.setOnClickListener(this);
        upload.setOnClickListener(this);
        print.setOnClickListener(this);
        allchoose.setOnClickListener(this);


        EventBus.getDefault().register(this);
        myApp = (MyApp) getApplication();

        sp = new SharedHelper(getApplicationContext());
        //samples = dbUtils.search20SamplesByTime();

//

        try {
                    samples = dbUtils.searchSamples();
                    //samples.addAll(samples.subList(0,samples.size()));
                    adapter = new SampleAdapter(OperActivity.this,samples);
                    lv_oper.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }

      adapter.setOnItemState(new SampleAdapter.OnItemState() {
          @Override
          public void init(int position) {
              for (int i = 0; i < MyApp.selectMap.size(); i++) {
                  if (MyApp.selectMap.containsValue(false)) {
                      allchoose.setChecked(false);
                  } else {
                      allchoose.setChecked(true);
                  }
              }
          }
      });


        lv_oper.setOnItemClickListener(this);
        lv_oper.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        lv_oper.getRefreshableView().setDivider(null);
        lv_oper.getRefreshableView().setSelector(android.R.color.transparent);
        lv_oper.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            List<Sample> peoples = new ArrayList<Sample>();

                            peoples.clear();
                            peoples.addAll(samples);

                            samples.clear();
                            samples.addAll(peoples);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mHandler.sendEmptyMessage(0);
                }
                }).start();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BleEvent response) {
        //这里改数据
//        Sample sample = response.getSample();
//        PrintUtil.printTest(mSocket,sample);
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_oper_choose:      //全选
                allchoose.setChecked(true);
                for (int i = 0; i < samples.size(); i++) {
                    //if(isChecked) {
                    samples.get(i).setFlagClick(allchoose.isChecked());
                    adapter.notifyDataSetChanged();
                    // }
                }
                break;


            case R.id.id_oper_delete:  //删除
                final View view ;
                final TextView tip;

                Button ensure,cancel;

                view = View.inflate(this, R.layout.promptlayout, null);

                tip = (TextView) view.findViewById(R.id.id_prompt_tip);
                ensure = (Button) view.findViewById(R.id.id_prompt_ensure);
                cancel = (Button)view.findViewById(R.id.id_prompt_cancel);

                 tip.setText(getString(R.string.string32)+"\n");


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
                     for (int j = 0; j < samples.size(); j++) {
                        if(samples.get(j).isFlagClick()){
                                Sample sample = samples.get(j);
                                DBUtils.deleteSampleByTime(sample);
                            //samples.remove(j);
                    }
                }
                        samples.clear();
                        samples.addAll(dbUtils.searchSamples());
                         //samples=dbUtils.searchSamples();
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();

                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        allchoose.setChecked(false);
                        dialog.dismiss();
                    }
                });


                break;


            case R.id.id_oper_upload:       //上传
                Project project = new Project();
                for (int i = 0; i < samples.size(); i++) {
                    if(samples.get(i).isFlagClick()){
                        //pushBool[0] = true;
                        Sample sample = samples.get(i);
                        String timeStr = sample.getTestTime();
                        timeStr = timeStr.replaceAll("-", "");
                        timeStr = timeStr.replaceAll(" ", "");
                        timeStr = timeStr.replaceAll(":", "");
                        System.out.println("数据timeStr" + timeStr);

                        String textTime = timeStr;
                        String minTime="yyyyMMdd";
                        String resultTime = TimeUtils.getWantDate(textTime,minTime);

                        project = dbUtils.searchProject(sample.getProNum());  //上传

                        if(project==null){
                            showToast(this,getString(R.string.string221),1000);
                            return;
                        }else{
                            samples.get(i).setFlagClick(false);
                            adapter.notifyDataSetChanged();
                            showToast(this,getString(R.string.string222),1000);
                        }
                        sample.setProject(project);
                        int amount = Integer.valueOf(sample.getProject().getAmount());
                        String[] datas = new String[100];
                        datas = sample.getProject().getData().split(",");
                        Pro[] pros = new Pro[amount];
                        for (int j = 0; j < amount; j++) {
                            pros[j] = new Pro();
                            pros[j].setReferLow(datas[0+20*j]);
                            pros[j].setReferHigh(datas[1+20*j]);
                            pros[j].setUnit(datas[4+20*j]);
                        }

                        String[] cons = sample.getConcen().split("/");

                        for (int j = 0; j < amount; j++) {
                            String printString ="";
                            printString +="H|\\^&|||LS40000|||||Lis||P|E1394-97|"+resultTime+"\r\n" ;
                            printString +="P|1| |" + sample.getSamNum() + "||" + sample.getName() + "||" + sample.getAge() + "|";
                            if (getString(R.string.string3).equals(sample.getGender())) {
                                printString += "M";
                            } else if (getString(R.string.string4).equals(sample.getGender())) {
                                printString += "F";
                            } else {
                                printString += "U";
                            }
                            printString += "||||||" + sample.getDepart() + "||||甲状腺|||||||23" + "\r\n"
                                    + "O|1|" + sample.getSamNum() + "||" + sample.getProName() + "\r\n";

                            printString += "R|1|^^^" + sample.getProName() + "|" + cons[j] + "|";
                            printString += pros[j].getUnit() + "|" + pros[j].getReferLow() + " to " + pros[j].getReferHigh() + "|"+ "||||||"+ timeStr + "\r\n";
                            printString += "L|1|N" + "\r\n";
                            final LisEvent methodCode;
                            methodCode = new LisEvent();
                            methodCode.setMes(printString);
                            methodCode.setComm((byte) 0x32);
                            EventBus.getDefault().post(methodCode);

                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    //pushBool[0] = false;
                                }
                            }, 1000);
                        }
                    }
                }
                    break;
            case R.id.id_oper_print:        //打印
                if(sp.readBluetooth()){
                    if( "1".equals(sp.readUpdateFlag())) {
                        for (int j = 0; j < samples.size(); j++) {
                            if (samples.get(j).isFlagClick()) {
                                Sample sample = samples.get(j);
                                BleEvent bleEvent = new BleEvent();
                                bleEvent.setSample(sample);
                                EventBus.getDefault().post(bleEvent);
                                new Handler().postDelayed(new Runnable() {
                                    public void run() {
                                    }
                                }, 500);
                                sample.setFlagClick(false);
                            }
                        }
                    } else{
                        showToast(this,getString(R.string.string245),1000);
                    }
                }else{
                    showToast(this,getString(R.string.string82),1000);
                }

                adapter.notifyDataSetChanged();
                allchoose.setChecked(false);
                break;

            case R.id.id_oper_back:     //返回
                finish();
                break;

            case R.id.id_oper_allchoose:            //全选按钮
               // allchoose.setChecked(true);
                for (int i = 0; i < samples.size(); i++) {
                    //if(isChecked) {
                    samples.get(i).setFlagClick(allchoose.isChecked());
                    adapter.notifyDataSetChanged();
                    // }
                }
                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long mCurTime = System.currentTimeMillis();
        System.out.println("进入双击监听数据");
        //判断是否发生了双击
        if (mLastTime != 0 && mCurTime - mLastTime < 500) {
            System.out.println("************双击数据************onclickTimes************" + onclickTimes + "第" + "条");
            Intent intent = new Intent(OperActivity.this, ResultActivity.class);
            Sample sample = samples.get(position-1);
            sample.setNid(System.currentTimeMillis() + "");
            Bundle bundle = new Bundle();
            bundle.putSerializable("sample", sample);
            intent.putExtras(bundle);
            startActivity(intent);
            mLastTime = mCurTime - 500;
        } else {
            isSingClick(onclickTimes);
            mLastTime = mCurTime;
        }
    }

    //判断是否是单击
    private void isSingClick(final long times) {

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (onclickTimes == times) {
                    System.out.println("************isSingClick************" + onclickTimes + "第" + "条");
                }

            }
        }.start();
    }

}





