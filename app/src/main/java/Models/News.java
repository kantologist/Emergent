package Models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by femi on 5/14/17.
 */

@IgnoreExtraProperties
public class News {
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;
    private boolean published;

    public News (){}

    public News(String author, String title, String description, String url, String urlToImage, String publishedAt, boolean published){
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
        this.published = published;
    }

    public void setPublished(boolean published) { this.published = published; }

    public boolean getPublished() { return this.published; }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor(){
        return this.author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result .put("author", author);
        result.put("title", title);
        result.put("description", description);
        result.put("url", url);
        result.put("urlToImage", urlToImage);
        result.put("publishedAt", publishedAt);
        result.put("published", published);

        return result;
    }
}
