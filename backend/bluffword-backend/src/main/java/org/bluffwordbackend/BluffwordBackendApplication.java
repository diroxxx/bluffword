package org.bluffwordbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BluffwordBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BluffwordBackendApplication.class, args);
    }

}




