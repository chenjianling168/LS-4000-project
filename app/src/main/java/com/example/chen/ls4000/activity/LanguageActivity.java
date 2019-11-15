package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.utils.MyContextWrapper;
import com.example.chen.ls4000.utils.SharedHelper;

public class LanguageActivity extends AppCompatActivity implements View.OnClickListener{

    private CheckBox chinese,english;
    private ImageView back;
    private Button save;
    private TextView chineseTv,englishTv;
    private SharedHelper sh;

    private Configuration config;
    private DisplayMetrics dm;
    private Resources resources;
    private String language = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

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
        chinese = (CheckBox)findViewById(R.id.id_language_chinese);
        english = (CheckBox)findViewById(R.id.id_language_english);
        chineseTv = (TextView)findViewById(R.id.id_language_chinesetv);
        englishTv = (TextView)findViewById(R.id.id_language_englishtv);
        back = (ImageView)findViewById(R.id.id_language_back);
        save = (Button)findViewById(R.id.id_language_save);

        hideBottomUIMenu();

        chinese.setOnClickListener(this);
        english.setOnClickListener(this);
        back.setOnClickListener(this);
        save.setOnClickListener(this);

        sh = new SharedHelper(this);
        language = sh.readLan();

        resources = getResources();// 获得res资源对象
        config = resources.getConfiguration();// 获得设置对象
        dm = resources.getDisplayMetrics();

        if("en".equals(sh.readLan())){
            english.setChecked(true);
            chinese.setChecked(false);
            englishTv.setTextColor(Color.parseColor("#363c4d"));
            chineseTv.setTextColor(Color.parseColor("#b8bfcc"));
//            englishTv.setTextSize(R.style.settingslanfont);
//            chineseTv.setTextSize(R.style.settingsinfofont);
        }else{
            chinese.setChecked(true);
            english.setChecked(false);
            chineseTv.setTextColor(Color.parseColor("#363c4d"));
            englishTv.setTextColor(Color.parseColor("#b8bfcc"));
//            chineseTv.setTextSize(R.style.settingslanfont);
//            englishTv.setTextSize(R.style.settingsinfofont);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_language_chinese:
                if(chinese.isChecked()){
                    english.setChecked(false);
                    chineseTv.setTextColor(Color.parseColor("#363c4d"));
                    englishTv.setTextColor(Color.parseColor("#b8bfcc"));
                }
                break;
            case R.id.id_language_english:
                if(english.isChecked()){
                    chinese.setChecked(false);
                    englishTv.setTextColor(Color.parseColor("#363c4d"));
                    chineseTv.setTextColor(Color.parseColor("#b8bfcc"));
                }
                break;
            case R.id.id_language_back:
                finish();
                break;
            case R.id.id_language_save:
                if(english.isChecked()){
                    sh.saveLan("en");//("English");
                }else{
                    sh.saveLan("zh");//("中文");
                }
//                freshView();
//                showLanguage(sh.readLan());
                //initLocaleLanguage();
                language = sh.readLan();
                recreate();
                if(sh.readLan().equals("zh")){
                    showToast(this,"语言切换成功！",1000);
                    //Toast.makeText(this,"语言切换成功！",Toast.LENGTH_SHORT).show();
                }else {
                    showToast(this,"Language switching succeeded!",1000);
                    //Toast.makeText(this,"Language switching succeeded!",Toast.LENGTH_SHORT).show();
                }

                startActivity(new Intent(LanguageActivity.this,MainActivity.class));
                break;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MyContextWrapper.wrap(newBase, "en"));
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
