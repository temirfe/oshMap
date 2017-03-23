package kg.prosoft.oshmapreport;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.RadioGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment implements View.OnClickListener{

    LinearLayout ll_login;
    LinearLayout ll_about;
    LinearLayout ll_news;
    LinearLayout ll_feedback;
    LinearLayout ll_goToAccount;
    Context context;
    SessionManager session;
    TextView tv_name;
    RadioGroup radio_group_lang;

    public MenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //setHasOptionsMenu(true);
        View layout= inflater.inflate(R.layout.fragment_menu, container, false);

        context = getActivity();
        ll_login = (LinearLayout) layout.findViewById(R.id.id_ll_login);
        ll_login.setOnClickListener(this);
        ll_about = (LinearLayout) layout.findViewById(R.id.id_ll_about);
        ll_about.setOnClickListener(this);
        ll_news = (LinearLayout) layout.findViewById(R.id.id_ll_news);
        ll_news.setOnClickListener(this);
        ll_feedback = (LinearLayout) layout.findViewById(R.id.id_ll_feedback);
        ll_feedback.setOnClickListener(this);
        ll_goToAccount = (LinearLayout) layout.findViewById(R.id.id_ll_goToAccount);
        ll_goToAccount.setOnClickListener(this);

        radio_group_lang=(RadioGroup) layout.findViewById(R.id.id_rgroup_lng);
        if(LocaleHelper.getLanguage(context).equals("ky")){
            radio_group_lang.check(R.id.id_radio_ky);
        }
        radio_group_lang.setOnCheckedChangeListener(changeLangListener);

        tv_name = (TextView) layout.findViewById(R.id.id_tv_name);
        session = new SessionManager(context.getApplicationContext());
        if(session.isLoggedIn()){
            String name=session.getName();
            tv_name.setText(name);
            Log.i("NAME IN SESSION",name);
            ll_login.setVisibility(View.GONE);
        }else{
            Log.i("SESSION","Not logged in");
            ll_goToAccount.setVisibility(View.GONE);
        }
        return layout;
    }

    RadioGroup.OnCheckedChangeListener changeLangListener = new RadioGroup.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId)
        {
            MainActivity mainAct=(MainActivity)context;
            switch(checkedId)
            {
                case R.id.id_radio_ru:
                    mainAct.updateViews("ru");
                    break;
                case R.id.id_radio_ky:
                    mainAct.updateViews("ky");
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        //do what you want to do when button is clicked
        switch (v.getId()) {
            case R.id.id_ll_login:
                Intent login_int=new Intent(context, LoginActivity.class);
                startActivity(login_int);
                break;
            case R.id.id_ll_about:
                Intent ab_int=new Intent(context, AboutActivity.class);
                startActivity(ab_int);
                break;
            case R.id.id_ll_news:
                Intent feeds_int=new Intent(context, FeedsActivity.class);
                startActivity(feeds_int);
                break;
            case R.id.id_ll_feedback:
                Intent feedb_int=new Intent(context, FeedbackActivity.class);
                startActivity(feedb_int);
                break;
            case R.id.id_ll_goToAccount:
                Intent acc_int=new Intent(context, AccountActivity.class);
                startActivity(acc_int);
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
