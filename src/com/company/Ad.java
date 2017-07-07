package com.company;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vagrant on 7/5/17.
 */
public class Ad implements Serializable {

    private static final long serialVersionUID = 1L;
    public int adId;
    public String data_asin;
    public String query;
    public String category;
    public double price;
    public int campaignId;
    public double relevanceScore;
    public double pClick;
    public double bidPrice;
    public double rankScore;
    public double qualityScore;
    public double costPerClick;
    public int position;
    public String title;
    public String thumbnail;
    public String description;
    public String brand;
    public String detail_url;
    public int query_group_id;


    public List<String> keyWords;
    public Ad() {
        keyWords = new ArrayList<String>();
        relevanceScore = 0.0;
        rankScore = 0.0;
        pClick = 0.0;
        qualityScore = 0.0;
        costPerClick = 0.0;
        position = 0;
    }
}
