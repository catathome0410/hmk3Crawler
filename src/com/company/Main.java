package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import com.fasterxml.jackson.databind.*;


public class Main {

    public static void main(String[] args) {
        // write your code here
        DemoCrawler crawler = new DemoCrawler();

//        crawler.getAmazonProd(url);E
//        crawler.getAmazonProds("nikon d3400");
//        crawler.testProxy();
//        crawler.initProxy();
//        crawler.testProxy();

        List<String> queryStrs = new ArrayList<>();
        List<Double> bidList = new ArrayList<>();
        List<Integer> campaignIdList = new ArrayList<>();
        List<Integer> queryGroupIdList = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader("rawQuery3.txt"));
            String line;
            while ( (line = br.readLine()) != null) {
//                fw.write(line + System.getProperty("line.separator"));
                String[] words = line.split(",");
                if (words[0].trim() != null & words[0].trim().length() != 0) {
//                    System.out.println(words[0].trim());
                    queryStrs.add(words[0].trim());
                    bidList.add(Double.parseDouble(words[1].trim()));
                    campaignIdList.add(Integer.parseInt(words[2].trim()));
                    queryGroupIdList.add(Integer.parseInt(words[3].trim()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashSet<String> prod_asinSet = new HashSet<>();
        int adCount = 0;

//        crawler.getAmazonProds(queryStrs.get(0));

        for (int k = 0; k<queryStrs.size(); k++) {
            for (Integer pNum = 1; pNum <= 3; pNum++) {
                List<Ad> adList0 = crawler.getAmazonAds(pNum, queryStrs.get(k), bidList.get(k), campaignIdList.get(k), queryGroupIdList.get(k));

                ObjectMapper mapper = new ObjectMapper();
                try {
                    Writer fileWriter = new FileWriter("objectMap.json", true);
                    for (int i = 0; i < adList0.size(); i++) {
                        Ad curAd = adList0.get(i);

                        if (prod_asinSet.contains(curAd.data_asin)) continue;

                        curAd.adId = adCount++;
                        prod_asinSet.add(curAd.data_asin);
                        String objectStr = mapper.writeValueAsString(adList0.get(i));
                        fileWriter.write(objectStr);
                        fileWriter.write(System.getProperty("line.separator"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Stored ads for " + queryStrs.get(k));
        }

//        List<Ad> adList0 = crawler.getAmazonAds(1, "PlayStation 4 video games", bidList.get(0), campaignIdList.get(0), queryGroupIdList.get(0));

    }
}
