package main.java.entities;

/**
 *
 */
public class CMMSActivity {
    private String activityId;
    private String type;
    private String typeId;
    private String startTime;
    private String endTime;
    private boolean status;

    public CMMSActivity(String activityId, String type, String typeId, String startTime, String endTime, boolean status) {
        this.activityId = activityId;
        this.type = type;
        this.typeId = typeId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    // Getters and Setters
    public String getActivityId() { return activityId; }
    public void setActivityId(String activityId) { this.activityId = activityId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTypeId() { return typeId; }
    public void setTypeId(String typeId) { this.typeId = typeId; }

    public String getStartTime () { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime;}

    public String getendTime (){return endTime;}
    public void setendTime(String endTime){this.endTime = endTime;}

    public String getStatus(){return status;}
    public void setStatus(String status){this.status = status;}

    @Override
    public String toString() {
        return String.format("CMMSActivity{ActivityID='%s', Type='%s', TypeID='%s'}",
                activityId, type, typeId);
    }
}
