package kg.prosoft.oshmapreport;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class AccountActivity extends Activity implements View.OnClickListener {

    LinearLayout ll_logout;
    LinearLayout ll_myincidents;
    SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
        }

        session = new SessionManager(getApplicationContext());
        ll_logout = (LinearLayout) findViewById(R.id.id_ll_logout);
        ll_logout.setOnClickListener(this);
        ll_myincidents = (LinearLayout) findViewById(R.id.id_ll_myincidents);
        ll_myincidents.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                /*Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("from","account");
                startActivity(intent);*/
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View v) {
        //do what you want to do when button is clicked
        switch (v.getId()) {
            case R.id.id_ll_logout:
                session.logoutUser();
                finish();
                break;
            case R.id.id_ll_myincidents:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("from","myIncidents");
                intent.putExtra("user_id",session.getUserId());
                startActivity(intent);
                finish();
                break;
        }
    }
}
