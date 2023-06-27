package com.icplaza.sdk;


import com.google.gson.*;
import com.icplaza.sdk.pojo.*;
import org.bitcoinj.core.ECKey;
import org.bouncycastle.util.encoders.Hex;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class Transaction {
    /**
     */
    public static String signJson(String json, String privateKey) {
        GsonBuilder gsonBuilder = new GsonBuilder().disableHtmlEscaping().enableComplexMapKeySerialization().serializeSpecialFloatingPointValues().setLenient();

        JsonSerializer<Number> numberJsonSerializer = (src, typeOfSrc, context) -> {//toJson()
            BigDecimal v = new BigDecimal(String.valueOf(src));
            BigInteger i = v.toBigInteger();
            BigDecimal v1 = new BigDecimal(i);
            if (v.compareTo(v1) == 0) {
                return new JsonPrimitive(i);
            }
            return new JsonPrimitive(v);
        };

        Gson gson = gsonBuilder.registerTypeAdapter(Double.class, numberJsonSerializer).create();

        RequestJson jsonObject = gson.fromJson(json, RequestJson.class);
        List<Msg> msgs = jsonObject.getMsgs();
        for (int i = 0; i < msgs.size(); i++) {
            Msg msg = msgs.get(i);
            String value = gson.toJson(msg.getValue());
            String sortedValue = sortJson(value);
            Object v = gson.fromJson(sortedValue, Object.class);
            msgs.get(i).setValue(v);
        }

        String signDataJson = gson.toJson(jsonObject);


        byte[] b = signDataJson.getBytes();


        if (b.length != 32) {
            b = Hash.sha3(b);
        }


        ECKeyPair keyPair = ECKeyPair.create(Hex.decode(privateKey));
        Sign.SignatureData signatureData = Sign.signMessage(b, keyPair, false);


        String r = Hex.toHexString(signatureData.getR());
        String s = Hex.toHexString(signatureData.getS());

        String v = "00";
        if (new BigInteger(signatureData.getV()).compareTo(new BigInteger("27")) > 0) {
            v = "01";
        }
        String rsv = r + s + v;


        byte[] sig = Hex.decode(rsv);
        String signatureStr = Base64.getEncoder().encodeToString(sig);

        ECKey k = ECKey.fromPrivate(new BigInteger(privateKey, 16));
        String pk = Base64.getEncoder().encodeToString(k.getPubKey());

        Pub_key pubkey = new Pub_key();
        pubkey.setType("ethermint/PubKeyEthSecp256k1");
        pubkey.setValue(pk);

        Signature signature = new Signature();
        signature.setPub_key(pubkey);
        signature.setSignature(signatureStr);

        ResultJson resultJson = new ResultJson();
        resultJson.setMode(jsonObject.getMemo());
        // tx
        Tx tx = new Tx();
        tx.setMemo("");
        tx.setMsg(jsonObject.getMsgs());
        tx.setFee(jsonObject.getFee());

        List<Signature> signatures = new ArrayList<>();
        signatures.add(signature);

        tx.setSignatures(signatures);
        tx.setType("auth/StdTx");
        resultJson.setTx(tx);
        resultJson.setMode("sync");
        return gson.toJson(resultJson);
    }

    /**
     *
     * @return
     */
    private static Comparator<String> getComparator() {
        return Comparator.naturalOrder();
    }

    /**
     *
     * @param e
     */
    public static void sort(JsonElement e) {
        if (e.isJsonNull() || e.isJsonPrimitive()) {
            return;
        }

        if (e.isJsonArray()) {
            JsonArray a = e.getAsJsonArray();
            Iterator<JsonElement> it = a.iterator();
            it.forEachRemaining(i -> sort(i));
            return;
        }

        if (e.isJsonObject()) {
            Map<String, JsonElement> tm = new TreeMap<>(getComparator());
            for (Map.Entry<String, JsonElement> en : e.getAsJsonObject().entrySet()) {
                tm.put(en.getKey(), en.getValue());
            }

            String key;
            JsonElement val;
            for (Map.Entry<String, JsonElement> en : tm.entrySet()) {
                key = en.getKey();
                val = en.getValue();
                e.getAsJsonObject().remove(key);
                e.getAsJsonObject().add(key, val);
                sort(val);
            }
        }
    }

    /**
     *
     * @param json
     * @return
     */
    private static String sortJson(String json) {
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        JsonParser p = new JsonParser();
        JsonElement e = p.parse(json);
        sort(e);
        return g.toJson(e);
    }
}


