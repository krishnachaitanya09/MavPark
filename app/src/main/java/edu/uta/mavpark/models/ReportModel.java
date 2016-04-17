package edu.uta.mavpark.models;

/**
 * Created by krish on 3/20/2016.
 */
public class ReportModel {

    public ReportModel(String reportType, String comments, String parkingLot, String licensePlateId, String reportTime) {
        this.reportType = reportType;
        this.comments = comments;
        this.parkingLot = parkingLot;
        this.licensePlateId = licensePlateId;
        this.reportTime = reportTime;
    }

    public String reportType;
    public String comments;
    public String parkingLot;
    public String licensePlateId;
    public String reportTime;
}
