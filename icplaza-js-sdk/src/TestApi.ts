import {Wallet} from '@ethersproject/wallet'
import {createMessageSend, Sender} from '@tharsis/transactions'

import {
    broadcast, ethToIcplaza,
    getSender,
    LOCALNET_CHAIN,
    LOCALNET_FEE,
    signTransaction,
} from './libs/signer'
    ;
import BigNumber from "bignumber.js";
import {join} from 'path';
import fetch from "node-fetch";

const wallet = new Wallet("[[[[[[[[[[[[[[[ input  your  private key ]]]]]]]]]]]]]]]");

async function testSend() {
    let toAmount = 10000000;
    let toAccount = "icplaza1r0zhmfz56tmglev420hjvxwqj7062gsveun4m9";
    let sender: Sender;
    try {
        sender = await getSender(wallet)
    } catch (error) {
        console.log(error)
    }
    // @ts-ignore
    if (sender == undefined) {
        console.log(toAccount.toString() + ' Fetch  Error   ' + toAmount.toString() + " sender is null   ")
        return;
    }

    const txSimple = createMessageSend(LOCALNET_CHAIN, sender, LOCALNET_FEE, '', {
        destinationAddress: toAccount.toString(),
        amount: toAmount.toString(),
        denom: 'uict',
    })

    const resKeplr = await signTransaction(wallet, txSimple)
    const broadcastRes = await broadcast(resKeplr)
    if (broadcastRes.tx_response) {
        if (broadcastRes.tx_response.code === 0) {
            console.log(toAccount.toString() + '   Success   ' + toAmount.toString())
        } else if (broadcastRes.tx_response.code === 99) {
            console.log(toAccount.toString() + ' Fetch  Error   ' + toAmount.toString() + " broadcast error   ")
        } else {
            console.log(toAccount.toString() + '   Error   ' + toAmount.toString() + "   " + broadcastRes.tx_response.raw_log)
        }
    } else {
        console.log(toAccount.toString() + '   ' + JSON.stringify(broadcastRes) + '   ' + toAmount.toString())
    }
}

async function testCreateNewAddress() {
    const newWallet = Wallet.createRandom();
    console.log(newWallet.address);
    console.log(newWallet.privateKey);
    const icplazaAddress = ethToIcplaza(newWallet.address)
    console.log(icplazaAddress);
}

async function testGetLatestBlock() {
    const url: string = 'https://cosrpc.icplaza.pro';
    let post;
    try {

        post = await fetch(`${url}/blocks/latest`, {
            method: 'get',
            headers: {'Content-Type': 'application/json'},
        })
    } catch (error) {
        console.log(error)
    }

    const data = post != undefined ? await post.json() : {};
    console.log(JSON.stringify(data));
}

async function testGetBlock() {
    let blockNumber = 403740;
    const url: string = 'https://cosrpc.icplaza.pro';
    let post;
    try {

        post = await fetch(`${url}/blocks/` + blockNumber, {
            method: 'get',
            headers: {'Content-Type': 'application/json'},
        })
    } catch (error) {
        console.log(error)
    }

    const data = post != undefined ? await post.json() : {};
    console.log(JSON.stringify(data));
}

async function testGetTx() {
    let hash = "0EF6EE72B218B01BADDF62B30DA157FF7C0E868D5224D0E9D007458B29119450";
    const url: string = 'https://cosrpc.icplaza.pro';
    let post;
    try {

        post = await fetch(`${url}/icplaza/tx/v1beta1/txs/` + hash, {
            method: 'get',
            headers: {'Content-Type': 'application/json'},
        })
    } catch (error) {
        console.log(error)
    }

    const data = post != undefined ? await post.json() : {};
    // console.log(JSON.stringify(data));
    let isTransferSuccess = false;
    if (data.tx_response.code  || data.tx_response.code == 0) {
        isTransferSuccess = true;
    }
    if (isTransferSuccess) {
        console.log(" tx success ");
    } else {
        console.log(" tx fail ");
        //   https://github.com/cosmos/cosmos-sdk/blob/main/types/errors/errors.go
    }

    let messages:[] = data.tx.body.messages;
    for ( let i in messages)
    {
        let message = messages[i];
        if ("/cosmos.bank.v1beta1.MsgSend" == message["@type"]) {

            message
            // @ts-ignore
            console.log("to_address:    " + message.to_address);
            // @ts-ignore
            console.log("from_address:    " + message.from_address);
            // @ts-ignore
            console.log("amount:  " + message["amount"][0].amount + "         denom :      " +  message["amount"][0].denom
            );
        }
    }
}

async function testGetAccountInfo() {
    let address = "icplaza1r0zhmfz56tmglev420hjvxwqj7062gsveun4m9";
    const url: string = 'https://cosrpc.icplaza.pro';
    let post;
    try {

        post = await fetch(`${url}/icplaza/auth/v1beta1/accounts/` + address, {
            method: 'get',
            headers: {'Content-Type': 'application/json'},
        })
    } catch (error) {
        console.log(error)
    }

    const data = post != undefined ? await post.json() : {};
    console.log(JSON.stringify(data));
}

async function testGetAccountBalance() {
    let address = "icplaza1r0zhmfz56tmglev420hjvxwqj7062gsveun4m9";
    const url: string = 'https://cosrpc.icplaza.pro';
    let post;
    try {

        post = await fetch(`${url}/bank/balances/` + address, {
            method: 'get',
            headers: {'Content-Type': 'application/json'},
        })
    } catch (error) {
        console.log(error)
    }

    const data = post != undefined ? await post.json() : {};
    console.log(JSON.stringify(data));
}


testSend();
testCreateNewAddress();
testGetLatestBlock();
testGetBlock();
testGetTx();
testGetAccountInfo();
testGetAccountBalance();
