package com.philosophizer.demeter.entity.Kroger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductSearchResponse(List<KrogerItemDto> data) {}
