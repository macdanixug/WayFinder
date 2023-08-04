package com.example.wayfinder;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;

import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class FirstActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, AdapterView.OnItemSelectedListener {

    private com.google.android.gms.location.LocationListener googleLocationListener;
    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    Location LastLocation;
    LocationRequest locationRequest;
    private LatLng CustomerPickUpLocation;
    private int radius = 1;
    Marker destinationMarker, previousMarker;
    GeoQuery geoQuery;
    String [] destination = {"Destination","Pediatrics", "Natasha Clinic", "OPD"};
    private Polyline polyline,previousPolyline;
    private Circle userLocationCircle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Spinner spin = (Spinner) findViewById(R.id.destinationInput);
        spin.setOnItemSelectedListener(this);

        ArrayAdapter<String> destinationAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, destination);
        destinationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(destinationAdapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);

        LatLngBounds bounds = new LatLngBounds(
                new LatLng(-0.6169113, 30.657587),
                new LatLng(-0.6169113, 30.657587)
        );

        LatLng centerLatLng = bounds.getCenter();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(centerLatLng));

        mMap.setMinZoomPreference(13);
        mMap.setMaxZoomPreference(20);
        mMap.setLatLngBoundsForCameraTarget(bounds);

        buildGoogleApiClient();

        // Create the user's current location circle
        userLocationCircle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(0, 0))
                .radius(10)
                .strokeWidth(3)
                .strokeColor(Color.BLUE)
                .fillColor(Color.argb(70, 0, 0, 255)));


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTitle() != null && marker.getTitle().contains("Natasha Clinic")) {
                    // Inflate the custom popup layout
                    View popupView = getLayoutInflater().inflate(R.layout.custom_popup_layout, null);

                    int popupWidth = 600; // Specify the desired width in pixels
                    PopupWindow popupWindow = new PopupWindow(
                            popupView,
                            popupWidth,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            true
                    );
                    TextView doctorNameTextView = popupView.findViewById(R.id.doctorName);
                    doctorNameTextView.setText("Doctor Name");
                    TextView doctorTelTextView = popupView.findViewById(R.id.doctorTel);
                    doctorTelTextView.setText("Doctor Number");

                    TextView doctor1TextView = popupView.findViewById(R.id.doctor1);
                    doctor1TextView.setText("Oluga Daniel");
                    TextView doctor1TelTextView = popupView.findViewById(R.id.doctor1Tel);
                    doctor1TelTextView.setText("0755332706");

                    TextView doctor2TextView = popupView.findViewById(R.id.doctor2);
                    doctor2TextView.setText("Akamumpa Doreen");
                    TextView doctor2TelTextView = popupView.findViewById(R.id.doctor2Tel);
                    doctor2TelTextView.setText("0778476663");

                    // Show the PopupWindow at the center of the screen
                    popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                }
                return true;
            }



        });


    }


    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            //

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        // Getting the updated location
        LastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

        // Remove the previous user's current location circle from the map
        if (userLocationCircle != null) {
            userLocationCircle.remove();
        }

        // Update the user's current location circle
        userLocationCircle = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(10)
                .strokeWidth(3)
                .strokeColor(Color.BLUE)
                .fillColor(Color.argb(70, 0, 0, 255)));
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }


    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        String selectedDestination = destination[position];
        if (previousMarker != null) {
            previousMarker.remove();
        }

        if (selectedDestination.equals("Natasha Clinic")) {
            double latitude = -0.617086;
            double longitude = 30.657504;
            // Getting the updated location
            LatLng clinicLocation = new LatLng(latitude, longitude);
            String dayOfWeek = getDayOfWeek();
            destinationMarker = mMap.addMarker(new MarkerOptions()
                    .position(clinicLocation)
                    .title("Natasha Clinic "+dayOfWeek)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            previousMarker = destinationMarker;

            if (previousPolyline != null) {
                previousPolyline.remove();
            }

            if (LastLocation != null) {
                LatLng userLocation = new LatLng(LastLocation.getLatitude(), LastLocation.getLongitude());
                PolylineOptions polylineOptions = new PolylineOptions()
                        .add(userLocation, clinicLocation)
                        .width(5)
                        .color(Color.BLUE);
                previousMarker = destinationMarker;

                polyline = mMap.addPolyline(polylineOptions);

                // Update the previousPolyline with the new polyline
                previousPolyline = polyline;
            }

        }

        else if (selectedDestination.equals("OPD")) {
            double latitude = -0.616531;
            double longitude = 30.658699;
            // Getting the updated location
            LatLng clinicLocation = new LatLng(latitude, longitude);
            String dayOfWeek = getDayOfWeek();
            destinationMarker = mMap.addMarker(new MarkerOptions()
                    .position(clinicLocation)
                    .title("OPD "+dayOfWeek)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            previousMarker = destinationMarker;

            if (previousPolyline != null) {
                previousPolyline.remove();
            }

            if (LastLocation != null) {
                LatLng userLocation = new LatLng(LastLocation.getLatitude(), LastLocation.getLongitude());
                PolylineOptions polylineOptions = new PolylineOptions()
                        .add(userLocation, clinicLocation)
                        .width(5)
                        .color(Color.BLUE);

                polyline = mMap.addPolyline(polylineOptions);

                // Update the previousPolyline with the new polyline
                previousPolyline = polyline;
            }
        }

        else if (selectedDestination.equals("Pediatrics")) {
            double latitude = -0.617123;
            double longitude = 30.658332;
            // Getting the updated location
            LatLng clinicLocation = new LatLng(latitude, longitude);
            String dayOfWeek = getDayOfWeek();
            destinationMarker = mMap.addMarker(new MarkerOptions()
                    .position(clinicLocation)
                    .title("Pediatrics Clinic "+dayOfWeek)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            previousMarker = destinationMarker;

            if (previousPolyline != null) {
                previousPolyline.remove();
            }

            if (LastLocation != null) {
                LatLng userLocation = new LatLng(LastLocation.getLatitude(), LastLocation.getLongitude());
                PolylineOptions polylineOptions = new PolylineOptions()
                        .add(userLocation, clinicLocation)
                        .width(5)
                        .color(Color.BLUE);

                polyline = mMap.addPolyline(polylineOptions);

                previousPolyline = polyline;
            }
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    private String getDayOfWeek() {
        String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        int dayIndex = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        return daysOfWeek[dayIndex - 1];
    }
}