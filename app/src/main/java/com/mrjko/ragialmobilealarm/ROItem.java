package com.mrjko.ragialmobilealarm;

/**
 * Created by jko on 16-02-17.
 */
public class ROItem {
    private String name;
    private String price;
    private String quantity;
    private String link;

    public ROItem(String n, String p, String q, String l){
        this.name = n;
        this.price = p;
        this.quantity = q;
        this.link = l;
    }

    public String getName(){
        return this.name;
    }

    public String getPrice(){
        return this.price;
    }

    public String getQuantity(){
        return this.quantity;
    }

    public String getLink(){
        return this.link;
    }

    public int getQuantityInteger(){
        String qty = this.quantity;
        String result = "";
        for (int i = 0; i < qty.length(); i++){
            char c = qty.charAt(i);
            if (c != ',' && c != 'x'){
                result = result + c;
            }
        }
        return Integer.parseInt(result);
    }

}
