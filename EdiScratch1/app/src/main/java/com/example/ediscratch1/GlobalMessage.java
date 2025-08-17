package com.example.ediscratch1;

public class GlobalMessage {

    String Title;
    String Description;
    String Date_and_Time;
    String Id;
    String ImageUrl;

    public GlobalMessage() {
    }

    public GlobalMessage(String title, String description, String date_and_Time, String id, String imageUrl) {
        Title = title;
        Description = description;
        Date_and_Time = date_and_Time;
        Id = id;
        ImageUrl = imageUrl;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDate_and_Time() {
        return Date_and_Time;
    }

    public void setDate_and_Time(String date_and_Time) {
        Date_and_Time = date_and_Time;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}
