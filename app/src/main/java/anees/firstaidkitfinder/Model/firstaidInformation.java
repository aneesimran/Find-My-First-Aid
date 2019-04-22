package anees.firstaidkitfinder.Model;

import java.util.ArrayList;

public class firstaidInformation {

    String kitID;
    String name;
    String description;
    String location;
    double latitude;
    double longitude;
    String imagePath;
    String time;
    String date;
    ArrayList<String> boxData;

    public firstaidInformation()
    {

    }

    public firstaidInformation(String kitID, String name, String description, String location, double latitude, double longitude, String imagePath, String time, String date, ArrayList<String> boxData)
    {
        this.kitID = kitID;
        this.name = name;
        this. description = description;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imagePath = imagePath;
        this.time = time;
        this.date = date;
        this.boxData = boxData;
    }

    public String getKitID() {
        return kitID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public ArrayList<String> getBoxData() {
        return boxData;
    }
}
