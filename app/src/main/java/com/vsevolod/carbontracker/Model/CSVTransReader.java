package com.vsevolod.carbontracker.Model;

import android.content.Context;

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
 * Class read and store data from skytrain CSV file.
 * Store all skyTrain Stops.
 */

public class CSVTransReader {

    private Context context;
    private List<String> transportation = new ArrayList<>();
    private List<String> startingStations = new ArrayList<>();
    private List<String> destinationStation = new ArrayList<>();

    public CSVTransReader(Context context){
        this.context = context;
    }

    public List<String> getSkytrains(){
        HashSet<String> nonRepeat = new HashSet<>();
        List<String> nonRepeatTrans = new ArrayList<>();
        for(String trans : transportation){
            if(!nonRepeat.contains(trans)){
                nonRepeatTrans.add(trans);
                nonRepeat.add(trans);
            }
        }
        return nonRepeatTrans;
    }

    public void readTheFile(int id){
        InputStream inputStream = context.getResources().openRawResource(id);

        BufferedReader reader = new BufferedReader(new InputStreamReader
                (inputStream, Charset.forName("UTF-8")));

        String line = "";
        try{
            while((line = reader.readLine()) != null){
                String[] tokens = line.split(",");
                transportation.add(tokens[0]);
                startingStations.add(tokens[1]);
                //destinationStation.add(tokens[1]);
            }
            reader.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public List<String> getCurrentStation(String currentSkytrain){
        int startIndex = getStartIndex(currentSkytrain);
        int endIndex = getEndIndex(startIndex,currentSkytrain);

        HashSet<String> hashSet = new HashSet<>();
        List<String> currentOptions = new ArrayList<>();

        for(int i = startIndex; i <= endIndex; i++){
            String optionAtCurrentIndex = startingStations.get(i);
            if(!hashSet.contains(optionAtCurrentIndex)){
                currentOptions.add(optionAtCurrentIndex);
                hashSet.add(optionAtCurrentIndex);
            }
        }
        return currentOptions;
    }

    private int getStartIndex(String option){
        int startIndex = 0;
        for(String choice: transportation){
            if(choice.equals(option)){
                break;
            }
            startIndex++;
        }
        return startIndex;
    }

    private int getEndIndex(int startIndex, String option){
        int endIndex = startIndex;
        int size = transportation.size();
        for(int i = startIndex + 1; i < size; i++){
            if(transportation.get(i).equals(option)){
                endIndex++;
            }else{
                break;
            }
        }
        return endIndex;
    }

}
