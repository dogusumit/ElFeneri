package com.dogusumit.elfeneri;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;


public class activity2 extends AppCompatActivity {

    int curBrightnessValue;
    boolean parladiMi = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2_layout);

        try {
            curBrightnessValue = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
            parlaklikDegistir(255);
            parladiMi = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parlaklikDegistir(int deger) {
        try {
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.screenBrightness = deger / 255.0f;
            getWindow().setAttributes(layoutParams);

            android.provider.Settings.System.putInt(getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS, deger);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(parladiMi){
           parlaklikDegistir(curBrightnessValue);
        }
    }

}
