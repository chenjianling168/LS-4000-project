package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.utils.SharedHelper;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends Activity implements View.OnClickListener{

    private EditText hos,depart,doc1,doc2,doc3,aud1,aud2;
    TextView type;
    ListView lvType;
    String peoNum;
    Button ensure , cancel;
    private Button save;
    private ImageView back;
    private SharedHelper sh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        sh = new SharedHelper(getApplicationContext());

        initviews();
    }



    private void initviews(){
        hos = (EditText)findViewById(R.id.id_user_hos);
        depart = (EditText)findViewById(R.id.id_user_depart);
        doc1 = (EditText)findViewById(R.id.id_user_doc1);
        doc2 = (EditText)findViewById(R.id.id_user_doc2);
        doc3 = (EditText)findViewById(R.id.id_user_doc3);
        aud1 = (EditText)findViewById(R.id.id_user_aud1);
        aud2 = (EditText)findViewById(R.id.id_user_aud2);
        type = (TextView) findViewById(R.id.id_user_type);
        back = (ImageView) findViewById(R.id.id_user_back);
        save = (Button)findViewById(R.id.id_user_save);

        hideBottomUIMenu();

        back.setOnClickListener(this);
        save.setOnClickListener(this);


        sh = new SharedHelper(this);

        if (sh.readLan().equals("en")){
            hos.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        }else {
            hos.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});
        }

        if (sh.readLan().equals("en")){
            depart.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        }else {
            depart.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
        }

        if (sh.readLan().equals("en")){
            doc1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        }else {
            doc1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
        }

        if (sh.readLan().equals("en")){
            doc2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        }else {
            doc2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
        }

        if (sh.readLan().equals("en")){
            doc3.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        }else {
            doc3.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
        }

        if (sh.readLan().equals("en")){
            aud1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        }else {
            aud1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
        }

        if (sh.readLan().equals("en")){
            aud2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        }else {
            aud2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
        }

        type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popTypeTip(type.getText().toString());

            }
        });


        hos.setText(sh.readHosUser());
        depart.setText(sh.readDepartUser());
        doc1.setText(sh.readDocUser1());
        doc2.setText(sh.readDocUser2());
        doc3.setText(sh.readDocUser3());
        aud1.setText(sh.readAuditor1());
        aud2.setText(sh.readAuditor2());
        //type.setText(sh.readType());

        if(getString(R.string.string8).equals(sh.readType())){
            type.setText(getString(R.string.string8));
        }else if(getString(R.string.string9).equals(sh.readType())){
            type.setText(getString(R.string.string9));
        }else if(getString(R.string.string10).equals(sh.readType())){
            type.setText(getString(R.string.string10));
        }else if(getString(R.string.string11).equals(sh.readType())){
            type.setText(getString(R.string.string11));
        }else{
            type.setText(getString(R.string.string7));
        }
    }


    int typeNum = 0;
    private void popTypeTip(String str){
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


        if(getString(R.string.string7).equals(sh.readType())) {
            wholebloodChoose.setVisibility(View.VISIBLE);
            wholeblood.setBackgroundResource(R.drawable.samtype_shape_corner);
            wholebloodText.setTextColor(getResources().getColor(R.color.colorwhite));

        }else if(getString(R.string.string8).equals(sh.readType())){
            serumChoose.setVisibility(View.VISIBLE);
            serum.setBackgroundResource(R.drawable.samtype_shape_corner);
            serumText.setTextColor(getResources().getColor(R.color.colorwhite));

        }else if(getString(R.string.string10).equals(sh.readType())){
            urineChoose.setVisibility(View.VISIBLE);
            urine.setBackgroundResource(R.drawable.samtype_shape_corner);
            urineText.setTextColor(getResources().getColor(R.color.colorwhite));

        }else if(getString(R.string.string9).equals(sh.readType())){
            plasmaChoose.setVisibility(View.VISIBLE);
            plasma.setBackgroundResource(R.drawable.samtype_shape_corner);
            plasmaText.setTextColor(getResources().getColor(R.color.colorwhite));

        }else if(getString(R.string.string11).equals(sh.readType())){
            otherChoose.setVisibility(View.VISIBLE);
            other.setBackgroundResource(R.drawable.samtype_shape_corner);
            otherText.setTextColor(getResources().getColor(R.color.colorwhite));

        }

        final android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this, R.style.AlertDialog).create();
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
                    type.setText(getString(R.string.string7));
                }
                if (serumChoose.getVisibility() == View.VISIBLE) {
                    typeNum = 1;
                    type.setText(getString(R.string.string8));
                }
                if (urineChoose.getVisibility() == View.VISIBLE) {
                    typeNum = 2;
                    type.setText(getString(R.string.string10));
                }
                if(plasmaChoose.getVisibility() == View.VISIBLE){
                    typeNum = 3;
                    type.setText(getString(R.string.string9));
                }
                if(otherChoose.getVisibility() == View.VISIBLE){
                    typeNum = 4;
                    type.setText(getString(R.string.string11));
                }
                dialog.dismiss();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_user_save:
                sh.saveUser(hos.getText().toString(),depart.getText().toString(),doc1.getText().toString(),
                        doc2.getText().toString(),doc3.getText().toString(),aud1.getText().toString(),
                        aud2.getText().toString(),type.getText().toString());
                finish();
                break;
            case R.id.id_user_back:
                finish();
                break;
        }
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

    //点击空白处收起键盘
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (UserActivity.this.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(UserActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }
}
