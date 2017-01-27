package kg.prosoft.oshmapreport;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class IncidentViewActivity extends Activity {

    public TextView tv_title;
    public TextView tv_location_name;
    public TextView tv_date;
    public TextView tv_status;
    public TextView tv_categories;
    public TextView tv_descripton;
    String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_view);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
        }
        tv_title=(TextView)findViewById(R.id.id_tv_title);
        tv_location_name=(TextView)findViewById(R.id.id_tv_location_name);
        tv_date=(TextView)findViewById(R.id.id_tv_date);
        tv_status=(TextView)findViewById(R.id.id_tv_status);
        tv_categories=(TextView)findViewById(R.id.id_tv_categories);
        tv_descripton=(TextView)findViewById(R.id.id_tv_description);
        Intent intent=getIntent();
        int id=intent.getIntExtra("id",0);
        from=intent.getStringExtra("from");
        getIncident(id);

    }

    public void getIncident(int id){
        String uri = String.format("http://api.temirbek.com/incidents/%1$s",id);

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try{
                    int id = jsonObject.getInt("id");
                    JSONArray categories = jsonObject.getJSONArray("categories");
                    String ctgs;
                    List<String> ctg_list = new LinkedList<>();

                    for(int i=0; i < categories.length(); i++){
                        JSONObject ctgObj = categories.getJSONObject(i);
                        //Log.i("aaaaaaaaa",ctgObj.toString());
                        String ctg_title=ctgObj.getString("category_title");
                        String ctg_image=ctgObj.getString("category_image");
                        int ctg_id=ctgObj.getInt("id");
                        ctg_list.add(ctg_title);

                    }
                    ctgs = TextUtils.join(",",ctg_list);

                    JSONObject location = jsonObject.getJSONObject("location");
                    double lat=location.getDouble("latitude");
                    double lng=location.getDouble("longitude");

                    //int user_id = jsonObject.optInt("user_id",0);
                    //nt zoom = jsonObject.getInt("incident_zoom");
                    String text=jsonObject.getString("incident_description");
                    //String date=jsonObject.getString("incident_date");
                    int verified=jsonObject.getInt("incident_verified");
                    String status;
                    if(verified==1){status=getString(R.string.incident_fixed);}
                    else{status=getString(R.string.incident_not_fixed);}
                    tv_title.setText(jsonObject.getString("incident_title"));
                    tv_location_name.setText(location.getString("location_name"));
                    tv_date.setText(jsonObject.getString("incident_date"));
                    tv_status.setText(status);
                    tv_categories.setText(ctgs);
                    tv_descripton.setText(text);

                }catch(JSONException e){e.printStackTrace();}
            }
        };

        JsonObjectRequest volReq = new JsonObjectRequest(Request.Method.GET, uri, null, listener,null);


        MyVolley.getInstance(this).addToRequestQueue(volReq);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RequestQueue queue = MyVolley.getInstance(this).getRequestQueue();
        queue.cancelAll(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(from.equals("form")){
                    Intent goToList = new Intent(this,MainActivity.class);
                    startActivity(goToList);
                }
                else{
                    onBackPressed();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
