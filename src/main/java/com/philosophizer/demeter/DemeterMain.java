package com.philosophizer.demeter;

import com.philosophizer.demeter.kroger.KrogerClient;
import com.philosophizer.demeter.utils.SearchTermNormalizer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@SpringBootApplication
public class DemeterMain {



    public static void main(String[] args) {
        SpringApplication.run(DemeterMain.class, args);
    }

    @Bean
    CommandLineRunner debugRun(KrogerClient krogerClient, SearchTermNormalizer searchTermNormalizer) {
        return args -> {
            Optional<KrogerClient.ScoredProduct> token = krogerClient.pickBest("milk chocolate", krogerClient.getProductsForSearchTerm("milk chocolate"));

            System.out.println(token.toString());

        };
    }
}
