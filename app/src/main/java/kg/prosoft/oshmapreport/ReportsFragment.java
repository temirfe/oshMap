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

    private Toolbar toolbar;
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

    public ReportsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getActivity().getApplicationContext();
        session = new SessionManager(context);
        // Inflate the layout for this fragment
        View layout= inflater.inflate(R.layout.fragment_reports, container, false);
        setHasOptionsMenu(true);

        if(selectedCtgs==null){
            selectedCtgs = new ArrayList<>();
        }

        viewPager = (ViewPager) layout.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) layout.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        //if user clicked "My incidents" from menu
        String from = getArguments().getString("from");
        int user_id = session.getUserId();
        if(from!=null && from.equals("myIncidents") && user_id!=0){
            listFrag.user_id=user_id;
            show_from="me";
        }
        else{
            show_from="all";
            Log.i("FAIL","id "+user_id+" from"+from);
        }

        return layout;
    }
    private void setupViewPager(ViewPager viewPager) {
        ReportsTabAdapter adapter = new ReportsTabAdapter(getChildFragmentManager());

        mapFrag = new MapReportsFragment();
        listFrag = new ListReportsFragment();
        adapter.addFragment(mapFrag, "Карта");
        adapter.addFragment(listFrag, "Список");
        viewPager.setAdapter(adapter);
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
                if(listFrag.ctg!=0){
                    selectedCtgs.add(listFrag.ctg);
                }

                Intent filter_intent=new Intent(context, FilterActivity.class);
                filter_intent.putExtra("verify", verify);
                Log.i("SHOW", show_from);
                filter_intent.putExtra("show_from", show_from);
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
        Log.i("ActRes","came here");
        if (data == null) { Log.i("ActRes","null");  return;}
        if(requestCode==123){

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("api.temirbek.com")
                    .appendPath("incidents");

            verify = data.getStringExtra("verify");
            if(verify.equals("0") || verify.equals("1")){
                builder.appendQueryParameter("verified", verify);
            }
            show_from = data.getStringExtra("show_from");
            if(show_from.equals("me")){
                builder.appendQueryParameter("user_id", ""+session.getUserId());
            }

            received_text = data.getStringExtra("query_text");
            if(received_text!=null && !received_text.equals("")){
                builder.appendQueryParameter("text", received_text);
            }

            selectedCtgs = data.getIntegerArrayListExtra("ctg1");
            int selectedCount=selectedCtgs.size();
            if(selectedCount!=0){
                for (int ctg : selectedCtgs)
                {
                    builder.appendQueryParameter("category_id[]", Integer.toString(ctg));
                }
            }
            else{
                Log.i("ActRes","ctg is null");
            }

            listFrag.populateList(1,builder,true);
        }
    }

}
