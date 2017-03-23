package kg.prosoft.oshmapreport;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ProsoftPC on 1/20/2017.
 */

public class CategoriesAdapter extends BaseAdapter {

    private Context mContext;
    private List<Categories> mCategoriesList;
    private LayoutInflater inflater;
    private ArrayList<Integer> selectedCtgs;

    public CategoriesAdapter(Context mContext, List<Categories> mCategoriesList,ArrayList<Integer> alreadyList) {
        this.mContext = mContext;
        this.mCategoriesList = mCategoriesList;
        if(alreadyList!=null){selectedCtgs=alreadyList;}
        else selectedCtgs=new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mCategoriesList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCategoriesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.category_item,null);

        Categories Categories = mCategoriesList.get(position);
        String title;
        if(LocaleHelper.getLanguage(mContext).equals("ky")){
            title=Categories.getTitleKy();
        }
        else{
            title=Categories.getTitle();
        }
        //String image=Categories.getImage();
        //String image_name=image.split("\\.")[0];
        int category_id=Categories.getId();

        TextView tv_category_title=(TextView) convertView.findViewById(R.id.id_tv_category_title);
        tv_category_title.setText(title);

        //ImageView imgv_category=(ImageView) convertView.findViewById(R.id.id_imgv_category);
        //URI imgsrc=new URI("http://map.oshcity.kg/media/uploads/");
        //int img_id = convertView.getResources().getIdentifier("kg.prosoft.oshmapreport:drawable/" + image_name, null, null);
        //imgv_category.setImageResource(img_id);

        convertView.setTag(category_id);

        /*if(position==getCount()-1){
            Log.i("END", "Reached");
        }*/
        CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.id_chb_select_ctg);
        checkBox.setOnCheckedChangeListener(null); //otherwise setChecked method fires it
        if(selectedCtgs!=null && !selectedCtgs.isEmpty() && selectedCtgs.contains(category_id)){
            checkBox.setChecked(true);
        }
        else{
            checkBox.setChecked(false);
        }
        checkBox.setTag(category_id);
        checkBox.setOnCheckedChangeListener(checkListener);


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //int id = Integer.parseInt(v.getTag().toString());
                try {
                    CheckBox cb = (CheckBox) v.findViewById(R.id.id_chb_select_ctg);
                    if(cb.isChecked()){
                        cb.setChecked(false);
                    }
                    else{
                        cb.setChecked(true);
                    }

                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        });

        return convertView;
    }


    CompoundButton.OnCheckedChangeListener checkListener=new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int ctg=Integer.parseInt(buttonView.getTag().toString());
            if (isChecked) {
                selectedCtgs.add(ctg);
                //Log.i("CategoriesAdapter 125","ctg:"+ctg);
            }else{
                selectedCtgs.remove(Integer.valueOf(ctg));
            }
        }
    };

    ArrayList<Integer> getSelectedCtgs(){
        return selectedCtgs;
    }

}
