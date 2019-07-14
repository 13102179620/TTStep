package com.syt.ttstep.beans;

public class PedometerBean {

    private int id;

    private int stepsCount;
    private double calorie;
    //走的距离
    private double distance;
    //一分钟走多少步
    private int pace;
    //速度
    private double speed;
    //开始记录时间
    private long startTime;
    //最后一步时间
    private long lastStepTime;
    //时间戳
    private long day;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStepsCount() {
        return stepsCount;
    }

    public void setStepsCount(int stepsCount) {
        this.stepsCount = stepsCount;
    }

    public double getCalorie() {
        return calorie;
    }

    public void setCalorie(double calorie) {
        this.calorie = calorie;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getPace() {
        return pace;
    }

    public void setPace(int pace) {
        this.pace = pace;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getLastStepTime() {
        return lastStepTime;
    }

    public void setLastStepTime(long lastStepTime) {
        this.lastStepTime = lastStepTime;
    }

    public long getDay() {
        return day;
    }

    public void setDay(long day) {
        this.day = day;
    }

    public void reset(){
        stepsCount = 0;
        calorie = 0;
        distance = 0;
    }

    @Override
    public String toString() {
        return "PedometerBean{" +
                "id=" + id +
                ", stepsCount=" + stepsCount +
                ", calorie=" + calorie +
                ", distance=" + distance +
                ", pace=" + pace +
                ", speed=" + speed +
                ", startTime=" + startTime +
                ", lastStepTime=" + lastStepTime +
                ", day=" + day +
                '}';
    }
}
