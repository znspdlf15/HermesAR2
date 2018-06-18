package hermes.mju.captdesign.hermesar.Mainitem;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.skt.Tmap.TMapMarkerItem;

import java.util.ArrayList;

import hermes.mju.captdesign.hermesar.POI;
import hermes.mju.captdesign.hermesar.R;
import hermes.mju.captdesign.hermesar.SearchActivity;

public class FavoriteActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public Button searchButton;

    ArrayList<POI> arrayPOI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        arrayPOI = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        user = mAuth.getCurrentUser();

        searchButton =  (Button)findViewById(R.id.fav_searchButton);
        searchButton.setOnClickListener(this);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 3:

                if((data.getSerializableExtra("LAT")) == null || (data.getSerializableExtra("LON") == null) ){
                    break;  // 에러처리
                }

                databaseReference.child(user.getEmail()).child("Favorite").child((String)data.getSerializableExtra("POI")).push().setValue((double)data.getSerializableExtra("LAT")); // 데이터 푸쉬
                databaseReference.child(user.getEmail()).child("Favorite").child((String)data.getSerializableExtra("POI")).push().setValue((double)data.getSerializableExtra("LON")); // 데이터 푸쉬


                break;
        }
    }

    @Override
    public void onClick(View view) {
        if(view == searchButton){
            startActivityForResult(new Intent(this, SearchActivity.class),3);
        }


    }
}
