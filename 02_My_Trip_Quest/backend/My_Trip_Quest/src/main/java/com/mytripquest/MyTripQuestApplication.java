package com.mytripquest;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value = "com.mytripquest.domain", annotationClass = org.apache.ibatis.annotations.Mapper.class)
public class MyTripQuestApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyTripQuestApplication.class, args);
	}

}
