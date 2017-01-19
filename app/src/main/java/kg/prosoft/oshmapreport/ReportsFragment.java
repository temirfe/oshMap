package kg.prosoft.oshmapreport;


import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportsFragment extends Fragment {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public ReportsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout= inflater.inflate(R.layout.fragment_reports, container, false);

        setHasOptionsMenu(true);

        viewPager = (ViewPager) layout.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) layout.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        return layout;
    }
    private void setupViewPager(ViewPager viewPager) {
        ReportsTabAdapter adapter = new ReportsTabAdapter(getChildFragmentManager());
        adapter.addFragment(new MapReportsFragment(), "Карта");
        adapter.addFragment(new ListReportsFragment(), "Список");
        viewPager.setAdapter(adapter);
    }

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
                Log.i("asdf","asdfasfsdf");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
