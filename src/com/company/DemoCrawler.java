package com.company; /**
 * Created by vagrant on 6/24/17.
 */

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.apache.lucene.analysis.*;

import java.io.IOException;
import java.io.StringReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DemoCrawler {

    private static final String AMAZON_QUERY_URL = "http://www.amazon.com/s/ref=nb_sb_noss?field-keywords=";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36";
    private final String authUser = "bittiger";
    private final String authPassword = "cs504";

    public void getAmazonProd(String url) {

        try {
            Document doc = Jsoup.connect(url).maxBodySize(0).timeout(1000).get();
//            String fulltext =  doc.text();
            String title = doc.title();
//
//            System.out.println(fulltext);
//            System.out.println(title);
//
//            System.out.println(fulltext.length());

            Element titleEle = doc.getElementById("productTitle");
            String titleStr = titleEle.text().trim();
            String bodyStr = doc.body().text();
            System.out.println("title: " + titleStr);
//            System.out.println("body: " + bodyStr);
//            System.out.println("body length is: " + bodyStr.length());
//
            Element priceEle = doc.getElementById("priceblock_ourprice");
            String priceStr = priceEle.text().trim();
            System.out.println("price: " + priceStr);
//
            Elements reviews = doc.getElementsByClass("a-list-item");
            System.out.println(reviews.size());
            for (Element review : reviews) {
                System.out.println("review content: " + review.text() );
            }

//
        } catch (IOException e) {
            e.printStackTrace();
            // log
        }
    }

    public void getAmazonProds(String query) {
        String url = AMAZON_QUERY_URL + query;
//          String url = "http://www.espn.com/";
        try {
            HashMap<String,String> headers = new HashMap<String,String>();
            headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            headers.put("Accept-Encoding", "gzip, deflate");
            headers.put("Accept-Language", "en-US,en;q=0.8");
            Document doc = Jsoup.connect(url).headers(headers).userAgent(USER_AGENT).timeout(10000).get();

            String docBody = doc.body().text();
            System.out.println(docBody);
            System.out.println("body size: " + docBody.length());
            System.out.println();

            Elements prods = doc.getElementsByClass("s-result-item celwidget ");
            System.out.println("number of prod: " + prods.size());
            System.out.println();

            Elements brands = doc.select("#leftNavContainer > ul:nth-child(16) > div > li");
            List<String> brandsArray = new ArrayList<>();
            for (Element ele : brands) {
                brandsArray.add(ele.text());
            }

            for (Integer i = 0; i<prods.size(); i++) {
                String id = "result_" + i.toString();
                Element prodsById = doc.getElementById(id);
//                System.out.println("id: " + id);

                String asin = prodsById.attr("data-asin");
                System.out.println("prod asin: " + asin);

                Elements titleEleList = prodsById.getElementsByAttribute("title");
                Element titleEle = titleEleList.first();
                System.out.println("prod title: " + titleEle.attr("title"));

                //#result_0 > div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(4) > div.a-column.a-span7 > div:nth-child(1) > div:nth-child(3) > a > span.a-color-base.sx-zero-spacing > span > sup.sx-price-fractional
                Elements priceEle = prodsById.getElementsByClass("sx-price-whole");
                Elements priceEle2 = prodsById.getElementsByClass("sx-price-fractional");
                if (priceEle.size() > 0 && priceEle2.size() > 0) {
                    System.out.println("prod price: " + priceEle.first().text() + "." + priceEle2.first().text() );
                }

                //#result_0 > div > div > div > div.a-fixed-left-grid-col.a-col-left > div > div > a > img
                Elements imageEle = prodsById.select("div > div > div > div.a-fixed-left-grid-col.a-col-left > div > div > a > img");
                System.out.println("image Url: " + imageEle.attr("src"));

                //#leftNavContainer > ul:nth-child(2) > div > li:nth-child(1) > span > a > h4
                Elements category = doc.select("#leftNavContainer > ul:nth-child(2) > div > li:nth-child(1) > span > a > h4");
                System.out.println("category: " + category.first().text());

                //.href
                Elements detailUrlEle = prodsById.getElementsByClass("a-link-normal s-access-detail-page  s-color-twister-title-link a-text-normal");
                String detailUrl = detailUrlEle.attr("href");
                if (detailUrl.contains("https://www.amazon.com")) {
                    System.out.println("detail Url: " + detailUrl);
                } else {
                    System.out.println("detail Url: " + "https://www.amazon.com" + detailUrl);
                }

                for (int j = 0; j<brandsArray.size(); j++) {
                    if (titleEle.text().contains(brandsArray.get(j))) {
                        System.out.println("brand: " + brandsArray.get(j));
                    }
                }

                System.out.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    List<Ad> getAmazonAds(Integer pageNum, String query, double bid, int campaignID, int queryGroupId) {
        String url = AMAZON_QUERY_URL + query + "&page=" + pageNum.toString();
        List<Ad> res = new ArrayList<>();

        try {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            headers.put("Accept-Encoding", "gzip, deflate");
            headers.put("Accept-Language", "en-US,en;q=0.8");
            Document doc = Jsoup.connect(url).headers(headers).userAgent(USER_AGENT).timeout(10000).get();

            Elements prods = doc.getElementsByClass("s-result-item celwidget ");

            Elements brands = doc.select("#leftNavContainer > ul:nth-child(16) > div > li");
            List<String> brandsArray = new ArrayList<>();
            for (Element ele : brands) {
                brandsArray.add(ele.text());
            }

            for (Integer i = 0; i < prods.size(); i++) {
                Ad prodAd = new Ad();
                prodAd.query = query;
                prodAd.bidPrice = bid;
                prodAd.campaignId = campaignID;
                prodAd.query_group_id = queryGroupId;

                String id = "result_" + i.toString();
                Element prodsById = doc.getElementById(id);

                if (prodsById == null) {
                    System.out.println(query + " ------> " + id + " is null!");
                    continue;
                }

                prodAd.data_asin = prodsById.attr("data-asin");

                Elements price0 = prodsById.getElementsByClass("sx-price-whole");
                Elements price1 = prodsById.getElementsByClass("sx-price-fractional");
                if (price0.size() > 0  && price1.size() > 0) {
                    prodAd.price = Double.parseDouble(price0.first().text().replaceAll(",","") + "." + price1.first().text());
                }

                Elements detailUrlEle = prodsById.getElementsByClass("a-link-normal s-access-detail-page  s-color-twister-title-link a-text-normal");
                String detailUrl = detailUrlEle.attr("href");
                if (detailUrl.contains("https://www.amazon.com")) {
                    prodAd.detail_url = detailUrl;
                } else {
                    prodAd.detail_url = "https://www.amazon.com" + detailUrl;
                }

                Elements imageEle = prodsById.select("div > div > div > div.a-fixed-left-grid-col.a-col-left > div > div > a > img");
                prodAd.thumbnail =  imageEle.attr("src");

                Elements category = doc.select("#leftNavContainer > ul:nth-child(2) > div > li:nth-child(1) > span > a > h4");
                prodAd.category =  category.first().text();

                Elements titleEle = prodsById.getElementsByAttribute("title");
                prodAd.title = titleEle.attr("title");

                for (int j = 0; j<brandsArray.size(); j++) {
                    if (titleEle.text().contains(brandsArray.get(j))) {
                        prodAd.brand =  brandsArray.get(j);
                    }
                }

                Analyzer analyzer = new StandardAnalyzer();
                try {
                    TokenStream stream = analyzer.tokenStream(null, new StringReader(titleEle.attr("title")));
                    stream.reset();
                    while (stream.incrementToken()) {
                        prodAd.keyWords.add(stream.getAttribute(CharTermAttribute.class).toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                res.add(prodAd);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

    public void initProxy() {
        System.setProperty("socksProxyHost", "199.101.97.161");
        System.setProperty("socksProxyPort", "61336");

        Authenticator.setDefault(
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(authUser, authPassword.toCharArray());
                    }
                }
        );
    }

    public void testProxy() {
        String test_url = "http://www.toolsvoid.com/what-is-my-ip-address";

        try {
            HashMap<String, String > headers = new HashMap<>();
            headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            headers.put("Accept-Encoding","gzip,deflate,br");
            headers.put("Accept-Language","en-US,en;q=0.8");
            Document doc = Jsoup.connect(test_url).headers(headers).userAgent(USER_AGENT).timeout(10000).get();

            //body > section.articles-section > div > div > div > div.col-md-8.display-flex > div > div.table-responsive > table > tbody > tr:nth-child(1) > td:nth-child(2) > strong
            String iP = doc.select("body > section.articles-section > div > div > div > div.col-md-8.display-flex > div > div.table-responsive > table > tbody > tr:nth-child(1) > td:nth-child(2) > strong").first().text();
            System.out.println("iP address: " + iP);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
