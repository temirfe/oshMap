package kg.prosoft.oshmapreport;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

public class FeedViewActivity extends Activity {

    TextView tv_title;
    TextView tv_date;
    TextView tv_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_view);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
        }

        tv_text=(TextView)findViewById(R.id.id_tv_feedview_text);
        tv_title=(TextView)findViewById(R.id.id_tv_feedview_title);
        tv_date=(TextView)findViewById(R.id.id_tv_feedview_date);

        Intent intent = getIntent();
        tv_title.setText(intent.getStringExtra("title"));

        String text=intent.getStringExtra("text");
        String purified=text.replaceAll("<p><span> </span></p>","");
        tv_text.setText(Html.fromHtml(purified));
        tv_date.setText(intent.getStringExtra("date"));
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
}
