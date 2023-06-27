package com.icplaza.sdk.common;

public class EnvIcplaza extends EnvBase {
    private String RestServerUrl = "";
    public EnvIcplaza(String url){
        this.RestServerUrl=url;
    }
    @Override
    public String GetMainPrefix() {
        return "icplaza";
    }
    @Override
    public String GetRestPathPrefix() {
        return "icplaza";
    }
    @Override
    public String GetRestServerUrl() {
        return this.RestServerUrl;
    }
    @Override
    public String GetDenom() {
        return "uict";
    }
    @Override
    public String GetChainid() {
        return "icplaza_13141619-1";
    }
}
