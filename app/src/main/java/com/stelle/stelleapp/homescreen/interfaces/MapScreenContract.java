package com.stelle.stelleapp.homescreen.interfaces;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

/**
 * Created by Sarthak on 29-04-2017
 */

public interface MapScreenContract {

    interface View {
        void init();

        List<LatLng> addMarkerPointsPolygon(LatLng newLatLng);

        Marker addMarker(LatLng latLng, boolean isArea);

        void drawPolyLine();

        boolean isLocationPermissionGranted();
    }

}
