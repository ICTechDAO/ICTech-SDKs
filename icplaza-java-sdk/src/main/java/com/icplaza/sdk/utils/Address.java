package com.icplaza.sdk.utils;

import org.bouncycastle.util.encoders.Hex;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

/**
 * address utils
 */
public class Address {
    private static final String PREFIX = "icplaza";

    /**
     * address from private key
     */
    public static String fromPrivateKey(String privateKey) throws Exception {
        String addr = ethAddressFromPrivateKey(privateKey);
        return from0x(addr);
    }

    /**
     * address from eth format address
     */
    public static String from0x(String ethAddress) throws Exception {
        if (ethAddress.startsWith("0x")) {
            ethAddress = ethAddress.substring(2);
        }
        byte[] bytes = Hex.decode(ethAddress);

        byte[] addr = ConvertBits.convertBits(bytes, 0, bytes.length, 8, 5, true);
        return Bech32.encode(PREFIX, addr);



    }

    /**
     * convert icplaza address to eth format address
     */
    public static String to0x(String icplazaAddress) throws Exception {
        Bech32.Bech32Data data = Bech32.decode(icplazaAddress);
        byte[] dec = data.getData();
        dec = ConvertBits.convertBits(dec, 0, dec.length, 5, 8, false);
        return "0x" + Hex.toHexString(dec);
    }

    /**
     * eth format address
     */
    private static String ethAddressFromPrivateKey(String privateKey) {
        ECKeyPair ecKeyPair = ECKeyPair.create(Hex.decode(privateKey));
        return "0x" + Keys.getAddress(ecKeyPair);
    }

    /**
     * to validator address
     */
    public static String toValAddress(String cosmosAddress, String prefix) throws Exception {
        Bech32.Bech32Data data = Bech32.decode(cosmosAddress);
        byte[] dec = data.getData();
        return Bech32.encode(prefix, dec);
    }
}
