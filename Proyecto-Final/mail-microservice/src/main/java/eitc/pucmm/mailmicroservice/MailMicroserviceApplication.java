package eitc.pucmm.mailmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MailMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MailMicroserviceApplication.class, args);

	}

}
