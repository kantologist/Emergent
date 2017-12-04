package models;

/**
 * Created by femi on 4/14/17.
 */

public class Contact {

    private int id;
    private String name;

    public Contact(int id, String name, int age){
        this.id = id;
        this.name=name;
    }

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
