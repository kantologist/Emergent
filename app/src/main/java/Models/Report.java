package Models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 23/11/2017.
 */

@IgnoreExtraProperties
public class Report {
    private String location;
    private String imageUrl;
    private String email;
    private String phone;

    public Report(){}

    public Report(String location, String imageUrl, String email, String phone){
        this.location = location;
        this.imageUrl = imageUrl;
        this.email = email;
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {

        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {

        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result .put("location", location);
        result.put("imageUrl", imageUrl);
        result.put("email", email);
        result.put("phone", phone);

        return result;
    }

}
