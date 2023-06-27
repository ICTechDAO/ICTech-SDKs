/**
  * Copyright 2022 bejson.com 
  */
package com.icplaza.sdk.pojo;

/**
 * Auto-generated: 2022-06-01 15:53:57
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Signature {

    private Pub_key pub_key;
    private String signature;
    public void setPub_key(Pub_key pub_key) {
         this.pub_key = pub_key;
     }
     public Pub_key getPub_key() {
         return pub_key;
     }

    public void setSignature(String signature) {
         this.signature = signature;
     }
     public String getSignature() {
         return signature;
     }

}