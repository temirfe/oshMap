package kg.prosoft.oshmapreport;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectCategoryActivity extends Activity {
    ListView lv_categories;
    CategoriesAdapter adapter;
    List<Categories> mCategoriesList;
    ArrayList<Integer> selectedCtgs;
    HashMap<Integer, Categories> allCtgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
        }

        Intent intent=getIntent();
        selectedCtgs=intent.getIntegerArrayListExtra("already");
        allCtgs=(HashMap<Integer, Categories>)intent.getSerializableExtra("categories");

        lv_categories = (ListView) findViewById(R.id.id_lv_categories);
        mCategoriesList = new ArrayList<Categories>();
        for(Map.Entry<Integer, Categories> entry : allCtgs.entrySet()) {
            int key = entry.getKey();
            Categories value = entry.getValue();
            mCategoriesList.add(new Categories(key,value.getTitle(),value.getImage()));
        }
        adapter = new CategoriesAdapter(this,mCategoriesList,selectedCtgs);
        lv_categories.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,0,R.string.done).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                done();
                return true;
            case 1:
                done();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void done(){
        Intent intent= new Intent();
        intent.putExtra("ctg1", adapter.getSelectedCtgs());
        setResult(RESULT_OK, intent);
        finish();
    }
}
