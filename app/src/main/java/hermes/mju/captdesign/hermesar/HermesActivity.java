package hermes.mju.captdesign.hermesar;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
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
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Tak on 2018-05-09.
 */

public class HermesActivity extends Activity implements TMapGpsManager.onLocationChangedCallback, View.OnClickListener, TMapView.OnLongClickListenerCallback,TMapView.OnClickListenerCallback ,TMapView.OnDisableScrollWithZoomLevelCallback{
    /* 버튼 */
    private Button arButton;
    private Button desButton;
    private Button startButton;
    private Button currentButton;
    private Button searchButton;
    private Button zoominButton;
    private Button zoomoutButton;
    private Button compassButton;

    // 좌표
    private TMapPoint startpoint;
    private TMapPoint endpoint;
    private TMapPoint tpoint;

    private TMapPoint Nowpoint;

    // 현재 좌표
    private double nowLatitude;
    private double nowLongitude;
    private double nowAltitude;

    // 롱클릭 좌표의 주소
    private String pointAddress;

    private TMapPoint startrenew;
    private boolean pathing = false;

    //gps manager
    private TMapGpsManager tmapgps = null;
    private TMapView tmapview = null;

    //마지막 클릭한 좌표
    private TMapPoint clickedPoint = null;
    private boolean m_bTrackingMode = true;

    final ArrayList<ARPoint> listOfPoint = new ArrayList<ARPoint>(); // 시작점 부터 도착점까지 좌표 체크 리스트
    
    private Context mContext = null;

    private LocationManager mlocationManager;

    private static String mApiKey = "b8758f45-a63e-46a6-a68d-14011fb031f7"; // 발급받은 appKey
    private static int mMarkerID;
    private ArrayList<TMapPoint> m_tmapPoint = new ArrayList<TMapPoint>();
    private ArrayList<String> mArrayMarkerID = new ArrayList<String>();

    private TMapData tmapdata;

    private String time;
    private String distance;
    private String min;
    private String sec;

    private boolean compass_mode= false;

    public void initButton(){
        arButton =  (Button)findViewById(R.id.ARbutton);
        desButton =  (Button)findViewById(R.id.DestiButton);
        startButton =  (Button)findViewById(R.id.startButton);
        currentButton =  (Button)findViewById(R.id.CurrentButton);
        searchButton = (Button)findViewById(R.id.SearchButton);
        zoominButton = (Button)findViewById(R.id.zoomin);
        zoomoutButton = (Button)findViewById(R.id.zoomout);
        compassButton = (Button)findViewById(R.id.compass);


        arButton.setOnClickListener(this);
        desButton.setOnClickListener(this);
        startButton.setOnClickListener(this);
        currentButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        zoominButton.setOnClickListener(this);
        zoomoutButton.setOnClickListener(this);
        compassButton.setOnClickListener(this);

    }
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hermes);

        // 초기화
        mContext = this;
        tmapdata = new TMapData();
        tmapgps = new TMapGpsManager(this);

        //위치 변경
        tmapgps.setMinTime(1);
        tmapgps.setMinDistance(5);
        //map.ctrl_nav.disableZoomWheel(); //지도 확대축소 기능을 막습니다.
        // 지도 띄우기 반드시 initTmap을 먼저해야한다.
        initTmap();

        // gps 초기화
        initGps();

        // button 연결
        initButton();

        mlocationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
    }

    @Override
    //위치가 바뀔경우 실행
    //경로안내 도중 현재위치가 바뀔경우 현재위치를 새로운 출발점으로 갱신하여 경로요청을 다시함
    public void onLocationChange(Location location) {
        if(pathing==true) {
            Nowpoint = tmapgps.getLocation();
            //tmapview.setCompassMode(true);
            tmapview.setIconVisibility(true);
            tmapview.setLocationPoint( Nowpoint.getLatitude(),Nowpoint.getLongitude());
            re_findPath(Nowpoint);
        }
        if (m_bTrackingMode) {
            nowLatitude = location.getLatitude();
            nowLongitude = location.getLongitude();
            nowAltitude = location.getAltitude();
            tmapview.setLocationPoint(nowLongitude, nowLatitude);
        }
    }

    public void initGps(){
        tmapgps.setMinTime(1);
        tmapgps.setMinDistance(5);
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
        //tmapview.setCompassMode(true);
        //tmapview.setUserScrollZoomEnable(true);


        /* 현위치 아이콘표시 */
        tmapview.setIconVisibility(true);

        /* 줌레벨 */
        tmapview.setZoomLevel(15);
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);


        /*  화면중심을 단말의 현재위치로 이동 */
        tmapview.setTrackingMode(true);
        tmapview.setSightVisible(false);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //ArrayList arTMapPOIItem_1 = tmapdata.findTitlePOI("SKT타워");
        //ArrayList arTMapPOIItem_2 = tmapdata.findAddressPOI("서울 용산구 이태원동");


        TMapPoint te = tmapgps.getLocation();
        double longitude = tmapgps.getLocation().getLongitude();
        double latitude = tmapgps.getLocation().getLatitude();
        tpoint = new TMapPoint(latitude, longitude);
        //startpoint = new TMapPoint(37.570243, 126.985567);
        //endpoint = new TMapPoint(37.565628, 126.974796);

        //findPath();
    }

    //현재 위치 변경시 경로안내 현재위치를 출발점으로 다시 함
    //spoint는 현재위치,새로운 출발점
    public void re_findPath(TMapPoint spoint){
        try {
            if ( spoint != null && endpoint != null ) {
                TMapPolyLine pathdata = tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, spoint, endpoint);
                findAllCoordinates(); // 길 안내에 필요한 좌표들을 listOfPoint에 리스트로 저장
                tmapview.addTMapPath(pathdata);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    };

    public void findPath() {
        try {
            if ( startpoint != null && endpoint != null ) {
                pathing=true;
                TMapPolyLine pathdata = tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, startpoint, endpoint);
                findAllCoordinates(); // 길 안내에 필요한 좌표들을 listOfPoint에 리스트로 저장
                tmapview.addTMapPath(pathdata);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    public void passardata(){
        Intent intent = new Intent(this,ARActivity.class);
        intent.putExtra("nowLongitutde", nowLongitude);
        intent.putExtra("nowLatitude", nowLatitude);
        intent.putExtra("listOfPoint", listOfPoint);

    }

    public void onClick(View view) {
        if(view == arButton){
            //listOfPoint를 ARActivity로 넘기는 작업
            Intent intent = new Intent(this,ARActivity.class);
            intent.putExtra("listOfPoint", listOfPoint);
            //출발지-목적지간의 시간,거리
            intent.putExtra("Time",min);
            intent.putExtra("Distance",distance);
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
        // 검색 버튼 클릭 시
        if(view == searchButton){
            gotoSearch();
        }
        // + 버튼 클릭 시
        if(view == zoominButton){
            tmapview.MapZoomIn();
        }
        // - 버튼 클릭 시
        if(view == zoomoutButton){
            tmapview.MapZoomOut();
        }
        // 방향  버튼 클릭 시
        if(view == compassButton){
            if(compass_mode==false){
                compassOn();
                TmapCurrent();
            }else if(compass_mode==true){
                compassOff();
                TmapCurrent();
            }
        }
    }

    public void TmapCurrent() {
        double nowLongitude = tmapgps.getLocation().getLongitude();
        double nowLatitude = tmapgps.getLocation().getLatitude();
        tmapview.setCenterPoint(nowLongitude, nowLatitude, true);
        tmapview.setLocationPoint(nowLongitude, nowLatitude);
    }
    public void TmapSetDestination(){
        endpoint = tmapview.getCenterPoint();                         //현재 지도 중심을 도착지로 설정
        TMapMarkerItem d_tItem = new TMapMarkerItem();
        Context s_context = mContext;
        d_tItem.setTMapPoint(endpoint);
        d_tItem.setName("목적지");
        d_tItem.setVisible(TMapMarkerItem.VISIBLE);
        Bitmap d_bitmap = BitmapFactory.decodeResource(s_context.getResources(),R.drawable.marker);
        d_tItem.setIcon(d_bitmap);
        tmapview.bringMarkerToFront(d_tItem);
        findPath();                                                 //설정된 출발지,도착지로 보행자 경로표시
    }
    public void TmapSetStart(){startpoint = tmapview.getCenterPoint();                         //현재 지도 중심을 출발지로 설정
        TMapMarkerItem tItem = new TMapMarkerItem();
        Context context = mContext;
        tItem.setTMapPoint(startpoint);
        tItem.setName("출발지");
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.marker2);
        tItem.setIcon(bitmap);
        tmapview.bringMarkerToFront(tItem);
    }
    public void gotoSearch(){
        startActivityForResult(new Intent(this, SearchActivity.class),3);

    }

    //방향 버튼 클릭시 나침반모드,시야표출 활성화, 비활성화
    public void compassOn(){
        tmapview.setCompassMode(true);
        tmapview.setSightVisible(true);
        compass_mode=true;
    }
    public void compassOff(){
        tmapview.setCompassMode(false);
        tmapview.setSightVisible(false);
        compass_mode=false;

    }

    public void findAllCoordinates(){
        listOfPoint.clear();
        tmapdata.findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH, startpoint, endpoint, new TMapData.FindPathDataAllListenerCallback() {
            @Override
            public void onFindPathDataAll(Document document) {
                Element root = document.getDocumentElement();
                NodeList nodeListPlacemark = root.getElementsByTagName("Placemark");

                NodeList disPlacemark = root.getElementsByTagName("tmap:totalDistance");    //출발지-목적지까지의 거리
                distance = disPlacemark.item(0).getTextContent();
                NodeList timePlacemark = root.getElementsByTagName("tmap:totalTime");       //출발지-목적지까지의 시간
                time = timePlacemark.item(0).getTextContent();
                addTimeDistance();

                for (int i = 0; i < nodeListPlacemark.getLength(); i++) {
                    NodeList nodeListPlacemarkItem = nodeListPlacemark.item(i).getChildNodes();
                    for (int j = 0; j < nodeListPlacemarkItem.getLength(); j++) {

                        if (nodeListPlacemarkItem.item(j).getNodeName().equals("LineString")) {
                            NodeList nl = nodeListPlacemarkItem.item(j).getChildNodes();
                            for (int k = 0; k < nl.getLength(); k++) {
                                if (nl.item(k).getNodeName().equals("coordinates")) {
                                    addARPoints(nl.item(k).getTextContent());
                                    //LineNodeList.addToNode(nl.item(k));
                                    Log.d("debug", nl.item(k).getTextContent());
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
        String[] temp = str.split("\\s");   // 공백으로 분리 각 좌표 분리

        for ( Integer i = 0; i < temp.length; i++){
            String[] temp2 = temp[i].split(",");    // ,로 경도 위도 분리
            listOfPoint.add(new ARPoint(i.toString(), Double.parseDouble(temp2[1]), Double.parseDouble(temp2[0]), nowAltitude));    // 일단 현재 고도로 설정함
        }
       /* if ( temp2.length == 2 ) {
            listOfPoint.add(new ARPoint(index.toString(), Double.parseDouble(temp2[0]), Double.parseDouble(temp2[1]), 210));
        } else if ( temp2.length == 4){
            listOfPoint.add(new ARPoint(index.toString(), Double.parseDouble(temp2[0]), Double.parseDouble(temp2[1]), 210));
            listOfPoint.add(new ARPoint(index.toString(), Double.parseDouble(temp2[2]), Double.parseDouble(temp2[3]), 210));
        }*/
    }

    //출발지-목적지 까지의 소요시간 및 거리 전달
    public void addTimeDistance(){
        Intent dt = new Intent(this,ARActivity.class);
        int i_time = Integer.parseInt(time);
        int minute =i_time/60;
        int second =i_time%60;
        min = Integer.toString(minute);
        sec = Integer.toString(second);
        min = min + "분 " + sec +"초";
        distance = distance + "m";
    }

    // 뒤로가기 버튼 클릭시
    public void onBackPressed(){
        startActivity(new Intent(this, MainActivity.class));
        // 로그인하면 main activity로 돌아가지 못하게 막기. 추후에 로그아웃 등으로 기능을 바꿔야함
    }

    @Override
    public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
        return false;
    }
    public boolean onPressEvent(ArrayList markerlist, ArrayList poilist, TMapPoint point, PointF pointf) {
        return false;
    }


    // 일단 임시로.
    @Override
    public void onLongPressEvent(ArrayList<TMapMarkerItem> markerList, ArrayList<TMapPOIItem> click_point_list, TMapPoint click_point) {
        clickedPoint = click_point;
        try {
            pointAddress = tmapdata.convertGpsToAddress(clickedPoint.getLatitude(),clickedPoint.getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        popUPOnMap();
    }

    public void popUPOnMap(){
        Intent popmap = new Intent(this, MapPopUP.class);
        popmap.putExtra("spoint",pointAddress);
        startActivityForResult(popmap, 0);   // requestCode 상수로 리팩토링 필요
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:
                makePoint(resultCode);
                break;
            case 3:

                if((data.getSerializableExtra("LAT")) == null || (data.getSerializableExtra("LON") == null) ){
                  break;  // 에러처리
                }
                endpoint.setLatitude((double)data.getSerializableExtra("LAT"));
                endpoint.setLongitude((double)data.getSerializableExtra("LON"));
                TMapMarkerItem tItem2 = new TMapMarkerItem();
                Context context = mContext;
                tItem2.setTMapPoint(endpoint);
                tItem2.setName("ping2");
                Bitmap bitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.marker2);
                tItem2.setIcon(bitmap2);
                tmapview.bringMarkerToFront(tItem2);
                findPath();
                break;
        }
    }

    // 이것도 상수로 리팩토링 필요 현재는 0: 시작점, 1: 도착점으로 구분했음
    public void makePoint(int pointType){

        if(pointType==0) {
            startpoint = clickedPoint;
            TMapMarkerItem tItem = new TMapMarkerItem();
            Context context = mContext;
            tItem.setTMapPoint(startpoint);
            tItem.setName("ping");
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.marker);
            tItem.setIcon(bitmap);
            tmapview.bringMarkerToFront(tItem);
        }
        if(pointType==1){
            endpoint = clickedPoint;
            TMapMarkerItem tItem2 = new TMapMarkerItem();
            Context context = mContext;
            tItem2.setTMapPoint(endpoint);
            tItem2.setName("ping2");
            Bitmap bitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.marker2);
            tItem2.setIcon(bitmap2);
            tmapview.bringMarkerToFront(tItem2);
            findPath();
        }if(pointType==3){

        }
    }

    @Override
    public void onDisableScrollWithZoomLevelEvent(float v, TMapPoint tMapPoint) {
        if(compass_mode==true) {
          compassOn();
        }
    }


    // 클릭해서 popup을 띄우고 싶은데, 이러면 화면 확대 축소할때도 떠서 일단 LongClick에다 popup을 띄우기로 함.
    // 추후 수정.

}