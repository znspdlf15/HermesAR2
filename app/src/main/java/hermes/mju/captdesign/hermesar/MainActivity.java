package hermes.mju.captdesign.hermesar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.TMapData;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import hermes.mju.captdesign.hermesar.Mainitem.FavoriteActivity;
import hermes.mju.captdesign.hermesar.Mainitem.LastestActivity;
import hermes.mju.captdesign.hermesar.Mainitem.SettingActivity;
import hermes.mju.captdesign.hermesar.login.LoginActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView textViewUserEmail;

    private ImageButton StartButton;
    private ImageButton lastestButton;
    private ImageButton favButton;
    private ImageButton OptionButton;
    private TextView logoutButton;

    private FirebaseAuth mAuth;
    private FirebaseUser user;



    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null){
            //로그인이 안 되었다면 이 액티비티를 종료함
            finish();
            //그리고 액티비티를 연다.
            startActivity(new Intent(getApplicationContext(), LoginActivity.class)); //추가해 줄 ProfileActivity
        }
        user = mAuth.getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
        }
        textViewUserEmail = (TextView) findViewById(R.id.LoginSucce);

        textViewUserEmail.setText("반갑습니다.\n"+ user.getEmail()+"으로 로그인 하였습니다.");


        StartButton = (ImageButton)findViewById(R.id.StartButton);
        lastestButton = (ImageButton)findViewById(R.id.lastestButton);
        favButton = (ImageButton)findViewById(R.id.favButton);
        OptionButton = (ImageButton)findViewById(R.id.OptionButton);
        logoutButton = (TextView)findViewById(R.id.LogoutButton);

        StartButton.setOnClickListener(this);
        lastestButton.setOnClickListener(this);
        favButton.setOnClickListener(this);
        OptionButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
    }

    public void onClick(View view) {
        if(view == StartButton){
            startActivity(new Intent(this, HermesActivity.class));
        }
        if(view == favButton){
            startActivity(new Intent(this, FavoriteActivity.class));
        }
        if(view == lastestButton){
            startActivity(new Intent(this, LastestActivity.class));
        }
        if(view == OptionButton){
            startActivity(new Intent(this, SettingActivity.class));
        }
        if(view == logoutButton){
            logoutProcess();
        }
    }

    public void logoutProcess(){
        mAuth.signOut();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }
}
