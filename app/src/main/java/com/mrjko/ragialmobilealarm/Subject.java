package com.mrjko.ragialmobilealarm;

/**
 * Created by jko on 16-04-28.
 */
public interface Subject {

    public void notifyObservers();
    public void registerObserver(Observer o);
    public void removeObserver(Observer o);
}
