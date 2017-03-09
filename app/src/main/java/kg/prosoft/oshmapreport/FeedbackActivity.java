package kg.prosoft.oshmapreport;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FeedbackActivity extends Activity {

    EditText et_name;
    EditText et_email;
    EditText et_phone;
    EditText et_message;
    Button btn_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
        }
        et_name=(EditText)findViewById(R.id.id_et_fb_name);
        et_email=(EditText)findViewById(R.id.id_et_fb_email);
        et_phone=(EditText)findViewById(R.id.id_et_fb_phone);
        et_message=(EditText)findViewById(R.id.id_et_fb_message);
        btn_send=(Button)findViewById(R.id.id_btn_submit_fb);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendFeedback(View v){
        boolean allGood=true;
        final String name=et_name.getText().toString();
        final String email=et_email.getText().toString();
        final String phone=et_phone.getText().toString();
        final String message=et_message.getText().toString();

        if(name.trim().equals("")){
            et_name.setError(getResources().getString(R.string.required));
            allGood=false;
        }
        if(email.trim().equals("")){
            et_email.setError(getResources().getString(R.string.required));
            allGood=false;
        } else if (!isEmailValid(email)) {
            et_email.setError(getString(R.string.error_invalid_email));
            allGood=false;
        }

        if(phone.trim().equals("")){
            et_phone.setError(getResources().getString(R.string.required));
            allGood=false;
        }
        if(message.trim().equals("")){
            et_message.setError(getResources().getString(R.string.required));
            allGood=false;
        }

        if(allGood)
        {
            final ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle(getResources().getString(R.string.sending));
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();
            String url="http://map.oshcity.kg/basic/site/feedback";
            Response.Listener<String> listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progress.dismiss();
                    try {
                        if(response.equals("success")){
                            Toast.makeText(FeedbackActivity.this, R.string.feedback_success, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else{
                            Toast.makeText(FeedbackActivity.this, R.string.feedback_fail, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Throwable t) {
                        Log.e("Feedback error",  response );
                    }
                }
            };

            StringRequest req = new StringRequest(Request.Method.POST, url, listener, null){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("message",message);
                    params.put("name",name);
                    params.put("email",email);
                    params.put("phone",phone);

                    return params;
                }
            };
            MyVolley.getInstance(this).addToRequestQueue(req);
        }
    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
}
