package com.amir.spring_redis;

import org.springframework.boot.SpringApplication;

public class TestSpringRedisApplication {

	public static void main(String[] args) {
		SpringApplication.from(SpringRedisApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
