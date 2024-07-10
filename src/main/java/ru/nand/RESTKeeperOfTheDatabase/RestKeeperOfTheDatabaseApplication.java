package ru.nand.RESTKeeperOfTheDatabase;

import org.modelmapper.ModelMapper;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RestKeeperOfTheDatabaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestKeeperOfTheDatabaseApplication.class, args);
	}

	// Помещаем маппер в контекст как бин
	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}
}
