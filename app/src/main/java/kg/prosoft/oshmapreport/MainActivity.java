package kg.prosoft.oshmapreport;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Locale;

public class MainActivity extends Activity {

    ReportsFragment homefrag;
    AddReportFragment secfrag;
    MenuFragment lfrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            homefrag = new ReportsFragment();
            secfrag = new AddReportFragment();
            lfrag = new MenuFragment();
            setContentView(R.layout.activity_main);
            putFragment(homefrag);
        }



        //hide icon
        if(getActionBar()!=null){
            getActionBar().setDisplayShowHomeEnabled(false);
        }

        //bottomNav
        RichBottomNavigationView botNav = (RichBottomNavigationView)
                findViewById(R.id.bottom_navigation);

        botNav.setOnNavigationItemSelectedListener(
                new RichBottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        //.getMenu().findItem(R.id.recent_item).setChecked(false);
                        switch (item.getItemId()) {
                            case R.id.home_item:
                                //getActionBar().setDisplayShowHomeEnabled(true);
                                setTitle("Карта обращений");
                                putFragment(homefrag);
                                break;
                            case R.id.second_item:
                                setTitle("Отправить сообщение");
                                //getActionBar().setDisplayShowHomeEnabled(false);
                                putFragment(secfrag);
                                break;
                            case R.id.likes_item:
                                setTitle("Меню");
                                putFragment(lfrag);
                                break;
                        }
                        return true;
                    }
                });
    }

    protected void putFragment(Fragment frag){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        //hide all first
        if (homefrag.isAdded()) { ft.hide(homefrag); }
        if (secfrag.isAdded()) { ft.hide(secfrag); }
        if (lfrag.isAdded()) { ft.hide(lfrag); }

        if(frag.isAdded()) {
            ft.show(frag);
        } else {
            ft.add(R.id.fragment_container, frag);
            //ft.addToBackStack(null);
        }
        ft.commit();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open_filter:
                Intent filter_intent=new Intent(this, FilterActivity.class);
                startActivityForResult(filter_intent, 123);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
