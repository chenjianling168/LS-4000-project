package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class QueryActivity extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener{

    private Button choose,delete,upload,print;
    private ImageView back,hide;
    private CheckBox allChoose;
    private PullToRefreshListView lv;
    private DBUtils dbUtils;
    private List<Sample> samples;
    private SampleAdapter adapter;
    private TextView screen;
    private RelativeLayout layout;
    private RelativeLayout alpha;
    private int onclickTimes = 0;
    private long mLastTime = 0;
    private int i = 0;
    private TimePicker timePicker;
    private DatePicker datePicker;
    private String timeBegin,timeEnd;
    private String proStr, samNumStr, nameStr, ageStr, genderStr,startTime,endTime;

    private Button setTimeSure,setTimeCancle;
    private LinearLayout layout4;

    private ImageView caleStart,caleEnd;
    private TextView startTV,endTV;
    private EditText samNum,name,age;
    private Spinner project,gender;
    private Button query;
    private SharedHelper sp;
    private MyApp myApp;


    private ArrayAdapter<String> projectAdapter;
    private ArrayAdapter<String> genderAdapter;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter.notifyDataSetChanged();
            lv.onRefreshComplete();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        initviews();

    }


    public void showToast(final Activity activity, final String word,final long time){
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

        dbUtils = new DBUtils(this);

        choose = (Button)findViewById(R.id.id_query_choose);    //全选
        delete = (Button)findViewById(R.id.id_query_delete);    //删除
        upload = (Button)findViewById(R.id.id_query_upload);    //上传
        print = (Button)findViewById(R.id.id_query_print);      //打印
        back = (ImageView)findViewById(R.id.id_query_back);     //返回
        allChoose = (CheckBox)findViewById(R.id.id_query_allchoose);    //全选
        lv = (PullToRefreshListView) findViewById(R.id.id_query_lv);    //集合
        screen = (TextView)findViewById(R.id.id_query_screen);          //筛选
        layout = (RelativeLayout)findViewById(R.id.id_query_layout);    //相对布局
        hide = (ImageView)findViewById(R.id.id_query_hide);         //上拉
        alpha = (RelativeLayout)findViewById(R.id.id_query_alpha);  //
        caleStart = (ImageView)findViewById(R.id.id_query_starttime);
        caleEnd = (ImageView)findViewById(R.id.id_query_endtime);
        startTV = (TextView)findViewById(R.id.id_query_starttimetv);        //测试开始时间
        endTV = (TextView)findViewById(R.id.id_query_endtimetv);        //测试结束时间
        samNum = (EditText)findViewById(R.id.id_query_samnum);      //样本号
        name = (EditText)findViewById(R.id.id_query_name);      //姓名
        age = (EditText)findViewById(R.id.id_query_age);        //年龄
        gender = (Spinner)findViewById(R.id.id_query_gender);   //性别
        query = (Button)findViewById(R.id.id_query_query);      //查询

        layout4=(LinearLayout)findViewById(R.id.id_query_layout4);

        hideBottomUIMenu();

        choose.setOnClickListener(this);
        delete.setOnClickListener(this);
        upload.setOnClickListener(this);
        print.setOnClickListener(this);
        back.setOnClickListener(this);
        screen.setOnClickListener(this);
        hide.setOnClickListener(this);
        allChoose.setOnClickListener(this);
        caleStart.setOnClickListener(this);
        caleEnd.setOnClickListener(this);
        query.setOnClickListener(this);

        EventBus.getDefault().register(this);
        myApp = (MyApp) getApplication();

        sp = new SharedHelper(getApplicationContext());

        //samples = dbUtils.search20SamplesByTime();  //


        try{
            samples = dbUtils.searchSamples();  //search20SamplesByTime  searchSamples
            //samples.addAll(samples.subList(0,samples.size()));
            adapter = new SampleAdapter(QueryActivity.this,samples);
            lv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }


        //设置查询筛选页面的project
        project = (Spinner) findViewById(R.id.id_query_project);
        List<String> data_list = new ArrayList<>();
        //data_list.add(getString(R.string.string141));
        data_list.add("");
        List<Project> projects = dbUtils.search20Projects();
        for (Project project : projects){
            data_list.add(project.getProName());
        }
        //适配器
        projectAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        projectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        project.setAdapter(projectAdapter);


        adapter.setOnItemState(new SampleAdapter.OnItemState() {
            @Override
            public void init(int position) {
                // System.out.println("数据走到这里");
                //  System.out.println("数据是"+MyApp.selectMap.size());
                for (int i = 0; i < MyApp.selectMap.size(); i++) {
                    //  System.out.println("数据是"+MyApp.selectMap.get(i));
                    if (MyApp.selectMap.containsValue(false)) {
                        allChoose.setChecked(false);
                        //  System.out.println("数据不是全选");
                    } else {
                        allChoose.setChecked(true);
                        //   System.out.println("数据是全选");
                    }
                }

            }
        });

        lv.setOnItemClickListener(this);

        lv.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        lv.getRefreshableView().setDivider(null);
        lv.getRefreshableView().setSelector(android.R.color.transparent);

        lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                pullDownToRefresh();
            }
        });
    }



    private void pullDownToRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    List<Sample> peoples = new ArrayList<Sample>();
                    i++;
                    int count = dbUtils.searchSampleNum();
                    if(count%20 != 0){
                        count += 1;
                    }
                    if(i<count) {
                        peoples.clear();
                        //peoples.addAll(samples);
                        //筛选
                        peoples.addAll(dbUtils.searchSomePeoplesByTime(i, proStr, samNumStr, nameStr, ageStr, genderStr, startTime, endTime));
//                    for (int j = 0; j < peoples.size(); j++) {
//                        System.out.println("查询数据"+peoples.size() + "*" + i);
//                    }
                    }
                    samples.clear();
                    samples.addAll(peoples);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(0);
            }
        }).start();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_query_choose:      //全选
                allChoose.setChecked(true);
                for (int i = 0; i < samples.size(); i++) {
                    //if(isChecked) {
                    samples.get(i).setFlagClick(allChoose.isChecked());
                    adapter.notifyDataSetChanged();
                    // }
                }
                break;
            case R.id.id_query_delete:  //删除
                break;
            case R.id.id_query_upload://上传

                Project project1 = new Project();
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

                        project1 = dbUtils.searchProject(sample.getProNum());  //上传
                        if(project1==null){
                            showToast(this,getString(R.string.string221),1000);
                            return;
                        }else{
                            samples.get(i).setFlagClick(false);
                            adapter.notifyDataSetChanged();
                            showToast(this,getString(R.string.string222),1000);
                        }
                        sample.setProject(project1);
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
            case R.id.id_query_print:       //打印
                if(sp.readBluetoothState()){
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
                    }else{
                        showToast(this,getString(R.string.string245),1000);
                    }
                }else{
                    showToast(this,getString(R.string.string82),1000);
                }

                adapter.notifyDataSetChanged();
                allChoose.setChecked(false);

                break;
            case R.id.id_query_back:
                finish();
                break;
            case R.id.id_query_allchoose:       //按钮全选

                for (int i = 0; i < samples.size(); i++) {
                    //if(isChecked) {
                    samples.get(i).setFlagClick(allChoose.isChecked());
                    adapter.notifyDataSetChanged();
                    // }
                }
                break;
            case R.id.id_query_screen:      //筛选
                layout.setVisibility(View.VISIBLE);
                screen.setVisibility(View.GONE);
                hide.setVisibility(View.VISIBLE);
                alpha.setVisibility(View.VISIBLE);
                layout4.setVisibility(View.GONE);
                break;
            case R.id.id_query_hide:        //上拉
                startTV.setText("");
                endTV.setText("");
                samNum.setText("");
                name.setText("");
                age.setText("");
                gender.setSelection(0);
                project.setSelection(0);

                layout.setVisibility(View.GONE);
                screen.setVisibility(View.VISIBLE);
                hide.setVisibility(View.GONE);
                alpha.setVisibility(View.GONE);
                layout4.setVisibility(View.VISIBLE);

                samples = dbUtils.searchSamples();
                adapter = new SampleAdapter(QueryActivity.this,samples);
                lv.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                break;
            case R.id.id_query_starttime:       //开始查询时间
                setTime("start");
                break;
            case R.id.id_query_endtime:            //结束查询时间
                setTime("end");
                break;
            case R.id.id_query_query:       //筛选查询


                    proStr = project.getSelectedItem().toString();
//                if(proStr.equals("项目")||proStr.equals("Item")){
//                    proStr="";
//                }
                    samNumStr = samNum.getText().toString();        //项目样本号
                    nameStr = name.getText().toString();            //项目姓名
                    ageStr = age.getText().toString();              //年龄
                    genderStr = gender.getSelectedItem().toString();
                    if(proStr.equals("项目")||proStr.equals("Item")){
                        proStr="";
                    }

//                    if(genderStr.equals("性别")||genderStr.equals("Gender")){
//                        genderStr="";
//                    }
                    startTime = startTV.getText().toString();
                    endTime = endTV.getText().toString();

                    if(sp.readLan().equals("en")){
                        startTime="测试起始时间";
                        endTime="测试结束时间";
                    }


                    samples.clear();

                    //筛选
                    samples.addAll(dbUtils.searchSomePeoplesByTime(0,proStr,samNumStr,
                            nameStr,ageStr,genderStr,startTime,endTime));
                    adapter.notifyDataSetChanged();
                    layout.setVisibility(View.GONE);
                    screen.setVisibility(View.VISIBLE);
                    hide.setVisibility(View.GONE);
                    alpha.setVisibility(View.GONE);
                    layout4.setVisibility(View.VISIBLE);
                    InputMethodManager imm =(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
                    break;



        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long mCurTime = System.currentTimeMillis();
        Sample mSample =new Sample();
        System.out.println("进入双击监听数据");
        //判断是否发生了双击
        if (mLastTime != 0 && mCurTime - mLastTime < 500) {
            try {
                System.out.println("************双击数据************onclickTimes************" + onclickTimes + "第" + "条");
                Intent intent = new Intent(QueryActivity.this, ResultActivity.class);
                Sample sample = samples.get(position-1);
                sample.setNid(System.currentTimeMillis() + "");
                Bundle bundle = new Bundle();
                bundle.putSerializable("sample", sample);
                intent.putExtras(bundle);
                startActivity(intent);
                mLastTime = mCurTime - 500;
            }catch (Exception e){
                e.printStackTrace();
            }

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

    //点击空白处收起键盘
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (QueryActivity.this.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(QueryActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

    private void setTime(final String str){
        View view = View.inflate(QueryActivity.this, R.layout.settime_layout,null);
        datePicker = (DatePicker)view.findViewById(R.id.id_settime_datePicker);
        timePicker = (TimePicker)view.findViewById(R.id.id_settime_timepicker);
        setTimeSure = (Button)view.findViewById(R.id.id_settime_sure);
        setTimeCancle = (Button)view.findViewById(R.id.id_settime_cancel);
        final AlertDialog dialog = new AlertDialog.Builder(QueryActivity.this).create();

        resizePicker(datePicker);
        resizePicker(timePicker);

        dialog.setView(view);
        System.out.println("数据数据");
        dialog.show();
        Calendar calendar = Calendar.getInstance();
//        String curHour = timeBegin.substring(timeBegin.indexOf(" ")+1,timeBegin.length());
//        if(timeBegin.substring(timeBegin.indexOf(" "),timeBegin.length()-1).contains("0")){
//            curHour = curHour.substring(curHour.length()-1,curHour.length());
//        }



        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));

        String time = TimeUtils.getCurTimeMinute();
        //String time =TimeUtils.getCur2Time();
        timeBegin = time.substring(0,time.indexOf(" "));
        timeEnd = time.substring(time.indexOf(" ")+1,time.length());
        final int[] yearStr = new int[0];
        final int monthStr;
        final int dayStr;
        final int hourStr;
        final int minuteStr;

        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if(monthOfYear>8&&dayOfMonth>9) {
                    timeBegin = year + "-" + (monthOfYear+1) + "-" + dayOfMonth;
                }else if(monthOfYear<=8&&dayOfMonth>9){
                    timeBegin = year + "-0" + (monthOfYear+1) + "-" + dayOfMonth;
                }else if(monthOfYear>8&&dayOfMonth<=9){
                    timeBegin = year + "-" + (monthOfYear+1) + "-0" + dayOfMonth;
                }else{
                    timeBegin = year + "-0" + (monthOfYear+1) + "-0" + dayOfMonth;
                }
//                System.out.println("设置时间数据年份"+year);
//                System.out.println("设置时间数据月份"+monthOfYear);
//                System.out.println("设置时间数据日"+dayOfMonth);
//                timeBegin = year +""+ (monthOfYear+1)+"" + dayOfMonth + "";
//                System.out.println("设置时间数据"+timeBegin);
            }
        });
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
//                System.out.println("设置时间数据时"+hourOfDay);
//                System.out.println("设置时间数据分"+minute);
//                timeEnd += hourOfDay +""+ minute +"";
//                System.out.println("设置时间数据"+timeEnd);
                if(hourOfDay>9&&minute>9) {
                    timeEnd = hourOfDay + ":" + minute;
                }else if(hourOfDay<=9&&minute>9){
                    timeEnd = "0"+hourOfDay + ":" + minute;
                }else if(hourOfDay>9&&minute<=9){
                    timeEnd = hourOfDay + ":0" + minute;
                }else{
                    timeEnd = "0"+hourOfDay + ":0" + minute;
                }
            }
        });

        setTimeSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("设置时间数据"+timeBegin);

                //if(TimeUtils.getIfTime(timeBegin+" "+timeEnd)){
                if("start".equals(str)){
                    startTV.setText(timeBegin+" "+timeEnd);
                }else{
                    endTV.setText(timeBegin+" "+timeEnd);
                }
                dialog.dismiss();
//                }else{
//                    Toast.makeText(getActivity(),"您选择的时间有误，请重新选择时间！",Toast.LENGTH_SHORT).show();
//                }
            }
        });
        setTimeCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("start".equals(str)){
                    startTV.setText(R.string.string158);
                }else{
                    endTV.setText(R.string.string159);
                }
                dialog.dismiss();
            }
        });
    }


    //调整FrameLayout的大小
    private void resizePicker(FrameLayout tp){      //DatePicker和TimePicker继承自FrameLayout
        List<NumberPicker> nplist = findNumberPicker(tp);   //找到组成的NumberPicker
        for(NumberPicker np:nplist){
            resizeNumberPicker(np); //调整每个NumberPicker的宽度
        }
    }

    //得到viewGroup 里面的numberpicker组件
    private List<NumberPicker> findNumberPicker(ViewGroup viewGroup){
        List<NumberPicker> npList = new ArrayList<NumberPicker>();
        View child = null;
        if(null != viewGroup){
            for(int i=0;i<viewGroup.getChildCount();i++){
                child = viewGroup.getChildAt(i);
                if(child instanceof NumberPicker){
                    npList.add((NumberPicker)child);
                }else if(child instanceof LinearLayout){
                    List<NumberPicker> result = findNumberPicker((ViewGroup) child);
                    if(result.size()>0){
                        return result;
                    }
                }
            }
        }
        return npList;
    }

    //调整numberpicker大小
    private void resizeNumberPicker(NumberPicker np){       //ViewGroup.LayoutParams.WRAP_CONTENT
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 300);
        params.setMargins(10,50,10,50);
        np.setLayoutParams(params);
    }


      //隐藏虚拟按键，并且全屏
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

}
