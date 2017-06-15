package com.mftech.f;

import java.util.Date;

/**
 * Created by v.vanichkov on 14.06.2017.
 */
public class TestProperties {
    private int from;
    private int to;
    private Double sum;
    private int source;
    private int destination;
    private int method;
    private int servicePackage;
    private String date;
    private String result;

    public TestProperties(int aFrom, int aTo, Double aSum,int aSource, int destination, int aMethod,int aServicePackage,
                          String date, String result ){
        this.date = date;
        this.destination = destination;
        this.from = aFrom;
        this.to = aTo;
        this.source = aSource;
        this.sum = aSum;
        this.method = aMethod;
        this.servicePackage = aServicePackage;
        this.result = result;
    }

    public int getServicePackage() {
        return servicePackage;
    }

    public String getDate() {
        return date;
    }

    public Double getSum() {
        return sum;
    }

    public int getDestination() {
        return destination;
    }

    public int getFrom() {
        return from;
    }

    public int getMethod() {
        return method;
    }

    public int getSource() {
        return source;
    }

    public int getTo() {
        return to;
    }

    public String getResult() {
        return result;
    }
}
