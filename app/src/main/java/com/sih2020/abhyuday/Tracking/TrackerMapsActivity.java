package com.sih2020.abhyuday.Tracking;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.NumberPicker;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.sih2020.abhyuday.R;

import java.util.ArrayList;
import java.util.List;

public class TrackerMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Bundle bundle;
    private LatLng source, destination;
    private String transactionID;
    private String userType;
    private Marker sourceM, destinationM;
    private Polyline polyline;
    private PolylineOptions polylineOptions;
    private LocationManager locationManager;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Firebase Instantiation
        database = FirebaseDatabase.getInstance();



        source = new LatLng(0,0 );
        destination = new LatLng(0,0 );


        bundle = getIntent().getExtras();
        source = new LatLng(Double.parseDouble(bundle.getString("source").split(",")[0]), Double.parseDouble(bundle.getString("source").split(",")[1]));
        destination = new LatLng(Double.parseDouble(bundle.getString("destination").split(",")[0]), Double.parseDouble(bundle.getString("destination").split(",")[1]));
        transactionID = bundle.getString("transactionID");
        userType = bundle.getString("userType");
        reference = database.getReference("Trips").child(transactionID);


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.966784, 77.673830),(float)14.0));

        if(userType.equalsIgnoreCase("ambulance"))

        {
            if(destinationM != null)
                destinationM.remove();

            destinationM = mMap.addMarker(new MarkerOptions().position(destination).title("Patient").icon(BitmapDescriptorFactory.fromResource(R.drawable.patient)));
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000,5,locationListener);

        }
        else
        {
            if(source != null)
            {
                sourceM.remove();
            }
            sourceM = mMap.addMarker(new MarkerOptions().title("Me").position(source).icon(BitmapDescriptorFactory.fromResource(R.drawable.patient)));

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    destination = dataSnapshot.child("destination").getValue(LatLng.class);
                    if(destinationM != null)
                        destinationM.remove();
                    destinationM = mMap.addMarker(new MarkerOptions().position(destination).title("Ambulance").icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance)));
                    getDirections(source,destination);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            source = new LatLng(location.getLatitude(),location.getLongitude());
            pushLocationToFirebase(source,transactionID);
            updateMarkers(source);
            getDirections(source,destination);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {


        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
        // handler=null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
        //  handler=null;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    private void getDirections(LatLng source, LatLng destination)
    {
        if(polyline != null)
            polyline.remove();


        List<LatLng> paths = new ArrayList<>();

        GeoApiContext context  = new GeoApiContext.Builder().apiKey(getResources().getString(R.string.google_maps_key)).build();
        DirectionsApiRequest req = DirectionsApi.getDirections(context,source.latitude+","+source.longitude,destination.latitude+","+destination.longitude);
        try {
            DirectionsResult res = req.await();

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];

                if (route.legs !=null) {
                    for(int i=0; i<route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j=0; j<leg.steps.length;j++){
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length >0) {
                                    for (int k=0; k<step.steps.length;k++){
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                paths.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            paths.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch(Exception ex) {
            Log.e("TAG", ex.getLocalizedMessage());
        }

        //Draw the retrieved polylines
        if(paths.size() > 0)
        {
            polylineOptions = new PolylineOptions().addAll(paths).color(Color.GREEN).width(5);
            polyline = mMap.addPolyline(polylineOptions);
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);

        //Centre Animate the Map
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(source);
        builder.include(destination);
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,200);
        mMap.animateCamera(cu);


        return;



    }

    private void pushLocationToFirebase(LatLng location, String transactionID)
    {
        //Firebase code
        reference.child("destination").setValue(location);
    }

    //TODO: Only For Updating Ambulance's Marker when Ambulance is Using this
    private void updateMarkers(LatLng location)
    {
            if(sourceM != null)
                sourceM.remove();
            sourceM = mMap.addMarker(new MarkerOptions().position(location).title("Me").icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance)));

    }




}