package kg.prosoft.oshmapreport;


import android.app.DatePickerDialog;
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
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class AddReportFragment extends Fragment implements View.OnClickListener {

    public Button btn_add_category;
    public TextView tv_addcategory;
    public TextView tv_title;
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
            tv_title.setText(savedInstanceState.getString("title"));
        }
        context=getActivity().getApplicationContext();
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_add_report, container, false);

        tv_addcategory=(TextView)rootView.findViewById(R.id.id_tv_addcategory);
        //tv_addcategory.setOnClickListener(this);
        tv_title=(TextView)rootView.findViewById(R.id.id_tv_title);
        tv_date=(TextView)rootView.findViewById(R.id.id_tv_date);
        tv_date.setOnClickListener(this);
        tv_time=(TextView)rootView.findViewById(R.id.id_tv_time);
        tv_time.setOnClickListener(this);

        btn_add_category=(Button)rootView.findViewById(R.id.id_btn_addcategory);
        btn_add_category.setOnClickListener(this);
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
                break;
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        String savedTitle="asdfadf";
        savedInstanceState.putString("title", savedTitle);
    }


}
