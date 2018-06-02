package hermes.mju.captdesign.hermesar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import hermes.mju.captdesign.hermesar.login.LoginActivity;

public class splashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Handler mHandler = new Handler();

        // 권한설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //위치 권한
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.CAMERA}, 3);
            }
            //카메라 권한
        //    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
          //      ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 2);
          //  }
        }
        mHandler.postDelayed(new Runnable()
        {
            @Override     public void run()
            {
                AppStart();
                //시간안에 안눌렀으면 취소하는거 만들어야함.
            }
        }, 5000);
    }

    public void AppStart(){
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
