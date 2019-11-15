package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
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

public class UpdateTestActivity extends AppCompatActivity implements View.OnClickListener {

    EditText name,age,gender,samplenum;
    Button save;
    Spinner testPeo , aud;
    private ImageView back,proGoto,typeGoto;
    private MyApp myApp;
    private Sample sample;
    private RadioButton male,female;
    private TextView idnum,proName,sampleType;
    private RelativeLayout pro,type;
    private int num1,num2;
    private SharedHelper sp;
    private Project project;
    private DBUtils dbUtils;
    private boolean readFlag;
    private Button addCon;



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
        setContentView(R.layout.activity_updatetest);

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
        idnum = (EditText) findViewById(R.id.id_updatetest_idnum);
        name = (EditText) findViewById(R.id.id_updatetest_name);
        age = (EditText) findViewById(R.id.id_updatetest_age);
        testPeo = (Spinner) findViewById(R.id.id_updatetest_testPerson);
        samplenum = (EditText) findViewById(R.id.id_updatetest_samnum);
        proName = (TextView) findViewById(R.id.id_updatetest_proname);
        sampleType = (TextView) findViewById(R.id.id_updatetest_samtype);
        back = (ImageView)findViewById(R.id.id_updatetest_back);
        save = (Button)findViewById(R.id.id_updatetest_save);
        addCon = (Button)findViewById(R.id.id_updatetest_addcon);
        female = (RadioButton)findViewById(R.id.id_updatetest_female);
        male = (RadioButton)findViewById(R.id.id_updatetest_male);
        proGoto = (ImageView)findViewById(R.id.id_updatetest_progoto);      //项目ID
        typeGoto = (ImageView)findViewById(R.id.id_updatetest_typegoto);
        pro = (RelativeLayout)findViewById(R.id.id_updatetest_pro);
        type = (RelativeLayout)findViewById(R.id.id_updatetest_type);
        aud = (Spinner)findViewById(R.id.id_updatetest_audPerson);

        hideBottomUIMenu();

        dbUtils = new DBUtils(this);
        idnum.setOnClickListener(this);


        Bundle bundle = getIntent().getExtras();
        sample = (Sample) bundle.getSerializable("sample");
        if(!sample.isFlagNull()) {
            idnum.setText(sample.getIDNum());
            name.setText(sample.getName());
//
////得到AssetManager
//        AssetManager mgr=getAssets();
//
////根据路径得到Typeface
//        Typeface tf=Typeface.createFromAsset(mgr, "fonts/W5(P).TTF");
//
////设置字体
//        name.setTypeface(tf);
//        name.setText("修改字体");
            age.setText(sample.getAge());
//        gender.setText(sample.getGender());
            if(getString(R.string.string3).equals(sample.getGender())){
                male.setChecked(true);
            }else if(getString(R.string.string4).equals(sample.getGender())){
                female.setChecked(true);
            }else{
                male.setChecked(false);
                female.setChecked(false);
            }
            //testPeo.setText(sample.getTestPeo());
            samplenum.setText(sample.getSamNum());
            proName.setText(sample.getProName());
            sampleType.setText(sample.getSamType());
            if("CRP".equals(proName)){
                num1 = 1;
            }else if("NT_proBNP".equals(proName)){
                num1 = 2;
            }else if("cTnl".equals(proName)){
                num1 = 3;
            }else{
                num1 = 0;
            }

            if(getString(R.string.string8).equals(sampleType.getText().toString())){
                num2 = 1;
            }else if(getString(R.string.string9).equals(sampleType.getText().toString())){
                num2 = 3;
            }else if(getString(R.string.string7).equals(sampleType.getText().toString())){
                num2 = 0;
            }else if(getString(R.string.string10).equals(sampleType.getText().toString())){
                num2 = 2;
            }else if(getString(R.string.string11).equals(sampleType.getText().toString())){
                num2 = 4;
            }
        }

        back.setOnClickListener(this);
        save.setOnClickListener(this);
        addCon.setOnClickListener(this);
        female.setOnClickListener(this);
        male.setOnClickListener(this);
        proGoto.setOnClickListener(this);
        typeGoto.setOnClickListener(this);
        pro.setOnClickListener(this);
        type.setOnClickListener(this);

        myApp = (MyApp) getApplication();

        sp = new SharedHelper(this);
        EventBus.getDefault().register(this);


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

        final  List<String> testtype;
        testtype = new ArrayList<String>();
        final ArrayAdapter<String> testtypeAdapter;

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

        readFlag = false;



    }


    private String peoNum  ,peoAud;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BleReadEvent response){
        int  comm = response.getComm();
        String data = response.getMes();
        byte[] bys = response.getBys();
        System.out.println("数据"+data+comm);
        if(comm == 0x02){
            String proBatch = data;


            project = dbUtils.searchProject(proBatch);

            if(project != null){
                int num = TimeUtils.overdueBool(project.getBornTime(),project.getShelfLife());
                if(num == 0){
                    proName.setText(project.getProName()+"");
                    showToast(this,getString(R.string.string240),1000);
                }else if(num == -1){
//                    popDialog("E108"+getString(R.string.string5));
//                    methodCode = new BleMethodCode(3);
//                    methodCode.setMes("");
//                    methodCode.setComm((byte) 0x01);
//                    EventBus.getDefault().post(methodCode);


                }else{
//                    popDialog("E108"+getString(R.string.string292));
//                    methodCode = new BleMethodCode(3);
//                    methodCode.setMes("");
//                    methodCode.setComm((byte) 0x01);
//                    EventBus.getDefault().post(methodCode);
                }
            }else{
                //if("000".equals(data.substring(0,3))) {
                showToast(this, getString(R.string.string6),1000);
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
        }else if(comm == 0x32){
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_updatetest_back://返回
                BleMethodCode methodCode = new BleMethodCode(3);
                methodCode.setMes("");
                //methodCode.setMes("");
                methodCode.setComm((byte) 0x0A);
                EventBus.getDefault().post(methodCode);
                finish();
                break;
            case R.id.id_updatetest_save://保存
                if("1".equals(myApp.getCardIDFlap())){
                    idnum.setFocusable(false);
                    name.setFocusable(false);
                    age.setFocusable(false);
                    female.setClickable(false);
                    male.setClickable(false);
                }


                if(!"".equals(proName.getText().toString())) {

                    Boolean idCard = IDCard.IDCardValidate(idnum.getText().toString());
                    if("".equals(idnum.getText().toString())){
                        sample.setIDNum(idnum.getText().toString());
                    }else if(idCard  ){
                        sample.setIDNum(idnum.getText().toString());
                    }else{
                        showToast(this,getString(R.string.string243),1000);
                        return;
                    }
                    sample.setIDNum(idnum.getText().toString());
                    sample.setName(name.getText().toString());
                    sample.setAge(age.getText().toString());
                    if (female.isChecked()) {
                        sample.setGender(getString(R.string.string4));
                    } else {
                        sample.setGender(getString(R.string.string3));
                    }
                    sample.setTestPeo(peoNum);
                    sample.setAudPeo(peoAud);
                    //sample.setTestPeo(testPeo.getText().toString());
                    sample.setSamType(sampleType.getText().toString());
                    sample.setProName(proName.getText().toString());
                    sample.setSamNum(samplenum.getText().toString());

                      //sample.setNid(System.currentTimeMillis() + "");

                    //第二次保存出bug,改后的代码
                    if (project!=null){
                        sample.setProject(project);
                        sample.setProNum(project.getBatch());
                        sample.setRefer(project.getcRefer());
                    }

                    sample.setHos(sp.readHosUser());
                    sample.setDepart(sp.readDepartUser());
                    //sample.setDoc(sp.readDocUser());
                    sample.setFlagNull(false);
                    int index = -1;
                    for (int i = 0; i < myApp.getTestPeoples().size(); i++) {
                        if (sample.getNid().equals(myApp.getTestPeoples().get(i).getNid())) {
                            index = i;
                            myApp.getTestPeoples().set(i, sample);
                            myApp.getResultPeoples().add(sample);
                            break;
                        }
                    }
                    if(index == -1){
                        sample.setNid(System.currentTimeMillis() + "");
                        for (int i = 0; i < myApp.getTestPeoples().size(); i++) {
                            if (myApp.getTestPeoples().get(i).isFlagNull()) {
                                myApp.getTestPeoples().add(i, sample);
                                break;
                            }
                        }
                        myApp.getResultPeoples().add(sample);
                    }
                    finish();
                }else{
                    showToast(this,getString(R.string.string2),1000);
                    //Toast.makeText(this,getString(R.string.string2),Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.id_updatetest_addcon:
                if(!"".equals(proName.getText().toString())) {
                    sample.setIDNum(idnum.getText().toString());
                    sample.setName(name.getText().toString());
                    sample.setAge(age.getText().toString());
                    if (female.isChecked()) {
                        sample.setGender(getString(R.string.string4));
                    } else {
                        sample.setGender(getString(R.string.string3));
                    }
                    sample.setTestPeo(peoNum);
                    sample.setAudPeo(peoAud);
                    //sample.setTestPeo(testPeo.getText().toString());
                    sample.setSamType(sp.readType());
                    sample.setProName(proName.getText().toString());
                    sample.setSamNum(samplenum.getText().toString());
                    //  sample.setNid(System.currentTimeMillis() + "");
                    sample.setProject(project);
                    sample.setHos(sp.readHosUser());
                    sample.setProNum(project.getBatch());
                    sample.setDepart(sp.readDepartUser());
                    //sample.setDoc(sp.readDocUser());
                    sample.setRefer("<1.0");
                    sample.setFlagNull(false);
                    int index = -1;
                    for (int i = 0; i < myApp.getTestPeoples().size(); i++) {
                        if (sample.getNid().equals(myApp.getTestPeoples().get(i).getNid())) {
                            index = i;
                            myApp.getTestPeoples().add(index, sample);
                            break;
                        }
                    }
                    if(index == -1){
                        sample.setNid(System.currentTimeMillis() + "");
                        for (int i = 0; i < myApp.getTestPeoples().size(); i++) {
                            if (myApp.getTestPeoples().get(i).isFlagNull()) {
                                myApp.getTestPeoples().add(i, sample);
                                break;
                            }
                        }
                        myApp.getResultPeoples().add(sample);
                    }
                    idnum.setText("");
                    name.setText("");
                    age.setText("");
                    //testPeo.setText("");
                    proName.setText("");
                    samplenum.setText("");
                    methodCode = new BleMethodCode(3);
                    methodCode.setMes("");
                    //methodCode.setMes("");
                    methodCode.setComm((byte) 0x01);
                    EventBus.getDefault().post(methodCode);
                }else{
                    showToast(this,getString(R.string.string2),1000);
                    //Toast.makeText(this,getString(R.string.string2),Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.id_addtest_female:
                if(female.isChecked()){
                    female.setChecked(false);
                    male.setChecked(true);

                }else{
                    female.setChecked(true);
                    male.setChecked(false);

                }
                break;
            case R.id.id_addtest_male:
                if(male.isChecked()){
                    male.setChecked(false);
                    female.setChecked(true);

                }else{
                    male.setChecked(true);
                    female.setChecked(false);

                }
                break;
            case R.id.id_updatetest_progoto:

                //popProTip(num1);
                break;
            case R.id.id_updatetest_typegoto:
                popTypeTip(num2);
                break;
            case R.id.id_updatetest_pro:
                //popProTip(num1);
                break;
            case R.id.id_updatetest_type:
                popTypeTip(num2);
                break;

            case R.id.id_updatetest_idnum:
                CardEvent cardEvent = new CardEvent();
                cardEvent.setBys(new byte[]{(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x20, 0x01, 0x22});
                cardEvent.setComm((byte) 0x31);
                EventBus.getDefault().post(cardEvent);
                myApp.setCardIDFlap("1");
                idnum.setFocusable(false);
                name.setFocusable(false);
                age.setFocusable(false);
                female.setClickable(false);
                male.setClickable(false);
                break;
        }
    }

    //点击空白处收起键盘
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (UpdateTestActivity.this.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(UpdateTestActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

    int proNum = 0;
    private void popProTip(int num){
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
            if(i == num){
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
                projectAdapter.notifyDataSetChanged();
                flag[0] = position;
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (pctChoose.getVisibility() == View.VISIBLE) {
//                    proNum = 0;
//                    proName.setText("PCT");
//                }
//                if (crpChoose.getVisibility() == View.VISIBLE) {
//                    proNum = 1;
//                    proName.setText("CRP");
//                }
//                if (ntprobnpChoose.getVisibility() == View.VISIBLE) {
//                    proNum = 2;
//                    proName.setText("NT_proBNP");
//                }
//                if (ctniChoose.getVisibility() == View.VISIBLE) {
//                    proNum = 3;
//                    proName.setText("cTNI");
//                }
                proName.setText(proNames[flag[0]]);
//                project = dbUtils.searchProjectByProName(proNames[flag[0]]);
                project = dbUtils.searchProName(proName.getText().toString());
                dialog.dismiss();
            }
        });
    }

    int typeNum = 0;
    private void popTypeTip(int num){
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

//        switch (num){
//            case 0:
//                wholebloodChoose.setVisibility(View.VISIBLE);
//                wholeblood.setBackgroundResource(R.drawable.samtype_shape_corner);
//                wholebloodText.setTextColor(getResources().getColor(R.color.colorwhite));
//
//                serumChoose.setVisibility(View.GONE);
//                serum.setBackgroundResource(R.drawable.unsamtype_shape_corner);
//                serumText.setTextColor(getResources().getColor(R.color.colorlightblue));
//
//                urineChoose.setVisibility(View.GONE);
//                urine.setBackgroundResource(R.drawable.unsamtype_shape_corner);
//                urineText.setTextColor(getResources().getColor(R.color.colorlightblue));
//
//                otherChoose.setVisibility(View.GONE);
//                other.setBackgroundResource(R.drawable.unsamtype_shape_corner);
//                otherText.setTextColor(getResources().getColor(R.color.colorlightblue));
//
//                plasmaChoose.setVisibility(View.GONE);
//                plasma.setBackgroundResource(R.drawable.unsamtype_shape_corner);
//                plasmaText.setTextColor(getResources().getColor(R.color.colorlightblue));
//                break;
//            case 1:
//                serumChoose.setVisibility(View.VISIBLE);
//                serum.setBackgroundResource(R.drawable.samtype_shape_corner);
//                serumText.setTextColor(getResources().getColor(R.color.colorwhite));
//
//                wholebloodChoose.setVisibility(View.GONE);
//                wholeblood.setBackgroundResource(R.drawable.unsamtype_shape_corner);
//                wholebloodText.setTextColor(getResources().getColor(R.color.colorlightblue));
//
//                urineChoose.setVisibility(View.GONE);
//                urine.setBackgroundResource(R.drawable.unsamtype_shape_corner);
//                urineText.setTextColor(getResources().getColor(R.color.colorlightblue));
//
//                otherChoose.setVisibility(View.GONE);
//                other.setBackgroundResource(R.drawable.unsamtype_shape_corner);
//                otherText.setTextColor(getResources().getColor(R.color.colorlightblue));
//
//                plasmaChoose.setVisibility(View.GONE);
//                plasma.setBackgroundResource(R.drawable.unsamtype_shape_corner);
//                plasmaText.setTextColor(getResources().getColor(R.color.colorlightblue));
//                break;
//            case 2:
//                urineChoose.setVisibility(View.VISIBLE);
//                urine.setBackgroundResource(R.drawable.samtype_shape_corner);
//                urineText.setTextColor(getResources().getColor(R.color.colorwhite));
//
//                wholebloodChoose.setVisibility(View.GONE);
//                wholeblood.setBackgroundResource(R.drawable.unsamtype_shape_corner);
//                wholebloodText.setTextColor(getResources().getColor(R.color.colorlightblue));
//
//                serumChoose.setVisibility(View.GONE);
//                serum.setBackgroundResource(R.drawable.unsamtype_shape_corner);
//                serumText.setTextColor(getResources().getColor(R.color.colorlightblue));
//
//                otherChoose.setVisibility(View.GONE);
//                other.setBackgroundResource(R.drawable.unsamtype_shape_corner);
//                otherText.setTextColor(getResources().getColor(R.color.colorlightblue));
//
//                plasmaChoose.setVisibility(View.GONE);
//                plasma.setBackgroundResource(R.drawable.unsamtype_shape_corner);
//                plasmaText.setTextColor(getResources().getColor(R.color.colorlightblue));
//                break;
//
//            case 3:
//                plasmaChoose.setVisibility(View.VISIBLE);
//                plasma.setBackgroundResource(R.drawable.unsamtype_shape_corner);
//                plasmaText.setTextColor(getResources().getColor(R.color.colorlightblue));
//
//                urineChoose.setVisibility(View.GONE);
//                urine.setBackgroundResource(R.drawable.samtype_shape_corner);
//                urineText.setTextColor(getResources().getColor(R.color.colorwhite));
//
//                wholebloodChoose.setVisibility(View.GONE);
//                wholeblood.setBackgroundResource(R.drawable.unsamtype_shape_corner);
//                wholebloodText.setTextColor(getResources().getColor(R.color.colorlightblue));
//
//                serumChoose.setVisibility(View.GONE);
//                serum.setBackgroundResource(R.drawable.unsamtype_shape_corner);
//                serumText.setTextColor(getResources().getColor(R.color.colorlightblue));
//
//                otherChoose.setVisibility(View.GONE);
//                other.setBackgroundResource(R.drawable.unsamtype_shape_corner);
//                otherText.setTextColor(getResources().getColor(R.color.colorlightblue));
//
//
//                break;
//
//            case 4:
//                otherChoose.setVisibility(View.VISIBLE);
//                other.setBackgroundResource(R.drawable.unsamtype_shape_corner);
//                otherText.setTextColor(getResources().getColor(R.color.colorlightblue));
//
//                urineChoose.setVisibility(View.GONE);
//                urine.setBackgroundResource(R.drawable.samtype_shape_corner);
//                urineText.setTextColor(getResources().getColor(R.color.colorwhite));
//
//                wholebloodChoose.setVisibility(View.GONE);
//                wholeblood.setBackgroundResource(R.drawable.unsamtype_shape_corner);
//                wholebloodText.setTextColor(getResources().getColor(R.color.colorlightblue));
//
//                serumChoose.setVisibility(View.GONE);
//                serum.setBackgroundResource(R.drawable.unsamtype_shape_corner);
//                serumText.setTextColor(getResources().getColor(R.color.colorlightblue));
//
//                plasmaChoose.setVisibility(View.GONE);
//                plasma.setBackgroundResource(R.drawable.unsamtype_shape_corner);
//                plasmaText.setTextColor(getResources().getColor(R.color.colorlightblue));
//                break;
//        }
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

        final AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialog).create();
        dialog.setView(view);
        dialog.setCancelable(false);
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        params.height = getWindow().getWindowManager().getDefaultDisplay().getHeight();
        dialog.getWindow().setAttributes(params);

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

                    plasmaChoose.setVisibility(View.GONE);
                    plasma.setBackgroundResource(R.drawable.unsamtype_shape_corner);
                    plasmaText.setTextColor(getResources().getColor(R.color.colorlightblue));

                    otherChoose.setVisibility(View.GONE);
                    other.setBackgroundResource(R.drawable.unsamtype_shape_corner);
                    otherText.setTextColor(getResources().getColor(R.color.colorlightblue));
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

                    wholebloodChoose.setVisibility(View.GONE);
                    wholeblood.setBackgroundResource(R.drawable.unsamtype_shape_corner);
                    wholebloodText.setTextColor(getResources().getColor(R.color.colorlightblue));

                    serumChoose.setVisibility(View.GONE);
                    serum.setBackgroundResource(R.drawable.unsamtype_shape_corner);
                    serumText.setTextColor(getResources().getColor(R.color.colorlightblue));

                    plasmaChoose.setVisibility(View.GONE);
                    plasma.setBackgroundResource(R.drawable.unsamtype_shape_corner);
                    plasmaText.setTextColor(getResources().getColor(R.color.colorlightblue));

                    otherChoose.setVisibility(View.GONE);
                    other.setBackgroundResource(R.drawable.unsamtype_shape_corner);
                    otherText.setTextColor(getResources().getColor(R.color.colorlightblue));
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
                if (plasmaChoose.getVisibility() == View.VISIBLE) {
                    typeNum = 3;
                    sampleType.setText(getString(R.string.string9));
                }
                if (otherChoose.getVisibility() == View.VISIBLE) {
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
        Matcher m = pattern.matcher(str);

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
