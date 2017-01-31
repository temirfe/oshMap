package kg.prosoft.oshmapreport;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ProsoftPC on 10/19/2016.
 */
public class ReportListAdapter extends BaseAdapter {

    private Context mContext;
    private List<ReportList> mCommentList;
    private LayoutInflater inflater;
    int range=0;

    public ReportListAdapter(Context mContext, List<ReportList> mCommentList) {
        this.mContext = mContext;
        this.mCommentList = mCommentList;
    }

    @Override
    public int getCount() {
        return mCommentList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCommentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.item_report_row,null);

        ReportList incident = mCommentList.get(position);
        String title=incident.getTitle();
        final SpannableStringBuilder boldTitle = new SpannableStringBuilder(title);
        boldTitle.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, title.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView statusBox=(TextView) convertView.findViewById(R.id.id_tv_box);
        int verified = incident.getVerified();
        if(verified==1){
            statusBox.setBackgroundResource(R.color.green);
        }
        else{
            statusBox.setBackgroundResource(R.color.red);
        }
        TextView tv_title=(TextView) convertView.findViewById(R.id.id_tv_title);
        tv_title.setText(boldTitle);
        TextView tv_text=(TextView) convertView.findViewById(R.id.id_tv_text);
        tv_text.setText(incident.getText());
        TextView dateTv=(TextView) convertView.findViewById(R.id.textView_date);
        dateTv.setText(incident.getDate());

        convertView.setTag(incident.getId());

        /*if(position==getCount()-1){
            Log.i("END", "Reached");
        }*/

        return convertView;
    }
}
