package com.wandoujia.mms.model.dao;

import java.util.List;


public class ParkingLotResult {
    private Integer status;
    private String message;
    private Integer total;
    private List<ParkingLot> results;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<ParkingLot> getResults() {
        return results;
    }

    public void setResults(List<ParkingLot> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "ParkingLotResult{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", total=" + total +
                ", results=" + results +
                '}';
    }
}
