package kg.prosoft.oshmapreport;

import android.app.ActionBar;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.Manifest;
import android.location.Location;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SetLocationActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapClickListener {

    private GoogleMap mMap;

    // Create a LatLngBounds that includes Osh. (sw,ne)
    private LatLngBounds OSH = new LatLngBounds(new LatLng(40.479966, 72.754476), new LatLng(40.565694, 72.852959));
    //private LatLngBounds OSH = new LatLngBounds(new LatLng(42.790932,74.5002453), new LatLng(42.92415,74.6766403)); //it's actually bishkek

    protected static final String TAG = "SetLocationActivity";
    public Marker myMarker;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    public double lat;
    public double lng;
    public boolean marker_already=false;

    public double new_lat;
    public double new_lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        Intent intent=getIntent();
        lat=intent.getDoubleExtra("lat",0.0);
        lng=intent.getDoubleExtra("lng",0.0);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                done();
                return true;
            case R.id.action_back:
                done();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void done(){
        Intent intent= new Intent();
        intent.putExtra("new_lat", new_lat);
        intent.putExtra("new_lng", new_lng);
        setResult(RESULT_OK, intent);
        finish();
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
        //LatLng bishkek = new LatLng(42.8742589,74.6131682);
        LatLng myLocation=new LatLng(40.51719,72.8037146); //Osh city hall

        if(lat!=0.0 && lng!=0.0){
            myLocation=new LatLng(lat, lng);
            myMarker=mMap.addMarker(new MarkerOptions().position(myLocation).draggable(true));
            marker_already=true;

        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,14));


        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setLatLngBoundsForCameraTarget(OSH);

        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerDragListener(this);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        if(!marker_already){
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //mMap.setMyLocationEnabled(true);
                if (mLastLocation != null) {
                    double mylat=mLastLocation.getLatitude();
                    double mylng=mLastLocation.getLongitude();
                    Log.i("My location", "My current loc:"+mylat+","+mylng);

                    LatLng myLocation=new LatLng(mylat, mylng);
                    if(OSH.contains(myLocation)){
                        myMarker=mMap.addMarker(new MarkerOptions().position(myLocation).draggable(true));
                    }
                } else {
                    Toast.makeText(this, "No location detected", Toast.LENGTH_LONG).show();
                    //Log.i("Error nah", "No location detected");
                }
            } else {
                // Show rationale and request permission.
                Toast.makeText(this, "Please, give permission to locate your location", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onMapClick(LatLng point) {
        //mTapTextView.setText("tapped, point=" + point);
        if(myMarker!=null){myMarker.remove();}
        myMarker=mMap.addMarker(new MarkerOptions().position(point).draggable(true));
        new_lat=point.latitude;
        new_lng=point.longitude;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng newpos=marker.getPosition();
        new_lat=newpos.latitude;
        new_lng=newpos.longitude;
        Log.i("Dragged to:", ""+newpos);
    }
}
