package com.example.ediscratch1;

public class ProductsModel {

    private String Address;
    private String Date_and_Time;
    private String Description;
    private String Id;
    private String MobileNo;
    private String Name;
    private String Resolved_Status;
    private String Ward;
    private String ImageUrl;
    private String VideoUrl;

    public ProductsModel(){

    }
    public ProductsModel(String Address,String Date_and_Time,String Description,String Id,String MobileNo,String Name,String Resolved_Status, String Ward,String ImageUrl,String VideoUrl)
    {
        this.Address = Address;
        this.Date_and_Time = Date_and_Time;
        this.Description = Description;
        this.Id = Id;
        this.MobileNo = MobileNo;
        this.Name = Name;
        this.Resolved_Status = Resolved_Status;
        this.Ward = Ward;
        this.ImageUrl = ImageUrl;
        this.VideoUrl = VideoUrl;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getDate_and_Time() {
        return Date_and_Time;
    }

    public void setDate_and_Time(String date_and_Time) {
        Date_and_Time = date_and_Time;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getResolved_Status() {
        return Resolved_Status;
    }

    public void setResolved_Status(String resolved_Status) {
        Resolved_Status = resolved_Status;
    }

    public String getWard() {
        return Ward;
    }

    public void setWard(String ward) {
        Ward = ward;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return VideoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        VideoUrl = videoUrl;
    }
}
