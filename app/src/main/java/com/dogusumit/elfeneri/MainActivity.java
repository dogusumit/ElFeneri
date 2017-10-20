package com.dogusumit.elfeneri;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class MainActivity extends AppCompatActivity {

    private boolean acikMi;

    private ImageButton button;

    private Camera camera;
    private Camera.Parameters p;
    private CameraManager mCameraManager;
    private String mCameraId;
    private int gecikme;

    private boolean acikMi2;

    final Context context = this;

    Handler mHandler;
    Runnable yakSondur;

    PowerManager pm;
    PowerManager.WakeLock wl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getString(R.string.app_name));
        wl.acquire();

        final Button btn2 = (Button) findViewById(R.id.button2);
        final CheckBox chk1 = (CheckBox) findViewById(R.id.checkbox1);
        final SeekBar seek1 = (SeekBar) findViewById(R.id.seekbar1);
        final TextView tv2 = (TextView) findViewById(R.id.textview2);

        button = (ImageButton) findViewById(R.id.button);

        acikMi = false;
        acikMi2 = false;
        seek1.setEnabled(false);
        seek1.setMax(2000);
        gecikme = 1000;
        seek1.setProgress(1000);

        mHandler = new Handler();
        yakSondur = new Runnable() {
            @Override
            public void run() {
                try {
                    if (acikMi2) {
                        flashKapat();
                        acikMi2 = false;
                    }
                    else {
                        flashAc();
                        acikMi2 = true;
                    }
                } finally {
                    mHandler.postDelayed(yakSondur, gecikme);
                }
            }
        };


        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beyazEkran();
            }
        });

        chk1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    seek1.setEnabled(true);
                    tv2.setText((gecikme / 1000.0 + getString(R.string.str1)));
                    if (acikMi)
                        yakSondur.run();
                } else {
                    seek1.setEnabled(false);
                    mHandler.removeCallbacks(yakSondur);
                }
            }
        });

        seek1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress==0)
                    progress++;
                gecikme = progress;
                tv2.setText((gecikme / 1000.0 + getString(R.string.str1)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                    mCameraId = mCameraManager.getCameraIdList()[0];
                } else {
                    camera = Camera.open();
                    p = (Camera.Parameters) camera.getParameters();
                }
            } catch (Exception e) {
                hataYazdir(e.getMessage());
            }


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!acikMi) {
                        flashAc();
                        button.setImageResource(R.drawable.kapat);
                        acikMi = true;
                        if (chk1.isChecked())
                            yakSondur.run();
                    } else {
                        flashKapat();
                        button.setImageResource(R.drawable.ac);
                        acikMi = false;
                        mHandler.removeCallbacks(yakSondur);
                    }
                }
            });
        } else {
            TextView tv = (TextView) findViewById(R.id.textview);
            tv.setText(getString(R.string.flashyok));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    beyazEkran();
                }
            });
            chk1.setEnabled(false);
        }

        try {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int width = displaymetrics.widthPixels;
            button.getLayoutParams().width = width/2;
            button.getLayoutParams().height = width/2;
        } catch (Exception e) {
            hataYazdir(e.getMessage());
        }

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }
    private void flashAc() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCameraManager.setTorchMode(mCameraId, true);
            }
            else {
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(p);
                //camera.startPreview();
            }
        } catch (Exception e) {
            hataYazdir(e.getMessage());
        }
    }

    private void flashKapat() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCameraManager.setTorchMode(mCameraId, false);
            }
            else {
                p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(p);
                //camera.stopPreview();
            }

        } catch (Exception e) {
            hataYazdir(e.getMessage());
        }
    }

    private void hataYazdir(String str) {
        Toast.makeText(context,getString(R.string.hata)+str,Toast.LENGTH_LONG).show();
    }

    private void beyazEkran() {
        try {
            Intent intent = new Intent(context, activity2.class);
            startActivity(intent);
        } catch (Exception e) {
            hataYazdir(e.getMessage());

            TextView tv = (TextView) findViewById(R.id.textview);
            tv.setText(e.getMessage());

        }
    }

    private void uygulamayiOyla()
    {
        Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
            } catch (Exception ane) {
                hataYazdir(e.getMessage());
            }
        }
    }

    private void marketiAc()
    {
        try {
            Uri uri = Uri.parse("market://developer?id="+getString(R.string.play_store_id));
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/developer?id="+getString(R.string.play_store_id))));
            } catch (Exception ane) {
                hataYazdir(e.getMessage());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.oyla:
                uygulamayiOyla();
                return true;
            case R.id.market:
                marketiAc();
                return true;
            case R.id.cikis:
                System.exit(0);
                android.os.Process.killProcess(android.os.Process.myPid());
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(acikMi){
                mHandler.removeCallbacks(yakSondur);
                flashKapat();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        try{
            if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                camera.release();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        try{
            wl.release();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}