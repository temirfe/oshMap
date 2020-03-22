package kg.prosoft.oshmapreport;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.app.Fragment;
import androidx.fragment.app.Fragment;

//import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    LinearLayout ll_add;
    LinearLayout ll_list;
    LinearLayout ll_map;
    LinearLayout ll_news;
    MainActivity mainActivity;
    Context context;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();

        ll_add=(LinearLayout)rootView.findViewById(R.id.id_ll_add);
        ll_add.setOnClickListener(this);

        ll_list=(LinearLayout)rootView.findViewById(R.id.id_ll_list);
        ll_list.setOnClickListener(this);

        ll_map=(LinearLayout)rootView.findViewById(R.id.id_ll_map);
        ll_map.setOnClickListener(this);

        ll_news=(LinearLayout)rootView.findViewById(R.id.id_ll_news);
        ll_news.setOnClickListener(this);

        mainActivity=(MainActivity)getActivity();


        return rootView;
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        View view=mainActivity.botNav.findViewById(R.id.home_item);
        switch (id) {
            case R.id.id_ll_add:
                view = mainActivity.botNav.findViewById(R.id.add_item);
                break;
            case R.id.id_ll_list:
                view = mainActivity.botNav.findViewById(R.id.reports_item);
                mainActivity.tabIndex=1;
                break;
            case R.id.id_ll_map:
                view = mainActivity.botNav.findViewById(R.id.reports_item);
                mainActivity.tabIndex=0;
                break;
            case R.id.id_ll_news:
                Intent feeds_int=new Intent(context, FeedsActivity.class);
                startActivity(feeds_int);
                break;
        }
        view.performClick();
    }

}
