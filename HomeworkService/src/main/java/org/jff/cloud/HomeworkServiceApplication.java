package org.jff.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class HomeworkServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(HomeworkServiceApplication.class, args);
    }
}
