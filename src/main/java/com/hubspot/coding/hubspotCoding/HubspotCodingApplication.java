package com.hubspot.coding.hubspotCoding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.rmi.server.ExportException;

@SpringBootApplication
public class HubspotCodingApplication {

	public static void main(String[] args) throws Exception{

		System.out.println("Inside the API Application.");
		SpringApplication.run(HubspotCodingApplication.class, args);

		HubspotCodingApplication hubspotCoding = new HubspotCodingApplication();
		hubspotCoding.initateApiCallProcess();


	}


	public void initateApiCallProcess() throws Exception {

		PartnersApiController apiController = new PartnersApiController();

		try{

			apiController.getPartnersDetails();

		}catch (ExportException ex){
			ex.printStackTrace();
		}

	}

}
