
package hermes.mju.captdesign.hermesar;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPOIItem;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    TMapData mapData;
    EditText editSearch;
    ImageButton btnSearch;
    ListView lvSearch;
    ImageButton voiceSearch;

    SearchAdapter adapter;
    ArrayList<POI> arrayPOI;

    Intent intent;
    SpeechRecognizer mRecognizer;
    TextView textView;
    TextView searchView;
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mapData = new TMapData();

        editSearch = (EditText)findViewById(R.id.editSearch);
        btnSearch = (ImageButton)findViewById(R.id.btnSearch);
        lvSearch = (ListView)findViewById(R.id.lvSearch);

        adapter = new SearchAdapter();
        arrayPOI = new ArrayList<>();
        lvSearch.setAdapter(adapter);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO
                );
            }
        }

        //음성검색
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(recognitionListener);
        searchView = (TextView) findViewById(R.id.editSearch);
        textView = (TextView) findViewById(R.id.textView);
        voiceSearch = (ImageButton) findViewById(R.id.voiceSearch);
        voiceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecognizer.startListening(intent);
            }
        });


        lvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int index, long l) {
                try {
                    if (index >= arrayPOI.size()) {
                        return;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                    builder.setTitle("안내")
                            .setMessage(String.format("%s를 도착지로 설정하시겠습니까?", arrayPOI.get(index).name))
                            .setNegativeButton("아니오", null)
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent();
                                    intent.putExtra("POI", arrayPOI.get(index).name);
                                    intent.putExtra("LON", arrayPOI.get(index).longitude);
                                    intent.putExtra("LAT", arrayPOI.get(index).latitude);

                                    setResult(3, intent);
                                    finish();
                                }
                            }).show();
                } catch(Exception ex){
                    Log.d("Exception:", ex.getMessage());
                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mapData.findAllPOI(editSearch.getText().toString(), 20, new TMapData.FindAllPOIListenerCallback() {
                        @Override
                        public void onFindAllPOI(final ArrayList<TMapPOIItem> arrayList) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.clear();
                                    arrayPOI.clear();

                                    for(int i=0; i<arrayList.size(); i++) {
                                        TMapPOIItem poiItem = arrayList.get(i);

                                        String secondLine = poiItem.upperBizName+"/"+poiItem.middleBizName+"/"+poiItem.lowerBizName;
                                        adapter.addItem(poiItem.getPOIName(), secondLine);

                                        POI poi = new POI();
                                        poi.name = poiItem.getPOIName();
                                        poi.latitude = poiItem.getPOIPoint().getLatitude();
                                        poi.longitude = poiItem.getPOIPoint().getLongitude();

                                        arrayPOI.add(poi);
                                    }

                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                } catch(Exception ex) {
                    Log.d("Exception:", ex.getMessage());
                }
            }
        });
    }
    //음성검색 함수
    private RecognitionListener recognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float v) {
        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int i) {
            textView.setText("다시 말해주세요.");

        }

        @Override
        public void onResults(Bundle bundle) {
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = bundle.getStringArrayList(key);

            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);

            searchView.setText(rs[0]);
            textView.setText(rs[0]);
            btnSearch.performClick();
        }

        @Override
        public void onPartialResults(Bundle bundle) {
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
        }
    };
}