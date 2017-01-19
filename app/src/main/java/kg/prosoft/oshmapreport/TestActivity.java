package kg.prosoft.oshmapreport;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class TestActivity extends Activity {

    public EditText etEmail;
    public EditText etName;
    public Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        etName=(EditText) findViewById(R.id.editText);
        etEmail=(EditText) findViewById(R.id.editText2);
        btnSend=(Button)findViewById(R.id.button2);
    }

    public void sendForm(View v){
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        submit(name,email);
    }

    public void submit(final String name, final String email){
        String url="http://temir.ml/pages";
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                if(response.equals("success")){
                    Log.i("Comment post ACTION","SUCCESS");
                }
                else{
                    Log.i("Comment ACTION","FAIL: "+response);
                }
            }

        };

        StringRequest req = new StringRequest(Request.Method.POST, url,responseListener, null){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("title",name);
                params.put("text",email);
                params.put("myar[1]","Yooo");
                params.put("myar[2]","Niga");

                return params;
            }
        };
        // Post params to be sent to the server
        /*Map<String, String> params = new HashMap<String, String>();
        params.put("title", name);
        params.put("text", email);
        params.put("myar[]", "derzkiy");
        params.put("myar[]", "manyak"); //can't send array as param :(
        Log.i("dump",params.toString());

        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("Response:%n %s", response.toString(4));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Error: ", error.getMessage());
            }
        });*/



        // Request a string response from the provided URL.
        /*StringRequest volReq = new StringRequest(Request.Method.GET, uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i("Response is: ", response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("AAAAAA","That didn't work!");
            }
        });*/

        MyVolley.getInstance(this.getApplicationContext()).addToRequestQueue(req);
    }
}
