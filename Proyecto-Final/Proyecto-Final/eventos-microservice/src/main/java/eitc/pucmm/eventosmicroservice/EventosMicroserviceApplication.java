package eitc.pucmm.eventosmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class EventosMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventosMicroserviceApplication.class, args);
	}

}
