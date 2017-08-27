package com.vsevolod.carbontracker.Model;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import com.vsevolod.carbontracker.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Class to read and store data from CSV file.
 */

public class CSVReader {

    private Context context;
    private List<String> years = new ArrayList<>();
    private List<String> models = new ArrayList<>();
    private List<String> makes = new ArrayList<>();
    private List<Integer> cityMPG = new ArrayList<>();
    private List<Integer> highwayMPG = new ArrayList<>();
    private List<String> engineDisplacement = new ArrayList<>();
    private List<String> transmission = new ArrayList<>();
    private List<String> fuelType = new ArrayList<>();


    public CSVReader(Context context) {
        this.context = context;
    }

    public List<String> getYears() {
        return years;
    }

    public List<String> getModels() {
        HashSet<String> nonRepeat = new HashSet<>();
        List<String> nonRepeatModels = new ArrayList<>();
        for(String mod: models) {
            if(!nonRepeat.contains(mod)) {
                nonRepeatModels.add(mod);
                nonRepeat.add(mod);
            }
        }
        return nonRepeatModels;
    }

    public List<String> getMakes() {
        HashSet<String> nonRepeat = new HashSet<>();
        List<String> nonRepeatMakes = new ArrayList<>();
        for(String mod: makes) {
            if(!nonRepeat.contains(mod)) {
                nonRepeatMakes.add(mod);
                nonRepeat.add(mod);
            }
        }
        return nonRepeatMakes;
    }

    public List<Integer> getHighwayMPG() {
        return highwayMPG;
    }



    public void readTheFile() {

        InputStream is = context.getResources().openRawResource(R.raw.last_version);

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line = "";
        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                cityMPG.add(Integer.parseInt(tokens[0]));
                engineDisplacement.add(tokens[1]);
                fuelType.add(tokens[2]);
                highwayMPG.add(Integer.parseInt(tokens[3]));
                makes.add(tokens[4]);
                models.add(tokens[5]);
                transmission.add(tokens[6]);
                years.add(tokens[7]);

            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public List<String> getCurrentModels(String currentMake) {
        int startIndex = getStartIndex(currentMake);
        int endIndex = getEndIndex(startIndex, currentMake);

        HashSet<String> hashSet = new HashSet<>();
        List<String> currentOptions = new ArrayList<>();

        for (int i = startIndex; i <= endIndex; i++) {
            String optionAtCurrentIndex = models.get(i);
            if(!hashSet.contains(optionAtCurrentIndex)) {
                currentOptions.add(optionAtCurrentIndex);
                hashSet.add(optionAtCurrentIndex);
            }
        }
        Collections.sort(currentOptions);
        return currentOptions;
    }

    public List<String> getCurrentYears(String currentModel, String currentMake) {
        int startIndex = getStartIndex(currentMake);
        int endIndex = getEndIndex(startIndex, currentMake);

        HashSet<String> hashSet = new HashSet<>();
        List<String> currentOptions = new ArrayList<>();

        for (int i = startIndex; i <= endIndex; i++) {
            if(currentModel.equals(models.get(i))) {
                String yearAtCurrentIndex = years.get(i);
                if(!hashSet.contains(yearAtCurrentIndex)) {
                    currentOptions.add(yearAtCurrentIndex);
                    hashSet.add(yearAtCurrentIndex);
                }
            }
        }
        Collections.sort(currentOptions);
        return currentOptions;
    }

    public List<String> getCurrentEngineDisplacements(String currentModel, String currentMake, String currentYear) {

        int startIndex = getStartIndex(currentMake);
        int endIndex = getEndIndex(startIndex, currentMake);
        HashSet<String> hashSet = new HashSet<>();
        List<String> currentOptions = new ArrayList<>();
        for (int i = startIndex; i <= endIndex; i++) {
            if(currentModel.equals(models.get(i)) && currentYear.equals((years.get(i)))) {
                String engineDisplacementAtCurrentIndex = engineDisplacement.get(i);
                if(!hashSet.contains(engineDisplacementAtCurrentIndex)) {
                    currentOptions.add(engineDisplacementAtCurrentIndex);
                    hashSet.add(engineDisplacementAtCurrentIndex);
                }

            }
        }
        return currentOptions;
    }

    public List<String> getCurrentTransmissions(String currentModel, String currentMake, String currentYear,
                                                String currentEngineDisplacement) {

        int startIndex = getStartIndex(currentMake);
        int endIndex = getEndIndex(startIndex, currentMake);
        HashSet<String> hashSet = new HashSet<>();
        List<String> currentOptions = new ArrayList<>();
        for (int i = startIndex; i <= endIndex; i++) {
            if(currentModel.equals(models.get(i)) && currentYear.equals((years.get(i)))
                    && currentEngineDisplacement.equals(engineDisplacement.get(i))) {
                String transmissionAtCurrentIndex = transmission.get(i);
                if(!hashSet.contains(transmissionAtCurrentIndex)) {
                    currentOptions.add(transmissionAtCurrentIndex);
                    hashSet.add(transmissionAtCurrentIndex);
                }

            }
        }
        return currentOptions;

    }

    public String getFuelType(String currentModel, String currentMake, String currentYear,
                              String currentEngineDisplacement, String currentTransmission) {

        int startIndex = getStartIndex(currentMake);
        int endIndex = getEndIndex(startIndex, currentMake);
        String currentFuelType = null;

        for (int i = startIndex; i <= endIndex; i++) {
            if(currentModel.equals(models.get(i)) && currentYear.equals(years.get(i))
                    && currentEngineDisplacement.equals(engineDisplacement.get(i))
                    && currentTransmission.equals(transmission.get(i))) {
                currentFuelType = fuelType.get(i);
            }
        }

        return currentFuelType;



    }


    private int getStartIndex(String option) {
        int startIndex = 0;
        for (String optionAtIndex: makes) {
            if(optionAtIndex.equals(option)) {
                break;
            }
            startIndex++;
        }
        return startIndex;
    }


    private int getEndIndex(int startIndex, String option) {
        int endIndex = startIndex;
        int size = makes.size();
        for (int i = startIndex + 1; i < size; i++) {
            if(makes.get(i).equals(option)) {
                endIndex++;
            } else {
                break;
            }
        }
        return endIndex;
    }


    public Pair<Integer, Integer> getMPGS(String currentMake, String currentModel, String currentYear,
                                  String currentEngineDisplacement, String currentTransmission) {

        int startIndex = getStartIndex(currentMake);
        int endIndex = getEndIndex(startIndex, currentMake);
        int currentCityMpg = 0;
        int currentHighwayMpg = 0;

        for (int i = startIndex; i <= endIndex; i++) {
            if(currentMake.equals(makes.get(i)) && currentModel.equals(models.get(i))
                    && currentYear.equals(years.get(i))
                    && currentEngineDisplacement.equals(engineDisplacement.get(i))
                    && currentTransmission.equals(transmission.get(i))) {
                currentCityMpg = cityMPG.get(i);
                currentHighwayMpg = highwayMPG.get(i);
                break;
            }
        }
        return new Pair<>(currentCityMpg, currentHighwayMpg);
    }


}
