package kg.prosoft.oshmapreport;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import kg.prosoft.oshmapreport.utils.FirebaseConfig;
import kg.prosoft.oshmapreport.utils.NotificationUtils;

public class HomeActivity extends Activity implements View.OnClickListener{

    LinearLayout ll_add;
    LinearLayout ll_list;
    LinearLayout ll_map;
    LinearLayout ll_news;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ll_add=(LinearLayout)findViewById(R.id.id_ll_add);
        ll_add.setOnClickListener(this);

        ll_list=(LinearLayout)findViewById(R.id.id_ll_list);
        ll_list.setOnClickListener(this);

        ll_map=(LinearLayout)findViewById(R.id.id_ll_map);
        ll_map.setOnClickListener(this);

        ll_news=(LinearLayout)findViewById(R.id.id_ll_news);
        ll_news.setOnClickListener(this);



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
    public void onClick(View v) {
        int id = v.getId();
        Intent mainActIntent=new Intent(this, MainActivity.class);
        switch (id) {
            case R.id.id_ll_add:
                mainActIntent.putExtra("open","add");
                startActivity(mainActIntent);
                break;
            case R.id.id_ll_list:
                mainActIntent.putExtra("open","list");
                startActivity(mainActIntent);
                break;
            case R.id.id_ll_map:
                mainActIntent.putExtra("open","map");
                startActivity(mainActIntent);
                break;
            case R.id.id_ll_news:
                Intent feeds_int=new Intent(this, FeedsActivity.class);
                startActivity(feeds_int);
                break;
        }
        //view.performClick();
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

    //change language
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
