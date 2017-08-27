package com.vsevolod.carbontracker.Model;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vsevolod on 2017-03-16.
 */

public class BillCollection {


    private List<Bill> listOfBills = new ArrayList<>();
    private float CO2GasForLast365Days;
    private float CO2ElectricityForLast365Days;



    public BillCollection() {
    }


    public void setHumanMode(boolean humanMode) {
        for(Bill bill: listOfBills) {
            bill.setHumanMode(humanMode);
        }
    }
    public void addBill(Bill bill) {
        listOfBills.add(bill);
    }

    public void addBillOnIndex(Bill bill, int index) {
        validateIndexWithException(index);
        listOfBills.remove(index);
        listOfBills.add(index, bill);
    }

    public void removeBill(int index) {
        validateIndexWithException(index);
        listOfBills.remove(index);
    }

    private void validateIndexWithException(int index) {
        if(index < 0 || index >= countBills()){
            throw new IllegalArgumentException();
        }
    }

    private int countBills() {
        return listOfBills.size();
    }


    public List<String> getBillsDescription() {
        List<String> descriptions = new ArrayList<>();
        for(Bill bill: listOfBills) {
            descriptions.add(bill.getDescription());
        }

        return descriptions;

    }

    public Bill getBillAtIndex(int index) {
        validateIndexWithException(index);
        return listOfBills.get(index);
    }

    public List<Bill> getBillList() {
        return listOfBills;
    }


    public Bill getClosestElectricityBill(Date date) {
        if(listOfBills.isEmpty()) {
            return null;
        }
        Bill toReturn = null;
        for(Bill bill: listOfBills)
        {   if(bill.getType().equals(Bill.Type.Electricity.toString())) {
                Date startDate = bill.getStartDate();
                Date endDate = bill.getEndDate();

                if (date.after(startDate) && date.before(endDate)) {
                    System.out.println("1nd loop");
                    return bill;
                }
            }
        }



        if(listOfBills.size() < 2 && listOfBills.get(0).getStartDate().before(date) &&
                listOfBills.get(0).getType().equals(Bill.Type.Electricity.toString())) {
            return listOfBills.get(0);
        }

        int dayDifference = Integer.MAX_VALUE;
        if(listOfBills.get(0).getEndDate().before(date) &&
                listOfBills.get(0).getType().equals(Bill.Type.Electricity.toString())) {
            System.out.println("2nd loop");
            dayDifference = Bill.getDatesDifference(listOfBills.get(0).getEndDate(), date);
            toReturn = listOfBills.get(0);
        }

        for (int i = 1; i < listOfBills.size(); i++ ) {
            Date currentDate = listOfBills.get(i).getEndDate();

            if(currentDate.before(date) && listOfBills.get(i).getType().equals(Bill.Type.Electricity.toString())) {
                if(Bill.getDatesDifference(currentDate, date) < dayDifference) {
                    System.out.println("3nd loop");
                    dayDifference = Bill.getDatesDifference(currentDate, date);
                    toReturn = listOfBills.get(i);
                }
            }
        }

        return toReturn;
    }

    public Bill getClosestGasBill(Date date) {
        if(listOfBills.isEmpty()) {
            return null;
        }
        Bill toReturn = null;
        for(Bill bill: listOfBills)
        {   if(bill.getType().equals(Bill.Type.Gas.toString())) {
            Date startDate = bill.getStartDate();
            Date endDate = bill.getEndDate();

            if (date.after(startDate) && date.before(endDate)) {
                System.out.println("1nd loop");
                return bill;
            }
        }
        }



        if(listOfBills.size() < 2 && listOfBills.get(0).getStartDate().before(date) &&
                listOfBills.get(0).getType().equals(Bill.Type.Gas.toString())) {
            return listOfBills.get(0);
        }

        int dayDifference = Integer.MAX_VALUE;
        if(listOfBills.get(0).getEndDate().before(date) &&
                listOfBills.get(0).getType().equals(Bill.Type.Gas.toString())) {
            System.out.println("2nd loop");
            dayDifference = Bill.getDatesDifference(listOfBills.get(0).getEndDate(), date);
            toReturn = listOfBills.get(0);
        }

        for (int i = 1; i < listOfBills.size(); i++ ) {
            Date currentDate = listOfBills.get(i).getEndDate();

            if(currentDate.before(date) && listOfBills.get(i).getType().equals(Bill.Type.Gas.toString())) {
                if(Bill.getDatesDifference(currentDate, date) < dayDifference) {
                    System.out.println("3nd loop");
                    dayDifference = Bill.getDatesDifference(currentDate, date);
                    toReturn = listOfBills.get(i);
                }
            }
        }

        return toReturn;
    }

    public float getAverageElectricityCO2Emissions() {
        float toReturn = 0;
        int counter = 0;
        for(Bill bill: listOfBills) {
            if(bill.getType().equals(Bill.Type.Electricity.toString())) {
                toReturn += bill.getCO2Emission();
                counter++;
            }
        }
        if(counter != 0) {
            return toReturn / counter;
        }
        return 0;
    }

    public float getAverageGasCO2Emissions() {
        float toReturn = 0;
        int counter = 0;
        for(Bill bill: listOfBills) {
            if(bill.getType().equals(Bill.Type.Gas.toString())) {
                toReturn += bill.getCO2Emission();
                counter++;
            }
        }
        if(counter != 0) {
            return toReturn / counter;
        }
        return 0;
    }


    public float getCO2ElectricityForLast28Days() {
        Date today = new Date();
        float toReturn = 0;

        for(Bill currentBill: listOfBills) {
            if(currentBill.getType().equals(Bill.Type.Electricity.toString())) {
                if (currentBill.getEndDate().before(today) && (Bill.getDatesDifference(currentBill.getEndDate(), today) < 28)) {
                    toReturn += currentBill.getDailyShareForCO2() * 28;
                    System.out.println(28 - Bill.getDatesDifference(currentBill.getEndDate(), today));
                } else if (currentBill.getStartDate().before(today) && currentBill.getEndDate().after(today)) {
                    toReturn += currentBill.getDailyShareForCO2() * (Bill.getDatesDifference(currentBill.getStartDate(), today));
                    System.out.println(Bill.getDatesDifference(currentBill.getStartDate(), today));
                }
            }

        }
        return toReturn;
    }

    public float getCO2GasForLast28Days() {
            Date today = new Date();
            float toReturn = 0;

            for(Bill currentBill: listOfBills) {
                if(currentBill.getType().equals(Bill.Type.Gas.toString())) {
                    if (currentBill.getEndDate().before(today) && (Bill.getDatesDifference(currentBill.getEndDate(), today) < 28)) {
                        toReturn += currentBill.getDailyShareForCO2() * (28 - Bill.getDatesDifference(currentBill.getEndDate(), today));
                        System.out.println(28 - Bill.getDatesDifference(currentBill.getEndDate(), today));
                    } else if (currentBill.getStartDate().before(today) && currentBill.getEndDate().after(today)) {
                        toReturn += currentBill.getDailyShareForCO2() * (Bill.getDatesDifference(currentBill.getStartDate(), today));
                        System.out.println(Bill.getDatesDifference(currentBill.getStartDate(), today));
                    }
                }

            }

            return toReturn;

    }

    public float getCO2GasForLast365Days() {
        Date today = new Date();
        float toReturn = 0;

        for(Bill currentBill: listOfBills) {
            if(currentBill.getType().equals(Bill.Type.Gas.toString())) {
                if (currentBill.getEndDate().before(today) && (Bill.getDatesDifference(currentBill.getEndDate(), today) < 365)) {
                    toReturn += currentBill.getDailyShareForCO2() * (365 - Bill.getDatesDifference(currentBill.getEndDate(), today));
                    System.out.println(365 - Bill.getDatesDifference(currentBill.getEndDate(), today));
                } else if (currentBill.getStartDate().before(today) && currentBill.getEndDate().after(today)) {
                    toReturn += currentBill.getDailyShareForCO2() * (Bill.getDatesDifference(currentBill.getStartDate(), today));
                    System.out.println(Bill.getDatesDifference(currentBill.getStartDate(), today));
                }
            }

        }
        System.out.println(toReturn);

        return toReturn;

    }


    public float getCO2ElectricityForLast365Days() {
        Date today = new Date();
        float toReturn = 0;

        for(Bill currentBill: listOfBills) {
            if(currentBill.getType().equals(Bill.Type.Electricity.toString())) {
                if (currentBill.getEndDate().before(today) && (Bill.getDatesDifference(currentBill.getEndDate(), today) < 365)) {
                    toReturn += currentBill.getDailyShareForCO2() * (365 - Bill.getDatesDifference(currentBill.getEndDate(), today));
                    System.out.println(365 - Bill.getDatesDifference(currentBill.getEndDate(), today));
                } else if (currentBill.getStartDate().before(today) && currentBill.getEndDate().after(today)) {
                    toReturn += currentBill.getDailyShareForCO2() * (Bill.getDatesDifference(currentBill.getStartDate(), today));
                    System.out.println(Bill.getDatesDifference(currentBill.getStartDate(), today));
                }
            }

        }
        System.out.println(toReturn);

        return toReturn;
    }

    public List<Bill> getGasBills() {

        List<Bill> gasBills = new ArrayList<>();


        for (Bill currentBill: listOfBills) {
            if(currentBill.getType().equals("Gas")) {
                gasBills.add(currentBill);
            }
        }
        return gasBills;
    }

    public List<Bill> getElectricityBills() {

        List<Bill> gasBills = new ArrayList<>();


        for (Bill currentBill: listOfBills) {
            if(currentBill.getType().equals("Electricity")) {
                gasBills.add(currentBill);
            }
        }
        return gasBills;
    }
}
