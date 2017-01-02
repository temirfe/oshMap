package kg.prosoft.oshmapreport;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openTest(View v) {
        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);
    }
}
