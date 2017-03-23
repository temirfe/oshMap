package kg.prosoft.oshmapreport;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

public class FeedViewActivity extends Activity {

    TextView tv_title;
    TextView tv_date;
    //TextView tv_text;
    WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_view);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
        }

        Intent intent = getIntent();

        //tv_text=(TextView)findViewById(R.id.id_tv_feedview_text);
        tv_title=(TextView)findViewById(R.id.id_tv_feedview_title);
        tv_date=(TextView)findViewById(R.id.id_tv_feedview_date);

        tv_title.setText(intent.getStringExtra("title"));

        String text=intent.getStringExtra("text");
        String purified=text.replaceAll("<p><span> </span></p>","");
        purified=purified.replaceAll("<p> </p>","");
        purified=purified.replaceAll("<p></p>","");
        //tv_text.setText(Html.fromHtml(purified));
        tv_date.setText(intent.getStringExtra("date"));

        wv=(WebView)findViewById(R.id.webview);
        wv.setBackgroundColor(Color.TRANSPARENT);
        wv.loadData(purified, "text/html; charset=utf-8", "UTF-8");
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
