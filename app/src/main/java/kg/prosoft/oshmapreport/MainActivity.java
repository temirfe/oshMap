package kg.prosoft.oshmapreport;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {

    ReportsFragment homefrag;
    AddReportFragment secfrag;
    MenuFragment menuFrag;
    ArrayList<Categories> mCategoriesList;
    Context context;
    RichBottomNavigationView botNav;
    String from;
    Bundle fromBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            homefrag = new ReportsFragment();
            secfrag = new AddReportFragment();
            menuFrag = new MenuFragment();
            setContentView(R.layout.activity_main);
        }

        Intent intent=getIntent();
        from =intent.getStringExtra("from");
        if(from==null){from="";}
        if(from.equals("login")){
            putFragment(menuFrag);
        }
        else{
            putFragment(homefrag);
        }

        context=this;
        getCategorized();

        //hide icon
        if(getActionBar()!=null){
            getActionBar().setDisplayShowHomeEnabled(false);
        }

        //bottomNav
        botNav = (RichBottomNavigationView) findViewById(R.id.bottom_navigation);

        botNav.setOnNavigationItemSelectedListener(
                new RichBottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        //.getMenu().findItem(R.id.recent_item).setChecked(false);
                        switch (item.getItemId()) {
                            case R.id.home_item:
                                //getActionBar().setDisplayShowHomeEnabled(true);
                                putFragment(homefrag);
                                setTitle(R.string.app_name_short);
                                break;
                            case R.id.second_item:
                                //getActionBar().setDisplayShowHomeEnabled(false);
                                putFragment(secfrag);
                                setTitle(R.string.send_incident);
                                break;
                            case R.id.likes_item:
                                putFragment(menuFrag);
                                setTitle(R.string.menu);
                                break;
                        }
                        return true;
                    }
                });
    }

    protected void putFragment(Fragment frag){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        //if user clicked "My incidents" from menu, then "from" will come with "myIncidnets"
        if(fromBundle==null){
            fromBundle = new Bundle();
            fromBundle.putString("from", from);
            frag.setArguments(fromBundle);
        }

        if(frag.isVisible()){
            return;
        }

        //hide all first
        if (homefrag.isAdded()) { ft.hide(homefrag); }
        if (secfrag.isAdded()) { ft.hide(secfrag); }
        if (menuFrag.isAdded()) { ft.hide(menuFrag); }

        if(frag.isAdded()) {
            ft.show(frag);
        } else {
            ft.add(R.id.fragment_container, frag);
            //ft.addToBackStack(null);//problem with bottomNavigationView
        }
        ft.commit();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter, menu);
        return true;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open_filter:
                Intent filter_intent=new Intent(this, FilterActivity.class);
                startActivityForResult(filter_intent, 123);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    public void getCategorized(){

        CategoriesCache cachedCtgs = new CategoriesCache().getObject(this);

        if(cachedCtgs == null)
        {
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
                        CategoriesCache cc = new CategoriesCache(mCategoriesList);
                        cc.saveObject(cc, context);

                    }catch(JSONException e){e.printStackTrace();}
                }
            };

            JsonArrayRequest volReq = new JsonArrayRequest(Request.Method.GET, uri, null, listener,null);
            MyVolley.getInstance(this).addToRequestQueue(volReq);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RequestQueue queue = MyVolley.getInstance(context).getRequestQueue();
        queue.cancelAll(this);

        try {
            trimCache(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }

    /*@Override
    public void onBackPressed() {
        FragmentManager manager = getFragmentManager();
        if(manager.getBackStackEntryCount() > 0) {
            super.onBackPressed();
            Fragment currentFragment = manager.findFragmentById(R.id.fragment_container);
            if(currentFragment instanceof ReportsFragment){
                botNav.getMenu().getItem(0).setChecked(true);
            }
            else if(currentFragment instanceof AddReportFragment){
                botNav.getMenu().getItem(1).setChecked(true);
            }
            else if(currentFragment instanceof MenuFragment){
                botNav.getMenu().getItem(2).setChecked(true);
            }
        }

    }*/
}
