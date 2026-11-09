package com.philosophizer.demeter.KrogerClient;

import com.philosophizer.demeter.kroger.KrogerClient;
import com.philosophizer.demeter.entity.Kroger.KrogerItemDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = KrogerClient.class)
public class KrogerClientTest {

    @Autowired
    private KrogerClient krogerClient;


    @Test
    public void getResultFromKroger() throws Exception{

        String searchTerm = "chicken breast";
        List<KrogerItemDto> results = krogerClient.getProductsForSearchTerm(searchTerm);
        System.out.println(results.size());
        assertTrue(!results.isEmpty());

    }
}
