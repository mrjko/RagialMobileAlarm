package com.mrjko.ragialmobilealarm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.logging.*;

/**
 * Created by jko on 16-04-11.
 */
public class ParseVendingItems {

    public static StringBuffer url = new StringBuffer();
    private ROItems itemList = new ROItems();

    public ParseVendingItems(){
        try {
            String url = ParseCharacterURL.url.toString();
            if (url.equals("noshop")){
                return;
            }
            Document doc = Jsoup.connect(url).userAgent("Mozilla, Chrome").timeout(300000).get();
            Elements prices = doc.getElementsByClass("price");
            Elements amounts = doc.getElementsByClass("amt");
            Elements names = doc.getElementsByClass("name");

            for (int i =0 ; i < names.size(); i++){
                // need to find a better way of getting ahref, NOT using substrings...
                Element link = names.get(i);
                String absHref= link.toString().substring(26, 73);
                ROItem item = new ROItem(names.get(i).text(), prices.get(i).text(), amounts.get(i).text(), absHref);
                this.itemList.getItems().add(item);
            }

        } catch (IOException ex) {
            Logger.getLogger(ParseVendingItems.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public ROItems getItemList(){
        return this.itemList;
    }

}
