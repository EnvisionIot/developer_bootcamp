package com.example.batteryweb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {
    @Value("${enos.apim.addr}")
    public String addr;

    @Value("${enos.app.key}")
    public String accessKey;

    @Value("${enos.app.secret}")
    public String accessSecret;

    @Value("${enos.ou.id}")
    public String orgId;

    @Value("${enos.battery.assetTreeId}")
    public String assetTreeId;

    @Value("${enos.battery.parentAssetId}")
    public String parentAssetId;

    @Value("${enos.sub.server}")
    public String subServer;

    @Value("${enos.sub.id}")
    public String subId;
}
