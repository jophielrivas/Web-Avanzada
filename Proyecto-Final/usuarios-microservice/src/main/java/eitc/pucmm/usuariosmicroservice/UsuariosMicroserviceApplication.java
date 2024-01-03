package eitc.pucmm.usuariosmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class UsuariosMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsuariosMicroserviceApplication.class, args);
	}

}
