package kg.prosoft.oshmapreport;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class FeedsActivity extends Activity {


    ListView listView;
    FeedListAdapter adapter;
    List<Feed> feedList;
    Context context;
    private int page=1;
    private int current_page=1;
    private int total_pages=0;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
        }
        context=this;

        listView = (ListView)findViewById(R.id.id_lv_feeds);
        listView.setOnScrollListener(onScrollDo);

        pb = (ProgressBar) findViewById(R.id.progressBar3);

        feedList = new ArrayList<Feed>();
        adapter = new FeedListAdapter(this,feedList);
        listView.setAdapter(adapter);

        populateList(page);

        listView.setOnItemClickListener(itemClickListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener(){
        public void onItemClick(AdapterView<?> listView,
                                View itemView,
                                int position,
                                long id) {
            Feed item =feedList.get(position);
            int myid=item.getId();
            String title=item.getTitle();
            String text=item.getText();
            String date=item.getDate();
            Intent intent = new Intent(context, FeedViewActivity.class);
            intent.putExtra("id",myid);
            intent.putExtra("title",title);
            intent.putExtra("text",text);
            intent.putExtra("date",date);
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
                    populateList(next_page);
                }
            }
        }
    };


    public void populateList(int page){

        String uri = "http://api.temirbek.com/feeds?page="+page;

        Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    for(int i=0; i < response.length(); i++){
                        JSONObject jsonObject = response.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String title=jsonObject.getString("item_title");
                        String text=jsonObject.getString("item_description");
                        String date=jsonObject.getString("item_date");

                        Feed feed = new Feed(id, title,text,date);
                        feedList.add(feed);
                    }

                }catch(JSONException e){e.printStackTrace();}

                adapter.notifyDataSetChanged();
                pb.setVisibility(View.GONE);
            }
        };
        Response.ErrorListener errorListener =new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Произошла ошибка, перезагрузите приложение", Toast.LENGTH_LONG).show();
                pb.setVisibility(View.GONE);
            }
        };

        JsonArrayRequest volReq = new JsonArrayRequest(Request.Method.GET, uri, null, listener,errorListener){
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


        MyVolley.getInstance(this).addToRequestQueue(volReq);
    }
}
