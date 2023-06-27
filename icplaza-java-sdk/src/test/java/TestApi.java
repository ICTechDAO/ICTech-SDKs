import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.google.gson.Gson;
import com.icplaza.sdk.common.EnvBase;
import com.icplaza.sdk.common.EnvIcplaza;
import com.icplaza.sdk.common.EnvInstance;
import com.icplaza.sdk.common.HttpUtils;
import com.icplaza.sdk.crypto.Crypto;
import com.icplaza.sdk.crypto.encode.Sha256;
import com.icplaza.sdk.msg.MsgSend;
import com.icplaza.sdk.msg.utils.Message;
import com.icplaza.sdk.msg.utils.type.MsgSendValue;
import com.icplaza.sdk.utils.AddressUtil;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class TestApi {

    private static String RPC_URL = "https://cosrpc.icplaza.pro/";

    /**
     * send tx
     *
     * @throws Exception
     */
    @Test
    public void sendIct() throws Exception {
        EnvBase envBase = new EnvIcplaza(RPC_URL);
        String gas = "200000";

        MsgSend msg = new MsgSend();
        msg.setMsgType("cosmos-sdk/MsgSend");
        String privateKey = "[[[[[[[[[[[[[[[ input  your  private key ]]]]]]]]]]]]]]]";
        msg.init(privateKey);
        BigInteger bigInteger = new BigDecimal(String.valueOf(0.01)).multiply(new BigDecimal(Math.pow(10, 18))).toBigInteger();
        Message messages = msg.produceSendMsg(EnvInstance.getEnv().GetDenom(), String.valueOf(bigInteger), "icplaza1r0zhmfz56tmglev420hjvxwqj7062gsveun4m9");
        JSONObject jsonObject = msg.submit(messages, new BigDecimal("0.000002").multiply(new BigDecimal(Math.pow(10, 18))).toBigInteger().toString(), gas, "");
        System.out.println(jsonObject);
    }

    /**
     * create Account
     *
     * @throws Exception
     */
    @Test
    public void createAddress() throws Exception {

        EnvBase envBase = new EnvIcplaza(RPC_URL);
        String privateKey = Crypto.generatePrivateKey();
        String icplazaAddress = AddressUtil.convertEthAddressToCosmos(Crypto.getAddress(privateKey), envBase.GetMainPrefix());
        System.out.println(privateKey);
        System.out.println(icplazaAddress);


    }

    /**
     * Query latest block information
     *
     * @throws Exception
     */
    @Test
    public void getLatestBlock() {
        String blockUrl = RPC_URL + "/blocks/latest";
        String s = HttpUtils.httpGet(blockUrl);
        JSONObject json = JSON.parseObject(s);
        System.out.println(s);
        JSONArray txs = json.getJSONObject("block").getJSONObject("data").getJSONArray("txs");
        System.out.println(txs);
        for (String tx : txs.toJavaList(String.class)
        ) {
            String hash = Sha256.tx2Sha256(tx).toUpperCase();
            System.out.println(hash);
        }
    }

    /**
     * Query block information
     *
     * @throws Exception
     */
    @Test
    public void getBlock() {
        long blockNumber = 403740;
        String blockUrl = RPC_URL + "/blocks/" + blockNumber;
        String s = HttpUtils.httpGet(blockUrl);
        JSONObject json = JSON.parseObject(s);
        System.out.println(s);
        JSONArray txs = json.getJSONObject("block").getJSONObject("data").getJSONArray("txs");
        System.out.println(txs);
        for (String tx : txs.toJavaList(String.class)
        ) {
            String hash = Sha256.tx2Sha256(tx).toUpperCase();
            System.out.println(hash);
        }
    }

    /**
     * Query transaction information
     *
     * @throws Exception
     */
    @Test
    public void getTx() {
        //
        String hash = "0EF6EE72B218B01BADDF62B30DA157FF7C0E868D5224D0E9D007458B29119450";
        String txUrl = RPC_URL + "/icplaza/tx/v1beta1/txs/" + hash;
        String s = HttpUtils.httpGet(txUrl);
        JSONObject json = JSON.parseObject(s, Feature.DisableSpecialKeyDetect);
        System.out.println(json);
        boolean isTransferSuccess = false;
        if (json.getJSONObject("tx_response").getInteger("code") == null || json.getJSONObject("tx_response").getInteger("code") == 0) {
            isTransferSuccess = true;
        }
        if (isTransferSuccess) {
            System.out.println(" tx success ");
        } else {
            System.out.println(" tx fail ");
            //error  https://github.com/cosmos/cosmos-sdk/blob/main/types/errors/errors.go
        }
        long blockHeight = json.getJSONObject("tx_response").getLongValue("height");
        System.out.println("blockHeight " + blockHeight);


        JSONArray messages = json.getJSONObject("tx").getJSONObject("body").getJSONArray("messages");
        for (JSONObject message : messages.toJavaList(JSONObject.class)) {
            if ("/cosmos.bank.v1beta1.MsgSend".equalsIgnoreCase(message.getString("@type"))) {
                message.remove("@type");
                MsgSendValue sendValue = message.toJavaObject(MsgSendValue.class);
                System.out.println(sendValue);

                System.out.println("to_address:    " + message.getString("to_address"));
                System.out.println("from_address:    " + message.getString("from_address"));
                System.out.println("amount:  " + message.getJSONArray("amount").getJSONObject(0).getString("amount") +
                        "         denom :      " + message.getJSONArray("amount").getJSONObject(0).getString("denom")
                );
            }
        }

        String feeJson = json.getJSONObject("tx").getJSONObject("auth_info").getJSONObject("fee").getString("amount");
        BigDecimal feeAmount = BigDecimal.ZERO;
        String feeDenom = null;
        if (feeJson.startsWith("[")) {
            JSONArray jsonArray = JSONArray.parseArray(feeJson);
            feeAmount = jsonArray.getJSONObject(0).getBigDecimal("amount");
            feeDenom = jsonArray.getJSONObject(0).getString("denom");
        } else {
            JSONObject jsonObject = JSONObject.parseObject(feeJson);
            feeAmount = jsonObject.getBigDecimal("amount");
            feeDenom = jsonObject.getString("denom");
        }

        System.out.println(" feeAmount " + feeAmount + "    feeDenom   " + feeDenom);


    }

    /**
     * get account_number and  sequence
     */
    @Test
    public void getAccountInfo() {
        String address = "icplaza1r0zhmfz56tmglev420hjvxwqj7062gsveun4m9";
        String url = RPC_URL + "icplaza/auth/v1beta1/accounts/" + address;
        String rs = HttpUtil.get(url);
        Map map = new HashMap();
        if (rs != null) {
            Map m = new Gson().fromJson(rs, Map.class);
            Map account = (Map) m.get("account");
            Map base_account = (Map) account.get("base_account");
            map.put("account_number", base_account.get("account_number"));
            map.put("sequence", base_account.get("sequence"));
        }
        System.out.println("account info   " + JSONObject.toJSONString(map));
    }

    /**
     * get balance
     */
    @Test
    public void getAccountBalance() {
        String address = "icplaza1r0zhmfz56tmglev420hjvxwqj7062gsveun4m9";
        String url = RPC_URL + "bank/balances/" + address;
        String rs = HttpUtil.get(url);
        if (rs != null) {
            Map m = new Gson().fromJson(rs, Map.class);

            System.out.println("account info   " + JSONObject.toJSONString(m));
        }
    }


}
