package com.vsevolod.carbontracker.Model;
import android.content.Context;
import com.vsevolod.carbontracker.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import static com.vsevolod.carbontracker.Model.Tips.TipType.ELECTRIC;
import static com.vsevolod.carbontracker.Model.Tips.TipType.GAS;
import static com.vsevolod.carbontracker.Model.Tips.TipType.TRANSPORT;
/**
 * Created by myharmonykitty on 2017-03-19.
 */
public class Tips {
    public enum TipType {
        ELECTRIC, GAS, TRANSPORT
    }
    private ArrayList<String> electricTips = new ArrayList<>();
    private ArrayList<String> gasTips = new ArrayList<>();
    private ArrayList<String> transportTips = new ArrayList<>();
    private ArrayList<String> recent = new ArrayList<>();
    private ArrayList<TipType> tipOrder = new ArrayList<>();
    private final int ELECTRIC_TIP_COUNT = 7;
    private final int GAS_TIP_COUNT = 7;
    private final int TRANSPORT_TIP_COUNT = 7;
    private final int REPEAT_CONSTRAINT = 7;
    public Tips(Context context) {
        initializeTips(context);
    }
    private void initializeTips(Context context) {
        String[] electric = context.getResources().getStringArray(R.array.electric_tips);
        String[] gas = context.getResources().getStringArray(R.array.gas_tips);
        String[] transport = context.getResources().getStringArray(R.array.transport_tips);
        for (int i = 0; i < ELECTRIC_TIP_COUNT; i++) {
            electricTips.add(electric[i]);
        }
        for (int i = 0; i < GAS_TIP_COUNT; i++) {
            gasTips.add(gas[i]);
        }
        for (int i = 0; i < TRANSPORT_TIP_COUNT; i++) {
            transportTips.add(transport[i]);
        }
        resetTipPriority(0, 0, 0);
    }

    public TipType getTopPriority() {
        return tipOrder.get(0);
    }
    public void resetTipPriority(float electric, float gas, float transport) {
        tipOrder.clear();
        if (electric >= gas && electric >= transport) {
            tipOrder.add(ELECTRIC);
            if (gas >= transport) {
                tipOrder.add(GAS);
                tipOrder.add(TRANSPORT);
            } else {
                tipOrder.add(TRANSPORT);
                tipOrder.add(GAS);
            }
        } else if (gas >= electric && gas >= transport) {
            tipOrder.add(GAS);
            if (electric >= transport) {
                tipOrder.add(ELECTRIC);
                tipOrder.add(TRANSPORT);
            } else {
                tipOrder.add(TRANSPORT);
                tipOrder.add(ELECTRIC);
            }
        } else {
            tipOrder.add(TRANSPORT);
            if (electric >= gas) {
                tipOrder.add(ELECTRIC);
                tipOrder.add(GAS);
            } else {
                tipOrder.add(GAS);
                tipOrder.add(ELECTRIC);
            }
        }
    }
    public String getPriorityTip() {
        String temp;
        if (tipOrder.get(0) == ELECTRIC) {
            temp = getElectricTip();
        } else if (tipOrder.get(0) == GAS) {
            temp = getGasTip();
        } else {
            temp = getTransportTip();
        }
        tipOrder.add(tipOrder.get(0));
        tipOrder.remove(0);
        return temp;
    }
    private boolean checkRepeat(String string) {
        boolean match = false;
        for (String s : recent) {
            if (s.equals(string)) {
                match = true;
            }
        }
        return match;
    }
    private void maintainRepeat(String string) {
        recent.add(string);
        if (recent.size() >= REPEAT_CONSTRAINT) {
            recent.remove(0);
        }
    }
    private String getTip(ArrayList<String> tipList, int count) {
        Random random = new Random();
        String temp = tipList.get(random.nextInt(count));
        while (checkRepeat(temp)) {
            temp = tipList.get(random.nextInt(count));
        }
        maintainRepeat(temp);
        return temp;
    }
    private String getElectricTip() {
        return getTip(electricTips, ELECTRIC_TIP_COUNT);
    }
    private String getGasTip() {
        return getTip(gasTips, GAS_TIP_COUNT);
    }
    private String getTransportTip() {
        return getTip(transportTips, TRANSPORT_TIP_COUNT);
    }
}