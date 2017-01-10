package io.stoh.hackcompanion.io.stoh.hackcompanion.data;

/**
 * Created by csinko on 1/9/17.
 */

public class Hackathon {
    private int id;
    private String name;
    private String startDate;
    private String endDate;
    private String location;

    public Hackathon(int id, String name, String startDate, String endDate, String location) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getLocation() {
        return location;
    }


}
