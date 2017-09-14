package com.ssu.sangjunianjuni.smartbabycare.Analysis;

/**
 * Created by kang on 2017-06-28.
 */

public class HeartbeatItem {
    private String Time;
    private String Heartbeat;



    public void setTime(String time){Time=time;}

    public void setHeartbeat(String heartbeat){Heartbeat=heartbeat;}

    public String getTime(){return this.Time;}

    public String getHeartbeat(){return this.Heartbeat;}
}
