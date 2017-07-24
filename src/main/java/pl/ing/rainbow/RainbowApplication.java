package pl.ing.rainbow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by B0621351 on 2016-08-31.
 */

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class RainbowApplication {

    public static void main(String[] args) {
        System.out.println("Starting..");
        SpringApplication.run(RainbowApplication.class, args);
    }
}
