package com.icplaza.sdk.msg;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.google.gson.Gson;
import com.icplaza.sdk.common.*;
import com.icplaza.sdk.crypto.Crypto;
import com.icplaza.sdk.msg.utils.BoardcastTx;
import com.icplaza.sdk.msg.utils.Data2Sign;
import com.icplaza.sdk.msg.utils.Message;
import com.icplaza.sdk.msg.utils.TxValue;
import com.icplaza.sdk.types.Fee;
import com.icplaza.sdk.types.Pubkey;
import com.icplaza.sdk.types.Signature;
import com.icplaza.sdk.types.Token;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class MsgBase {

    protected String restServerUrl = EnvInstance.getEnv().GetRestServerUrl();

    public String sequenceNum;
    public String accountNum;
    protected String pubKeyString;
    protected String address;
    protected String operAddress;
    protected String priKeyString;

    static protected String msgType;

    public void setMsgType(String type) {
        msgType = type;
    }

    static Signature sign(Data2Sign obj, String privateKey) throws Exception {
        String sigResult = null;
        sigResult = obj2byte(obj, privateKey);
        Signature signature = new Signature();
        Pubkey pubkey = new Pubkey();
        pubkey.setType("ethermint/PubKeyEthSecp256k1");
        pubkey.setValue(Strings.fromByteArray(Base64.encode(Hex.decode(Crypto.generatePubKeyHexFromPriv(privateKey)))));
        signature.setPubkey(pubkey);
        signature.setSignature(sigResult);

        return signature;
    }


    static String obj2byte(Data2Sign data, String privateKey) {

        String sigResult = null;
        try {
            String signDataJson = Utils.serializer.toJson(data);

            byte[] byteSignData = signDataJson.getBytes();

            if (byteSignData.length != 32) {
                byteSignData = Hash.sha3(byteSignData);
            }

            ECKeyPair keyPair = ECKeyPair.create(Hex.decode(privateKey));
            Sign.SignatureData signatureData = Sign.signMessage(byteSignData, keyPair, false);

            String r = Hex.toHexString(signatureData.getR());
            String s = Hex.toHexString(signatureData.getS());
            String v = "00";
            if (new BigInteger(signatureData.getV()).compareTo(new BigInteger("27")) > 0) {
                v = "01";
            }
            String rsv = r + s + v;

            byte[] sig = Hex.decode(rsv);

            sigResult = Strings.fromByteArray(Base64.encode(sig));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("serialize msg failed");
        }
        return sigResult;
    }


    public JSONObject submitDebug(Message message,
                                  String feeAmount,
                                  String gas,
                                  String memo) {
        try {
            List<Token> amountList = new ArrayList<>();
            Token amount = new Token();
            amount.setDenom(EnvInstance.getEnv().GetDenom());
            amount.setAmount(feeAmount);
            amountList.add(amount);

            Fee fee = new Fee();
            fee.setAmount(amountList);
            fee.setGas(gas);


            Message[] msgs = new Message[1];
            msgs[0] = message;

            Data2Sign data = new Data2Sign(accountNum, EnvInstance.getEnv().GetChainid(), fee, memo, msgs, sequenceNum);
            System.out.println(new Gson().toJson(data));
            Signature signature = MsgBase.sign(data, priKeyString);

            BoardcastTx cosmosTransaction = new BoardcastTx();
            cosmosTransaction.setMode("sync");

            TxValue cosmosTx = new TxValue();
            cosmosTx.setType("auth/StdTx");
            cosmosTx.setMsgs(msgs);

            if (EnvInstance.getEnv().HasFee()) {
                cosmosTx.setFee(fee);
            }

            cosmosTx.setMemo(memo);

            List<Signature> signatureList = new ArrayList<>();
            signatureList.add(signature);
            cosmosTx.setSignatures(signatureList);

            cosmosTransaction.setTx(cosmosTx);
            String tx = cosmosTransaction.toJson();
            System.out.println(tx);
            return boardcast(tx);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("serialize transfer msg failed");
        }
        return null;
    }

    public JSONObject submit(Message message,
                             String feeAmount,
                             String gas,
                             String memo) {
        try {
            List<Token> amountList = new ArrayList<>();
            Token amount = new Token();
            amount.setDenom(EnvInstance.getEnv().GetDenom());
            amount.setAmount(feeAmount);
            amountList.add(amount);

            Fee fee = new Fee();
            fee.setAmount(amountList);
            fee.setGas(gas);


            Message[] msgs = new Message[1];
            msgs[0] = message;

            Data2Sign data = new Data2Sign(accountNum, EnvInstance.getEnv().GetChainid(), fee, memo, msgs, sequenceNum);

            Signature signature = MsgBase.sign(data, priKeyString);

            BoardcastTx cosmosTransaction = new BoardcastTx();
            cosmosTransaction.setMode("sync");

            TxValue cosmosTx = new TxValue();
            cosmosTx.setType("auth/StdTx");
            cosmosTx.setMsgs(msgs);

            if (EnvInstance.getEnv().HasFee()) {
                cosmosTx.setFee(fee);
            }

            cosmosTx.setMemo(memo);

            List<Signature> signatureList = new ArrayList<>();
            signatureList.add(signature);
            cosmosTx.setSignatures(signatureList);

            cosmosTransaction.setTx(cosmosTx);

            return boardcast(cosmosTransaction.toJson());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("serialize transfer msg failed");
        }
        return null;
    }

    public JSONObject submitMany(Message[] msgs,
                                 String feeAmount,
                                 String gas,
                                 String memo) throws Exception {
        try {
            List<Token> amountList = new ArrayList<>();
            Token amount = new Token();
            amount.setDenom(EnvInstance.getEnv().GetDenom());
            amount.setAmount(feeAmount);
            amountList.add(amount);

            Fee fee = new Fee();
            fee.setAmount(amountList);
            fee.setGas(gas);

            Data2Sign data = new Data2Sign(accountNum, EnvInstance.getEnv().GetChainid(), fee, memo, msgs, sequenceNum);
            Signature signature = MsgBase.sign(data, priKeyString);

            BoardcastTx cosmosTransaction = new BoardcastTx();

            cosmosTransaction.setMode("sync");
//            cosmosTransaction.setMode("BROADCAST_MODE_SYNC");

            TxValue cosmosTx = new TxValue();
            cosmosTx.setType("auth/StdTx");
            cosmosTx.setMsgs(msgs);

            if (EnvInstance.getEnv().HasFee()) {
                cosmosTx.setFee(fee);
            }

            cosmosTx.setMemo(memo);

            List<Signature> signatureList = new ArrayList<>();
            signatureList.add(signature);
            cosmosTx.setSignatures(signatureList);

            cosmosTransaction.setTx(cosmosTx);
            return boardcast(cosmosTransaction.toJson());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public JSONObject submitMany(Message[] msgs,
                                 String feeAmount,
                                 String feeDenom,
                                 String gas,
                                 String memo) throws Exception {
        try {
            List<Token> amountList = new ArrayList<>();
            Token amount = new Token();
            amount.setDenom(feeDenom);
            amount.setAmount(feeAmount);
            amountList.add(amount);

            Fee fee = new Fee();
            fee.setAmount(amountList);
            fee.setGas(gas);

            Data2Sign data = new Data2Sign(accountNum, EnvInstance.getEnv().GetChainid(), fee, memo, msgs, sequenceNum);
            Signature signature = MsgBase.sign(data, priKeyString);

            BoardcastTx cosmosTransaction = new BoardcastTx();

            cosmosTransaction.setMode("sync");
//            cosmosTransaction.setMode("BROADCAST_MODE_SYNC");

            TxValue cosmosTx = new TxValue();
            cosmosTx.setType("auth/StdTx");
            cosmosTx.setMsgs(msgs);

            if (EnvInstance.getEnv().HasFee()) {
                cosmosTx.setFee(fee);
            }

            cosmosTx.setMemo(memo);

            List<Signature> signatureList = new ArrayList<>();
            signatureList.add(signature);
            cosmosTx.setSignatures(signatureList);

            cosmosTransaction.setTx(cosmosTx);
            return boardcast(cosmosTransaction.toJson());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    void initMnemonic(String mnemonic) {
        String prikey = Crypto.generatePrivateKeyFromMnemonic(mnemonic);
        init(prikey);
    }

    /**
     */
    public void init(String privateKey) {
        pubKeyString = Hex.toHexString(Crypto.generatePubKeyFromPriv(privateKey));
        address = Crypto.generateEthStyleAddressFromPriv(privateKey);

        AccountInfo accountInfo = GlobalCtx.getSequence(address);
        if (accountInfo == null) {
            try {


                String jsonStr = getAccountPrivate(address);
                if (jsonStr == null) {
                    sequenceNum = null;
                    accountNum = null;
                } else {
                    ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
                    JSONObject accountJson = JSON.parseObject(jsonStr);
                    accountNum = getAccountNumber(accountJson);
                    sequenceNum = getSequance(accountJson);
                    accountInfo = new AccountInfo();
                    accountInfo.setAccountNumber(Long.valueOf(accountNum));
                    accountInfo.setSequenceNumber(Long.valueOf(sequenceNum) + 1);
                    GlobalCtx.setSequence(address, accountInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
                accountNum = BigInteger.ZERO.toString();
                sequenceNum = BigInteger.ZERO.toString();
            }
        } else {
            accountNum = accountInfo.getAccountNumber().toString();
            sequenceNum = accountInfo.getSequenceNumber().toString();
        }

        priKeyString = privateKey;

        operAddress = Crypto.generateValidatorAddressFromPub(pubKeyString);
    }


    public String getOperAddress() {
        return operAddress;
    }

    private String getAccountPrivate(String userAddress) {
        if (!restServerUrl.endsWith("/")) {
            restServerUrl = restServerUrl + "/";
        }
        String url = restServerUrl + EnvInstance.getEnv().GetRestPathPrefix() + Constants.COSMOS_ACCOUNT_URL_PATH + userAddress;
        System.out.println(url);
        return HttpUtils.httpGet(url);
    }

    private String getSequance(JSONObject account) {
        if (account.toJSONString().contains("ethermint.types.v1.EthAccount")) {
            return (String) account.getJSONObject("account")
                    .getJSONObject("base_account")
                    .get("sequence");
        }
        String res = (String) account
                .getJSONObject("account")
                .get("sequence");
        return res;
    }

    private String getAccountNumber(JSONObject account) {
        if (account.toJSONString().contains("ethermint.types.v1.EthAccount")) {
            return (String) account.getJSONObject("account")
                    .getJSONObject("base_account")
                    .get("account_number");
        }
        String res = (String) account
                .getJSONObject("account")
                .get("account_number");
        return res;
    }

    protected JSONObject boardcast(String tx) {
        String res = HttpUtils.httpPost(restServerUrl + EnvInstance.getEnv().GetTxUrlPath(), tx);
        JSONObject result = JSON.parseObject(res);
        if (result.toJSONString().contains("please verify account number")) {
            GlobalCtx.reset(this.address);
        }
        return result;
    }
}
