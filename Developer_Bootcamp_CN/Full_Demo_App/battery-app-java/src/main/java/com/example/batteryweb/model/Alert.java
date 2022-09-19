package com.example.batteryweb.model;

import com.envisioniot.enos.event_service.vo.ActiveAlert;

public class Alert extends ActiveAlert {
    private String assetName;

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }
}
