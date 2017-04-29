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
import android.util.Log;
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
import com.google.maps.android.SphericalUtil;
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
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by Sarthak on 29-04-2017
 */

public class MapScreenFragment extends Fragment implements MapScreenContract.View, OnMapReadyCallback, GoogleMap.OnMapClickListener {

    @Bind(R.id.mapView)
    MapView mMapView;
    @Bind(R.id.imageDone)
    ImageView imageDone;
    @Bind(R.id.imageRemove)
    ImageView imageRemove;
    @Bind(R.id.imageUndo)
    ImageView imageUndo;

    private ArrayList<Marker> mapMarkers = new ArrayList<>();
    private List<LatLng> points = new ArrayList<>();
    private List<LatLng> pointsAdded = new ArrayList<>();
    private LatLng pointCapture;
    private Double area;

    private GoogleMap googleMap;
    private HashMap<Integer, List<LatLng>> multiplePolyMap;


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
    public List<LatLng> addMarkerPointsPolygon(LatLng newLatLng) {

        List<LatLng> polygonPoints = new ArrayList<>();
        polygonPoints.clear();
        if (points.size() >= 3) {

            float MIN = getDistance(newLatLng, points.get(0));
            int minPos1 = 0;
            int minPos2 = 0;
            List<LatLng> tempPoints = new ArrayList<>();
            for (int i = 1; i < points.size(); i++) {
                if (getDistance(newLatLng, points.get(i)) < MIN) {
                    MIN = getDistance(newLatLng, points.get(i));
                    minPos1 = i;
                }
            }
            for (int i = 0; i < points.size(); i++) {
                if (i != minPos1) {
                    tempPoints.add(points.get(i));
                    break;
                }
            }
            MIN = getDistance(newLatLng, tempPoints.get(0));
            for (int i = 0; i < points.size(); i++) {

                if ((i != minPos1) && getDistance(newLatLng, points.get(i)) < MIN) {
                    MIN = getDistance(newLatLng, points.get(i));
                    minPos2 = i;
                }
            }

            if (Math.abs(minPos1 - minPos2) > 1) {
                if (minPos1 - minPos2 > 1) {
                    points.add(minPos1 + 1, newLatLng);
                } else {
                    points.add(minPos2 + 1, newLatLng);
                }
            } else if (minPos1 - minPos2 == 1) {
                points.add(minPos1, newLatLng);
            } else if (minPos1 - minPos2 == -1) {
                points.add(minPos1 + 1, newLatLng);
            }

            for (int i = 0; i < points.size(); i++) {
                polygonPoints.add(points.get(i));
            }
            polygonPoints.add(points.get(0));
        } else {
            points.add(newLatLng);
            for (int i = 0; i < points.size(); i++) {
                polygonPoints.add(points.get(i));
            }
            polygonPoints.add(points.get(0));
        }
        googleMap.clear();
        for (int i = 0; i < points.size(); i++) {
            addMarker(points.get(i), false);
        }
        return polygonPoints;
    }


    @Override
    public Marker addMarker(LatLng latLng, boolean isArea) {
        IconGenerator iconGenerator = new IconGenerator(getActivity());
        if (isArea) {
            iconGenerator.setBackground(getResources().getDrawable(R.drawable.drawable_circle_marker_white));
        } else {
            iconGenerator.setBackground(getResources().getDrawable(R.drawable.drawable_circle_marker));
        }
        iconGenerator.setRotation(0);
        LayoutInflater myInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View markerView;
        if (isArea) {
            markerView = myInflater.inflate(R.layout.map_marker_white, null, false);
        } else {
            markerView = myInflater.inflate(R.layout.marker_map, null, false);
        }

        if (isArea) {
            AppTextView textView = (AppTextView) markerView.findViewById(R.id.textCount);
            textView.setText(Double.toString(area));
        }
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
        pointCapture = latLng;
        pointsAdded.add(latLng);
        Marker marker = addMarker(latLng, false);
        mapMarkers.add(marker);
        if (mapMarkers.size() <= 2) {
            points.add(latLng);
            drawPolyLine();
        } else if (mapMarkers.size() > 2) {
            drawPolygon(latLng);
        }

    }

    private void drawPolygon(LatLng latLng) {
        googleMap.addPolygon(new PolygonOptions().addAll(addMarkerPointsPolygon(latLng))
                .fillColor(Color.CYAN).strokeColor(Color.WHITE));
        area = SphericalUtil.computeArea(points);
        addMarker(findMapCenter(), true);
    }

    @Override
    public void drawPolyLine() {

        PolylineOptions options = new PolylineOptions().width(8).color(Color.WHITE).geodesic(true);
        for (int i = 0; i < points.size(); i++) {
            LatLng point = points.get(i);
            options.add(point);
        }
        googleMap.addPolyline(options);
    }

    public LatLng findMapCenter() {
        double longitude = 0;
        double latitude = 0;
        double maxLat = 0, minLat = 0, maxLon = 0, minLon = 0;
        int i = 0;
        for (LatLng p : points) {
            latitude = p.latitude;
            longitude = p.longitude;
            if (i == 0) {
                maxLat = latitude;
                minLat = latitude;
                maxLon = longitude;
                minLon = longitude;
            } else {
                if (maxLat < latitude)
                    maxLat = latitude;
                if (minLat > latitude)
                    minLat = latitude;
                if (maxLon < longitude)
                    maxLon = longitude;
                if (minLon > longitude)
                    minLon = longitude;
            }
            i++;
        }
        latitude = (maxLat + minLat) / 2;
        longitude = (maxLon + minLon) / 2;
        return new LatLng(latitude, longitude);
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

    @OnClick(R.id.imageUndo)
    public void onClickUndo() {
        googleMap.clear();
        List<LatLng> newPointsList = new ArrayList<>();
        newPointsList.clear();
        if (points.size() > 3) {
            newPointsList = getUndoList();
            googleMap.addPolygon(new PolygonOptions().addAll(newPointsList)
                    .fillColor(Color.CYAN).strokeColor(Color.WHITE));
            area = SphericalUtil.computeArea(points);

        } else if (points.size() <= 3 && points.size() > 1) {

            newPointsList = getUndoList();
            drawPolyLine();
        } else {
            points.clear();
        }
    }

    @OnClick(R.id.imageRemove)
    public void onClickRemove() {
        googleMap.clear();
        points.clear();
        pointsAdded.clear();
    }

    @OnClick(R.id.imageDone)
    public void onClickDone() {
        googleMap.clear();
        List<LatLng> pointsList = new ArrayList<>();
        pointsList.clear();
        pointsList = points;
        pointsList.add(points.get(0));
        googleMap.addPolygon(new PolygonOptions().addAll(pointsList)
                .fillColor(Color.CYAN).strokeColor(Color.WHITE));
        area = SphericalUtil.computeArea(points);
        addMarker(findMapCenter(), true);
    }

    public List<LatLng> getUndoList() {
        List<LatLng> newPointsList = new ArrayList<>();
        newPointsList.clear();
        for (int i = 0; i < points.size(); i++) {
            if (!points.get(i).equals(pointsAdded.get(pointsAdded.size() - 1))) {
                newPointsList.add(points.get(i));
            }
        }
        points.clear();
        pointsAdded.remove(pointsAdded.size() - 1);
        points = newPointsList;
        for (int i = 0; i < points.size(); i++) {
            addMarker(points.get(i), false);
        }
        newPointsList.add(points.get(0));
        return newPointsList;
    }

}
