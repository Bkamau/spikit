package fi.benson.spikit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class Splash extends Activity {
    private static final int SPLASH_TIME = 2 * 1000;// 3 seconds
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       requestWindowFeature(Window.FEATURE_NO_TITLE);
       getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        
        
        try {
        new Handler().postDelayed(new Runnable() {
 
            public void run() {
                 
               Intent intent = new Intent(Splash.this,
                   Spikit.class);
                startActivity(intent);
 
                Splash.this.finish();
 
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
 
            }
             
             
        }, SPLASH_TIME);
         
        new Handler().postDelayed(new Runnable() {
              public void run() {
                     } 
                }, SPLASH_TIME);
        } catch(Exception e){}
    }
 
     
    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }
}