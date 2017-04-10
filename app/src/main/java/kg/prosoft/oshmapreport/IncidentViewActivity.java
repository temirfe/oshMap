package kg.prosoft.oshmapreport;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class IncidentViewActivity extends Activity {

    SessionManager session;
    public TextView tv_title;
    public TextView tv_location_name;
    public TextView tv_date;
    public TextView tv_status;
    public EditText et_comment_input;
    public EditText et_comment_name;
    public EditText et_comment_email;
    public LinearLayout ll_categories;
    public LinearLayout ll_incident_view;
    public LinearLayout ll_thumb_holder;
    public LinearLayout ll_comments;
    public TextView tv_descripton;
    public TextView tv_rating;
    public TextView tv_not_active;
    public Button btn_submit_comment;
    public ImageButton ibtn_rate_up;
    public ImageButton ibtn_rate_down;
    public int user_id;
    public int incident_id;
    public RelativeLayout rl_map;
    String from;
    ProgressBar pb;
    Activity activity;
    ArrayList<String> images = new ArrayList<String>();
    boolean saveCredentials;
    int rating;
    int new_rating;
    public double lat;
    public double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_view);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setTitle(getResources().getString(R.string.back));
        }
        activity=this;
        session = new SessionManager(activity);
        saveCredentials=false;

        Intent intent=getIntent();
        incident_id=intent.getIntExtra("id",0);
        from=intent.getStringExtra("from");

        pb = (ProgressBar)findViewById(R.id.progressBarView);
        tv_title=(TextView)findViewById(R.id.id_tv_title);
        tv_not_active=(TextView)findViewById(R.id.id_tv_not_active);

        tv_location_name=(TextView)findViewById(R.id.id_tv_location_name);
        tv_date=(TextView)findViewById(R.id.id_tv_date);
        tv_status=(TextView)findViewById(R.id.id_tv_status);
        ll_categories=(LinearLayout) findViewById(R.id.id_ll_categories);
        ll_incident_view=(LinearLayout) findViewById(R.id.id_ll_incident_view);
        ll_thumb_holder=(LinearLayout) findViewById(R.id.id_ll_thumb_holder);
        ll_comments=(LinearLayout) findViewById(R.id.id_ll_comments);
        tv_descripton=(TextView)findViewById(R.id.id_tv_description);
        tv_rating=(TextView)findViewById(R.id.id_tv_rating);
        et_comment_input=(EditText)findViewById(R.id.id_et_comment_input);
        et_comment_name=(EditText)findViewById(R.id.id_et_comment_name);
        et_comment_email=(EditText)findViewById(R.id.id_et_comment_email);
        btn_submit_comment=(Button)findViewById(R.id.id_btn_submit_comment);
        btn_submit_comment.setOnClickListener(sendComment);
        ibtn_rate_up=(ImageButton)findViewById(R.id.id_ibtn_rate_up);
        ibtn_rate_up.setOnClickListener(rateClick);
        ibtn_rate_down=(ImageButton)findViewById(R.id.id_ibtn_rate_down);
        ibtn_rate_down.setOnClickListener(rateClick);

        if(session.isLoggedIn()){
            String name=session.getName();
            String email=session.getEmail();
            user_id=session.getUserId();
            if(name.length()>0){
                et_comment_name.setText(name);
                et_comment_name.setVisibility(View.GONE);
            }
            if(email.length()>0){
                et_comment_email.setText(email);
                et_comment_email.setVisibility(View.GONE);
            }
        }
        else if(session.isItrue()){
            String name=session.getIname();
            String email=session.getIemail();
            et_comment_name.setText(name);
            et_comment_email.setText(email);
        }
        else {saveCredentials=true;}

        getIncident(incident_id);
        getComments(incident_id);
        getRatings(incident_id);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    protected void showMapFrame(){
        FrameMapFragment fmfragment=new FrameMapFragment();
        Bundle bundle = new Bundle();
        bundle.putDouble("lat", lat);
        bundle.putDouble("lng", lng);
        Log.i("LATLNG", "lat"+lat+" lng"+lng);
        fmfragment.setArguments(bundle);
        putFragment(fmfragment);

        rl_map=(RelativeLayout)findViewById(R.id.id_rl_map);
        Button button = new Button(this);
        button.getBackground().setAlpha(0);
        button.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        rl_map.addView(button);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MapsActivity.class);
                intent.putExtra("lat",lat);
                intent.putExtra("lng",lng);
                startActivity(intent);
            }
        });
    }

    public void getRatings(final int id){
        String uri="http://map.oshcity.kg/basic/ratings?incident_id="+id;
        StringRequest volReq = new StringRequest(Request.Method.GET, uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("[]")){
                            tv_rating.setText("0");
                        }
                        else{
                            tv_rating.setText(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("IncViewActivity","getRatings sucked");
            }
        });

        MyVolley.getInstance(this).addToRequestQueue(volReq);
    }

    public void getComments(final int id){
        final String uri = "http://map.oshcity.kg/basic/comments?incident_id="+id;

        Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    for(int i=0; i < response.length(); i++){
                        JSONObject jsonObject = response.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String name=jsonObject.getString("comment_author");
                        String comment=jsonObject.getString("comment_description");
                        String cdate=jsonObject.getString("comment_date");
                        showCommentItem(name, comment, cdate);
                    }

                }catch(JSONException e){e.printStackTrace();}
            }
        };

        JsonArrayRequest volReq = new JsonArrayRequest(Request.Method.GET, uri, null, listener,null);


        MyVolley.getInstance(this).addToRequestQueue(volReq);
    }

    public String getCatTitle(JSONObject ctgObj, int ctg_id){
        String title="";
        if(LocaleHelper.getLanguage(activity).equals("ky")){
            CategoriesCache cachedCtgs = new CategoriesCache().getObject(activity.getApplicationContext());
            if(cachedCtgs!= null)
            {
                for(Categories ctg : cachedCtgs.getCategories()){
                    if(ctg.getId()==ctg_id){
                        title=ctg.getTitleKy();
                    }
                }
            }
            else{
                //Log.i("GETCATTITLEEEEE","cached is nullllllllll");
                }
        }
        else{
            try {
                title = ctgObj.getString("category_title");
            }catch(JSONException e){e.printStackTrace();}
        }
        return title;
    }

    public void getIncident(final int id){
        String uri = String.format("http://map.oshcity.kg/basic/incidents/%1$s",id);

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try{
                    if(jsonObject.has("categories")){
                        JSONArray categories = jsonObject.getJSONArray("categories");
                        for(int i=0; i < categories.length(); i++){
                            JSONObject ctgObj = categories.getJSONObject(i);
                            //String ctg_title=ctgObj.getString("category_title");
                            //String ctg_image=ctgObj.getString("category_image");
                            int ctg_id=ctgObj.getInt("id");
                            String ctg_title=getCatTitle(ctgObj, ctg_id);
                            TextView tv = new TextView(activity);
                            tv.setBackgroundResource(R.drawable.blue_view_click);
                            tv.setPadding(10, 1, 10, 2);
                            tv.setTag(ctg_id);
                            tv.setText(ctg_title);
                            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            llp.setMargins(0, 0, 10, 0); // llp.setMargins(left, top, right, bottom);
                            tv.setLayoutParams(llp);
                            tv.setOnClickListener(ctgClick);
                            ll_categories.addView(tv);

                        }
                    }

                    if(jsonObject.has("media")){
                        JSONArray media = jsonObject.getJSONArray("media");
                        String imgUrl="http://map.oshcity.kg/media/uploads";
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(100, 100);
                        lp.setMargins(0, 0, 10, 10);
                        for(int i=0; i < media.length(); i++){
                            JSONObject medObj = media.getJSONObject(i);
                            int mediaId=medObj.getInt("id");
                            int mediaType=medObj.getInt("media_type");
                            if(mediaType==1){
                                String thumb=medObj.getString("media_thumb");
                                String large=medObj.getString("media_link");
                                ImageView imageViewPreview = new ImageView(activity);
                                imageViewPreview.setLayoutParams(lp);
                                Glide.with(activity).load(imgUrl+"/"+thumb)
                                        .centerCrop()
                                        .crossFade()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(imageViewPreview);
                                imageViewPreview.setOnClickListener(thumbClick);
                                imageViewPreview.setTag(i);
                                ll_thumb_holder.addView(imageViewPreview);
                                images.add(imgUrl+"/"+large);
                            }

                        }
                    }

                    JSONObject location = jsonObject.getJSONObject("location");
                    lat=location.getDouble("latitude");
                    lng=location.getDouble("longitude");
                    //Log.i("LOCATION","lat"+lat+" lng"+lng);
                    showMapFrame();
                    //int user_id = jsonObject.optInt("user_id",0);
                    //nt zoom = jsonObject.getInt("incident_zoom");
                    String text=jsonObject.getString("incident_description");
                    //String date=jsonObject.getString("incident_date");
                    int verified=jsonObject.getInt("incident_verified");
                    int active=jsonObject.getInt("incident_active");
                    if(active==0){
                        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        llp.setMargins(0, 0, 10, 0); // llp.setMargins(left, top, right, bottom);
                        tv_not_active.setLayoutParams(llp);
                        tv_not_active.setVisibility(View.VISIBLE);
                    }
                    String status; int statusColor;
                    if(verified==1){status=getString(R.string.incident_fixed); statusColor= ContextCompat.getColor(activity, R.color.green);}
                    else{status=getString(R.string.incident_not_fixed); statusColor=Color.RED;}
                    tv_title.setText(jsonObject.getString("incident_title"));
                    tv_location_name.setText(location.getString("location_name"));
                    tv_date.setText(getDate(jsonObject.getString("incident_date")));
                    tv_status.setText(status);
                    tv_status.setTextColor(statusColor);
                    tv_descripton.setText(text);
                    pb.setVisibility(ProgressBar.GONE);
                    ll_incident_view.setVisibility(View.VISIBLE);

                }catch(JSONException e){e.printStackTrace();}
            }
        };

        JsonObjectRequest volReq = new JsonObjectRequest(Request.Method.GET, uri, null, listener,null);


        MyVolley.getInstance(this).addToRequestQueue(volReq);
    }

    public void showCommentItem(String name, String comment, String cdate){
        TextView nameTv=new TextView(activity);
        nameTv.setText(name);
        nameTv.setTypeface(null, Typeface.BOLD);

        TextView commentTv=new TextView(activity);
        commentTv.setText(comment);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llp.setMargins(0, 2, 0, 2); // llp.setMargins(left, top, right, bottom);
        commentTv.setLayoutParams(llp);

        TextView dateTv=new TextView(activity);
        dateTv.setText(getDate(cdate));
        dateTv.setTextSize(12);
        dateTv.setTextColor(Color.GRAY);
        LinearLayout.LayoutParams cllp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cllp.setMargins(0, 0, 0, 10); // llp.setMargins(left, top, right, bottom);
        dateTv.setLayoutParams(cllp);

        ll_comments.addView(nameTv);
        ll_comments.addView(commentTv);
        ll_comments.addView(dateTv);
        ll_comments.setPadding(0,15,0,10);
    }

    protected void putFragment(Fragment frag){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.id_fl_map, frag);
        //ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    View.OnClickListener sendComment = new View.OnClickListener() {
        public void onClick(View v)
        {
            String comment = et_comment_input.getText().toString();
            if(comment.length()>0){
                String name = et_comment_name.getText().toString();
                if(name.length()>0){
                    String email = et_comment_email.getText().toString();
                    if(email.length()>0){
                        Log.i("Comment",comment+" "+name+" "+email);
                        if(saveCredentials){
                            session.createIncidentSession(name,email);
                        }
                        submitComment(name,email,comment,incident_id);

                    }
                    else {
                        et_comment_email.setError(getResources().getString(R.string.required));
                    }
                }
                else {
                    et_comment_name.setError(getResources().getString(R.string.required));
                }
            }
            else {
                et_comment_input.setError(getResources().getString(R.string.required));
            }
        }
    };

    public void submitComment(final String name, final String email, final String comment, final int inc_id){
        String url="http://map.oshcity.kg/basic/comments";
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    try{
                        int id = obj.getInt("id");
                        if(id!=0){
                            et_comment_input.setText("");
                            showCommentItem(name, comment, "");
                        }

                    }catch(JSONException e){e.printStackTrace();}

                } catch (Throwable t) {
                    Log.e("submitComment", "Could not parse malformed JSON: \"" + response + "\"");
                }
            }
        };

        Response.ErrorListener errorResp =new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // As of f605da3 the following should work
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        Object json = new JSONTokener(res).nextValue();
                        if (json instanceof JSONObject){
                            JSONObject err = new JSONObject(res);
                            Log.i("RESPONSE err 1", err.toString());
                        }
                        else if (json instanceof JSONArray){
                            JSONArray err = new JSONArray(res);
                            Log.i("RESPONSE err 1", err.toString());
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setMessage(R.string.app_error).setNegativeButton(R.string.close,null).create().show();
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                        Log.i("RESPONSE err 2", "here");
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                        Log.i("RESPONSE err 3", "here");
                    }
                }
            }
        };

        StringRequest req = new StringRequest(Request.Method.POST, url, listener, errorResp){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("comment_author",name);
                params.put("comment_email",email);
                params.put("comment_description",comment);
                if(user_id!=0){params.put("user_id",Integer.toString(user_id));}
                params.put("incident_id",Integer.toString(inc_id));

                return params;
            }
        };
        req.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyVolley.getInstance(activity).addToRequestQueue(req);
    }

    View.OnClickListener ctgClick = new View.OnClickListener(){
        public void onClick(View v){
            int tag =(Integer) v.getTag();
            Intent intent= new Intent();
            intent.putExtra("ctg", tag);
            setResult(RESULT_OK, intent);
            finish();
        }
    };
    View.OnClickListener rateClick = new View.OnClickListener(){
        public void onClick(View v)
        {
            rating=Integer.parseInt(tv_rating.getText().toString());
            final int rate;
            if(v.getId()==R.id.id_ibtn_rate_up){
                new_rating=rating+1;
                rate=1;
            }
            else{
                new_rating=rating-1;
                rate=-1;
            }
            tv_rating.setText(Integer.toString(new_rating));

            String url="http://map.oshcity.kg/basic/ratings";
            Response.Listener<String> listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        if(response.equals("\"already\"")){
                            //if user has voted with same direction already then return previous rating
                            tv_rating.setText(Integer.toString(rating));
                        }

                    } catch (Throwable t) {
                        Log.e("submitRating", "Could not parse malformed JSON: \"" + response + "\"");
                    }
                }
            };
            StringRequest req = new StringRequest(Request.Method.POST, url, listener, null){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("rating",Integer.toString(rate));
                    if(user_id!=0){params.put("user_id",Integer.toString(user_id));}
                    params.put("incident_id",Integer.toString(incident_id));

                    return params;
                }
            };
            req.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MyVolley.getInstance(activity).addToRequestQueue(req);

        }
    };

    View.OnClickListener thumbClick = new View.OnClickListener(){
        public void onClick(View v){
            int tag =(Integer) v.getTag();
            Intent intent = new Intent(activity, GalleryActivity.class);
            intent.putStringArrayListExtra(GalleryActivity.EXTRA_NAME, images);
            intent.putExtra("tag",tag);
            startActivity(intent);
        }
    };

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
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(from.equals("form")){
            Intent goToList = new Intent(this,MainActivity.class);
            startActivity(goToList);
        }
        else{
            super.onBackPressed();
        }
    }

    public String getDate(String date) {
        Locale locale = new Locale("ru");
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",locale);
        try{
            Date dateObj = formatter.parse(date);
            SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy HH:mm",locale);
            return fmt.format(dateObj);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
