package hermes.mju.captdesign.hermesar;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mju4 on 2018-05-01.
 */

public class ARPoint implements Parcelable {
    Location location;
    String name;

    public ARPoint(String name, double lat, double lon, double altitude) {
        this.name = name;
        location = new Location("ARPoint");
        location.setLatitude(lat);
        location.setLongitude(lon);
        location.setAltitude(altitude);
    }

    protected ARPoint(Parcel in) {
        location = in.readParcelable(Location.class.getClassLoader());
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(location, flags);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ARPoint> CREATOR = new Creator<ARPoint>() {
        @Override
        public ARPoint createFromParcel(Parcel in) {
            return new ARPoint(in);
        }

        @Override
        public ARPoint[] newArray(int size) {
            return new ARPoint[size];
        }
    };

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }
}