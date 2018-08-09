package com.example.user.a2013081088;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

public class SplashActivity extends Activity {      //스플래시 이미지 보여주는 액티비티 클래스
    private final int SPLASH_DISPLAY_LENGTH = 2000; //2초

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //2초간만 실행 후 메인액티비티로 넘김
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
