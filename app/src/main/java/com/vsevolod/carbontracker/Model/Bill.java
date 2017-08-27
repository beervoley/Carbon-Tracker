package com.vsevolod.carbontracker.Model;

import java.util.Date;

/**
 * Created by vsevolod on 2017-03-16.
 */

public class Bill {



    public enum Type {Electricity, Gas};


    private final static double CO2KGPERGWH = 9000;
    private final static double CO2KGPERGJ = 56.1;
    private final float KG_PER_TREE_EMISSION = (float) 5.023;
    private boolean humanMode = false;



    private Type type = null;
    private double amount = 0;
    private Date startDate = null;
    private Date endDate = null;


    private int numberOfPeople = 0;

    private String electricityMetrics = "GWh";
    private String naturalGasMetric = "GJ";



    private long invoiceNumber = 0;



    public Bill() {}

    public Bill(Type type, double amount, Date startDate, Date endDate, long invoiceNumber, int numberOfPeople) {

        this.type = type;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.invoiceNumber = invoiceNumber;
        this.numberOfPeople = numberOfPeople;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setType(Type inputType) {
        this.type = inputType;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getType() {
        return type.toString();
    }


    public float getDailyShareForCO2() {
        if(getDatesDifference() != 0) {
            return getCO2Emission() / (getDatesDifference());
        }
        return 0;
    }



    public int getDatesDifference() {
        int difference=
                ((int)((endDate.getTime()/(24*60*60*1000))
                        -(int)(startDate.getTime()/(24*60*60*1000))));
        return difference;
    }

    public static int getDatesDifference(Date date1, Date date2) {
        int difference=
                ((int)((date2.getTime()/(24*60*60*1000))
                        -(int)(date1.getTime()/(24*60*60*1000))));
        return difference;
    }




    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(long invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public boolean isHumanMode() {
        return humanMode;
    }

    public void setHumanMode(boolean humanMode) {
        this.humanMode = humanMode;
    }

    public float getCO2Emission() {
        float emission;

        if(type.equals(Type.Electricity)) {
            emission = (float)(amount * CO2KGPERGWH) / numberOfPeople;
        } else {
            emission = (float)(amount * CO2KGPERGJ) / numberOfPeople;
        }

        if(humanMode) {
            emission /= KG_PER_TREE_EMISSION;

        }
        return emission;

    }



    public String getDescription() {
        String toReturn = getType() + " #" + getInvoiceNumber() + " Amount: " + getAmount() + " ";
        toReturn += type.equals(Type.Electricity) ? electricityMetrics : naturalGasMetric;
        return toReturn;

    }

}
