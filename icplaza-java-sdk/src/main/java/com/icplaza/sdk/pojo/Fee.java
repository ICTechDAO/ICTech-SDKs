/**
  * Copyright 2022 bejson.com 
  */
package com.icplaza.sdk.pojo;
import java.util.List;

/**
 * Auto-generated: 2022-06-01 15:52:2
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Fee {

    private List<Amount> amount;
    private String gas;
    public void setAmount(List<Amount> amount) {
         this.amount = amount;
     }
     public List<Amount> getAmount() {
         return amount;
     }

    public void setGas(String gas) {
         this.gas = gas;
     }
     public String getGas() {
         return gas;
     }

}