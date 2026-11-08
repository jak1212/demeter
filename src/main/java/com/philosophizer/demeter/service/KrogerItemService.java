package com.philosophizer.demeter.service;

import com.philosophizer.demeter.kroger.KrogerClient;
import com.philosophizer.demeter.utils.SearchTermNormalizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class KrogerItemService {


    @Autowired
    private KrogerClient krogerClient;

    @Autowired
    private SearchTermNormalizer searchTermNormalizer;

    private final RestClient restClient = RestClient.create();


    public KrogerItemService(){}







}
