/**
  * Copyright 2022 bejson.com 
  */
package com.icplaza.sdk.pojo;
import java.util.List;

/**
 * Auto-generated: 2022-06-01 15:53:57
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Tx {

    private String memo;
    private List<Msg> msg;
    private Fee fee;
    private List<Signature> signatures;
    private String type;
    public void setMemo(String memo) {
         this.memo = memo;
     }
     public String getMemo() {
         return memo;
     }

    public void setMsg(List<Msg> msg) {
         this.msg = msg;
     }
     public List<Msg> getMsg() {
         return msg;
     }

    public void setFee(Fee fee) {
         this.fee = fee;
     }
     public Fee getFee() {
         return fee;
     }

    public void setSignatures(List<Signature> signatures) {
         this.signatures = signatures;
     }
     public List<Signature> getSignatures() {
         return signatures;
     }

    public void setType(String type) {
         this.type = type;
     }
     public String getType() {
         return type;
     }

}