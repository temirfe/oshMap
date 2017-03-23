package kg.prosoft.oshmapreport;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapReportsFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener{

    private GoogleMap mMap;
    private Marker myMarker;
    Activity activity;
    Uri.Builder uriB;
    public int user_id;
    public int ctg;
    MapFragment mapFragment;
    // Create a LatLngBounds that includes Osh. (sw,ne)
    private LatLngBounds OSH = new LatLngBounds(new LatLng(40.479966, 72.754476), new LatLng(40.565694, 72.852959));
    //private LatLngBounds BISHKEK = new LatLngBounds(new LatLng(42.790932,74.5002453), new LatLng(42.92415,74.6766403));


    public MapReportsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_map_reports, container, false);
        activity=getActivity();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        if (Build.VERSION.SDK_INT < 21) {
            mapFragment = (MapFragment) activity.getFragmentManager()
                    .findFragmentById(R.id.map_list_markers);
        } else {
            mapFragment = (MapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map_list_markers);
        }
        mapFragment.getMapAsync(this);

        return rootView;
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

        LatLng oshCityHall = new LatLng(40.51719,72.8037146);
        LatLng oshStadium = new LatLng(40.520819,72.8026853);
        //LatLng bishkek = new LatLng(42.8742589,74.6131682);
        //mMap.addMarker(new MarkerOptions().position(oshCityHall).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(oshCityHall,13));
        mMap.setLatLngBoundsForCameraTarget(OSH);

        populateMap(null);

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(activity.getLayoutInflater()));

        // Set a listener for marker click.
        //mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);


    }

    public void populateMap(Uri.Builder urlB){

        uriB=urlB;
        Log.i("CTG is","asd "+ctg);

        if(uriB==null){
            uriB = new Uri.Builder();
            uriB.scheme("http").authority("map.oshcity.kg").appendPath("basic").appendPath("locations");
        }
        if(user_id!=0)//when MainActivity launched by AccountActivity bc of "show my incidents"
        {
            uriB.appendQueryParameter("user_id", ""+user_id);
        }
        Log.i("MAP USER ID", ""+user_id);

        String uri = uriB.build().toString();

        Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    mMap.clear();
                    int leng=response.length();
                    if(leng>0){
                        for(int i=0; i < leng; i++){
                            JSONObject jsonObject = response.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            int incident_id = jsonObject.getInt("incident_id");
                            int incident_verified = jsonObject.optInt("incident_verified");
                            double lat = jsonObject.getDouble("latitude");
                            double lng = jsonObject.getDouble("longitude");
                            String location_name=jsonObject.getString("location_name");
                            String incident_title=jsonObject.getString("incident_title");
                            float color=BitmapDescriptorFactory.HUE_GREEN;
                            if(incident_verified==0){
                                color=BitmapDescriptorFactory.HUE_RED;
                            }

                            myMarker=mMap.addMarker(
                                    new MarkerOptions()
                                            .position(new LatLng(lat,lng))
                                            .title(location_name)
                                            .snippet(incident_title)
                                            .icon(BitmapDescriptorFactory.defaultMarker(color))
                            );
                            myMarker.setTag(incident_id);

                        }
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setMessage(R.string.no_result).setNegativeButton(R.string.close,null).create().show();
                    }


                }catch(JSONException e){e.printStackTrace();}
            }
        };
        Response.ErrorListener errorListener =new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        };

        JsonArrayRequest volReq = new JsonArrayRequest(Request.Method.GET, uri, null, listener,errorListener);
        MyVolley.getInstance(activity).addToRequestQueue(volReq);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Integer markId = (Integer) marker.getTag();
        Intent intent = new Intent(activity, IncidentViewActivity.class);
        intent.putExtra("id",markId);
        intent.putExtra("from","map");
        startActivityForResult(intent,123);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        getParentFragment().onActivityResult(requestCode,resultCode,data);
    }
}
