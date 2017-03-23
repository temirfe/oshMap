package kg.prosoft.oshmapreport;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class FilterActivity extends Activity {

    RadioGroup radio_group_verify;
    RadioGroup radio_group_ctgs;
    RadioGroup radio_group_users;
    EditText et_text_search;
    public HashMap<Integer, Categories> ctgMap;
    ArrayList<Integer> selectedCtgs;
    String received_verify;
    String received_text;
    String received_user;
    Intent received_intent;
    public TextView tv_addcategory;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        activity=this;
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
        }
        //prevent keyboard appearing onStart
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        et_text_search=(EditText) findViewById(R.id.id_et_text_search);

        received_intent=getIntent();
        received_verify=received_intent.getStringExtra("verify");
        received_text=received_intent.getStringExtra("received_text");
        received_user=received_intent.getStringExtra("show_from");
        selectedCtgs =received_intent.getIntegerArrayListExtra("selectedCtgs");
        radio_group_verify=(RadioGroup) findViewById(R.id.id_rgroup_verify);
        radio_group_ctgs=(RadioGroup) findViewById(R.id.id_rgroup_ctgs);
        radio_group_ctgs.setOnCheckedChangeListener(checkListener);
        radio_group_users=(RadioGroup) findViewById(R.id.id_rgroup_users);

        if(received_verify!=null){
            if(received_verify.equals("0")){
                radio_group_verify.check(R.id.id_radio_not_solved);
            }
            else if(received_verify.equals("1")){
                radio_group_verify.check(R.id.id_radio_solved);
            }
        }
        if(received_user!=null && received_user.equals("me")){
            radio_group_users.check(R.id.id_radio_from_me);
        }

        if(received_text!=null){
            et_text_search.setText(received_text);
        }

        ctgMap=new HashMap<Integer, Categories>();
        requestCategories(ctgMap);

        tv_addcategory=(TextView)findViewById(R.id.id_tv_addcategory);
        tv_addcategory.setOnClickListener(ctgClick);
        listSelectedCtgs();
    }

    View.OnClickListener ctgClick = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            openSelectCtg();
        }
    };

    public void openSelectCtg(){
        Intent ctg_intent=new Intent(activity,SelectCategoryActivity.class);
        ctg_intent.putExtra("already",selectedCtgs);
        ctg_intent.putExtra("categories",ctgMap);
        startActivityForResult(ctg_intent,1);
    }

    RadioGroup.OnCheckedChangeListener checkListener = new RadioGroup.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId)
        {
            switch(checkedId)
            {
                case R.id.id_radio_select_ctg_all:
                    selectedCtgs.clear();
                    tv_addcategory.setText("");
                    break;
                case R.id.id_radio_select_ctg_custom:
                    openSelectCtg();
                    break;
            }
        }
    };

    public void listSelectedCtgs(){
        if(selectedCtgs!=null){
            int selectedCount=selectedCtgs.size();
            if(selectedCount!=0){
                List<String> strings = new LinkedList<>();
                for (int ctg : selectedCtgs)
                {
                    Categories ctgO= ctgMap.get(ctg);
                    strings.add(ctgO.getTitle());
                }

                String selected=TextUtils.join("\n", strings);
                selected=getResources().getString(R.string.selected)+"\n"+selected;
                tv_addcategory.setText(selected);
                radio_group_ctgs.setOnCheckedChangeListener (null);
                radio_group_ctgs.check(R.id.id_radio_select_ctg_custom);
                radio_group_ctgs.setOnCheckedChangeListener(checkListener);
            }
        }
        else
            tv_addcategory.setText("");
    }

    public void requestCategories(final HashMap<Integer, Categories> ctgMap){
        CategoriesCache cachedCtgs = new CategoriesCache().getObject(activity);
        if(cachedCtgs!= null)
        {
            for(Categories ctg : cachedCtgs.getCategories()){
                int id=ctg.getId();
                ctgMap.put(id,ctg);
            }
        }
        else{
            String uri = String.format("http://map.oshcity.kg/basic/categories");

            Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try{
                        for(int i=0; i < response.length(); i++){
                            JSONObject jsonObject = response.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            String title=jsonObject.getString("category_title");
                            String title_ky=jsonObject.getString("title_ky");
                            String image=jsonObject.getString("category_image");

                            Categories categories = new Categories(id, title,image, title_ky);
                            ctgMap.put(id,categories);
                        }

                    }catch(JSONException e){e.printStackTrace();}
                }
            };

            JsonArrayRequest volReq = new JsonArrayRequest(Request.Method.GET, uri, null, listener,null);
            MyVolley.getInstance(activity).addToRequestQueue(volReq);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {return;}
        if(requestCode==1 && resultCode==RESULT_OK){ //selected categories
            selectedCtgs = data.getIntegerArrayListExtra("ctg1");
            int selectedCount=0;
            if(selectedCtgs!=null){selectedCount=selectedCtgs.size();}
            if(selectedCount!=0){
                List<String> strings = new LinkedList<>();
                for (int ctg : selectedCtgs)
                {
                    Categories ctgO= ctgMap.get(ctg);
                    strings.add(ctgO.getTitle());
                }
                String selected=TextUtils.join("\n", strings);
                selected=getResources().getString(R.string.selected)+"\n"+selected;
                tv_addcategory.setText(selected);
            }
            else
            {

                radio_group_ctgs.setOnCheckedChangeListener (null);
                radio_group_ctgs.check(R.id.id_radio_select_ctg_all);
                radio_group_ctgs.setOnCheckedChangeListener(checkListener);
                tv_addcategory.setText("");
            }
        }
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
        int show_mine=radio_group_users.getCheckedRadioButtonId();
        String ver, from;
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
        switch(show_mine){
            case R.id.id_radio_from_all:
                from="all";
                break;
            case R.id.id_radio_from_me:
                from="me";
                break;
            default: from="all";

        }
        Log.i("FROM",from);
        intent.putExtra("verify", ver);
        intent.putExtra("show_from", from);
        intent.putExtra("ctg1", selectedCtgs);
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
