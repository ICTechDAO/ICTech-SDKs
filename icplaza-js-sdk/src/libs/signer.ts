import fetch from 'node-fetch'
import {TypedDataUtils, SignTypedDataVersion} from '@metamask/eth-sig-util'
import {Wallet} from '@ethersproject/wallet'
import {
    arrayify,
    concat,
    splitSignature,
    joinSignature,
} from '@ethersproject/bytes'
import {createTxRaw} from '@tharsis/proto'
import {
    createTxRawEIP712,
    signatureToWeb3Extension,
    Sender,
    TxGenerated,
    Chain,
} from '@tharsis/transactions'
import {signatureToPubkey} from '@hanchon/signature-to-pubkey'
import {ETH} from '@evmos/address-converter'

const bech32_1 = require("bech32");

// Chain helpers

export const LOCALNET_CHAIN = {
    chainId: 13141619,
    cosmosChainId: 'icplaza_13141619-1',
}

export const LOCALNET_FEE = {
    amount: '20',
    denom: 'uict',
    gas: '200000',
}



// Get Account
/* eslint-disable camelcase */
interface AccountResponse {
    account: {
        '@type': string
        base_account: {
            address: string
            pub_key?: {
                '@type': string
                key: string
            }
            account_number: string
            sequence: string
        }
        code_hash: string
    }
}

export async function generatePubkey(wallet: Wallet) {
    // Sign the personal message `generate_pubkey` and generate the pubkey from that signature
    const signature = await wallet.signMessage('generate_pubkey')
    return signatureToPubkey(
        signature,
        Buffer.from([
            50, 215, 18, 245, 169, 63, 252, 16, 225, 169, 71, 95, 254, 165, 146, 216,
            40, 162, 115, 78, 147, 125, 80, 182, 25, 69, 136, 250, 65, 200, 94, 178,
        ]),
    )
}

function makeBech32Encoder(prefix: any) {
    return (data: any) => bech32_1.bech32.encode(prefix, bech32_1.bech32.toWords(data));
}

function makeBech32Decoder(currentPrefix: any) {
    return (data: any) => {
        const {prefix, words} = bech32_1.bech32.decode(data);
        if (prefix !== currentPrefix) {
            throw Error('Unrecognised address format');
        }
        return Buffer.from(bech32_1.bech32.fromWords(words));
    };
}

const bech32Chain = (name: any, prefix: any) => ({
    decoder: makeBech32Decoder(prefix),
    encoder: makeBech32Encoder(prefix),
    name,
});
const ETHERMINT = bech32Chain('icplaza', 'icplaza');
export const ethToIcplaza = (ethAddress: any) => {
    const data = ETH.decoder(ethAddress);
    return ETHERMINT.encoder(data);
};

const icplazaToEth = (icplazaAddress: any) => {
    const data = ETHERMINT.decoder(icplazaAddress);
    return ETH.encoder(data);
};


export async function getSender(
    wallet: Wallet,
    // url: string = 'http://127.0.0.1:1317',
    url: string = 'https://cosrpc.icplaza.pro',
) {
    // const icplazaAddress = ethToEthermint(wallet.address)

    const icplazaAddress = ethToIcplaza(wallet.address)
    let addrRequest ;
    try{
        addrRequest = await fetch(
            `${url}/icplaza/auth/v1beta1/accounts/${icplazaAddress}`,
        )
    } catch (error) {
        console.log(error)
        throw  new Error("fetch error");
        // return {accountAddress:undefined, sequence:0, accountNumber:0, pubkey:undefined } ;
    }
    const resp = (await addrRequest.json()) as AccountResponse

    const sender = {
        accountAddress: icplazaAddress,
        sequence: parseInt(resp.account.base_account.sequence as string, 10),
        accountNumber: parseInt(resp.account.base_account.account_number, 10),
        pubkey:
            resp.account.base_account.pub_key?.key || (await generatePubkey(wallet)),
    }
    return sender
}

// Broadcast a transaction in json.stringify format
export async function broadcast(
    transactionBody: string,
    // url: string = 'http://127.0.0.1:1317',
    url: string = 'https://cosrpc.icplaza.pro',
) {
    let post;
    try {

        post = await fetch(`${url}/icplaza/tx/v1beta1/txs`, {
            method: 'post',
            body: transactionBody,
            headers: {'Content-Type': 'application/json'},
        })
    } catch (error) {
        console.log(error)
    }
    // .catch( (err) => {
    //         console.log(err);
    //     });
    const data = post != undefined ? await post.json() : {tx_response: {code: 99}};
    return data
}

// Sign transaction using payload method (keplr style)
export async function signTransaction(
    wallet: Wallet,
    tx: TxGenerated,
    broadcastMode: string = 'BROADCAST_MODE_BLOCK',
) {
    const dataToSign = `0x${Buffer.from(
        tx.signDirect.signBytes,
        'base64',
    ).toString('hex')}`

    /* eslint-disable no-underscore-dangle */
    const signatureRaw = wallet._signingKey().signDigest(dataToSign)
    const splitedSignature = splitSignature(signatureRaw)
    const signature = arrayify(concat([splitedSignature.r, splitedSignature.s]))

    const signedTx = createTxRaw(
        tx.signDirect.body.serializeBinary(),
        tx.signDirect.authInfo.serializeBinary(),
        [signature],
    )
    const body = `{ "tx_bytes": [${signedTx.message
        .serializeBinary()
        .toString()}], "mode": "${broadcastMode}" }`

    return body
}

// Sign transaction using eip712 method (metamask style)
export async function signTransactionUsingEIP712(
    wallet: Wallet,
    sender: string,
    tx: TxGenerated,
    chain: Chain = LOCALNET_CHAIN,
    broadcastMode: string = 'BROADCAST_MODE_BLOCK',
) {
    const dataToSign = arrayify(
        TypedDataUtils.eip712Hash(tx.eipToSign as any, SignTypedDataVersion.V4),
    )
    /* eslint-disable no-underscore-dangle */
    const signatureRaw = wallet._signingKey().signDigest(dataToSign)
    const signature = joinSignature(signatureRaw)

    const extension = signatureToWeb3Extension(
        chain,
        {accountAddress: sender} as Sender,
        signature,
    )
    const signedTx = createTxRawEIP712(
        tx.legacyAmino.body,
        tx.legacyAmino.authInfo,
        extension,
    )

    return `{ "tx_bytes": [${signedTx.message
        .serializeBinary()
        .toString()}], "mode": "${broadcastMode}" }`
}
