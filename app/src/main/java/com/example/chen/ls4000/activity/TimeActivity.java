package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.utils.MyApp;
import com.example.chen.ls4000.utils.TimeUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimeActivity extends Activity implements View.OnClickListener{

    private Button save;
    private ImageView back;
    private String yearTime,monthTime,dayTime,hourTime,minuteTime;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private MyApp myApp;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
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



    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initviews(){
        save = (Button)findViewById(R.id.id_time_save);
        back = (ImageView)findViewById(R.id.id_time_back);
        datePicker = (DatePicker)findViewById(R.id.id_time_datepicker);
        timePicker = (TimePicker)findViewById(R.id.id_time_timepicker);

        resizePicker(datePicker);
        resizePicker(timePicker);

        hideBottomUIMenu();
        myApp = (MyApp) getApplication();
        save.setOnClickListener(this);
        back.setOnClickListener(this);

        setDatePickerDividerColor(datePicker);

        timePicker.setIs24HourView(true);

//        lan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if(position==0){
//                    sp.saveLan("中文");
//                }else{
//                    sp.saveLan("English");
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });

//        layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                int heightDiff = layout.getRootView().getHeight() - layout.getHeight();
//                if (heightDiff > dpToPx(getActivity(), 200)) { // if more than 200 dp, it's probably a keyboard...
//                    // ... do something here
//                    Log.d("TAG","aaaa");//显示
//                }else {
//                    Log.d("TAG","bbbb");//消失
//                    hideBottomUIMenu();
//                }
//
//            }
//        });

        Calendar calendar = Calendar.getInstance();
        yearTime = calendar.get(Calendar.YEAR) + "";
        monthTime = (calendar.get(Calendar.MONTH)+1) + "";
        dayTime = calendar.get(Calendar.DAY_OF_MONTH) + "";
        hourTime = timePicker.getHour()+"";
        minuteTime = timePicker.getMinute()+"";
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                yearTime = year + "";
                monthTime = (monthOfYear+1) + "";
                dayTime = dayOfMonth + "";
            }
        });
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                hourTime = hourOfDay + "";
                minuteTime = minute + "";
            }
        });

        Resources systemResources = Resources.getSystem();
        int hourNumberPickerId = systemResources.getIdentifier("hour", "id", "android");
        int minuteNumberPickerId = systemResources.getIdentifier("minute", "id", "android");

        NumberPicker hourNumberPicker = (NumberPicker) timePicker.findViewById(hourNumberPickerId);
        NumberPicker minuteNumberPicker = (NumberPicker) timePicker.findViewById(minuteNumberPickerId);

        setNumberPickerDivider(hourNumberPicker);
        setNumberPickerDivider(minuteNumberPicker);
    }

    /**
     * 调整FrameLayout的大小
     * */
    private void resizePicker(FrameLayout tp){      //DatePicker和TimePicker继承自FrameLayout
        List<NumberPicker> nplist = findNumberPicker(tp);   //找到组成的NumberPicker
        for(NumberPicker np:nplist){
            resizeNumberPicker(np); //调整每个NumberPicker的宽度
        }
    }

    /**
     * 得到viewGroup 里面的numberpicker组件
     * */
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

    /**
     * 调整numberpicker大小
     * */
    private void resizeNumberPicker(NumberPicker np){       //ViewGroup.LayoutParams.WRAP_CONTENT
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 350);
        params.setMargins(10,50,10,50);
        np.setLayoutParams(params);
    }


        @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_time_save:
//                DatePicker datePicker = new DatePicker(this);
//                datePicker = (DatePicker)findViewById(R.id.id_time_datepicker);
//                setDatePickerDividerColor(datePicker);
                if(myApp.getTestPeoples().size() == 0){
                    setTime(yearTime+"-"+monthTime+"-"+dayTime+" "+hourTime+":"+minuteTime);
                    showToast(this,getString(R.string.string87),1000);
                    finish();
                }else{
                    showToast(this,getString(R.string.string325),1000);
                }

                break;
            case R.id.id_time_back:
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

    private void setDatePickerDividerColor(DatePicker datePicker) {
        // Divider changing:

        // 获取 mSpinners
        LinearLayout llFirst = (LinearLayout) datePicker.getChildAt(0);

        // 获取 NumberPicker
        LinearLayout mSpinners = (LinearLayout) llFirst.getChildAt(0);
        for (int i = 0; i < mSpinners.getChildCount(); i++) {
            NumberPicker picker = (NumberPicker) mSpinners.getChildAt(i);

            Field[] pickerFields = NumberPicker.class.getDeclaredFields();
            for (Field pf : pickerFields) {
                if (pf.getName().equals("mSelectionDivider")) {
                    pf.setAccessible(true);
                    try {
                        pf.set(picker, new ColorDrawable(Color.parseColor("#73a3f0")));//设置分割线颜色
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    private void setNumberPickerDivider(NumberPicker numberPicker) {
        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            try{
                Field dividerField = numberPicker.getClass().getDeclaredField("mSelectionDivider");
                dividerField.setAccessible(true);
                ColorDrawable colorDrawable = new ColorDrawable(
                        ContextCompat.getColor(this, R.color.colorlightblue));
                dividerField.set(numberPicker,colorDrawable);
                numberPicker.invalidate();
            }
            catch(NoSuchFieldException | IllegalAccessException | IllegalArgumentException e){
                Log.w("setNumberPickerTxtClr", e);
            }
        }
    }

    private void setTime(String ss){
        //private static final String ACTION_SET_CURTIME_MILLIS="ismart.intent.action_set_curtime_millis";
        //广播名称:ismart.intent.action_set_curtime_millis
        //传递参数(long)   “millis”     时间
        //示例:
        Intent time = new Intent("ismart.intent.action_set_curtime_millis");
        time.putExtra("millis", TimeUtils.getTimeSecond(ss));
        sendBroadcast(time);
    }
}
