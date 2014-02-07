package com.example.ipark;


/**
 * Created with IntelliJ IDEA.
 * User: chendong
 * Date: 14-2-6
 * Time: 下午2:50
 * To change this template use File | Settings | File Templates.
 */
public class ParkingLot {

    public static class Location {
        public Double lat;
        public Double lng;
    }
    private String uid;
    private String name;
    private String address;
    private Location location;
    private Double price;
    private Integer maxNum;
    private Integer idleNum;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "ParkingLot{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", location=" + location +
                ", price=" + price +
                ", maxNum=" + maxNum +
                ", idleNum=" + idleNum +
                ", description='" + description + '\'' +
                '}';
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getIdleNum() {
        return idleNum;
    }

    public void setIdleNum(Integer idleNum) {
        this.idleNum = idleNum;
    }

    public Integer getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(Integer maxNum) {
        this.maxNum = maxNum;
    }
}
