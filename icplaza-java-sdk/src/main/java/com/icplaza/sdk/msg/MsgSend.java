package com.icplaza.sdk.msg;

import com.alibaba.fastjson.JSONObject;
import com.icplaza.sdk.common.EnvInstance;
import com.icplaza.sdk.msg.utils.BoardcastTx;
import com.icplaza.sdk.msg.utils.Data2Sign;
import com.icplaza.sdk.msg.utils.Message;
import com.icplaza.sdk.msg.utils.TxValue;
import com.icplaza.sdk.msg.utils.type.MsgSendValue;
import com.icplaza.sdk.types.Fee;
import com.icplaza.sdk.types.Signature;
import com.icplaza.sdk.types.Token;

import java.util.ArrayList;
import java.util.List;

public class MsgSend extends MsgBase {
    public static void main(String[] args) {

    }

    public Message produceSendMsg(String denom, String amountDenom, String to) {

        List<Token> amountList = new ArrayList<>();
        Token amount = new Token();
        amount.setDenom(denom);
        amount.setAmount(amountDenom);
        amountList.add(amount);

        MsgSendValue value = new MsgSendValue();
        value.setFromAddress(this.address);
        value.setToAddress(to);
        value.setAmount(amountList);

        Message<MsgSendValue> msg = new Message<>();
        msg.setType("cosmos-sdk/MsgSend");
        msg.setValue(value);
        return msg;
    }

    public JSONObject submit(Message message,
                             String feeAmount,
                             String gas,
                             String memo, String nonce, String accountN) {
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
            Data2Sign data = new Data2Sign(accountN, EnvInstance.getEnv().GetChainid(), fee, memo, msgs, nonce);

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

    public void add() {
        sequenceNum = String.valueOf(Long.parseLong(sequenceNum) + 1);
    }

}
