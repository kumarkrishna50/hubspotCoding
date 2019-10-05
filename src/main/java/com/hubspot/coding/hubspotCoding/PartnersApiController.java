package com.hubspot.coding.hubspotCoding;

import org.json.JSONArray;
import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PartnersApiController {


    @GetMapping("getclientsdata")
    public void getPartnersDetails() throws Exception {

        System.out.println("Inside Get Partners Details.");

        URL url = new URL("https://candidate.hubteam.com/candidateTest/v3/problem/dataset?userKey=c7008e2d0423972a23bbc2a7c095");
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.connect();

        int responseCode = httpConn.getResponseCode();

        if( responseCode == 200){

            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;

            StringBuffer sb = new StringBuffer();

            // write the response to the string.
            while((line = br.readLine()) != null){
                sb.append(line);
            }
            br.close();


            JSONObject jsonObj = new JSONObject(sb.toString());

            JSONArray jsonArray = jsonObj.getJSONArray("partners");


            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(jsonObj);

         //   System.out.println(jsonObj);


            // deserialize the jsonArray object.
            ObjectMapper objMapper = new ObjectMapper();

            PartnersDetailsPojo[] partnersDetails = objMapper.readValue(jsonArray.toString(), PartnersDetailsPojo[].class);

            // now use the partners details to find out the available dates.
            PartnersDatesLogic partnersDatesLogic = new PartnersDatesLogic();

            partnersDatesLogic.getPartnersAvailableDates(partnersDetails);

        }

    }


//    @PostMapping("postclientsdata")
    public void postPartnersDetails(JSONObject jsonObj) throws Exception {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(jsonObj);

        System.out.println("String : "+json);


        URL url = new URL("https://candidate.hubteam.com/candidateTest/v3/problem/result?userKey=c7008e2d0423972a23bbc2a7c095");

        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("Content-Type", "application/json");
        httpConn.setDoInput(true);
        httpConn.setDoOutput(true);


        OutputStream outputStream = httpConn.getOutputStream();


        outputStream.write(jsonObj.toString().getBytes());

        outputStream.flush();


        System.out.println("Response :"+httpConn.getResponseCode());


        if( httpConn.getResponseCode() == 200){
            System.out.println("All ok.");
        }

    }

}
