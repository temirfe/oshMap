package kg.prosoft.oshmapreport;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import kg.prosoft.oshmapreport.utils.FirebaseConfig;
import kg.prosoft.oshmapreport.utils.NotificationUtils;

public class MainActivity extends Activity {

    HomeFragment homefrag;
    ReportsFragment reportfrag;
    AddReportFragment secfrag;
    MenuFragment menuFrag;
    ArrayList<Categories> mCategoriesList;
    Context context;
    RichBottomNavigationView botNav;
    String from;
    Bundle fromBundle;
    int tabIndex;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            homefrag = new HomeFragment();
            reportfrag = new ReportsFragment();
            secfrag = new AddReportFragment();
            menuFrag = new MenuFragment();
            setContentView(R.layout.activity_main);
        }

        //bottomNav
        botNav = (RichBottomNavigationView) findViewById(R.id.bottom_navigation);

        Intent intent=getIntent();
        from =intent.getStringExtra("from");
        if(from==null){from="";}
        if(from.equals("login")){
            putFragment(menuFrag);
            botNav.getMenu().findItem(R.id.likes_item).setChecked(false);
            botNav.getMenu().findItem(R.id.likes_item).setChecked(true);
        }
        else{
            putFragment(homefrag);
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
                        //.getMenu().findItem(R.id.recent_item).setChecked(false);
                        switch (item.getItemId()) {
                            case R.id.home_item:
                                //getActionBar().setDisplayShowHomeEnabled(true);
                                putFragment(homefrag);
                                setTitle(R.string.app_name_short);
                                break;
                            case R.id.reports_item:
                                //getActionBar().setDisplayShowHomeEnabled(true);
                                putFragment(reportfrag);
                                setTitle(R.string.app_name_short);
                                break;
                            case R.id.add_item:
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

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(FirebaseConfig.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(FirebaseConfig.TOPIC_GLOBAL);

                } else if (intent.getAction().equals(FirebaseConfig.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.push_notification)+" " + message, Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(FirebaseConfig.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(FirebaseConfig.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    protected void putFragment(Fragment frag){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
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
            Log.i("ARGUMENTS","notNull");
        }

        if(frag.isVisible()){
            return;
        }

        //hide all first
        if (homefrag.isAdded()) { ft.hide(homefrag); }
        if (reportfrag.isAdded()) { ft.hide(reportfrag); }
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

    //change language
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
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
