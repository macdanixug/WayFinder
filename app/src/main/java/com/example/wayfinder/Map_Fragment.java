package com.example.wayfinder;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

public class Map_Fragment extends Fragment {
    public Map_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_, container, false);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.MY_MAP);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                LatLng mbararaReferralHospital = new LatLng(-0.616389, 30.658889); // Latitude and Longitude for Mbarara Regional Referral Hospital

                // Create a list of LatLng points to define the perimeter of the hospital
                List<LatLng> perimeterPoints = new ArrayList<>();//0.617715, 30.657336
                perimeterPoints.add(new LatLng(-0.616449, 30.657642));
                perimeterPoints.add(new LatLng(-0.616518, 30.657529));
                perimeterPoints.add(new LatLng(-0.617715, 30.657336));
                perimeterPoints.add(new LatLng(-0.617677, 30.657781));
                perimeterPoints.add(new LatLng(-0.617661, 30.657996));
                perimeterPoints.add(new LatLng(-0.617473, 30.658114));
                perimeterPoints.add(new LatLng(-0.617409, 30.658501));
                perimeterPoints.add(new LatLng(-0.616996, 30.658469));
                perimeterPoints.add(new LatLng(-0.616856, 30.658383));
                perimeterPoints.add(new LatLng(-0.617473, 30.658114));
//                perimeterPoints.add(new LatLng(-0.617473, 30.658114));


                // Create a polygon options object and add the perimeter points
                PolygonOptions polygonOptions = new PolygonOptions()
                        .addAll(perimeterPoints)
                        .strokeWidth(5) // Set the stroke width of the polygon outline
                        .strokeColor(0xFF0000FF) // Set the stroke color of the polygon outline (blue)
                        .fillColor(0x220000FF); // Set the fill color of the polygon (semi-transparent blue)

                // Add the polygon overlay to the map
                Polygon polygon = googleMap.addPolygon(polygonOptions);

                // Adjust the camera position to show the polygon
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mbararaReferralHospital, 17)); // Adjust the zoom level to display the full view

                // Enable gestures and zoom controls
                UiSettings uiSettings = googleMap.getUiSettings();
                uiSettings.setAllGesturesEnabled(true);
                uiSettings.setZoomControlsEnabled(true);
            }
        });

        return view;
    }
}
