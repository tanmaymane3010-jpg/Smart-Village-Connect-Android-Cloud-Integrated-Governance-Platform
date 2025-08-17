package com.example.ediscratch1;

public class Helper {

    private String Date_and_Time;
    private String Document_Requested;
    private String Id;
    private String Mobile_Number;
    private String Name;
    private String Resolved_Status;
    private String Village;
    private String Ward;

    public Helper() {

    }

    public Helper(String Date_and_Time,String Document_Requested,String Id,String Mobile_Number,String Name,String Resolved_Status,String Village,String Ward)
    {
        this.Date_and_Time = Date_and_Time;
        this.Document_Requested = Document_Requested;
        this.Id = Id;
        this.Mobile_Number = Mobile_Number;
        this.Name = Name;
        this.Resolved_Status = Resolved_Status;
        this.Village = Village;
        this.Ward =Ward;
    }

    public String getDate_and_Time() {
        return Date_and_Time;
    }

    public void setDate_and_Time(String date_and_Time) {
        Date_and_Time = date_and_Time;
    }

    public String getDocument_Requested() {
        return Document_Requested;
    }

    public void setDocument_Requested(String document_Requested) {
        Document_Requested = document_Requested;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getMobile_Number() {
        return Mobile_Number;
    }

    public void setMobile_Number(String mobile_Number) {
        Mobile_Number = mobile_Number;
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

    public String getVillage() {
        return Village;
    }

    public void setVillage(String village) {
        Village = village;
    }

    public String getWard() {
        return Ward;
    }

    public void setWard(String ward) {
        Ward = ward;
    }
}
