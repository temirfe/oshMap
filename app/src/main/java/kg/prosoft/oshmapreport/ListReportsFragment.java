package kg.prosoft.oshmapreport;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListReportsFragment extends Fragment {

    ListView listView;
    ReportListAdapter adapter;
    List<ReportList> mCommentList;
    Context context;
    Activity activity;
    private int page=1;
    private int current_page=1;
    private int total_pages;
    Uri.Builder uriB;
    ProgressBar pb;
    ProgressDialog progress;
    Button btn_reload;
    LinearLayout ll_reload;
    public int ctg;
    public int user_id;
    public ListReportsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity=getActivity();
        context=activity.getApplicationContext();
        // Inflate the layout for this fragment
        View layout= inflater.inflate(R.layout.fragment_list_reports, container, false);

        listView = (ListView) layout.findViewById(R.id.id_lv_incidents);
        listView.setOnScrollListener(onScrollDo);
        ll_reload=(LinearLayout)layout.findViewById(R.id.id_ll_reload);
        btn_reload=(Button)layout.findViewById(R.id.id_btn_reload);
        btn_reload.setOnClickListener(reloadClickListener);

        pb = (ProgressBar) layout.findViewById(R.id.progressBar1);

        mCommentList = new ArrayList<ReportList>();
        adapter = new ReportListAdapter(context,mCommentList);
        listView.setAdapter(adapter);

        populateList(page, null,false);

        listView.setOnItemClickListener(itemClickListener);

        return layout;
    }

    View.OnClickListener reloadClickListener = new View.OnClickListener(){
        public void onClick(View v){
            populateList(page, null,false);
            pb.setVisibility(ProgressBar.VISIBLE);
            ll_reload.setVisibility(View.GONE);
        }
    };

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener(){
        public void onItemClick(AdapterView<?> listView,
                                View itemView,
                                int position,
                                long id) {
            ReportList item =mCommentList.get(position);
            int myid=item.getId();
            Intent intent = new Intent(context, IncidentViewActivity.class);
            intent.putExtra("id",myid);
            intent.putExtra("from","list");
            Log.i("ListRep","start for result");
            startActivityForResult(intent, 123);
        }
    };

    AbsListView.OnScrollListener onScrollDo = new AbsListView.OnScrollListener() {
        private int currentVisibleItemCount;
        private int currentScrollState;
        private int currentFirstVisibleItem;
        private int totalItem;


        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            this.currentScrollState = scrollState;
            this.isScrollCompleted();
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            this.currentFirstVisibleItem = firstVisibleItem;
            this.currentVisibleItemCount = visibleItemCount;
            this.totalItem = totalItemCount;
        }

        private void isScrollCompleted() {

            int threshold=totalItem-(currentFirstVisibleItem+currentVisibleItemCount);

            if(threshold<=2 && this.currentScrollState == SCROLL_STATE_IDLE){
                if(current_page<total_pages){
                    Log.i("Threshold reached", "loading next. current:"+current_page+" total:"+total_pages);
                    int next_page=current_page+1;
                    populateList(next_page, uriB, false);
                }
            }

            if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                    && this.currentScrollState == SCROLL_STATE_IDLE) {
                Log.i("END of Current", "reached current:"+current_page+" total:"+total_pages);
                progress = new ProgressDialog(getActivity());
                progress.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                progress.setMessage(getResources().getString(R.string.loading));
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();
            }
        }
    };

    public void populateList(int page,Uri.Builder urlB, final boolean applyNewFilter){

        if(applyNewFilter){
            progress = new ProgressDialog(getActivity());
            progress.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            progress.setMessage(getResources().getString(R.string.loading));
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();
        }

        uriB=urlB;

        if(uriB==null){
            uriB = new Uri.Builder();
            uriB.scheme("http").authority("api.temirbek.com").appendPath("incidents");
        }
        if(user_id!=0){
            uriB.appendQueryParameter("user_id", ""+user_id);
        }
        Uri.Builder otherBuilder = Uri.parse(uriB.build().toString()).buildUpon();

        otherBuilder.appendQueryParameter("page", Integer.toString(page));

        String uri = otherBuilder.build().toString();

        Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    if(progress!=null){progress.dismiss();}
                    if(applyNewFilter){mCommentList.clear();}
                    int leng=response.length();
                    if(leng>0){
                        for(int i=0; i < leng; i++){
                            JSONObject jsonObject = response.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            int location_id = jsonObject.getInt("location_id");
                            int user_id = jsonObject.optInt("user_id",0);
                            int zoom = jsonObject.getInt("incident_zoom");
                            String title=jsonObject.getString("incident_title");
                            String text=jsonObject.getString("incident_description");
                            String date=jsonObject.getString("incident_date");
                            int verified=jsonObject.getInt("incident_verified");

                            ReportList comment = new ReportList(id, title,text,location_id,user_id,date,verified,zoom);
                            mCommentList.add(comment);
                        }
                    }
                    else{

                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setMessage(R.string.no_result).setNegativeButton(R.string.close,null).create().show();
                    }


                }catch(JSONException e){e.printStackTrace();}

                if(applyNewFilter){
                    adapter = new ReportListAdapter(context,mCommentList);
                    listView.setAdapter(adapter);
                }
                else{
                    adapter.notifyDataSetChanged();
                }
                pb.setVisibility(ProgressBar.INVISIBLE);
                ll_reload.setVisibility(View.GONE);
            }
        };
        Response.ErrorListener errorListener =new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pb.setVisibility(ProgressBar.INVISIBLE);
                ll_reload.setVisibility(View.VISIBLE);
            }
        };

        JsonArrayRequest  volReq = new JsonArrayRequest(Request.Method.GET, uri, null, listener,errorListener){
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                try {
                    current_page=Integer.parseInt(response.headers.get("X-Pagination-Current-Page"));
                    total_pages=Integer.parseInt(response.headers.get("X-Pagination-Page-Count"));
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                    return Response.success(new JSONArray(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }
        };


        MyVolley.getInstance(context).addToRequestQueue(volReq);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        if(requestCode==123){

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("api.temirbek.com")
                    .appendPath("incidents");

            ctg = data.getIntExtra("ctg",0);
            if(ctg!=0){
                builder.appendQueryParameter("category_id[]", Integer.toString(ctg));
            }

            populateList(1,builder,true);
        }
    }

}
