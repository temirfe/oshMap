package kg.prosoft.oshmapreport;

import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ProsoftPC on 10/19/2016.
 */
public class ReportList {
    private int id;
    private int location_id;
    private int user_id; //id of user who left comment
    private String title; //username of user who left comment
    private String text; //comment text
    private String date;
    private int verified;
    private int zoom;

    public ReportList(int id, String title, String text, int location_id, int user_id, String date, int verified, int zoom) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.location_id=location_id;
        this.user_id=user_id;
        this.date=date;
        this.verified=verified;
        this.zoom=zoom;
    }

    public int getId() {
        return id;
    }

    public int getLocation_id() {
        return location_id;
    }
    public int getUser_id() {
        return user_id;
    }
    public int getZoom() {
        return zoom;
    }
    public String getTitle() {
        return title;
    }
    public int getVerified() { return verified;}

    public String getText() {
        String question="";
        text = text.replace("\\n", "");
        String[] split = text.split("______________________", 2);
        if(split.length > 1 && split[1] != null)
        {
            question=split[1];
        }
        else {
            String[] split2 = text.split("----------------", 2);
            if (split2.length > 1 && split2[1] != null) {
                question=split2[1];
            }
        }
        if(question.length()>0){text=question;}

        text = text.replace("--", "");
        text = text.replace("__", "");
        if (text.length() > 155) {
            text = text.substring(0, 155) + "...";
        }
        text=text.trim();

        return text;
    }

    public String getDate() {
        //long timeNow = System.currentTimeMillis();
        //long timeThen;
        Locale locale = new Locale("ru");
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",locale);
        try{
            Date dateObj = formatter.parse(date);
            SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy HH:mm",locale);
            return fmt.format(dateObj);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }



        /*try {
            if(date==null){
                timeThen=timeNow;
            }
            else{
                Date dateObj = formatter.parse(date);
                timeThen=dateObj.getTime();
            }
            return DateUtils.getRelativeTimeSpanString(timeThen, timeNow,
                    DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE)
                    .toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        return "getDate() error";
    }
}
