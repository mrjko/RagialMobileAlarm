package com.mrjko.ragialmobilealarm;

import android.content.ComponentName;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jko on 16-04-28.
 */
public class VendItems implements Subject{

    private ROItems vendItems;
    private ROItems alertItems;
    private List<Observer> observers = new ArrayList<>();

    public VendItems(ROItems items, MainActivity ma){
        this.vendItems = items;
        registerObserver(ma);
    }

    @Override
    public void notifyObservers() {
        for (int i = 0; i < this.observers.size(); i++){
            this.observers.get(i).update();
        }
    }

    @Override
    public void registerObserver(Observer o) {
        this.observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        this.observers.remove(o);
    }

    public void saveItems(){
        this.alertItems = this.vendItems;
    }

    public void updateItems(ROItems items){
        this.vendItems = items;
    }

    public void checkVendStatus(){
        if (alertItems.getSize() != vendItems.getSize()){
            alertItems = vendItems;
            notifyObservers();
        } else {
            return;
        }
    }

}
