package com.icplaza.sdk.common;

import com.alibaba.fastjson.JSONObject;
import com.icplaza.sdk.msg.MsgSend;
import com.icplaza.sdk.msg.utils.Message;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public abstract class EnvBase {
    private String RestServerUrl = "";

    public EnvBase() {
        EnvInstance.setEnv(this);
    }


    public String GetMainPrefix() {
        return null;
    }

    public String GetDenom() {
        return null;
    }

    public String GetChainid() {
        return null;
    }

    public String GetRestServerUrl() {
        return this.RestServerUrl;
    }

    public void SetRestServerUrl(String url) {
        this.RestServerUrl = url;
    }

    public String GetHDPath() {
        return "M/44H/991H/0H/0/0";
    }

    public String GetValidatorAddrPrefix() {
        return String.format("%svaloper", GetMainPrefix());
    }

    public String GetTendermintConsensusPubkeyPrefix() {
        return String.format("%svalconspub", GetMainPrefix());
    }

    public String GetRestPathPrefix() {
        return "gauss";
    }

    public String GetDelegationsUrlPath(String address) {
        return this.GetRestPathPrefix() + "/staking/v1beta1/delegations/" + address;
    }

    public boolean HasFee() {
        return true;
    }

    public String GetTxUrlPath() {
        return "/txs";
    }

    public String GetBalanceUrlPath(String address) {
        return "/" + this.GetRestPathPrefix() + "/bank/v1beta1/balances/" + address;
    }

    public String GetTxsPath(String hash) {
        return "/" + this.GetRestPathPrefix() + "/tx/v1beta1/txs/" + hash;
    }


    public Map<String, String> transfer(String privateKey, String toAddress, double amount, String feeAmount, String memo) throws Exception {
        if (memo == null) {
            memo = "";
        }
        if (feeAmount == null) {
            feeAmount = "5000";
        }
        String gas = "200000";
        Map<String, String> map = new HashMap();

        MsgSend msg = new MsgSend();
        msg.setMsgType("cosmos-sdk/MsgSend");
        msg.init(privateKey);
        amount *= 1000000.0D;
        BigInteger bigInteger = BigInteger.valueOf((long) amount);
        Message messages = msg.produceSendMsg(EnvInstance.getEnv().GetDenom(), String.valueOf(bigInteger), toAddress);

        try {
            JSONObject jsonObject = msg.submit(messages, feeAmount, gas, memo);
            String txhash = (String) jsonObject.get("txhash");
            Integer code = (Integer) jsonObject.get("code");
            String raw_log;
            if (code != null) {
                raw_log = (String) jsonObject.get("raw_log");
                map.put("error", raw_log);
            } else if (txhash == null) {
                raw_log = (String) jsonObject.get("raw_log");
                map.put("error", raw_log);
            } else {
                map.put("txhash", txhash);
            }
        } catch (Exception var16) {
            map.put("error", var16.getMessage());
        }

        return map;
    }


    public String getBalance(String address) {
        String url = this.GetRestServerUrl() + this.GetBalanceUrlPath(address);
        try {
            return HttpUtils.httpGet(url);
        } catch (Exception e) {
            return null;
        }
    }


    public String getDelegations(String address) {
        String url = this.GetRestServerUrl() + this.GetDelegationsUrlPath(address);
        try {
            return HttpUtils.httpGet(url);
        } catch (Exception e) {
            return null;
        }
    }

    //    public String getValidatorsInfo(){
////        icplazavaloper1pt56lfj5048qeytry2makfnrtppckw38jmknd4
//    }
    public String getTxs(String hash) {
        String url = this.GetRestServerUrl() + this.GetTxsPath(hash);
        return HttpUtils.httpGet(url);
    }

}
