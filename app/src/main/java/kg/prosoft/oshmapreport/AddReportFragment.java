package kg.prosoft.oshmapreport;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddReportFragment extends Fragment implements View.OnClickListener {

    public Button btn_add_category;
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
    public double lat;
    public double lng;

    public ArrayList<Integer> selectedCtgs;
    public HashMap<Integer, Categories> ctgMap;
    private TextView tv_date;
    private TextView tv_time;
    private int year, month, day, hour, minute;
    private DatePickerDialog dateDialog;
    private TimePickerDialog timeDialog;
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;
    public Calendar calendar;

    Context context;


    public AddReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            //et_title.setText(savedInstanceState.getString("title"));
        }
        context=getActivity().getApplicationContext();
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

        btn_add_category=(Button)rootView.findViewById(R.id.id_btn_addcategory);
        btn_add_category.setOnClickListener(this);
        btn_submit_report=(Button)rootView.findViewById(R.id.id_btn_submit_report);
        btn_submit_report.setOnClickListener(this);
        selectedCtgs=new ArrayList<>();
        ctgMap=new HashMap<Integer, Categories>();
        requestCategories(ctgMap);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        timeFormatter = new SimpleDateFormat("H:mm", Locale.US);

        calendar = Calendar.getInstance();
        tv_date.setText(dateFormatter.format(calendar.getTime()));
        tv_time.setText(timeFormatter.format(calendar.getTime()));
        setDateTimeField();

        return rootView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_btn_addcategory:
                Intent ctg_intent=new Intent(getActivity(),SelectCategoryActivity.class);
                ctg_intent.putExtra("already",selectedCtgs);
                ctg_intent.putExtra("categories",ctgMap);
                startActivityForResult(ctg_intent,1);
                break;
            case R.id.id_tv_date:
                dateDialog.show();
                break;
            case R.id.id_tv_time:
                timeDialog.show();
            case R.id.id_btn_submit_report:
                submitForm();
                break;
        }
    }

    public void submitForm(){
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
            allGood=false;
        }
        if(description.trim().equals("")){
            et_description.setError(getResources().getString(R.string.required));
            allGood=false;
        }
        if(address.trim().equals("")){
            et_address.setError(getResources().getString(R.string.required));
            allGood=false;
        }
        if(name.trim().equals("")){
            et_name.setError(getResources().getString(R.string.required));
            allGood=false;
        }
        if(email.trim().equals("")){
            et_email.setError(getResources().getString(R.string.required));
            allGood=false;
        }
        if(phone.trim().equals("")){
            et_phone.setError(getResources().getString(R.string.required));
            allGood=false;
        }
        if(selectedCtgs.size()==0){
            Toast.makeText(context, getResources().getString(R.string.ctg_required), Toast.LENGTH_SHORT).show();
            allGood=false;
        }

        if(allGood){
            final ProgressDialog progress = new ProgressDialog(getActivity());
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
                            Log.i("RESPONSE TITLE", " "+id);
                            if(id!=0){
                                Intent intent = new Intent(context, IncidentViewActivity.class);
                                intent.putExtra("id",id);
                                intent.putExtra("from","form");
                                startActivity(intent);
                            }

                        }catch(JSONException e){e.printStackTrace();}

                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                    }
                }
            };

            StringRequest req = new StringRequest(Request.Method.POST, url, listener, null){
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
                    int i=1;
                    for (int ctg : selectedCtgs)
                    {
                        params.put("category["+i+"]",Integer.toString(ctg));
                        i++;
                    }
                    params.put("incident_mode","5"); //5 is android

                    return params;
                }
            };
            MyVolley.getInstance(context).addToRequestQueue(req);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
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
        Log.i("RECEIVED",selectedCtgs.toString());
    }

    public void requestCategories(final HashMap<Integer, Categories> ctgMap){
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
                       // mCommentList.add(comment);
                    }

                }catch(JSONException e){e.printStackTrace();}
            }
        };

        JsonArrayRequest volReq = new JsonArrayRequest(Request.Method.GET, uri, null, listener,null);
        MyVolley.getInstance(context).addToRequestQueue(volReq);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RequestQueue queue = MyVolley.getInstance(context).getRequestQueue();
        queue.cancelAll(this);
    }

    private void setDateTimeField() {
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        dateDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int yearSelected, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(yearSelected, monthOfYear, dayOfMonth);
                tv_date.setText(dateFormatter.format(newDate.getTime()));
            }
        },year, month, day);

        timeDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
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


}
