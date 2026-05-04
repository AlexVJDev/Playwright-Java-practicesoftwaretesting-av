package com.practicesoftwaretesting.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.ConfigFactory;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "system:env",
        "classpath:api.properties"
})
public interface ApiConfig extends Config {

    ApiConfig INSTANCE = ConfigFactory.create(ApiConfig.class);

    @Key("api.base.uri")
    String baseUri();
}
