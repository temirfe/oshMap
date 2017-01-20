package kg.prosoft.oshmapreport;

import java.io.Serializable;

/**
 * Created by ProsoftPC on 1/20/2017.
 */

public class Categories implements Serializable {
    private int id;
    private String title;
    private String image;

    public Categories(int id, String title, String image) {
        this.id = id;
        this.image = image;
        this.title = title;
    }

    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

}
