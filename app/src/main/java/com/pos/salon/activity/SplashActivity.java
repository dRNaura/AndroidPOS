package com.pos.salon.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.pos.salon.R;

public class SplashActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

//        checkPermission();



        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        int SPLASH_DISPLAY_LENGTH = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */

//                Intent mainIntent = new Intent(SplashActivity.this, DemoAppScreen.class);
//                    startActivity(mainIntent);

               if (sharedPreferences.getString("user", "").equals("")) {
                   // Login Screen
                    Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(mainIntent);
                    finish();
                   overridePendingTransition(R.anim.enter, R.anim.exit);
                } else {
                    Intent mainIntent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(mainIntent);
                    finish();
                   overridePendingTransition(R.anim.enter, R.anim.exit);
                }

//                Intent mainIntent = new Intent(SplashActivity.this, PrintDemo.class);
//                    startActivity(mainIntent);
//                    finish();

            }
        }, SPLASH_DISPLAY_LENGTH);
    }

}
