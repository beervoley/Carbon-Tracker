package com.vsevolod.carbontracker.Model;

import android.support.v7.widget.LinearLayoutCompat;

/**
 *
 */

public abstract class TransportationImpl implements Transportation {
    private String name;
    private boolean visible;
    private double emission;

    public TransportationImpl(String name, boolean visible){
        this.name = name;
        this.visible = visible;
    }

    public TransportationImpl(){ }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean getVisibility() {
        return visible;
    }

    @Override
    public void setVisibility(boolean visible) {
        this.visible = visible;
    }

}
