package kg.prosoft.oshmapreport;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ProsoftPC on 2/1/2017.
 */

public class FeedListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Feed> feedList;
    private LayoutInflater inflater;
    int range=0;

    public FeedListAdapter(Context mContext, List<Feed> mCommentList) {
        this.mContext = mContext;
        this.feedList = mCommentList;
    }

    @Override
    public int getCount() {
        return feedList.size();
    }

    @Override
    public Object getItem(int position) {
        return feedList.get(position);
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
            convertView = inflater.inflate(R.layout.item_feed_row,null);

        Feed feed = feedList.get(position);
        String title=feed.getTitle();
        final SpannableStringBuilder boldTitle = new SpannableStringBuilder(title);
        boldTitle.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, title.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        TextView tv_title=(TextView) convertView.findViewById(R.id.id_tv_title);
        tv_title.setText(boldTitle);
        TextView dateTv=(TextView) convertView.findViewById(R.id.textView_date);
        dateTv.setText(feed.getDate());

        convertView.setTag(feed.getId());

        /*if(position==getCount()-1){
            Log.i("END", "Reached");
        }*/

        return convertView;
    }
}
