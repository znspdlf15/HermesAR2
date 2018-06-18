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

public class ARPopUp extends Activity implements View.OnClickListener{
    private Button button;
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
        setContentView(R.layout.ar_pop_up);

        button = (Button) findViewById(R.id.ok_button);
        button.setOnClickListener(this);



    }

    // 상수를 이용해서 리팩토링 필요 : HermesActivity의 popUPOnMap()도 같이!
    @Override
    public void onClick(View view) {
        if(view == button){
            finish();
        }
    }
}