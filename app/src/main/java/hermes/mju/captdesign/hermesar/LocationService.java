package hermes.mju.captdesign.hermesar;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class LocationService extends Service {
    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        super.onCreate();
        // 맨처음에 받아야함
        // 변수 초기화
        // 서비스에서 가장 먼저 호출됨(최초에 한번만)
        Log.d("test", "석섹스석섹스석섹스석섹스석섹스석섹스석섹스석섹스석섹스석섹스석섹스석섹스석섹스석섹스석섹스석섹스석섹스석섹스석섹스석섹스석섹스석섹스석섹스석섹스");

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 서비스가 호출될 때마다 실행


        Log.d("test", "서비스의 onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 서비스가 종료될 때 실행
        Log.d("test", "서비스의 onDestroy");
    }
}
