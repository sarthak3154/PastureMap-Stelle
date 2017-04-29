package com.stelle.stelleapp.homescreen.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;
import com.stelle.stelleapp.R;
import com.stelle.stelleapp.homescreen.interfaces.MapScreenContract;
import com.stelle.stelleapp.widgets.AppTextView;
import com.stelle.stelleapp.widgets.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by Sarthak on 29-04-2017
 */

public class MapScreenFragment extends Fragment implements MapScreenContract.View, OnMapReadyCallback, GoogleMap.OnMapClickListener {

    @Bind(R.id.mapView)
    MapView mMapView;

    private ArrayList<Marker> mapMarkers = new ArrayList<>();
    private ArrayList<LatLng> points = new ArrayList<>();

    private GoogleMap googleMap;


    public MapScreenFragment() {

    }

    public static MapScreenFragment newInstance() {
        Bundle args = new Bundle();
        MapScreenFragment fragment = new MapScreenFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_screen, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView.onCreate(savedInstanceState);
        init();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void init() {
        mMapView.onResume();
        MapsInitializer.initialize(this.getActivity().getApplicationContext());
        mMapView.getMapAsync(this);
    }

    @Override
    public List<LatLng> addMarkerPointsPolygon() {
        List<LatLng> polygonPoints = new ArrayList<>();
        if (points.size() > 3) {
            HashMap<Integer, LatLng> tempPoints = new HashMap<>();
            tempPoints.put(0, points.get(0));
            polygonPoints.add(0, points.get(0));
            float MIN = getDistance(points.get(0), points.get(1));
            for (int i = 0; i < points.size(); i++) {
                for (int j = i + 1; j < points.size(); j++) {
                    if (!tempPoints.containsValue(points.get(j)) && (getDistance(tempPoints.get(i), points.get(j)) <= MIN)) {
                        if (polygonPoints.size() == (i + 1)) {
                            polygonPoints.add(i + 1, points.get(j));
                        } else {
                            polygonPoints.set(i + 1, points.get(j));
                        }
                        tempPoints.put(i + 1, points.get(j));
                    }
                }
            }
        } else {
            for (int i = 0; i < points.size(); i++) {
                polygonPoints.add(points.get(i));
            }
        }
        polygonPoints.add(points.get(0));
        return polygonPoints;
    }

    @Override
    public Marker addMarker(LatLng latLng) {
        IconGenerator iconGenerator = new IconGenerator(getActivity());
        iconGenerator.setBackground(getResources().getDrawable(R.drawable.drawable_circle_marker));
        iconGenerator.setRotation(0);
        LayoutInflater myInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View markerView = myInflater.inflate(R.layout.marker_map, null, false);
        AppTextView textView = (AppTextView) markerView.findViewById(R.id.textCount);
        textView.setText(Integer.toString(mapMarkers.size() + 1));
        iconGenerator.setContentView(markerView);
        Marker marker = googleMap.addMarker(new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())).
                position(latLng));
        return marker;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Utils.showToast(getActivity().getApplicationContext(), getString(R.string.string_show_location));
            return;
        }
        this.googleMap = googleMap;
        googleMap.setMyLocationEnabled(true);
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setCompassEnabled(false);
        googleMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Marker marker = addMarker(latLng);
        points.add(latLng);
        mapMarkers.add(marker);
        if (mapMarkers.size() == 2) {
            drawPolyLine();
        } else if (mapMarkers.size() > 2) {
            drawPolygon();
        }

    }

    private void drawPolygon() {
        googleMap.addPolygon(new PolygonOptions().addAll(addMarkerPointsPolygon())
                .fillColor(Color.CYAN).strokeColor(Color.WHITE));
    }

    private void drawPolyLine() {

        PolylineOptions options = new PolylineOptions().width(8).color(Color.WHITE).geodesic(true);
        for (int i = 0; i < 2; i++) {
            LatLng point = points.get(i);
            options.add(point);
        }
        googleMap.addPolyline(options);
    }

    public float getDistance(LatLng pos1, LatLng pos2) {
        Location location1 = new Location("location1");
        location1.setLatitude(pos1.latitude);
        location1.setLongitude(pos1.longitude);
        Location location2 = new Location("location2");
        location2.setLatitude(pos2.latitude);
        location2.setLongitude(pos2.longitude);
        return location1.distanceTo(location2);

    }

}
