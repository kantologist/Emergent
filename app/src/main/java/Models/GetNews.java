package Models;

import java.util.List;

/**
 * Created by femi on 5/15/17.
 */

public class GetNews {
    private String status;
    private String source;
    private String sortBy;
    private List<News> articles;

    public GetNews(String status, String source, String sortBy, List<News> articles){
        this.status = status;
        this.source = source;
        this.sortBy = sortBy;
        this.articles = articles;
    }

    public List<News> getArticles(){
        return this.articles;
    }
}
