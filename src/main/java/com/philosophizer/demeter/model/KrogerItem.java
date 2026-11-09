package com.philosophizer.demeter.model;

import com.philosophizer.demeter.entity.Kroger.KrogerItemDto;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.Duration;
import java.time.Instant;

@DynamoDbBean
public class KrogerItem {

    private String searchKey;
    private String status;
    private String productId, upc, description, brand, matchedQuery;
    private double matchScore;
    private long ttl;
    private static final long MATCH_TTL_SECONDS = Duration.ofDays(30).toSeconds();
    private static final long NO_MATCH_TTL_SECONDS = Duration.ofDays(30).toSeconds();

    public KrogerItem() {}

    @DynamoDbPartitionKey
    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getMatchedQuery() {
        return matchedQuery;
    }

    public void setMatchedQuery(String matchedQuery) {
        this.matchedQuery = matchedQuery;
    }

    public double getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(double matchScore) {
        this.matchScore = matchScore;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public static KrogerItem from(KrogerItemDto dto, String searchKey, String matchedQuery, double matchScore) {
        KrogerItem krogerItem = new KrogerItem();
        krogerItem.setSearchKey(searchKey);
        krogerItem.setMatchedQuery(matchedQuery);
        krogerItem.setMatchScore(matchScore);
        krogerItem.setProductId(dto.productId());
        krogerItem.setBrand(dto.brand());
        krogerItem.setUpc(dto.upc());
        krogerItem.setDescription(dto.description());
        krogerItem.setTtl(Instant.now().plusSeconds(MATCH_TTL_SECONDS).getEpochSecond());
        return krogerItem;
    }

    public static KrogerItem noMatch(String searchKey) {
        KrogerItem item = new KrogerItem();
        item.setSearchKey(searchKey);
        item.setStatus("NO_MATCH");
        item.setTtl(Instant.now().plusSeconds(NO_MATCH_TTL_SECONDS).getEpochSecond());
        return item;
    }


}
