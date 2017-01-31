package kg.prosoft.oshmapreport;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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

public class FilterActivity extends Activity {

    /*RadioButton radio_all;
    RadioButton radio_solved;
    RadioButton radio_not_solved;*/
    RadioGroup radio_group_verify;
    ListView lv_filter_ctgs;
    EditText et_text_search;
    public HashMap<Integer, Categories> ctgMap;
    CategoriesAdapter adapter;
    List<Categories> mCategoriesList;
    ArrayList<Integer> selectedCtgs;
    String received_verify;
    String received_text;
    Intent received_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
        }

        et_text_search=(EditText) findViewById(R.id.id_et_text_search);

        lv_filter_ctgs=(ListView) findViewById(R.id.id_lv_filter_ctgs);

        received_intent=getIntent();
        received_verify=received_intent.getStringExtra("verify");
        received_text=received_intent.getStringExtra("received_text");
        selectedCtgs =received_intent.getIntegerArrayListExtra("selectedCtgs");
        radio_group_verify=(RadioGroup) findViewById(R.id.id_rgroup_verify);

        if(received_verify!=null){
            if(received_verify.equals("0")){
                radio_group_verify.check(R.id.id_radio_not_solved);
            }
            else if(received_verify.equals("1")){
                radio_group_verify.check(R.id.id_radio_solved);
            }
        }

        if(received_text!=null){
            et_text_search.setText(received_text);
        }

        mCategoriesList = new ArrayList<Categories>();

        requestCategories();
    }

    public void requestCategories(){
        CategoriesCache cachedCtgs = new CategoriesCache().getObject(this);
        if(cachedCtgs!= null)
        {
            mCategoriesList=cachedCtgs.getCategories();
        }
        else{
            String uri = String.format("http://api.temirbek.com/categories");

            Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    mCategoriesList=new ArrayList<>();
                    try{
                        for(int i=0; i < response.length(); i++){
                            JSONObject jsonObject = response.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            String title=jsonObject.getString("category_title");
                            String image=jsonObject.getString("category_image");

                            mCategoriesList.add(new Categories(id, title,image));
                        }
                        //adapter.notifyDataSetChanged();

                    }catch(JSONException e){e.printStackTrace();}
                }
            };

            JsonArrayRequest volReq = new JsonArrayRequest(Request.Method.GET, uri, null, listener,null);
            MyVolley.getInstance(this).addToRequestQueue(volReq);
        }

        adapter = new CategoriesAdapter(this,mCategoriesList,selectedCtgs);
        lv_filter_ctgs.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,2,0,R.string.apply).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            case 2:
                done();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void done(){
        Intent intent= new Intent();
        int verify=radio_group_verify.getCheckedRadioButtonId();
        String ver;
        switch(verify){
            case R.id.id_radio_all:
                ver="all";
                break;
            case R.id.id_radio_solved:
                ver="1";
                break;
            case R.id.id_radio_not_solved:
                ver="0";
                break;
            default: ver="all";

        }
        intent.putExtra("verify", ver);
        intent.putExtra("ctg1", adapter.getSelectedCtgs());
        intent.putExtra("query_text", et_text_search.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        RequestQueue queue = MyVolley.getInstance(this).getRequestQueue();
        queue.cancelAll(this);
    }
}
