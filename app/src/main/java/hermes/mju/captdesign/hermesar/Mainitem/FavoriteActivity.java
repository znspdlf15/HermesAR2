package hermes.mju.captdesign.hermesar.Mainitem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import hermes.mju.captdesign.hermesar.R;

public class FavoriteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        //databaseReference.child("chat").child(CHAT_NAME).push().setValue(chat); // 데이터 푸쉬
       // databaseReference.child("event").child(CHAT_NAME).push().setValue(event); // 데이터 푸쉬

       // Intent intent = new Intent(this, ChatRoomActivity.class);
       // intent.putExtra("chatName", CHAT_NAME);
       // intent.putExtra("userName", USER_NAME);
       // startActivity(intent);

    }
}
