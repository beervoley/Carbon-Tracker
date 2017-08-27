package com.vsevolod.carbontracker.Model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.vsevolod.carbontracker.R;
import com.vsevolod.carbontracker.UI.AddBillActivity;
import com.vsevolod.carbontracker.UI.JourneyList;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by myharmonykitty on 2017-04-04.
 */

public class Notifications {
    private String[] journeyNotifications;
    private String[] billNotifications;
    private String billTypeElectric;
    private String billTypeGas;
    private AlarmManager alarmManager;

    public Notifications(Context context) {
        initializeNotifications(context);
    }

    public void runNotifications(Singleton singleton, Context context) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }


    public Intent makeNotificationIntent(Singleton singleton, Context context) {
        Intent intent;
        Tips.TipType note = determineNotification(singleton);
        if(note == Tips.TipType.TRANSPORT) {
            intent = JourneyList.generateJourneyListIntent(context);
        } else {
            intent = AddBillActivity.generateIntent(context);
        }
        return intent;
    }

    public String getNotification(Singleton singleton) {
        Tips.TipType select = determineNotification(singleton);
        System.out.println("Electric days: " + daysSinceBill(singleton, true) + " Gas days: " + daysSinceBill(singleton, false));
        if(select == Tips.TipType.TRANSPORT) {
            return getJourneyNotification(singleton);
        }
        else if(select == Tips.TipType.ELECTRIC) {
            return getBillNotification(singleton, true);
        }
        else {
            return getBillNotification(singleton, false);
        }
    }

    public Tips.TipType determineNotification(Singleton singleton) {
        int numJourneys = singleton.getJourneyCollection().countJourney();
        int electricDays = daysSinceBill(singleton, true);
        int gasDays = daysSinceBill(singleton, false);
        if(numJourneys < 1) {
            return Tips.TipType.TRANSPORT;
        }
        else if(electricDays == 0) {
            if(gasDays == 0) {
                if(daysSinceBill(singleton, true) > daysSinceBill(singleton, false)) {
                    return Tips.TipType.ELECTRIC;
                }
                else {
                    return Tips.TipType.GAS;
                }
            }
            return Tips.TipType.ELECTRIC;
        }
        else if(gasDays == 0) {
            return Tips.TipType.GAS;
        }
        else return Tips.TipType.TRANSPORT;
    }

    private void initializeNotifications(Context context) {
        journeyNotifications = context.getResources().getStringArray(R.array.journey_notifications);
        billNotifications = context.getResources().getStringArray(R.array.bill_notifications);
        billTypeElectric = context.getResources().getString(R.string.electric_type);
        billTypeGas = context.getResources().getString(R.string.gas_type);
        alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
    }

    public String getJourneyNotification(Singleton singleton) {
        Date today = new Date();
        List<Journey> journeys = singleton.getJourneyCollection().getJourneysForADay(today);
        if(journeys == null && journeys.size() == 0) {
            return "You have no Journeys";
        }
        int journeysToday = journeys.size();
        String note = journeyNotifications[0] + journeysToday + journeyNotifications[1];
        return note;
    }

    private int daysSinceBill(Singleton singleton, boolean electric) {
        Date today = new Date();
        Date billDate = null;
        if(electric) {
            if(singleton.getBillCollection().getClosestElectricityBill(today) != null) {
                billDate = singleton.getBillCollection().getClosestElectricityBill(today).getStartDate();
            }
        } else {
            if(singleton.getBillCollection().getClosestGasBill(today) != null) {
                billDate = singleton.getBillCollection().getClosestGasBill(today).getStartDate();
            }
        }
        if(billDate == null) {
            return -1;
        }
        else {
            System.out.println(billDate.compareTo(today));
            return billDate.compareTo(today);
        }
    }

    public String getBillNotification(Singleton singleton, boolean electric) {
        String billType;
        int days;
        if(electric) {
            billType = billTypeElectric;
            days = daysSinceBill(singleton, true);
        }
        else {
            billType = billTypeGas;
            days = daysSinceBill(singleton, false);
        }
        if(billNotifications.length >= 3) {

            String note = billNotifications[0] + days + billNotifications[1] + billType + billNotifications[2];
            return note;
        }
        return "I DON'T KNOW";
    }
}
