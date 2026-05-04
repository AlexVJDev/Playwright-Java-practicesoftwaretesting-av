package com.practicesoftwaretesting.models.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Image {

    private String id;

    @JsonProperty("by_name")
    private String byName;

    @JsonProperty("by_url")
    private String byUrl;

    @JsonProperty("source_name")
    private String sourceName;

    @JsonProperty("source_url")
    private String sourceUrl;

    @JsonProperty("file_name")
    private String fileName;

    private String title;
}
