package hermes.mju.captdesign.hermesar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Tak on 2018-05-18.
 */

public class MapPopUP extends Activity implements View.OnClickListener{
    private Button buttonStartPoint;
    private Button buttonEndPoint;

    private TextView gpslocatext;

    private String selectpoint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //title바 삭제
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        WindowManager.LayoutParams layoutParams= new WindowManager.LayoutParams();

        //팝업 외부 뿌연 효과
        layoutParams.flags= WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //뿌연 효과 정도
        layoutParams.dimAmount= 0.7f;


        //적용
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.map_click_pop_up);

        // Button 설정
        gpslocatext = (TextView)findViewById(R.id.gpslocaView);
        buttonStartPoint = (Button)findViewById(R.id.btnStartPoint);
        buttonEndPoint = (Button)findViewById(R.id.btnEndPoint);

        buttonStartPoint.setOnClickListener(this);
        buttonEndPoint.setOnClickListener(this);
        Intent i = getIntent();
        selectpoint = (String)i.getSerializableExtra("spoint");
        gpslocatext.setText(selectpoint);

    }

    // 상수를 이용해서 리팩토링 필요 : HermesActivity의 popUPOnMap()도 같이!
    @Override
    public void onClick(View view) {
        if(view == buttonStartPoint){
            setResult(0);
            finish();
        }
        if(view == buttonEndPoint){
            setResult(1);
            finish();
        }
    }
}