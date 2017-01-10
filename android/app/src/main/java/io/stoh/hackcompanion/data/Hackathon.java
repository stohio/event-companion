package io.stoh.hackcompanion.data;

/**
 * Created by csinko on 1/9/17.
 */

public class Hackathon {
    private int id;
    private String name;
    private String startDate;
    private String endDate;
    private String location;
    private String type;

    public Hackathon(int id, String type, String name, String startDate, String endDate, String location) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;

    }

    public String getType() {
        return type;
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
