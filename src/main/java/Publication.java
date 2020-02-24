import java.util.List;

public class Publication {


    private String id;

    private String type;

    private String title;

    private String autorString;

    private List<String> autors;

    private String year;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAutorString() {
        return autorString;
    }

    public void setAutorString(String autorString) {
        this.autorString = autorString;
    }

    public List<String> getAutors() {
        return autors;
    }

    public void setAutors(List<String> autors) {
        this.autors = autors;
    }


    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
