package kg.prosoft.oshmapreport;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.R.attr.bitmap;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddReportFragment extends Fragment implements View.OnClickListener {


    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;

    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Camera";

    private Uri fileUri; // file url to store image/video

    public TextView btn_add_category;
    public Button btn_submit_report;
    public TextView tv_addcategory;

    public EditText et_title;
    public EditText et_description;
    public EditText et_name;
    public EditText et_email;
    public EditText et_phone;
    public EditText et_address;
    public EditText et_news_link;
    public EditText et_video_link;
    public LinearLayout ll_add_photo;
    public LinearLayout ll_images;
    public double lat;
    public double lng;

    public ArrayList<Integer> selectedCtgs;
    public ArrayList<String> selectedImages;
    public HashMap<Integer, Categories> ctgMap;
    private TextView tv_date;
    private TextView tv_time;
    private int year, month, day, hour, minute;
    private DatePickerDialog dateDialog;
    private TimePickerDialog timeDialog;
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;
    public Calendar calendar;
    SessionManager session;

    Context context;
    Context activity;
    private Bitmap bitmap;
    private int user_id;

    public AddReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            //et_title.setText(savedInstanceState.getString("title"));
        }
        activity=getActivity();
        context=activity.getApplicationContext();
        session = new SessionManager(context);
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_add_report, container, false);

        tv_addcategory=(TextView)rootView.findViewById(R.id.id_tv_addcategory);
        //tv_addcategory.setOnClickListener(this);

        et_title=(EditText)rootView.findViewById(R.id.id_et_title);
        et_description=(EditText)rootView.findViewById(R.id.id_et_description);
        et_name=(EditText)rootView.findViewById(R.id.id_et_name);
        et_email=(EditText)rootView.findViewById(R.id.id_et_email);
        et_phone=(EditText)rootView.findViewById(R.id.id_et_phone);
        et_address=(EditText)rootView.findViewById(R.id.id_et_address);
        et_news_link=(EditText)rootView.findViewById(R.id.id_et_news_link);
        et_video_link=(EditText)rootView.findViewById(R.id.id_et_video_link);

        tv_date=(TextView)rootView.findViewById(R.id.id_tv_date);
        tv_date.setOnClickListener(this);
        tv_time=(TextView)rootView.findViewById(R.id.id_tv_time);
        tv_time.setOnClickListener(this);
        ll_images=(LinearLayout) rootView.findViewById(R.id.id_ll_images);
        ll_add_photo=(LinearLayout) rootView.findViewById(R.id.id_ll_add_photo);
        ll_add_photo.setOnClickListener(this);

        btn_add_category=(TextView)rootView.findViewById(R.id.id_btn_addcategory);
        btn_add_category.setOnClickListener(this);
        btn_submit_report=(Button)rootView.findViewById(R.id.id_btn_submit_report);
        btn_submit_report.setOnClickListener(this);
        selectedCtgs=new ArrayList<>();
        selectedImages=new ArrayList<>();
        ctgMap=new HashMap<Integer, Categories>();
        requestCategories(ctgMap);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        timeFormatter = new SimpleDateFormat("H:mm", Locale.US);

        calendar = Calendar.getInstance();
        tv_date.setText(dateFormatter.format(calendar.getTime()));
        tv_time.setText(timeFormatter.format(calendar.getTime()));
        setDateTimeField();

        if(session.isItrue()){
            String name=session.getIname();
            String email=session.getIemail();
            String phone=session.getIphone();
            et_name.setText(name);
            et_email.setText(email);
            et_phone.setText(phone);
        }
        else if(session.isLoggedIn()){
            String name=session.getName();
            String email=session.getEmail();
            et_name.setText(name);
            et_email.setText(email);
        }
        if(session.isLoggedIn()){
            user_id = session.getUserId();
        }

        return rootView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_btn_addcategory:
                Intent ctg_intent=new Intent(activity,SelectCategoryActivity.class);
                ctg_intent.putExtra("already",selectedCtgs);
                ctg_intent.putExtra("categories",ctgMap);
                startActivityForResult(ctg_intent,1);
                break;
            case R.id.id_tv_date:
                dateDialog.show();
                break;
            case R.id.id_tv_time:
                timeDialog.show();
                break;
            case R.id.id_ll_add_photo:
                showImageUploadSelect();
                break;
            case R.id.id_btn_submit_report:
                submitForm();
                break;
        }
    }

    public void submitForm(){
        View focusView = null;
        boolean allGood=true;
        final String title = et_title.getText().toString();
        final String description = et_description.getText().toString();
        final String name = et_name.getText().toString();
        final String email = et_email.getText().toString();
        final String phone = et_phone.getText().toString();
        final String address = et_address.getText().toString();
        final String news_link = et_news_link.getText().toString();
        final String video_link = et_video_link.getText().toString();
        final String date = tv_date.getText().toString();
        final String time = tv_time.getText().toString();
        final String latitude="40.49234";
        final String longitude="72.8356";

        if(title.trim().equals("")){
            et_title.setError(getResources().getString(R.string.required));
            focusView = et_title;
            allGood=false;
        }
        if(description.trim().equals("")){
            et_description.setError(getResources().getString(R.string.required));
            focusView = et_description;
            allGood=false;
        }
        if(address.trim().equals("")){
            et_address.setError(getResources().getString(R.string.required));
            focusView = et_address;
            allGood=false;
        }
        if(name.trim().equals("")){
            et_name.setError(getResources().getString(R.string.required));
            focusView = et_name;
            allGood=false;
        }
        if(email.trim().equals("")){
            et_email.setError(getResources().getString(R.string.required));
            focusView = et_email;
            allGood=false;
        }
        if(phone.trim().equals("")){
            et_phone.setError(getResources().getString(R.string.required));
            focusView = et_phone;
            allGood=false;
        }
        if(selectedCtgs.size()==0){
            Toast.makeText(context, getResources().getString(R.string.ctg_required), Toast.LENGTH_SHORT).show();
            allGood=false;
        }

        if(allGood){

            //store personal details in session
            session.createIncidentSession(name,email,phone);

            final ProgressDialog progress = new ProgressDialog(activity);
            progress.setTitle(getResources().getString(R.string.sending));
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();
            String url="http://api.temirbek.com/incidents";
            Response.Listener<String> listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progress.dismiss();
                    try {

                        JSONObject obj = new JSONObject(response);

                        Log.d("My App", obj.toString());

                        try{
                            int id = obj.getInt("id");
                            if(id!=0){
                                Intent intent = new Intent(context, IncidentViewActivity.class);
                                intent.putExtra("id",id);
                                intent.putExtra("from","form");
                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                        }catch(JSONException e){e.printStackTrace();}

                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
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
                            progress.dismiss();
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
                    params.put("incident_title",title);
                    params.put("incident_description",description);
                    params.put("incident_date",date+" "+time);
                    params.put("person_name",name);
                    params.put("person_email",email);
                    params.put("person_phone",phone);
                    params.put("latitude",latitude);
                    params.put("longitude",longitude);
                    params.put("location_name",address);
                    params.put("news_link[1]",news_link);
                    params.put("video_link[1]",video_link);
                    if(user_id!=0){params.put("user_id",Integer.toString(user_id));}
                    int i=1;
                    for (int ctg : selectedCtgs)
                    {
                        params.put("category["+i+"]",Integer.toString(ctg));
                        i++;
                    }
                    int im=1;
                    for (String img : selectedImages)
                    {
                        params.put("images["+im+"]",img);
                        im++;
                    }
                    params.put("incident_mode","5"); //5 is android

                    return params;
                }
            };
            req.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MyVolley.getInstance(activity).addToRequestQueue(req);
        }
        else{
            focusView.requestFocus();}
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            String path=fileUri.getPath();
            previewImage(path);
        }
        if (data == null) {return;}
        if(requestCode==1 && resultCode==RESULT_OK){ //selected categories
            selectedCtgs = data.getIntegerArrayListExtra("ctg1");
            int selectedCount=selectedCtgs.size();
            if(selectedCount!=0){
                List<String> strings = new LinkedList<>();
                for (int ctg : selectedCtgs)
                {
                    Categories ctgO= ctgMap.get(ctg);
                    strings.add(ctgO.getTitle());
                }
                tv_addcategory.setText(TextUtils.join("\n", strings));
            }
            else
                tv_addcategory.setText(getResources().getString(R.string.selected)+" "+selectedCount);
        }

        else if(requestCode==101 && resultCode==RESULT_OK){ //get image from gallery
            Uri selectedImage = data.getData();

            //get file path
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            previewImage(filePath);

        }
    }

    public void previewImage(String path){
        try {
            bitmap = MyImageHelper.decodeSampledBitmapFromPath(path, 400, 400);
            selectedImages.add(getStringImage(bitmap));

            ImageView iv = new ImageView(activity);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(80, 80);
            iv.setLayoutParams(layoutParams);
            iv.setImageBitmap(bitmap);
            ll_images.setPadding(0,10,0,10);
            ll_images.addView(iv);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestCategories(final HashMap<Integer, Categories> ctgMap){
        CategoriesCache cachedCtgs = new CategoriesCache().getObject(context);
        if(cachedCtgs!= null)
        {
            for(Categories ctg : cachedCtgs.getCategories()){
                int id=ctg.getId();
                ctgMap.put(id,ctg);
            }
        }
        else{
            String uri = String.format("http://api.temirbek.com/categories");

            Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try{
                        for(int i=0; i < response.length(); i++){
                            JSONObject jsonObject = response.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            String title=jsonObject.getString("category_title");
                            String image=jsonObject.getString("category_image");

                            Categories categories = new Categories(id, title,image);
                            ctgMap.put(id,categories);
                        }

                    }catch(JSONException e){e.printStackTrace();}
                }
            };

            JsonArrayRequest volReq = new JsonArrayRequest(Request.Method.GET, uri, null, listener,null);
            MyVolley.getInstance(context).addToRequestQueue(volReq);
        }
    }

    private void setDateTimeField() {
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        dateDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int yearSelected, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(yearSelected, monthOfYear, dayOfMonth);
                tv_date.setText(dateFormatter.format(newDate.getTime()));
            }
        },year, month, day);

        timeDialog = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minuteS) {
                Calendar newTime = Calendar.getInstance();
                newTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                newTime.set(Calendar.MINUTE, minuteS);
                tv_time.setText(timeFormatter.format(newTime.getTime()));
            }
        },hour, minute, true);
    }

    /*@Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        String savedTitle="asdfadf";
        savedInstanceState.putString("title", savedTitle);
    }*/

    public void showImageUploadSelect(){

        final Item[] items = {
                new Item("Фото", android.R.drawable.ic_menu_camera),
                new Item("Галерея", android.R.drawable.ic_menu_gallery),
        };

        ListAdapter adapter = new ArrayAdapter<Item>(
                activity,
                android.R.layout.select_dialog_item,
                android.R.id.text1,
                items){
            public View getView(int position, View convertView, ViewGroup parent) {
                //Use super class to create the View
                View v = super.getView(position, convertView, parent);
                TextView tv = (TextView)v.findViewById(android.R.id.text1);

                //Put the image on the TextView
                tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);

                //Add margin between image and text (support various screen densities)
                int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
                tv.setCompoundDrawablePadding(dp5);

                return v;
            }
        };


        new AlertDialog.Builder(activity)
                .setTitle(R.string.add_image)
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if(item==0){

                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

                            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                            // start the image capture Intent
                            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                        }
                        else if(item==1){
                            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(i, 101);
                        }
                    }
                }).show();
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        }else {
            return null;
        }

        return mediaFile;
    }
}
