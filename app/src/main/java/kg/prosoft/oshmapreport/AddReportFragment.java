package kg.prosoft.oshmapreport;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddReportFragment extends Fragment implements View.OnClickListener {

    public Button btn_add_category;
    public ArrayList<Integer> selectedCtgs;
    public HashMap<Integer, Categories> ctgMap;

    Context context;


    public AddReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getActivity().getApplicationContext();
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_add_report, container, false);

        btn_add_category=(Button)rootView.findViewById(R.id.id_btn_addcategory);
        btn_add_category.setOnClickListener(this);
        selectedCtgs=new ArrayList<>();
        ctgMap=new HashMap<Integer, Categories>();
        requestCategories(ctgMap);
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
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        selectedCtgs = data.getIntegerArrayListExtra("ctg1");
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

}
