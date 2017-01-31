package kg.prosoft.oshmapreport;


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
import android.widget.ListView;
import android.widget.ProgressBar;
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
    private int page=1;
    private int current_page=1;
    private int total_pages=0;
    Uri.Builder uriB;
    ProgressBar pb;
    ProgressDialog progress;
    public ListReportsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getActivity().getApplicationContext();
        // Inflate the layout for this fragment
        View layout= inflater.inflate(R.layout.fragment_list_reports, container, false);

        listView = (ListView) layout.findViewById(R.id.id_lv_incidents);
        listView.setOnScrollListener(onScrollDo);

        pb = (ProgressBar) layout.findViewById(R.id.progressBar1);

        mCommentList = new ArrayList<ReportList>();
        adapter = new ReportListAdapter(context,mCommentList);
        listView.setAdapter(adapter);

        populateList(page, null,false);

        listView.setOnItemClickListener(itemClickListener);

        return layout;
    }

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
            startActivity(intent);
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
            if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                    && this.currentScrollState == SCROLL_STATE_IDLE) {
                /** To do code here **/
                Log.i("ENDDD", "reached current:"+current_page+" total:"+total_pages);
                if(current_page<total_pages){
                    int next_page=current_page+1;
                    populateList(next_page, uriB, false);
                }
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
        Uri.Builder otherBuilder = Uri.parse(uriB.build().toString()).buildUpon();

        otherBuilder.appendQueryParameter("page", Integer.toString(page));

        String uri = otherBuilder.build().toString();

        Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    if(applyNewFilter){mCommentList.clear();
                        progress.dismiss();}
                    for(int i=0; i < response.length(); i++){
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

                }catch(JSONException e){e.printStackTrace();}

                if(applyNewFilter){
                    adapter = new ReportListAdapter(context,mCommentList);
                    listView.setAdapter(adapter);
                }
                else{
                    adapter.notifyDataSetChanged();
                }
                pb.setVisibility(ProgressBar.INVISIBLE);
            }
        };
        Response.ErrorListener errorListener =new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Произошла ошибка, перезагрузите приложение", Toast.LENGTH_LONG).show();
                pb.setVisibility(ProgressBar.INVISIBLE);
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
    public void onDestroy() {
        super.onDestroy();
        RequestQueue queue = MyVolley.getInstance(context).getRequestQueue();
        queue.cancelAll(this);
    }

}
