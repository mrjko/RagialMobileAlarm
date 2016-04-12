package com.mrjko.ragialmobilealarm;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

/**
 * Created by jko on 16-04-11.
 */
public class ParseCharacterURL {

    public static StringBuffer url = new StringBuffer();

    public ParseCharacterURL(){
        try {
            Document doc = Jsoup.connect(MainActivity.getDatabaseURL()).userAgent("Mozilla, Chrome").timeout(30000).get();
            Element link = doc.getElementsByClass("ragial").first();
            if (link == null) {
                url.delete(0, url.length());
                url.append("noshop");
            } else {
                String absHref = link.attr("abs:href");
                url.delete(0, url.length());
                url.append(absHref);
            }
        } catch (IOException io){
            System.out.println("Caught IO Exception in ParseCharacterURL.java");
        }
    }

}
