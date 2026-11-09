package com.philosophizer.demeter.entity.Kroger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KrogerItemDto(String productId, String upc, String description, String brand) {

    @Override
    public String productId() {
        return productId;
    }

    @Override
    public String upc() {
        return upc;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public String brand() {
        return brand;
    }
}
