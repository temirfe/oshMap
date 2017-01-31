package kg.prosoft.oshmapreport;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment implements View.OnClickListener{

    LinearLayout ll_login;
    LinearLayout ll_about;
    LinearLayout ll_news;
    LinearLayout ll_feedback;

    public MenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //setHasOptionsMenu(true);
        View layout= inflater.inflate(R.layout.fragment_menu, container, false);
        ll_login = (LinearLayout) layout.findViewById(R.id.id_ll_login);
        ll_login.setOnClickListener(this);
        ll_about = (LinearLayout) layout.findViewById(R.id.id_ll_about);
        ll_about.setOnClickListener(this);
        ll_news = (LinearLayout) layout.findViewById(R.id.id_ll_news);
        ll_news.setOnClickListener(this);
        ll_feedback = (LinearLayout) layout.findViewById(R.id.id_ll_feedback);
        ll_feedback.setOnClickListener(this);
        return layout;
    }

    @Override
    public void onClick(View v) {
        //do what you want to do when button is clicked
        switch (v.getId()) {
            case R.id.id_ll_login:
                Log.i("LOIG","Clicked");
                break;
            case R.id.id_ll_about:
                Log.i("LOIG","About");
                break;
            case R.id.id_ll_news:
                Log.i("LOIG","NEws");
                break;
            case R.id.id_ll_feedback:
                Log.i("LOIG","feedbac");
                break;
        }
    }

    /*@Override
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
    }*/

}
