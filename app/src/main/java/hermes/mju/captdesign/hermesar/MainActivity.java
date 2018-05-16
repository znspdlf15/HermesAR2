package hermes.mju.captdesign.hermesar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

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

public class MainActivity extends AppCompatActivity
        implements TMapGpsManager.onLocationChangedCallback, View.OnClickListener {

    private Context mContext = null;
    private boolean m_bTrackingMode = true;

    private LocationManager mlocationManage;

    private TMapGpsManager tmapgps = null;
    private TMapView tmapview = null;
    private static String mApiKey = "b8758f45-a63e-46a6-a68d-14011fb031f7"; // 발급받은 appKey
    private static int mMarkerID;
    private ArrayList<TMapPoint> m_tmapPoint = new ArrayList<TMapPoint>();
    private ArrayList<String> mArrayMarkerID = new ArrayList<String>();

    private Button arButton;
    private Button desButton;
    private Button startButton;
    private Button currentButton;
    private Button loginButton;

    //
    @Override
    public void onLocationChange(Location location) {
        if (m_bTrackingMode) {
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
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
    public void initTmap(){
        mContext = this;

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

        tmapgps = new TMapGpsManager(MainActivity.this);
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(12);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER); //연결된 인터넷으로 현 위치를 받습니다.
        //실내일 때 유용합니다.
        tmapgps.setProvider(tmapgps.GPS_PROVIDER); //gps로 현 위치를 잡습니다.
        tmapgps.OpenGps();

        /*  화면중심을 단말의 현재위치로 이동 */
        tmapview.setTrackingMode(true);
        tmapview.setSightVisible(true);
        TMapData tmapdata = new TMapData();


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            ArrayList arTMapPOIItem_1 = tmapdata.findTitlePOI("SKT타워");
            ArrayList arTMapPOIItem_2 = tmapdata.findAddressPOI("서울 용산구 이태원동");
            ArrayList tempPoint = new ArrayList();

            TMapPoint tpoint = new TMapPoint(37.570841, 126.985302);
            TMapPoint startpoint = new TMapPoint(37.570243, 126.985567);
            TMapPoint endpoint = new TMapPoint(37.565628, 126.974796);
            tempPoint.add(tpoint);
            //TMapPolyLine pathdata = tmapdata.findPathData(startpoint, endpoint);
            TMapPolyLine pathdata = tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH , startpoint, endpoint);
            //Document docPath = tmapdata.findPathDataAllType(TMapData.TMapPathType.CAR_PATH, startpoint, endpoint);

            //    ArrayList pointList = pathdata.getLinePoint().;
            //   pointList.
            //   Log.d("debug", );

            final ArrayList listOfPoint = new ArrayList();

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

            for ( int i=0; i < listOfPoint.size(); i++){
                Log.d("debug", (String)(listOfPoint.get(i)));
            }

            //Log.d("debug", LineNodeList.getInstance().getCoordinates(0) );
            //for( int k=0; k< LineNodeList.getLength(); k++ ) {
            //Log.d("debug", LineNodeList.getCoordinates(k));
            //}

            //tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH , startpoint, endpoint, tempPoint, 0,
                /*new TMapData.FindPathDataListenerCallback() {
                    @Override
                    public void onFindPathData(TMapPolyLine polyLine) {
                        tmapview.addTMapPath(polyLine);
                    }
                });*/
            //findPathDataWithType(TMapPathType type, TMapPoint startpoint, TMapPoint endpoint);

            tmapview.addTMapPath(pathdata);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mlocationManage = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);

        arButton =  (Button)findViewById(R.id.ARbutton);
        desButton =  (Button)findViewById(R.id.DestiButton);
        startButton =  (Button)findViewById(R.id.startButton);
        currentButton =  (Button)findViewById(R.id.CurrentButton);
        loginButton = (Button)findViewById(R.id.LoginButton);

        arButton.setOnClickListener(this);
        desButton.setOnClickListener(this);
        startButton.setOnClickListener(this);
        currentButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);


        initTmap();

    }

    @Override
    public void onClick(View view) {
        if(view == arButton){
            startActivity(new Intent(this,ARActivity.class));
        }
        if(view == currentButton){
            TmapCurrent();
            //현재 위치 버튼 눌럿을때 할일
        }
        if(view == startButton){
            TmapSetStart();
            //시작지 버튼 눌럿을때 할일
        }
        if(view == desButton){
            TmapSetDestination();
            //목적지 버튼 눌럿을때 할일
        }
        if(view == loginButton){
            startActivity(new Intent(this, HermesActivity.class));
        }
    }
}
