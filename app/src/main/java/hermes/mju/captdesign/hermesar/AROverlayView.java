package hermes.mju.captdesign.hermesar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.location.Location;
import android.opengl.Matrix;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created by mju4 on 2018-05-01.
 */


public class AROverlayView extends View {

    Context context;
    private float[] rotatedProjectionMatrix = new float[16];
    private Location currentLocation;
    private List<ARPoint> arPoints;

    private float pointx[]= new float[100];
    private float pointy[]= new float[100];

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


    // 점 그리기 추후에 수정.
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
        if ( arPoints.size() > 0 ) {
            for (int i = 0; i < 1; i++) {
                //for (int i = 0; i < arPoints.size(); i ++) {
                arPoints.get(i).getLocation().setAltitude(currentLocation.getAltitude());
                float[] currentLocationInECEF = LocationHelper.WSG84toECEF(currentLocation);
                float[] pointInECEF = LocationHelper.WSG84toECEF(arPoints.get(i).getLocation());
                float[] pointInENU = LocationHelper.ECEFtoENU(currentLocation, currentLocationInECEF, pointInECEF);

                float[] cameraCoordinateVector = new float[4];
                Matrix.multiplyMV(cameraCoordinateVector, 0, rotatedProjectionMatrix, 0, pointInENU, 0);

                // cameraCoordinateVector[2] is z, that always less than 0 to display on right position
                // if z > 0, the point will display on the opposite
                if (cameraCoordinateVector[2] < 0) {
                    float x = (0.5f + cameraCoordinateVector[0] / cameraCoordinateVector[3]) * canvas.getWidth();
                    float y = (0.5f - cameraCoordinateVector[1] / cameraCoordinateVector[3]) * canvas.getHeight();
                    pointx[i] = x;
                    pointy[i] = y;
                } else {
                    //drawDirection();
                }
            }

//        for(int i = 0; i < arPoints.size(); i ++) {
//            drawArrow(paint, canvas, pointx[i], pointy[i], pointx[i + 1], pointy[i + 1]);
//
//            //기존에 있던 ARpoint마다 점 찍던것
//            //canvas.drawCircle(pointx[i], pointy[i], radius, paint);
//            //canvas.drawText(arPoints.get(i).getName(), pointx[i] - (30 * arPoints.get(i).getName().length() / 2), pointy[i] - 80, paint);
//        }
//        //tha line
//        for(int i = 0; i < arPoints.size()-1; i ++) {
//            canvas.drawLine(pointx[i], pointy[i], pointx[i + 1], pointy[i + 1],paint);
//        }
        }
    }
    private void drawDirection(){
        ((ARActivity)context).drawDirection();
    }
    private void drawArrow(Paint paint, Canvas canvas, float from_x, float from_y, float to_x, float to_y)
    {
        float angle,anglerad, radius, lineangle;

        //values to change for other appearance *CHANGE THESE FOR OTHER SIZE ARROWHEADS*
        radius=110;
        angle=115;

        //some angle calculations
        anglerad= (float) (PI*angle/180.0f);
        lineangle= (float) (atan2(to_y-from_y,to_x-from_x));

        //tha triangle
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(to_x, to_y);
        path.lineTo((float)(to_x-radius*cos(lineangle - (anglerad / 2.0))),
                (float)(to_y-radius*sin(lineangle - (anglerad / 2.0))));
        path.lineTo((float)(to_x-radius*cos(lineangle + (anglerad / 2.0))),
                (float)(to_y-radius*sin(lineangle + (anglerad / 2.0))));
        path.close();
        canvas.drawPath(path, paint);
    }


}