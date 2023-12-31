package com.example.lostandfound;

public class LostItems_data {
    String item,description, category, dateFound, location, image;

    public LostItems_data(){
        //Empty constructor needed for Firebase to create objects from db documents
    }

    public LostItems_data(String name, String desc, String category, String date, String location, String image) {
        this.item = name;
        this.description = desc;
        this.category = category;
        this.dateFound = date;
        this.location = location;
        this.image = image;
    }

    public String getItem() {
        return item;
    }

    public String getDescription() { return description; }

    public String getCategory() {
        return category;
    }

    public String getDateFound() { return dateFound; }

    public String getLocation() { return location; }

    public String getImage() { return image; }
}
