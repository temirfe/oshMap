package kg.prosoft.oshmapreport;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ProsoftPC on 2/1/2017.
 */

public class Feed {
    private int id;
    private String title; //username of user who left comment
    private String text; //comment text
    private String date;
    private String link;

    public Feed(int id, String title, String text, String date, String link) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.date=date;
        this.link=link;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
    public String getLink() {
        return link;
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
