package com.example.ledwisdom1.device.entity;

//    网关请求

public class AddHubRequest {
    public String gatewayId;
    public String routing;
    public String password;
    public String gatewayName;
    public String factoryId;
    public String productId;
    public String meshId;
    public String meshName;
    public String meshPassword;

    public AddHubRequest(String gatewayId, String routing, String password, String gatewayName, String factoryId, String productId) {
        this.gatewayId = gatewayId;
        this.routing = routing;
        this.password = password;
        this.gatewayName = gatewayName;
        this.factoryId = factoryId;
        this.productId = productId;
    }



}