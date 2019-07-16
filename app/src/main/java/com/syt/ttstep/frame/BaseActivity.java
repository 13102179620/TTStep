package com.syt.ttstep.frame;

import android.os.Bundle;

import android.view.Window;
import android.view.WindowManager;
import androidx.fragment.app.FragmentActivity;


public abstract class BaseActivity extends FragmentActivity {

    protected boolean isHideAppTitile = true;
    protected   boolean isHideSystemTitle = false;




    @Override
    protected void onCreate( Bundle savedInstanceState) {

        if (isHideAppTitile){
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        if (this.isHideSystemTitle){
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        super.onCreate(savedInstanceState);





    }




    //初始化组件
    protected abstract void initView();


    protected abstract void requestData();


}
