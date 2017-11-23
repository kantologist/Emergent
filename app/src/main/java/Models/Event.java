package Models;

import com.google.firebase.database.IgnoreExtraProperties;

import io.realm.RealmObject;

/**
 * Created by Femi on 08/06/2017.
 */

@IgnoreExtraProperties
public class Event{

    private String image;
    private String title;
    private String description;
    private Double lat;
    private Double lon;
    private long id;

    public Event(){}

    public Event(String image, String title, String description, Double lat, Double lon, long id){
        this.image = image;
        this.title = title;
        this.description =description;
        this.lat = lat;
        this.lon =lon;
        this.id = id;

    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {

        return id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getImage() {

        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }
}
