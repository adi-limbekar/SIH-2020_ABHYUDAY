package com.sih2020.abhyuday;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreenActivity extends AppCompatActivity {
    Animation topAnimation,bottomAnimation;
    SharedPreferences onBordingScreen;


    private static int SPLASH_SCREEN_TIME_OUT=3000;
    //After completion of 2000 ms, the next activity will get started.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //This method is used so that your splash activity
        //can cover the entire screen.
        setContentView(R.layout.activity_splash_screen);
        final ImageView img = findViewById(R.id.logo);
        ImageView image=findViewById(R.id.logo);
        TextView info=findViewById(R.id.info);
        topAnimation=AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnimation=AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        image.setAnimation(topAnimation);
        info.setAnimation(bottomAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onBordingScreen=getSharedPreferences("onBoardingScreen",MODE_PRIVATE);
                boolean isFirstTime=onBordingScreen.getBoolean("firstTime",true);
                if(isFirstTime)
                {
                    SharedPreferences.Editor editor=onBordingScreen.edit();
                    editor.putBoolean("firstTime",false);
                    editor.commit();
                    Intent i=new Intent(SplashScreenActivity.this, OnBoardActivity.class);
                    startActivity(i);
                    finish();

                }
                else {
                    // If user does not log the first time
                    SharedPreferences pref;
                    pref = getSharedPreferences("ABHYUDAY",MODE_PRIVATE);
                    final String isUserLoggedIn = pref.getString("EMAIL","");
                    if(isUserLoggedIn.equals(""))
                    {
                        Intent i=new Intent(SplashScreenActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else
                    {
                        Intent i=new Intent(SplashScreenActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();

//Direct Login
                    }


                }


            }
        }, SPLASH_SCREEN_TIME_OUT);
    }
}
