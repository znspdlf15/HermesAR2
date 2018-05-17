package hermes.mju.captdesign.hermesar;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
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

public class HermesActivity extends Activity implements TMapGpsManager.onLocationChangedCallback, View.OnClickListener {
    /* 버튼 */
    private Button arButton;
    private Button desButton;
    private Button startButton;
    private Button currentButton;

    // 좌표
    TMapPoint startpoint;
    TMapPoint endpoint;
    TMapPoint tpoint;

    // 현재 좌표
    double nowLatitude;
    double nowLongitude;

    //gps manager
    private TMapGpsManager tmapgps = null;
    private TMapView tmapview = null;

    final ArrayList<ARPoint> listOfPoint = new ArrayList<ARPoint>(); // 시작점 부터 도착점까지 좌표 체크 리스트

    private Context mContext = null;
    private boolean m_bTrackingMode = true;
    private LocationManager mlocationManager;

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
        tmapgps = new TMapGpsManager(this);

        // 지도 띄우기 반드시 initTmap을 먼저해야한다.
        initTmap();

        // gps 초기화
        initGps();

        // button 연결
        initButton();
        mlocationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);


    }

    @Override
    public void onLocationChange(Location location) {
        if (m_bTrackingMode) {
            nowLatitude = location.getLatitude();
            nowLongitude = location.getLongitude();
            tmapview.setLocationPoint(nowLongitude, nowLatitude);
        }
    }

    public void initGps(){
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(1);
        tmapgps.setProvider(tmapgps.GPS_PROVIDER); // gps로 현 위치를 잡습니다.
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER); //연결된 인터넷으로 현 위치를 받습니다.
        tmapgps.OpenGps();
    }

    public void initTmap(){

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.mapview);
        tmapview = new TMapView(this);
        //tmapview.setLocationPoint(LocationMana, ); // 현재 위치로 설정
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


        /*  화면중심을 단말의 현재위치로 이동 */
        tmapview.setTrackingMode(true);
        tmapview.setSightVisible(true);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //ArrayList arTMapPOIItem_1 = tmapdata.findTitlePOI("SKT타워");
        //ArrayList arTMapPOIItem_2 = tmapdata.findAddressPOI("서울 용산구 이태원동");


        TMapPoint te = tmapgps.getLocation();
        double longitude = tmapgps.getLocation().getLongitude();
        double latitude = tmapgps.getLocation().getLatitude();
        tpoint = new TMapPoint(latitude, longitude);
        startpoint = new TMapPoint(37.570243, 126.985567);
        endpoint = new TMapPoint(37.565628, 126.974796);


        findPath();
    }

    public void findPath() {
        try {

            TMapPolyLine pathdata = tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, startpoint, endpoint);
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
            //listOfPoint를 ARActivity로 넘기는 작업
            Intent intent = new Intent(this,ARActivity.class);
            intent.putExtra("listOfPoint", listOfPoint);
            startActivity(intent);
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
        double longitude = tmapgps.getLocation().getLongitude();
        double latitude = tmapgps.getLocation().getLatitude();
        tmapview.setCenterPoint(nowLongitude, nowLatitude, true);
        tmapview.setLocationPoint(nowLongitude, nowLatitude);
    }
    public void TmapSetDestination(){

    }
    public void TmapSetStart(){
        tmapview.setCenterPoint(126.985567, 37.570243, true);
        tmapview.setLocationPoint(126.985567, 37.570243);
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
                                    addARPoints(nl.item(k).getTextContent());
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

    // 얻은 Coordinate를 경도 위도로 분리하여 ARPoint 를 만듦
    // altitude 어떻게 할지 생각해야함.
    public void addARPoints(String str){
        String[] temp = str.split("\\s");

        for ( Integer i = 0; i < temp.length; i++){
            String[] temp2 = temp[i].split(",");
            listOfPoint.add(new ARPoint(i.toString(), Double.parseDouble(temp2[0]), Double.parseDouble(temp2[1]), 120));
        }

       /* if ( temp2.length == 2 ) {
            listOfPoint.add(new ARPoint(index.toString(), Double.parseDouble(temp2[0]), Double.parseDouble(temp2[1]), 210));
        } else if ( temp2.length == 4){
            listOfPoint.add(new ARPoint(index.toString(), Double.parseDouble(temp2[0]), Double.parseDouble(temp2[1]), 210));
            listOfPoint.add(new ARPoint(index.toString(), Double.parseDouble(temp2[2]), Double.parseDouble(temp2[3]), 210));
        }*/
    }


    // 뒤로가기 버튼 클릭시
    public void onBackPressed(){
        // 로그인하면 main activity로 돌아가지 못하게 막기. 추후에 로그아웃 등으로 기능을 바꿔야함
    }

}
