/**
 * Copyright 2022 bejson.com
 */
package com.icplaza.sdk.pojo;

public class ResultJson {

    private String mode;
    private Tx tx;

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public void setTx(Tx tx) {
        this.tx = tx;
    }

    public Tx getTx() {
        return tx;
    }

}