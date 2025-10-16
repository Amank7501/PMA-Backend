package com.rasp.app;

import com.rasp.app.decorator.IssueUserDecorator;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import platform.decorator.DecoratorManager;

@SpringBootApplication
@ComponentScan(basePackages = {"com.rasp.app.controller","controller", "platform.webservice.map", "platform.webservice.controller.base", "com.rasp.app.config", "platform.defined.account.controller", "com.rasp.app.service"})

public class Application {
	public static void main(String[] args) {
		Registry.register();
		DecoratorManager.getInstance().register(new IssueUserDecorator());
		// Load .env file
		Dotenv dotenv = Dotenv.configure().load();
		// Set system properties from .env
		dotenv.entries().forEach(entry -> {
			System.setProperty(entry.getKey(), entry.getValue());
		});


		SpringApplication.run(Application.class, args);

	}
}
