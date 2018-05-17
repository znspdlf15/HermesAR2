package hermes.mju.captdesign.hermesar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Tak on 2018-05-09.
 */

public class HermesActivity extends Activity implements TMapGpsManager.onLocationChangedCallback, View.OnClickListener{
    /* 버튼 */
    private Button arButton;
    private Button desButton;
    private Button startButton;
    private Button currentButton;

    // 좌표
    TMapPoint startpoint;
    TMapPoint endpoint;
    TMapPoint tpoint;

    //gps manager
    TMapGpsManager gps;

    final ArrayList listOfPoint = new ArrayList(); // 시작점 부터 도착점까지 좌표 체크 리스트

    private Context mContext = null;
    private boolean m_bTrackingMode = true;

    private LocationManager mlocationManage;

    private TMapGpsManager tmapgps = null;
    private TMapView tmapview = null;
    private static String mApiKey = "b8758f45-a63e-46a6-a68d-14011fb031f7"; // 발급받은 appKey
    private static int mMarkerID;
    private ArrayList<TMapPoint> m_tmapPoint = new ArrayList<TMapPoint>();
    private ArrayList<String> mArrayMarkerID = new ArrayList<String>();

    TMapData tmapdata;



    public void initButton(){
        arButton =  (Button)findViewById(R.id.ARbutton);
        desButton =  (Button)findViewById(R.id.DestiButton);
        startButton =  (Button)findViewById(R.id.startButton);
        currentButton =  (Button)findViewById(R.id.CurrentButton);


        arButton.setOnClickListener(this);
        desButton.setOnClickListener(this);
        startButton.setOnClickListener(this);
        currentButton.setOnClickListener(this);

    }
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hermes);

        // 초기화
        mContext = this;
        tmapdata = new TMapData();
        gps = new TMapGpsManager(this);

        // button 연결
        initButton();



        mlocationManage = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);

        // 지도 띄우기
        initTmap();
    }

    @Override
    public void onLocationChange(Location location) {
        if (m_bTrackingMode) {
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }



    public void initTmap(){

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.mapview);
        tmapview = new TMapView(this);
        linearLayout.addView(tmapview);
        tmapview.setSKTMapApiKey(mApiKey);


        /* 현재 보는 방향 */
        tmapview.setCompassMode(true);

        /* 현위치 아이콘표시 */
        tmapview.setIconVisibility(true);

        /* 줌레벨 */
        tmapview.setZoomLevel(15);
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);

        tmapgps = new TMapGpsManager(HermesActivity.this);
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(12);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER); //연결된 인터넷으로 현 위치를 받습니다.
        //실내일 때 유용합니다.
        tmapgps.setProvider(tmapgps.GPS_PROVIDER); //gps로 현 위치를 잡습니다.
        tmapgps.OpenGps();

        /*  화면중심을 단말의 현재위치로 이동 */
        tmapview.setTrackingMode(true);
        tmapview.setSightVisible(true);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            ArrayList arTMapPOIItem_1 = tmapdata.findTitlePOI("SKT타워");
            ArrayList arTMapPOIItem_2 = tmapdata.findAddressPOI("서울 용산구 이태원동");
            ArrayList tempPoint = new ArrayList();

            tpoint = new TMapPoint(37.570841, 126.985302);
            startpoint = new TMapPoint(37.570243, 126.985567);
            endpoint = new TMapPoint(37.565628, 126.974796);

            tempPoint.add(tpoint);
            TMapPolyLine pathdata = tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH , startpoint, endpoint);

            findAllCoordinates(); // 길 안내에 필요한 좌표들을 listOfPoint에 리스트로 저장

            tmapview.addTMapPath(pathdata);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    public void onClick(View view) {
        if(view == arButton){
            startActivity(new Intent(this,ARActivity.class));
        }
        //현재 위치 버튼 클릭 시
        if(view == currentButton){
            TmapCurrent();

        }
        //시작지 버튼 클릭 시
        if(view == startButton){
            TmapSetStart();

        }
        //목적지 버튼 클릭 시
        if(view == desButton){
            TmapSetDestination();
        }

    }

    public void TmapCurrent() {
        tmapview.setZoomLevel(15);
        tmapview.setCenterPoint(37.2250224,127.18757849999997 , true);
    }
    public void TmapSetDestination(){

    }
    public void TmapSetStart(){

    }
    public void findAllCoordinates(){
        tmapdata.findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH, startpoint, endpoint, new TMapData.FindPathDataAllListenerCallback() {
            @Override
            public void onFindPathDataAll(Document document) {
                Element root = document.getDocumentElement();
                NodeList nodeListPlacemark = root.getElementsByTagName("Placemark");
                for( int i=0; i<nodeListPlacemark.getLength(); i++ ) {
                    NodeList nodeListPlacemarkItem = nodeListPlacemark.item(i).getChildNodes();
                    for( int j=0; j<nodeListPlacemarkItem.getLength(); j++ ) {

                        if( nodeListPlacemarkItem.item(j).getNodeName().equals("LineString") ) {
                            NodeList nl = nodeListPlacemarkItem.item(j).getChildNodes();
                            for( int k=0; k<nl.getLength(); k++ ) {
                                if ( nl.item(k).getNodeName().equals("coordinates")) {
                                    listOfPoint.add(nl.item(k).getTextContent());
                                    //LineNodeList.addToNode(nl.item(k));
                                    //Log.d("debug", nl.item(k).getTextContent());
                                }
                            }
                        }
                    }
                }
            }
        });
    }
}
