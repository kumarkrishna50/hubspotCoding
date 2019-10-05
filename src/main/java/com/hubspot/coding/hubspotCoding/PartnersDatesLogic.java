package com.hubspot.coding.hubspotCoding;



import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class PartnersDatesLogic {


    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public void getPartnersAvailableDates(PartnersDetailsPojo[] partnerDetails) throws  Exception{

        System.out.println("Inside the getPartnersAvailable dates.");

        HashMap<String, String> finalOutPutMap = new HashMap<>();

        // Now from the partners details array, Create a country hashset and add all the countries.

        HashSet<String> countries = new HashSet<>();

        for( int i =0; i<partnerDetails.length; i++){
            countries.add(partnerDetails[i].getCountry());
        }

        // Now iterate through each country and figure out the logic and add the dates and country.
        for(String country : countries){

            List<String> datesList = new ArrayList<>();

            for ( int i=0; i< partnerDetails.length;i++){

                if( partnerDetails[i].getCountry().equalsIgnoreCase(country)){

                    // now we have all the dates list.  in datesList.
                    for( int j=0; j<partnerDetails[i].getAvailableDates().size()-1;j++)

                    // convert the string into the dates and then compare the date = dates +1;
                    {
                        String startingDate = compareDates(partnerDetails[i].getAvailableDates().get(j), partnerDetails[i].getAvailableDates().get(j + 1));
                        if( startingDate != null){
                            datesList.add(startingDate);
                        }
                    }
                }
            }

            // check the duplicates strings in the dates list and add it's count to a map.

            HashMap<String, Integer> datesCountMap = new HashMap<>();

            for( String dateStr : datesList){

                if(datesCountMap.containsKey(dateStr)){
                    datesCountMap.put(dateStr, datesCountMap.get(dateStr) +1);
                }else{
                    datesCountMap.put(dateStr, 1);
                }
            }

          //  System.out.println("datesCountMap : "+datesCountMap);


            int maxDateCount = 0;

            for(String mapDateStr : datesCountMap.keySet()){

                if(datesCountMap.get(mapDateStr) > maxDateCount){
                    maxDateCount = datesCountMap.get(mapDateStr);
                }

            }

            List<String> similarCountDatesList = new ArrayList<>();



            for(String mapDateStr : datesCountMap.keySet()){

                if(datesCountMap.get(mapDateStr) == maxDateCount){
                    similarCountDatesList.add(mapDateStr);
                }

            }

            Calendar finalCal = Calendar.getInstance();
            Date finalDate = null;

            if(similarCountDatesList.size() >1) {
                for (int i = 0; i < similarCountDatesList.size() - 1; i++) {

                    Calendar cal1 = Calendar.getInstance();
                    cal1.setTime(dateFormat.parse(similarCountDatesList.get(i)));

                    Calendar cal2 = Calendar.getInstance();
                    cal2.setTime(dateFormat.parse(similarCountDatesList.get(i + 1)));

                    if (cal1.compareTo(cal2) < 0) {
                        finalCal.setTime(cal1.getTime());
                        finalDate = finalCal.getTime();
                    } else {
                        finalCal.setTime(cal2.getTime());
                        finalDate = finalCal.getTime();
                    }

                }
            }else{
                finalDate = dateFormat.parse(similarCountDatesList.get(0));
            }

            if(finalDate != null) {
                String finalString = dateFormat.format(finalDate);

                System.out.println("Country :" + country + " , finalString :" + finalString);
                finalOutPutMap.put(country, finalString);
            } else {
                finalOutPutMap.put(country, null);
            }

        }


        // Now i will have the countries and dates in the finalOutPutMap.

        JSONObject finalJsonObj = generatePostResponse(finalOutPutMap, partnerDetails, countries);

        PartnersApiController partnersApiController = new PartnersApiController();

        partnersApiController.postPartnersDetails(finalJsonObj);

        System.out.println("COMPLETED.");

    }



    public JSONObject generatePostResponse(HashMap<String, String> finalOutPutMap, PartnersDetailsPojo[] partnerDetails,  HashSet<String> countries) throws Exception{

        JSONObject jsonObj = new JSONObject();
        List<PartnersOutPutPojo> finalOutputsList = new ArrayList<>();
        JSONArray jsonFinalArray = new JSONArray();

        for(String country : countries) {

            int attendeeCount =0;
            HashSet<String> attendeesList = new HashSet<>();

            for (int i = 0; i < partnerDetails.length; i++) {


                if( partnerDetails[i].getCountry().equalsIgnoreCase(country)){

                   List<String> datesList =  partnerDetails[i].getAvailableDates();


                        if( datesList.contains(finalOutPutMap.get(country))){

                            Date currDate = dateFormat.parse(finalOutPutMap.get(country));

                            Calendar cal1 = Calendar.getInstance();
                            cal1.setTime(currDate);
                            cal1.add(Calendar.DATE,1);

                            Date nextDay = cal1.getTime();


                            String nextDayStr = dateFormat.format(nextDay);

                            if(datesList.contains(nextDayStr)){
                                attendeesList.add(partnerDetails[i].getEmail());
                                attendeeCount++;
                            }

                        }
                }
            }

            PartnersOutPutPojo outPut = new PartnersOutPutPojo();

            outPut.setStartDate(finalOutPutMap.get(country));
            outPut.setName(country);
            outPut.setAttendeeCount(attendeeCount);
            List<String> attendeesListArray = new ArrayList<>(attendeesList);
            outPut.setAttendees(attendeesListArray);

            finalOutputsList.add(outPut);

                JSONObject jsonDetails = new JSONObject();
                jsonDetails.put("attendeeCount", attendeeCount);
                jsonDetails.put("attendees", attendeesListArray);
                jsonDetails.put("name", country);
                jsonDetails.put("startDate", finalOutPutMap.get(country));

                jsonFinalArray.put(jsonDetails);

        }

        JSONObject responseDetails = new JSONObject();
        responseDetails.put("countries",jsonFinalArray);

        return responseDetails;
    }



    public String compareDates(String startDateStr, String endDateStr){


        Date startDate;
        Date endDate;
        String finalDate = null;
        try{
            startDate = dateFormat.parse(startDateStr);
            endDate = dateFormat.parse(endDateStr);

            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(startDate);
            cal1.add(Calendar.DATE,1);

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(endDate);


            if( cal1.equals(cal2)){
                finalDate = startDateStr;
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }

        return finalDate;
    }



}
