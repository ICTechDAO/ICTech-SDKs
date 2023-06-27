/**
 * Copyright 2022 bejson.com
 */
package com.icplaza.sdk.pojo;

import java.util.List;

/**

 */
public class RequestJson {

    private String account_number;
    private String chain_id;
    private Fee fee;
    private String memo;
    private List<Msg> msgs;
    private String sequence;

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setChain_id(String chain_id) {
        this.chain_id = chain_id;
    }

    public String getChain_id() {
        return chain_id;
    }

    public void setFee(Fee fee) {
        this.fee = fee;
    }

    public Fee getFee() {
        return fee;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getMemo() {
        return memo;
    }

    public void setMsgs(List<Msg> msgs) {
        this.msgs = msgs;
    }

    public List<Msg> getMsgs() {
        return msgs;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getSequence() {
        return sequence;
    }

}