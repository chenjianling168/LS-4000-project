package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.adapter.ProjectAdapter;
import com.example.chen.ls4000.bean.ProString;
import com.example.chen.ls4000.bean.Project;
import com.example.chen.ls4000.bean.Sample;
import com.example.chen.ls4000.event.BleMethodCode;
import com.example.chen.ls4000.event.BleReadEvent;
import com.example.chen.ls4000.event.CardEvent;
import com.example.chen.ls4000.utils.DBUtils;
import com.example.chen.ls4000.utils.IDCard;
import com.example.chen.ls4000.utils.MyApp;
import com.example.chen.ls4000.utils.SharedHelper;
import com.example.chen.ls4000.utils.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddTestActivity extends AppCompatActivity implements View.OnClickListener {

    EditText idnum,name,age,gender,samplenum;       //testPeo
    TextView proName,sampleType;
    Button save,addCon;
    ImageView back,proGoto,typeGoto;
    Spinner testPeo , aud , sampTyle;
    private MyApp myApp;
    private RadioButton female,male;
    private RelativeLayout pro,type;
    private boolean readFlag;
    private DBUtils dbUtils;
    private SharedHelper sp;
    private Sample sample;

    private int onclickTimes = 0;

    private static final int MSG_CODE = 1001;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:
                    Bundle bundle = msg.getData();
                    String ss = bundle.getString("ss");
                    String nameStr = bundle.getString("name");
                    idnum.setText(ss.substring(5,ss.length()));
                    Calendar a=Calendar.getInstance();
                    System.out.println(a.get(Calendar.YEAR));//得到年
                    age.setText(a.get(Calendar.YEAR)-Integer.valueOf(ss.substring(1,5))+"");
                    if("2".equals(ss.substring(0,1))){
                        female.setChecked(true);
                    }else{
                        male.setChecked(true);
                    }
                    name.setText(nameStr);
                    break;
                case 2:
                    if(!readFlag) {
                        CardEvent cardEvent = new CardEvent();
                        cardEvent.setBys(new byte[]{(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x20, 0x01, 0x22});
                        cardEvent.setComm((byte) 0x31);
                        EventBus.getDefault().post(cardEvent);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtest);
        sp = new SharedHelper(getApplicationContext());
        initviews();

        EventBus.getDefault().register(this);


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
        idnum = (EditText) findViewById(R.id.id_addtest_idnum);
        name = (EditText) findViewById(R.id.id_addtest_name);
        age = (EditText) findViewById(R.id.id_addtest_age);
 //       gender = (EditText) findViewById(R.id.id_addtest_gender);
        female = (RadioButton) findViewById(R.id.id_addtest_female);//男
        male = (RadioButton) findViewById(R.id.id_addtest_male);    //女
        samplenum = (EditText) findViewById(R.id.id_addtest_samnum);
        proName = (TextView) findViewById(R.id.id_addtest_proname);     //Spinner
        sampleType = (TextView) findViewById(R.id.id_addtest_samtype);
        back = (ImageView)findViewById(R.id.id_addtest_back);
        save = (Button)findViewById(R.id.id_addtest_save);
        addCon = (Button)findViewById(R.id.id_addtest_addcon);
        proGoto = (ImageView)findViewById(R.id.id_addtest_progoto); //项目
        typeGoto = (ImageView)findViewById(R.id.id_addtest_typegoto);
        pro = (RelativeLayout)findViewById(R.id.id_addtest_pro);
        type = (RelativeLayout)findViewById(R.id.id_addtest_type);
        aud = (Spinner)findViewById(R.id.id_addtest_audPerson);         //审核人
        testPeo = (Spinner) findViewById(R.id.id_addtest_testPerson);       //医生

        hideBottomUIMenu();

        back.setOnClickListener(this);
        female.setOnClickListener(this);
        male.setOnClickListener(this);
        save.setOnClickListener(this);
        addCon.setOnClickListener(this);
        proGoto.setOnClickListener(this);
        typeGoto.setOnClickListener(this);
        pro.setOnClickListener(this);
        type.setOnClickListener(this);
        idnum.setOnClickListener(this);

        myApp = (MyApp) getApplication();

        if(getString(R.string.string8).equals(sp.readType())){
            sampleType.setText(getString(R.string.string8));
        }else if(getString(R.string.string9).equals(sp.readType())){
            sampleType.setText(getString(R.string.string9));
        }else if(getString(R.string.string10).equals(sp.readType())){
            sampleType.setText(getString(R.string.string10));
        }else if(getString(R.string.string11).equals(sp.readType())){
            sampleType.setText(getString(R.string.string11));
        }else{
            sampleType.setText(getString(R.string.string7));
        }
        final List<String> samTypes;
        samTypes = new ArrayList<String>();
        final ArrayAdapter<String> samTypeAdapter;
//      设置姓名长度
        if(sp.readLan().equals("en")){
            name.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        }else {
            name.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
        }
//设置检测人长度
//        if(sp.readLan().equals("en")){
//            testPeo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
//        }else {
//            testPeo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
//        }




//        if("中文".equals(lan)){
            samTypes.add(getString(R.string.string7));
            samTypes.add(getString(R.string.string8));
            samTypes.add(getString(R.string.string10));
            samTypes.add(getString(R.string.string9));
            samTypes.add(getString(R.string.string11));
//        }else if("English".equals(lan)){
//            sample.add("Whole blood");
//            sample.add("Serum");
//            sample.add("Urine");
//        }

        samTypeAdapter = new ArrayAdapter<String>(this, R.layout.simple__item, samTypes);
        samTypeAdapter.setDropDownViewResource(R.layout.simple__item);
//        sampTyle.setAdapter(samTypeAdapter);
//
//        sampTyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String[] sam = {"全血", "血清", "尿液"};
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                peoNum = sp.readType();
//            }
//        });



        final List<String> proNames;
        proNames = new ArrayList<String>();
        final ArrayAdapter<String> proNameAdapter;

        proNames.add("PCT");
        proNames.add("CRP");
        proNames.add("NT_proBNP");
        proNames.add("cTnl");

        proNameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, proNames);
        proNameAdapter.setDropDownViewResource(R.layout.simple__item);




        final  List<String> testtype;
        testtype = new ArrayList<String>();
        final  ArrayAdapter<String> testtypeAdapter;

        testtype.add(sp.readDocUser1());
        testtype.add(sp.readDocUser2());
        testtype.add(sp.readDocUser3());

        testtypeAdapter = new ArrayAdapter<String>(this,R.layout.simple__item,testtype);
        testtypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        testPeo.setAdapter(testtypeAdapter);

        testPeo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView)view;
                tv.setTextSize(20.0f);
                String[] sam = {sp.readDocUser1(),sp.readDocUser2(),sp.readDocUser3()};
                peoNum = sam[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                peoNum = sp.readDocUser1();
            }
        });

        final  List<String> testaud;
        testaud = new ArrayList<String>();
        final  ArrayAdapter<String> testaudAdapter;

        testaud.add(sp.readAuditor1());
        testaud.add(sp.readAuditor2());

        testaudAdapter = new ArrayAdapter<String>(this,R.layout.simple__item,testaud);
        testaudAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aud.setAdapter(testaudAdapter);

        aud.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView)view;
                tv.setTextSize(20.0f);
                String[] sam = {sp.readAuditor1(),sp.readAuditor2()};
                peoAud = sam[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                peoAud = sp.readAuditor1();
            }
        });


       // proName.setAdapter(proNameAdapter);

//        proName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                //String[] sam = {"全血", "血清", "尿液"};
//                //System.out.println("数据"+sam[position]);
//                //Toast.makeText(getActivity(),sam[position],Toast.LENGTH_LONG).show();
//                //  peoNum = sam[position];
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // Toast.makeText(getActivity(),"全血",Toast.LENGTH_LONG).show();
//                // peoNum = "全血";
//            }
//        });

        readFlag = false;

        //while (!readFlag){
//            Message message = handler.obtainMessage();
//            message.what = 1;
//            handler.sendMessage(message);
      //  }

        dbUtils = new DBUtils(this);

    }

    //点击空白处收起键盘
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (AddTestActivity.this.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(AddTestActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

    private String peoNum , peoAud;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }



    private Project project;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_addtest_back://返回
                idnum.setFocusable(true);
                name.setFocusable(true);
                age.setFocusable(true);
                female.setClickable(true);
                male.setClickable(true);
                BleMethodCode methodCode = new BleMethodCode(3);
                methodCode.setMes("");
                //methodCode.setMes("");
                methodCode.setComm((byte) 0x0A);
                EventBus.getDefault().post(methodCode);
                finish();
               // while (true) {

//                String ss = "738b";
//                String hexString="8b73";
//                ByteArrayOutputStream baos=new ByteArrayOutputStream(ss.length()/2);
//                //将每2位16进制整数组装成一个字节
//                for(int i=0;i<ss.length();i+=2)
//                    baos.write((hexString.indexOf(ss.charAt(i))<<4 |hexString.indexOf(ss.charAt(i+1))));
//                String bb = unicodeToString("\\u738b");
//                try {
//                    bb = new String(baos.toByteArray(), "Unicode");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                System.out.println("数据1"+bb);
//                    try {
//                        new Thread().wait(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
                break;
            case R.id.id_addtest_save://保存
                //身份证验证
                if(!"".equals(proName.getText().toString())) {
                    Boolean idCard = IDCard.IDCardValidate(idnum.getText().toString());
                    Sample sample = new Sample();
                    if("".equals(idnum.getText().toString())){
                        sample.setIDNum(idnum.getText().toString());
                    }else if(idCard  ){
                        sample.setIDNum(idnum.getText().toString());
                    }else{
                        showToast(this,getString(R.string.string243),1000);
                        return;
                    }
                    sample.setTestPeo(peoNum);
                    sample.setAudPeo(peoAud);
                    sample.setIDNum(idnum.getText().toString());
                    sample.setName(name.getText().toString());
                    sample.setAge(age.getText().toString());
                    sample.setProject(project);
                    sample.setProNum(project.getBatch());
                    if (female.isChecked()) {
                        sample.setGender(getString(R.string.string4));
                    } else if(male.isChecked()) {
                        sample.setGender(getString(R.string.string3));
                    }else{
                        sample.setGender("");
                    }
                    sample.setSamType(sampleType.getText().toString());
                    sample.setProName(proName.getText().toString());
                    sample.setSamNum(samplenum.getText().toString());
                    sample.setNid(System.currentTimeMillis() + "");
                    sample.setProject(project);
                    sample.setProNum(project.getBatch());
                    sample.setHos(sp.readHosUser());
                    sample.setDepart(sp.readDepartUser());
                    //sample.setDoc(sp.readDocUser());
                    sample.setRefer(project.getcRefer());
                    //sample.setFlagNull(true);
//                    for (int i = 0; i < myApp.getTestPeoples().size(); i++) {
//                        if (myApp.getTestPeoples().get(i).isFlagNull()) {
//                            myApp.getTestPeoples().add(i,sample);
//                            break;
//                        }
//                    }
                    if(myApp.getTestPeoples().size() == 1 && myApp.getTestPeoples().get(0).isFlagNull()){
                        myApp.getTestPeoples().set(0,sample);
                    }else {
                        myApp.getTestPeoples().add(sample);
                    }
//                    if(myApp.getTestPeoples().size()==1&&!myApp.getTestPeoples().get(0).isFlagNull()){
//                        myApp.getTestPeoples().set(0,sample);
//                    }else {
//                        myApp.getTestPeoples().add(sample);
//                    }
                    idnum.setFocusable(true);
                    name.setFocusable(true);
                    age.setFocusable(true);
                    female.setClickable(true);
                    male.setClickable(true);
                    myApp.getResultPeoples().add(sample);
                    methodCode = new BleMethodCode(3);
                    methodCode.setMes("");
                    //methodCode.setMes("");
                    methodCode.setComm((byte) 0x0A);
                    EventBus.getDefault().post(methodCode);
                    finish();
                }else{
                    Toast.makeText(this,getString(R.string.string2) ,Toast.LENGTH_SHORT).show();
                    methodCode = new BleMethodCode(3);
                    methodCode.setMes("");
                    methodCode.setComm((byte) 0x01);
                    EventBus.getDefault().post(methodCode);
                }
                break;
            case R.id.id_addtest_addcon://保存并继续添加
                if(!"".equals(proName.getText().toString())) {
                    Sample sample = new Sample();
                    Boolean idCard = IDCard.IDCardValidate(idnum.getText().toString());
                    if("".equals(idnum.getText().toString())){
                        sample.setIDNum(idnum.getText().toString());
                    }else if(idCard  ){
                        sample.setIDNum(idnum.getText().toString());
                    }else{
                        showToast(this,getString(R.string.string243),1000);
                        return;
                    }
                    sample.setTestPeo(peoNum);
                    sample.setAudPeo(peoAud);
                    sample.setIDNum(idnum.getText().toString());
                    sample.setName(name.getText().toString());
                    sample.setAge(age.getText().toString());
                    if (female.isChecked()) {
                        sample.setGender(getString(R.string.string4));
                    } else {
                        sample.setGender(getString(R.string.string3));
                    }
                    sample.setSamType(sampleType.getText().toString());
                    sample.setProName(proName.getText().toString());
                    sample.setSamNum(samplenum.getText().toString());
                    sample.setNid(System.currentTimeMillis() + "");
                    sample.setProject(project);
                    sample.setProNum(project.getBatch());
                    sample.setHos(sp.readHosUser());
                    sample.setDepart(sp.readDepartUser());
                   // sample.setDoc(sp.readDocUser());
                    sample.setRefer(project.getcRefer());
//                    for (int i = 0; i < myApp.getTestPeoples().size(); i++) {
//                        if (myApp.getTestPeoples().get(i).isFlagNull()) {
//                            myApp.getTestPeoples().add(i, sample);
//                            break;
//                        }
//                    }
                    if(myApp.getTestPeoples().size() == 1 && myApp.getTestPeoples().get(0).isFlagNull()){
                        myApp.getTestPeoples().add(0,sample);
                    }else {
                        myApp.getTestPeoples().add(sample);
                    }
                    myApp.getResultPeoples().add(sample);
                    idnum.setText("");
                    name.setText("");
                    age.setText("");
                    proName.setText("");
                    samplenum.setText("");
                    idnum.setFocusable(true);
                    name.setFocusable(true);
                    age.setFocusable(true);
                    female.setClickable(true);
                    male.setClickable(true);
                    finish();
                    Intent intent = new Intent(AddTestActivity.this,AddTestActivity.class);
                    startActivity(intent);
                    methodCode = new BleMethodCode(3);
                    methodCode.setMes("");
                    //methodCode.setMes("");
                    methodCode.setComm((byte) 0x01);
                    EventBus.getDefault().post(methodCode);
                    showToast(this,getString(R.string.string241) ,1000);
                }else{
                    showToast(this,getString(R.string.string2),1000);
                    methodCode = new BleMethodCode(3);
                    methodCode.setMes("");
                    methodCode.setComm((byte) 0x01);
                    EventBus.getDefault().post(methodCode);
                }
                break;
            case R.id.id_addtest_female:

                if(!female.isChecked()){
                    female.setChecked(true);
                    male.setChecked(false);
//                    female.setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.btn_selected_44, 0,0);
//                    male.setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.btn_unselected_44,0,0);
//                    female.setBackgroundResource(R.mipmap.btn_selected_44);
//                    male.setBackgroundResource(R.mipmap.btn_unselected_44);
                }
                break;
            case R.id.id_addtest_male:

                if(!male.isChecked()){
                    male.setChecked(true);
                    female.setChecked(false);
//                    male.setButtonDrawable(R.mipmap.btn_selected_44);
//                    female.setButtonDrawable(R.mipmap.btn_unselected_44);
                }
                break;
            case R.id.id_addtest_progoto:

                //popProTip();
                break;
            case R.id.id_addtest_typegoto:
                popTypeTip();
                break;
            case R.id.id_addtest_pro:
                //popProTip();
                break;
            case R.id.id_addtest_type:
                popTypeTip();
                break;
            case R.id.id_addtest_idnum:
                CardEvent cardEvent = new CardEvent();
                cardEvent.setBys(new byte[]{(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96,
                        0x69, 0x00, 0x03, 0x20, 0x01, 0x22});
                cardEvent.setComm((byte) 0x31);
                EventBus.getDefault().post(cardEvent);
                //myApp.setCardIDFlap("1");
                idnum.setFocusable(false);
                name.setFocusable(false);
                age.setFocusable(false);
                female.setClickable(false);
                male.setClickable(false);
                break;
        }
    }

    //确定的响应事件
    private void popDialog(String str) {
        new AlertDialog.Builder(this).setTitle(getString(R.string.string196))//设置对话框标题
                .setMessage(str)//设置显示的内容
                .setCancelable(false)
                .setPositiveButton(getString(R.string.string89), new DialogInterface.OnClickListener() {//添加确定按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                        dialog.dismiss();
                    }
                }).setNegativeButton(getString(R.string.string179), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(AddTestActivity.this,TimeActivity.class);
                startActivity(intent);
            }
        }).show();//在按键响应事件中显示此对话框

    }

    //确定的响应事件
    private void popDialog1(String str) {
        new AlertDialog.Builder(this).setTitle(getString(R.string.string196))//设置对话框标题
                .setMessage(str)//设置显示的内容
                .setCancelable(false)
                .setPositiveButton(getString(R.string.string89), new DialogInterface.OnClickListener() {//添加确定按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                        dialog.dismiss();
                    }
                }).setNegativeButton(getString(R.string.string150), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(AddTestActivity.this,PromanaActivity.class);
                startActivity(intent);
            }
        }).show();//在按键响应事件中显示此对话框

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BleReadEvent response){
        int  comm = response.getComm();
        String data = response.getMes();
        byte[] bys = response.getBys();
        BleMethodCode methodCode;
        if(comm == 0x02){
            String proBatch = data;

            project = dbUtils.searchProject(proBatch);


            if(project != null){
                int num = TimeUtils.overdueBool(project.getBornTime(),project.getShelfLife());
                if(num == 0){
                    proName.setText(project.getProName()+"");
                    showToast(this,getString(R.string.string240),1000);
                }else if(num == -1){
                    popDialog1("E108："+getString(R.string.string5));
                    methodCode = new BleMethodCode(3);
                    methodCode.setMes("");
                    methodCode.setComm((byte) 0x01);
                    EventBus.getDefault().post(methodCode);


                }else{
                    popDialog("E108："+getString(R.string.string309));    //getString(R.string.string292)
                    methodCode = new BleMethodCode(3);
                    methodCode.setMes("");
                    methodCode.setComm((byte) 0x01);
                    EventBus.getDefault().post(methodCode);
                }
            }else{
                //if("000".equals(data.substring(0,3))) {
                popDialog1("E108："+getString(R.string.string6));
                methodCode = new BleMethodCode(3);
                methodCode.setMes("");
                methodCode.setComm((byte) 0x01);
                EventBus.getDefault().post(methodCode);
                //}
            }
            //int pronum = Integer.valueOf(data.substring(0,3));
//            switch (pronum){
//                case 1:
//                    proName.setText("PCT");
//                    break;
//                case 2:
//                    proName.setText("CRP");
//                    break;
//                case 3:
//                    proName.setText("NT_proBNP");
//                    break;
//                case 4:
//                    proName.setText("cTNI");
//                    break;
//            }
        }
        else if(comm == 0x32){
            final String ss = unicodeToString(bys);
            final String nameStr = getName(bys);

            Message message = mHandler.obtainMessage();
            message.what = 1;
            Bundle bundle = new Bundle();
            bundle.putString("ss",ss);
            bundle.putString("name",nameStr);
            message.setData(bundle);
            mHandler.sendMessage(message);

        }
    }

    int proNum = 0;
    private void popProTip(){
        final View view;

//        if("English".equals(lan)){
//            view = View.inflate(this,R.layout.mydialog_eng,null);
//        }else{
        view = View.inflate(this, R.layout.protiplayout,null);
        //  }

//        final RelativeLayout pct,crp,ntprobnp,ctni;
//        final TextView pctText,crpText,ntprobnpText,ctniText;
//        final ImageView pctChoose,crpChoose,ntprobnpChoose,ctniChoose,close;
        ListView lv;
        ImageView close;
        final int[] flag = {0};

        lv = (ListView)view.findViewById(R.id.id_protip_lv);
        close = (ImageView)view.findViewById(R.id.id_protip_close);



//        pct = (RelativeLayout)view.findViewById(R.id.id_protip_pct);
//        crp = (RelativeLayout)view.findViewById(R.id.id_protip_crp);
//        ntprobnp = (RelativeLayout)view.findViewById(R.id.id_protip_ntprobnp);
//        ctni = (RelativeLayout)view.findViewById(R.id.id_protip_ctni);
//        pctText = (TextView)view.findViewById(R.id.id_protip_pcttext);
//        crpText = (TextView)view.findViewById(R.id.id_protip_crptext);
//        ntprobnpText = (TextView)view.findViewById(R.id.id_protip_ntprobnptext);
//        ctniText = (TextView)view.findViewById(R.id.id_protip_ctnitext);
//        pctChoose = (ImageView)view.findViewById(R.id.id_protip_pctchoose);
//        crpChoose = (ImageView)view.findViewById(R.id.id_protip_crpchoose);
//        ntprobnpChoose = (ImageView)view.findViewById(R.id.id_protip_ntprobnpchoose);
//        ctniChoose = (ImageView)view.findViewById(R.id.id_protip_ctnichoose);
//        close = (ImageView)view.findViewById(R.id.id_protip_close);
//
        final AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialog).create();
        dialog.setView(view);
        dialog.setCancelable(false);
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        params.height = getWindow().getWindowManager().getDefaultDisplay().getHeight();
        dialog.getWindow().setAttributes(params);

        final String[] proNames = sp.readPro().split(",");
        final List<ProString> proStrings = new ArrayList<ProString>();
        for (int i = 0; i < proNames.length; i++) {
            ProString proString = new ProString();
            proString.setProName(proNames[i]);
            if(i == 0){
                proString.setChoose(true);
            }else {
                proString.setChoose(false);
            }
            proStrings.add(proString);
        }

        final ProjectAdapter projectAdapter = new ProjectAdapter(this,proStrings);

        lv.setAdapter(projectAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < proStrings.size(); i++) {
                    proStrings.get(i).setChoose(false);
                }

                proStrings.get(position).setChoose(true);
                //proName.setText(proStrings.get(position).getProName());

                projectAdapter.notifyDataSetChanged();
                flag[0] = position;
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                proName.setText(proNames[flag[0]]);
                project = dbUtils.searchProName(proName.getText().toString());
//                project = dbUtils.searchProjectByProName(proNames[flag[0]]);
                dialog.dismiss();
            }
        });
    }

    int typeNum = 0;
    private void popTypeTip(){
        final View view;
//        if("English".equals(lan)){
//            view = View.inflate(this,R.layout.mydialog_eng,null);
//        }else{
        view = View.inflate(this, R.layout.typetiplayout,null);
        //  }

        final RelativeLayout wholeblood,serum,urine,other,plasma;
        final TextView wholebloodText,serumText,urineText,plasmaText,otherText;
        final ImageView wholebloodChoose,serumChoose,urineChoose,close,otherChoose,plasmaChoose;

        wholeblood = (RelativeLayout)view.findViewById(R.id.id_typetip_wholeblood);
        serum = (RelativeLayout)view.findViewById(R.id.id_typetip_serum);
        urine = (RelativeLayout)view.findViewById(R.id.id_typetip_urine);
        other = (RelativeLayout)view.findViewById(R.id.id_typetip_other);
        plasma = (RelativeLayout)view.findViewById(R.id.id_typetip_plasma);

        wholebloodText = (TextView)view.findViewById(R.id.id_typetip_wholebloodtext);
        serumText = (TextView)view.findViewById(R.id.id_typetip_serumtext);
        urineText = (TextView)view.findViewById(R.id.id_typetip_urinetext);
        plasmaText = (TextView)view.findViewById(R.id.id_typetip_plasmatext);
        otherText = (TextView)view.findViewById(R.id.id_typetip_othertext);

        wholebloodChoose = (ImageView)view.findViewById(R.id.id_typetip_wholebloodchoose);
        serumChoose = (ImageView)view.findViewById(R.id.id_typetip_serumchoose);
        urineChoose = (ImageView)view.findViewById(R.id.id_typetip_urinechoose);
        otherChoose = (ImageView)view.findViewById(R.id.id_typetip_otherchoose);
        plasmaChoose = (ImageView)view.findViewById(R.id.id_typetip_plasmachoose);

        close = (ImageView)view.findViewById(R.id.id_typetip_close);


        final AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialog).create();
        dialog.setView(view);
        dialog.setCancelable(false);
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        params.height = getWindow().getWindowManager().getDefaultDisplay().getHeight();
        dialog.getWindow().setAttributes(params);

        if(getString(R.string.string7).equals(sampleType.getText().toString())) {
            wholebloodChoose.setVisibility(View.VISIBLE);
            wholeblood.setBackgroundResource(R.drawable.samtype_shape_corner);
            wholebloodText.setTextColor(getResources().getColor(R.color.colorwhite));

        }else if(getString(R.string.string8).equals(sampleType.getText().toString())){
            serumChoose.setVisibility(View.VISIBLE);
            serum.setBackgroundResource(R.drawable.samtype_shape_corner);
            serumText.setTextColor(getResources().getColor(R.color.colorwhite));

        }else if(getString(R.string.string10).equals(sampleType.getText().toString())){
            urineChoose.setVisibility(View.VISIBLE);
            urine.setBackgroundResource(R.drawable.samtype_shape_corner);
            urineText.setTextColor(getResources().getColor(R.color.colorwhite));

        }else if(getString(R.string.string9).equals(sampleType.getText().toString())){
            plasmaChoose.setVisibility(View.VISIBLE);
            plasma.setBackgroundResource(R.drawable.samtype_shape_corner);
            plasmaText.setTextColor(getResources().getColor(R.color.colorwhite));

        }else if(getString(R.string.string11).equals(sampleType.getText().toString())){
            otherChoose.setVisibility(View.VISIBLE);
            other.setBackgroundResource(R.drawable.samtype_shape_corner);
            otherText.setTextColor(getResources().getColor(R.color.colorwhite));

        }

        wholeblood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wholebloodChoose.getVisibility() == View.GONE){
                    wholebloodChoose.setVisibility(View.VISIBLE);
                    wholeblood.setBackgroundResource(R.drawable.samtype_shape_corner);
                    wholebloodText.setTextColor(getResources().getColor(R.color.colorwhite));

                    serumChoose.setVisibility(View.GONE);
                    serum.setBackgroundResource(R.drawable.unsamtype_shape_corner);
                    serumText.setTextColor(getResources().getColor(R.color.colorlightblue));

                    urineChoose.setVisibility(View.GONE);
                    urine.setBackgroundResource(R.drawable.unsamtype_shape_corner);
                    urineText.setTextColor(getResources().getColor(R.color.colorlightblue));

                    otherChoose.setVisibility(View.GONE);
                    other.setBackgroundResource(R.drawable.unsamtype_shape_corner);
                    otherText.setTextColor(getResources().getColor(R.color.colorlightblue));

                    plasmaChoose.setVisibility(View.GONE);
                    plasma.setBackgroundResource(R.drawable.unsamtype_shape_corner);
                    plasmaText.setTextColor(getResources().getColor(R.color.colorlightblue));
                }
            }
        });
        serum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(serumChoose.getVisibility() == View.GONE){
                    serumChoose.setVisibility(View.VISIBLE);
                    serum.setBackgroundResource(R.drawable.samtype_shape_corner);
                    serumText.setTextColor(getResources().getColor(R.color.colorwhite));

                    wholebloodChoose.setVisibility(View.GONE);
                    wholeblood.setBackgroundResource(R.drawable.unsamtype_shape_corner);
                    wholebloodText.setTextColor(getResources().getColor(R.color.colorlightblue));

                    urineChoose.setVisibility(View.GONE);
                    urine.setBackgroundResource(R.drawable.unsamtype_shape_corner);
                    urineText.setTextColor(getResources().getColor(R.color.colorlightblue));

                    plasmaChoose.setVisibility(View.GONE);
                    plasma.setBackgroundResource(R.drawable.unsamtype_shape_corner);
                    plasmaText.setTextColor(getResources().getColor(R.color.colorlightblue));

                    otherChoose.setVisibility(View.GONE);
                    other.setBackgroundResource(R.drawable.unsamtype_shape_corner);
                    otherText.setTextColor(getResources().getColor(R.color.colorlightblue));
                }
            }
        });
        urine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(urineChoose.getVisibility() == View.GONE){
                    urineChoose.setVisibility(View.VISIBLE);
                    urine.setBackgroundResource(R.drawable.samtype_shape_corner);
                    urineText.setTextColor(getResources().getColor(R.color.colorwhite));

                    plasmaChoose.setVisibility(View.GONE);
                    plasma.setBackgroundResource(R.drawable.unsamtype_shape_corner);
                    plasmaText.setTextColor(getResources().getColor(R.color.colorlightblue));

                    otherChoose.setVisibility(View.GONE);
                    other.setBackgroundResource(R.drawable.unsamtype_shape_corner);
                    otherText.setTextColor(getResources().getColor(R.color.colorlightblue));

                    wholebloodChoose.setVisibility(View.GONE);
                    wholeblood.setBackgroundResource(R.drawable.unsamtype_shape_corner);
                    wholebloodText.setTextColor(getResources().getColor(R.color.colorlightblue));

                    serumChoose.setVisibility(View.GONE);
                    serum.setBackgroundResource(R.drawable.unsamtype_shape_corner);
                    serumText.setTextColor(getResources().getColor(R.color.colorlightblue));
                }
            }
        });

        plasma.setOnClickListener(new View.OnClickListener() {     //urine
            @Override
            public void onClick(View v) {
                if(plasmaChoose.getVisibility() == View.GONE){
                    plasmaChoose.setVisibility(View.VISIBLE);
                    plasma.setBackgroundResource(R.drawable.samtype_shape_corner);
                    plasmaText.setTextColor(getResources().getColor(R.color.colorwhite));

                    urineChoose.setVisibility(View.GONE);
                    urine.setBackgroundResource(R.drawable.unsamtype_shape_corner);
                    urineText.setTextColor(getResources().getColor(R.color.colorlightblue));

                    otherChoose.setVisibility(View.GONE);
                    other.setBackgroundResource(R.drawable.unsamtype_shape_corner);
                    otherText.setTextColor(getResources().getColor(R.color.colorlightblue));

                    wholebloodChoose.setVisibility(View.GONE);
                    wholeblood.setBackgroundResource(R.drawable.unsamtype_shape_corner);
                    wholebloodText.setTextColor(getResources().getColor(R.color.colorlightblue));

                    serumChoose.setVisibility(View.GONE);
                    serum.setBackgroundResource(R.drawable.unsamtype_shape_corner);
                    serumText.setTextColor(getResources().getColor(R.color.colorlightblue));
                }
            }
        });

        other.setOnClickListener(new View.OnClickListener() {       //urine
            @Override
            public void onClick(View v) {
                if(otherChoose.getVisibility() == View.GONE){
                    otherChoose.setVisibility(View.VISIBLE);
                    other.setBackgroundResource(R.drawable.samtype_shape_corner);
                    otherText.setTextColor(getResources().getColor(R.color.colorwhite));

                    urineChoose.setVisibility(View.GONE);
                    urine.setBackgroundResource(R.drawable.unsamtype_shape_corner);
                    urineText.setTextColor(getResources().getColor(R.color.colorlightblue));

                    plasmaChoose.setVisibility(View.GONE);
                    plasma.setBackgroundResource(R.drawable.unsamtype_shape_corner);
                    plasmaText.setTextColor(getResources().getColor(R.color.colorlightblue));

                    wholebloodChoose.setVisibility(View.GONE);
                    wholeblood.setBackgroundResource(R.drawable.unsamtype_shape_corner);
                    wholebloodText.setTextColor(getResources().getColor(R.color.colorlightblue));

                    serumChoose.setVisibility(View.GONE);
                    serum.setBackgroundResource(R.drawable.unsamtype_shape_corner);
                    serumText.setTextColor(getResources().getColor(R.color.colorlightblue));
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (wholebloodChoose.getVisibility() == View.VISIBLE) {
                    typeNum = 0;
                    sampleType.setText(getString(R.string.string7));
                }
                if (serumChoose.getVisibility() == View.VISIBLE) {
                    typeNum = 1;
                    sampleType.setText(getString(R.string.string8));
                }
                if (urineChoose.getVisibility() == View.VISIBLE) {
                    typeNum = 2;
                    sampleType.setText(getString(R.string.string10));
                }
                if(plasmaChoose.getVisibility() == View.VISIBLE){
                    typeNum = 3;
                    sampleType.setText(getString(R.string.string9));
                }
                if(otherChoose.getVisibility() == View.VISIBLE){
                    typeNum = 4;
                    sampleType.setText(getString(R.string.string11));
                }
                dialog.dismiss();
            }
        });

    }

    private static Pattern pattern = Pattern.compile("\\&\\#(\\d+)");

    public String unicodeToString(byte[] bys) {

        String age = "";
        String gender = "";
        String cardId = "";
        String ss = "";
        byte[] bytes = new byte[bys.length];

        int flag = -1;
        for (int i = 0; i < bys.length; i++) {
            if(bys[i] == 32 && bys[i+1] == 0 && bys[i+2] != 32){
                flag = i+2;
                break;
            }
        }
        for (int i = flag; i < flag + 2; i+=2) {
            gender += "&#"+bys[i+1]+bys[i]+";";
        }
        flag += 6;
        for (int i = flag; i < flag + 8; i+=2) {
            age += "&#"+bys[i+1]+bys[i]+";";
        }
        System.arraycopy(bys, flag + 8, bytes, 0, bys.length-(flag + 8));
        for (int i = 0; i < bytes.length; i++) {
            if(bytes[i] == 32 && bytes[i+1] == 0 && bytes[i+2] != 32){
                flag = i+2;
                break;
            }
        }
        for (int i = flag; i < flag + 36; i+=2) {
            cardId += "&#"+bytes[i+1]+bytes[i]+";";
        }

        ss = unicodeToascii(gender)+unicodeToascii(age)+unicodeToascii(cardId);
        return ss.toString();
    }

    public static String getName(byte[] bys) {

        String str = "";//unicode.replace("0x", "\\");
        int flag = -1;
        for (int i = 0; i < bys.length; i++) {
            if(bys[i] == 32){
                flag = i;
                break;
            }
        }
        String[] str1 = new String[flag - 14];
        int k = 0;
        for (int i = 14; i < flag; i++) {
            str1[k] = Integer.toHexString(bys[i]);
            if(str1[k].length()>2) {
                str1[k] = str1[k].substring(str1[k].length() - 2, str1[k].length());
            }else{
                for (int j = 0; j < 2-str1[k].length(); j++) {
                    str1[k] = "0"+str1[k];
                }
            }
            k++;
        }
        for (int i = 0; i < str1.length/2; i++) {
            //short s = (short)((bys[i*2+1]<<8)|(bys[i*2]&0xff));
            str += "\\u"+str1[i*2+1]+str1[i*2];
        }

        StringBuffer string = new StringBuffer();
        String[] hex = str.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            int data = Integer.parseInt(hex[i], 16);
            string.append((char) data);
        }
        System.out.println("身份证信息数据"+string.toString());
        return string.toString();
    }

    private String unicodeToascii(String str){
        StringBuilder sb = new StringBuilder();
        Matcher m =pattern.matcher(str);

        while (m.find()){
            sb.append((char)Integer.valueOf(m.group(1)).intValue());
        }

        return sb.toString();
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
}
