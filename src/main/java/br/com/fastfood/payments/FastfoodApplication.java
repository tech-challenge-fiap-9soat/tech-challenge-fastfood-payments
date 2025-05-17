package br.com.fastfood.payments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import javax.annotation.processing.Generated;

@EnableFeignClients(basePackages = "br.com.fastfood.payments.gateways.client")
@SpringBootApplication
public class FastfoodApplication {

	public static void main(String[] args) {
		SpringApplication.run(FastfoodApplication.class, args);
	}

}
