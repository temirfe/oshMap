package kg.prosoft.oshmapreport;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;

import java.util.Locale;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ReportsFragment homefrag = new ReportsFragment();
        putFragment(homefrag);

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
                                ReportsFragment homefrag = new ReportsFragment();
                                putFragment(homefrag);
                                break;
                            case R.id.second_item:
                                setTitle("Отправить сообщение");
                                //getActionBar().setDisplayShowHomeEnabled(false);
                                AddReportFragment secfrag = new AddReportFragment();
                                putFragment(secfrag);
                                break;
                            case R.id.likes_item:
                                setTitle("Меню");
                                MenuFragment lfrag = new MenuFragment();
                                putFragment(lfrag);
                                break;
                        }
                        return true;
                    }
                });
    }

    protected void putFragment(Fragment frag){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, frag);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
}
