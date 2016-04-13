package com.mrjko.ragialmobilealarm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jko on 16-04-11.
 */
public class ROItems {

    private List<ROItem> items;

    public ROItems() {
        items = new ArrayList<ROItem>();
    }

    public List<ROItem> getItems(){
        return this.items;
    }

    public int getTotalQuantity(){
        int counter = 0;
        for (int i =0 ; i< this.items.size(); i++){
            counter += this.items.get(i).getQuantityInteger();
        }
        return counter;
    }

    public int getSize(){
        return items.size();
    }

    public ROItem get(int i) {
        try {
            return items.get(i);
        } catch (IndexOutOfBoundsException io){
            return null;
        }
    }

    public void addItem(ROItem i){
        this.items.add(i);
        return;
    }


}
