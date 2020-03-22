package kg.prosoft.oshmapreport;

import android.app.Activity;
//import android.app.Fragment;
//import android.app.FragmentManager;
//import android.app.FragmentTransaction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    //HomeFragment homefrag;
    ReportsFragment reportfrag;
    AddReportFragment secfrag;
    MenuFragment menuFrag;
    ArrayList<Categories> mCategoriesList;
    Context context;
    RichBottomNavigationView botNav;
    String from;
    String open;
    Bundle fromBundle;
    int tabIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            //homefrag = new HomeFragment();
            reportfrag = new ReportsFragment();
            secfrag = new AddReportFragment();
            menuFrag = new MenuFragment();
        }
        if(reportfrag==null){reportfrag = new ReportsFragment();}
        if(secfrag==null){secfrag = new AddReportFragment();}
        if(menuFrag==null){menuFrag = new MenuFragment();}
        setContentView(R.layout.activity_main);

        //bottomNav
        botNav = (RichBottomNavigationView) findViewById(R.id.bottom_navigation);

        Intent intent=getIntent();
        from =intent.getStringExtra("from");
        open =intent.getStringExtra("open");
        if(from==null){from="";}
        if(open==null){open="";}
        if(from.equals("login")){
            putFragment(menuFrag);
            botNav.findViewById(R.id.likes_item).performClick();
        }
        else if(open.equals("map")){
            tabIndex=0;
            putFragment(reportfrag);
            setTitle(R.string.incidents);
            botNav.findViewById(R.id.reports_item).performClick();
        }
        else if(open.equals("list")){
            tabIndex=1;
            putFragment(reportfrag);
            setTitle(R.string.incidents);
            botNav.findViewById(R.id.reports_item).performClick();
        }
        else if(open.equals("add")){
            putFragment(secfrag);
            setTitle(R.string.send_incident);
            botNav.findViewById(R.id.add_item).performClick();
        }

        context=this;
        //Log.i("LANGUAGE",LocaleHelper.getLanguage(context));
        getCategorized();

        //hide icon
        /*if(getActionBar()!=null){
            getActionBar().setDisplayShowHomeEnabled(false);
        }*/


        botNav.setOnNavigationItemSelectedListener(
                new RichBottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.home_item:
                                Intent homeIntent=new Intent(context, HomeActivity.class);
                                startActivity(homeIntent);
                                //putFragment(homefrag);
                                //setTitle(R.string.home);
                                break;
                            case R.id.reports_item:
                                putFragment(reportfrag);
                                setTitle(R.string.incidents);
                                break;
                            case R.id.add_item:
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
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        fromBundle = new Bundle();
        //if user clicked "My incidents" from menu, then "from" will come with "myIncidnets"
        fromBundle.putString("from", from);
        //tab index (listFrag should be opened if it was clicked from home page)
        fromBundle.putInt("tabIndex", tabIndex);
        if(frag.getArguments() == null){
            frag.setArguments(fromBundle);
        }
        else {
            frag.getArguments().putAll(fromBundle);
        }

        if(frag.isVisible()){
            return;
        }

        //hide all first
        //if (homefrag.isAdded()) { ft.hide(homefrag); }
        if (reportfrag.isAdded()) { ft.hide(reportfrag); }
        if (secfrag.isAdded()) { ft.hide(secfrag); }
        if (menuFrag.isAdded()) { ft.hide(menuFrag); }

        if(frag.isAdded()) {
            ft.show(frag);
        } else {
            ft.add(R.id.fragment_container, frag);
            ft.addToBackStack(null);//problem with bottomNavigationView
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
            String uri = String.format("http://map.oshcity.kg/basic/categories");

            Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    mCategoriesList=new ArrayList<>();
                    try{
                        for(int i=0; i < response.length(); i++){
                            JSONObject jsonObject = response.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            String title=jsonObject.getString("category_title");
                            String title_ky=jsonObject.getString("title_ky");
                            String image=jsonObject.getString("category_image");

                            mCategoriesList.add(new Categories(id, title,image, title_ky));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FragmentManager manager = getSupportFragmentManager();
        if(manager.getBackStackEntryCount() > 0) {
            Fragment currentFragment = manager.findFragmentById(R.id.fragment_container);
            if(currentFragment instanceof ReportsFragment){
                botNav.findViewById(R.id.reports_item).performClick();
            }
            else if(currentFragment instanceof AddReportFragment){
                botNav.findViewById(R.id.add_item).performClick();
            }
            else if(currentFragment instanceof MenuFragment){
                botNav.findViewById(R.id.likes_item).performClick();
            }
            else if(currentFragment instanceof HomeFragment){
                botNav.findViewById(R.id.home_item).performClick();
            }
        }
        else{
            finish();
        }

    }

    public void updateViews(String languageCode) {
        LocaleHelper.setLocale(this, languageCode);
        //this.recreate();
        Intent myIntent = getIntent();
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(myIntent);
    }
}
