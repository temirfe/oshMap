package kg.prosoft.oshmapreport;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class FrameMapFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    Context context;
    MapView mMapView;
    private GoogleMap googleMap;
    private LatLngBounds OSH = new LatLngBounds(new LatLng(40.479966, 72.754476), new LatLng(40.565694, 72.852959));
    //private LatLngBounds OSH = new LatLngBounds(new LatLng(42.790932,74.5002453), new LatLng(42.92415,74.6766403)); //actually it's bishkek

    protected static final String TAG = "FrameMapFragment";
    public Marker myMarker;
    ParentFrag mParentFrag;

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
    public double mylat;
    public double mylng;
    public boolean marker_already=false;

    public FrameMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        lat = getArguments().getDouble("lat");
        lng = getArguments().getDouble("lng");
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_frame_map, container, false);
        context=getActivity().getApplicationContext();

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                LatLng myLocation = new LatLng(40.51719,72.8037146); //oshCityHall
                //LatLng myLocation = new LatLng(42.8742589,74.6131682); //bishkek
                if(lat!=0.0 && lng!=0.0){
                    myLocation=new LatLng(lat, lng);
                    marker_already=true;
                    myMarker=googleMap.addMarker(new MarkerOptions().position(myLocation).draggable(true));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
                }
                else{
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,12));
                }
                googleMap.setLatLngBoundsForCameraTarget(OSH);
                googleMap.getUiSettings().setAllGesturesEnabled(false);
            }
        });

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        onAttachToParentFragment(getParentFragment());
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        if(!marker_already){
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //mMap.setMyLocationEnabled(true);
                if (mLastLocation != null) {
                    mylat=mLastLocation.getLatitude();
                    mylng=mLastLocation.getLongitude();
                    if (mParentFrag != null)
                    {
                        mParentFrag.setParent();
                    }

                    Log.i("Temir My location", "My current loc:"+mylat+","+mylng);

                    LatLng myLocation=new LatLng(mylat, mylng);
                    if(OSH.contains(myLocation)){
                        myMarker=googleMap.addMarker(new MarkerOptions().position(myLocation).draggable(true));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16));
                    }
                } else {
                    Log.i("Error location", "No location detected");
                }
            } else {
                // Show rationale and request permission.
                Log.i("permission error", "permission not granted");
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

    public interface ParentFrag
    {
        public void setParent();
    }
    public void onAttachToParentFragment(Fragment fragment)
    {
        try
        {
            mParentFrag = (ParentFrag)fragment;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(
                    fragment.toString() + " must implement ParentFrag");
        }
    }
}
