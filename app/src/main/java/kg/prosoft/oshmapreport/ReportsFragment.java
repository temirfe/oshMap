package kg.prosoft.oshmapreport;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportsFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    Context context;
    public MapReportsFragment mapFrag;
    public ListReportsFragment listFrag;
    public String verify;
    public String received_text;
    public String show_from;
    ArrayList<Integer> selectedCtgs;
    SessionManager session;
    int user_id;

    public ReportsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getActivity().getApplicationContext();
        session = new SessionManager(context);
        user_id = session.getUserId();
        //Log.i("ONCRV", "Start");

        // Inflate the layout for this fragment
        View layout= inflater.inflate(R.layout.fragment_reports, container, false);
        setHasOptionsMenu(true);

        mapFrag = new MapReportsFragment();
        listFrag = new ListReportsFragment();

        //if user clicked "My incidents" from menu
        show_from="all";
        String from="";
        try {
            from = getArguments().getString("from");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if(from.equals("myIncidents") && user_id!=0){
            show_from="me";
            mapFrag.user_id=user_id;
            listFrag.user_id=user_id;
            //Log.i("USER ID", "SET TO "+user_id);
        }else{
            mapFrag.user_id=0;
            listFrag.user_id=0;
            //Log.i("USER ID", "SET TO 0");
        }

        viewPager = (ViewPager) layout.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) layout.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        showTab();

        return layout;
    }
    private void setupViewPager(ViewPager viewPager) {
        ReportsTabAdapter adapter = new ReportsTabAdapter(getChildFragmentManager());

        adapter.addFragment(mapFrag, "Карта");
        adapter.addFragment(listFrag, getResources().getString(R.string.list));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            showTab();
        }
    }

    public void showTab(){
        int tabIndx=0;
        try {
            tabIndx = getArguments().getInt("tabIndex");
            tabLayout.setScrollPosition(tabIndx,0f,true);
            viewPager.setCurrentItem(tabIndx);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        Log.i("REP TABINDX SHOW",tabIndx+"");
    }

    /*@Override
    public void onPrepareOptionsMenu(Menu menu) {

        MenuItem settingsMenuItem = menu.findItem(R.id.action_open_filter);
        SpannableString s = new SpannableString(settingsMenuItem.getTitle());
        s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
        settingsMenuItem.setTitle(s);

        super.onPrepareOptionsMenu(menu);
    }*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filter, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open_filter:
                //Intent intent = new Intent(this, InboxActivity.class);
                //startActivity(intent);
                Intent filter_intent=new Intent(context, FilterActivity.class);
                filter_intent.putExtra("verify", verify);

                filter_intent.putExtra("show_from", show_from);
                //Log.i("SHOW_FROM", "a "+show_from);
                filter_intent.putExtra("received_text", received_text);
                filter_intent.putExtra("selectedCtgs", selectedCtgs);
                startActivityForResult(filter_intent, 123);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.i("ActRes","came here");
        if (data == null) { Log.i("ActRes","null");  return;}
        if(requestCode==123){

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http").authority("map.oshcity.kg").appendPath("basic").appendPath("incidents");

            Uri.Builder map_builder = new Uri.Builder();
            map_builder.scheme("http").authority("map.oshcity.kg").appendPath("basic").appendPath("locations");

            verify = data.getStringExtra("verify");
            if(verify!=null && verify.equals("0") || verify!=null && verify.equals("1")){
                builder.appendQueryParameter("verified", verify);
                map_builder.appendQueryParameter("verified", verify);
            }
            show_from = data.getStringExtra("show_from");
            if(show_from!=null && show_from.equals("me")){
                //Log.i("SHOW_FROM",show_from);
                builder.appendQueryParameter("user_id", ""+user_id);
                map_builder.appendQueryParameter("user_id", ""+user_id);
            }
            else{
                mapFrag.user_id=0;
                listFrag.user_id=0;
                //Log.i("ON USER ID", "SET TO 0");
            }

            received_text = data.getStringExtra("query_text");
            if(received_text!=null && !received_text.equals("")){
                builder.appendQueryParameter("text", received_text);
                map_builder.appendQueryParameter("text", received_text);
            }

            selectedCtgs = data.getIntegerArrayListExtra("ctg1");
            if(selectedCtgs!=null) {
                int selectedCount = selectedCtgs.size();
                if (selectedCount != 0) {
                    for (int ctg : selectedCtgs) {
                        Log.i("ReportsFragment(182)", "ctg is "+ctg);
                        builder.appendQueryParameter("category_id[]", Integer.toString(ctg));
                        map_builder.appendQueryParameter("category_id[]", Integer.toString(ctg));
                    }
                } else {
                    Log.i("ReportsFragment(187)", "ctg is null");
                }
            } else {
                Log.i("ReportsFragment(190)", "selectedCtgs is null");
            }

            int ctgry = data.getIntExtra("ctg",0);
            if(ctgry!=0){
                builder.appendQueryParameter("category_id[]", Integer.toString(ctgry));
                map_builder.appendQueryParameter("category_id[]", Integer.toString(ctgry));
                if(selectedCtgs==null){
                    selectedCtgs = new ArrayList<>();
                }
                selectedCtgs.add(ctgry);
            }

            listFrag.populateList(1,builder,true);
            mapFrag.populateMap(map_builder);
        }
    }

}
