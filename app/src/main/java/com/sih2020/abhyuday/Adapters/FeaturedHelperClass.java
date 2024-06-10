package com.sih2020.abhyuday.Adapters;

public class FeaturedHelperClass {
    String image;
    String title,description;

    public FeaturedHelperClass(String image, String title, String description) {
        this.image = image;
        this.title = title;
        this.description = description;
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
}
