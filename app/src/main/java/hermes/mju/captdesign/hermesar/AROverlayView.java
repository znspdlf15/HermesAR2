package hermes.mju.captdesign.hermesar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.opengl.Matrix;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mju4 on 2018-05-01.
 */


public class AROverlayView extends View {

    Context context;
    private float[] rotatedProjectionMatrix = new float[16];
    private Location currentLocation;
    private List<ARPoint> arPoints;


    public AROverlayView(Context context) {
        super(context);

        this.context = context;

        //Demo points

    }

    public void updateARPoint(ArrayList<ARPoint> list){
        arPoints = list;
    }
    public void updateARPoint(){
        arPoints = new ArrayList<ARPoint>() {{
            /*add(new ARPoint("NORTH", 37.2219417 + 0.02, 127.18768109999996, 210));
            add(new ARPoint("SOUTH", 37.2219417 - 0.02, 127.18768109999996, 210));
            add(new ARPoint("WEST", 37.2219417, 127.18768109999996 - 0.02, 210));
            add(new ARPoint("EAST", 37.2219417, 127.18768109999996 + 0.02, 210));*/


            //add(new ARPoint("NORTH", currentLocation.getLatitude()+0.002, currentLocation.getLongitude(), currentLocation.getAltitude()));
            //add(new ARPoint("SOUTH",  currentLocation.getLatitude()-0.002, currentLocation.getLongitude(), currentLocation.getAltitude()));
            //add(new ARPoint("WEST",  currentLocation.getLatitude(), currentLocation.getLongitude()-0.002, currentLocation.getAltitude()));
            //add(new ARPoint("EAST",  currentLocation.getLatitude(), currentLocation.getLongitude()+0.002, currentLocation.getAltitude()));

        }};

    }

    public void updateRotatedProjectionMatrix(float[] rotatedProjectionMatrix) {
        this.rotatedProjectionMatrix = rotatedProjectionMatrix;
        this.invalidate();
    }

    public void updateCurrentLocation(Location currentLocation){
        this.currentLocation = currentLocation;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (currentLocation == null) {
            return;
        }

        final int radius = 30;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(60);

        for (int i = 0; i < arPoints.size(); i ++) {
            float[] currentLocationInECEF = LocationHelper.WSG84toECEF(currentLocation);
            float[] pointInECEF = LocationHelper.WSG84toECEF(arPoints.get(i).getLocation());
            float[] pointInENU = LocationHelper.ECEFtoENU(currentLocation, currentLocationInECEF, pointInECEF);

            float[] cameraCoordinateVector = new float[4];
            Matrix.multiplyMV(cameraCoordinateVector, 0, rotatedProjectionMatrix, 0, pointInENU, 0);

            // cameraCoordinateVector[2] is z, that always less than 0 to display on right position
            // if z > 0, the point will display on the opposite
            if (cameraCoordinateVector[2] < 0) {
                float x  = (0.5f + cameraCoordinateVector[0]/cameraCoordinateVector[3]) * canvas.getWidth();
                float y = (0.5f - cameraCoordinateVector[1]/cameraCoordinateVector[3]) * canvas.getHeight();

                canvas.drawCircle(x, y, radius, paint);
                canvas.drawText(arPoints.get(i).getName(), x - (30 * arPoints.get(i).getName().length() / 2), y - 80, paint);
            }
        }
    }
}