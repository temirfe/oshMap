package kg.prosoft.oshmapreport;

import java.io.Serializable;

/**
 * Created by ProsoftPC on 1/20/2017.
 */

public class Categories implements Serializable {
    private int id;
    private String title;
    private String title_ky;
    private String image;

    public Categories(int id, String title, String image, String title_ky) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.title_ky = title_ky;
    }

    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getTitleKy() {
        return title_ky;
    }

    public String getImage() {
        return image;
    }

}
