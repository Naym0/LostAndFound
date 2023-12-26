package com.example.lostandfound;

public class LostItems_data {
    String item,description, category, dateFound, location;

    public LostItems_data(){
        //Empty constructor needed for Firebase to create objects from db documents
    }

    public LostItems_data(String name, String desc, String category, String date, String location) {
        this.item = name;
        this.description = desc;
        this.category = category;
        this.dateFound = date;
        this.location = location;
    }

    public String getItem() {
        return item;
    }

    public String getDesc() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return dateFound;
    }

    public String getLocation() { return location; }
}
