package coffeeshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CoffeeShopApplication {
    // This is the entry point, but look how little it does.
    // SpringApplication.run() hands control to the framework.
    public static void main(String[] args) {
        SpringApplication.run(CoffeeShopApplication.class, args);
    }
}
