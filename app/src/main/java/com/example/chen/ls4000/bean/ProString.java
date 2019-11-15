package com.example.chen.ls4000.bean;

/**
 * Created by Administrator on 2017/6/28 0028.
 */

public class ProString {
    private String proName;
    private boolean choose;

    public ProString(String proName, boolean choose) {
        this.proName = proName;
        this.choose = choose;
    }

    public ProString() {
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public boolean isChoose() {
        return choose;
    }

    public void setChoose(boolean choose) {
        this.choose = choose;
    }
}
