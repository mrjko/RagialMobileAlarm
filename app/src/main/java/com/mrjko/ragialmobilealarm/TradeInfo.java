package com.mrjko.ragialmobilealarm;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jko on 16-04-12.
 */
public class TradeInfo extends Activity {

    private ListView listView;
    private TextView totalIncome;
    public TradeInfo(){

    }

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.trade_info);
        listView = (ListView) findViewById(R.id.soldItems);
        ArrayAdapter<String> arrayAdapter = getSoldInfo();
        listView.setAdapter(arrayAdapter);
        totalIncome = (TextView) findViewById(R.id.totalIncome);
        if (MainActivity.itemsSold.getSize() != 0) {
            totalIncome.setText(calculateTotalIncome(MainActivity.itemsSold));
        }
    }

    private String calculateTotalIncome(ROItems itemsSold) {
        int counter = 0;
        for (int i = 0; i < itemsSold.getSize(); i++){
            counter += itemsSold.get(i).getQuantityInteger() * itemsSold.get(i).getPriceInteger();
        }
        return convertToZeny(counter);
    }

    public String convertToZeny(int amt){
        String str = Integer.toString(amt);
        String result = "z";
        int j = 0;
        for (int i= str.length(); i > 0; i--){
            if (j == 3){
                result = str.charAt(i) + "," + result;
                j++;
            } else {
                result = str.charAt(i) + result;
                j++;
            }
        }
        return result;
    }

    private ArrayAdapter<String> getSoldInfo() {
        ROItems soldItems = MainActivity.itemsSold;
        ArrayList<String> itemNames = new ArrayList<String>();
        for (int i = 0; i < soldItems.getSize(); i++) {
            itemNames.add(soldItems.get(i).getName()
                    + "\n" + soldItems.get(i).getPrice() + " - " + soldItems.get(i).getQuantity());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemNames);
        return arrayAdapter;
    }
}
