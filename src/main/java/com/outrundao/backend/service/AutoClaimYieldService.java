package com.outrundao.backend.service;

import com.outrundao.backend.contract.OutETHVault;
import com.outrundao.backend.contract.OutUSDBVault;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

/**
 * Auto claim native yield every day and compute APY.
 */
@Slf4j
@Service
public class AutoClaimYieldService implements IAutoClaimYieldService {
    private static final String OUT_ETH_VAULT_ADDRESS = "0xb1eA15AA6deEE2267EeacD7b224eCc8DC019a3bC";

    private static final String OUT_USDB_VAULT_ADDRESS = "0x7044A1664b6d7Ab7779B7EcAE4Eb2604c260C525";

    private final Web3j CLIENT;

    private final Credentials CALLER;

    private final long CHAIN_ID;

    /**
     * @param client web3 client
     * @param caller caller to send transaction
     */
    @Autowired
    public AutoClaimYieldService(Web3j client, Credentials caller, long chainId) {
        this.CLIENT = client;
        this.CALLER = caller;
        this.CHAIN_ID = chainId;
    }

    @Override
    public void claimETHYield() throws Exception {
        log.info("Claiming ETH yield......");

        OutETHVault vault = OutETHVault.load(
                OUT_ETH_VAULT_ADDRESS,
                CLIENT,
                new RawTransactionManager(CLIENT, CALLER, CHAIN_ID),
                new DefaultGasProvider()
        );

        TransactionReceipt transactionReceipt = vault.claimETHYield().send();
        if (transactionReceipt.isStatusOK()) {
            log.info("ClaimETHYield success!");
        } else {
            log.error("ClaimETHYield failed!");
        }
        log.info("Block Number: {}, Transaction Hash: {}", transactionReceipt.getBlockNumber(), transactionReceipt.getTransactionHash());
    }

    @Override
    public void claimUSDBYield() throws Exception {
        log.info("Claiming USDB yield......");

        OutUSDBVault vault = OutUSDBVault.load(
                OUT_USDB_VAULT_ADDRESS,
                CLIENT,
                new RawTransactionManager(CLIENT, CALLER, CHAIN_ID),
                new DefaultGasProvider()
        );

        TransactionReceipt transactionReceipt = vault.claimUSDBYield().send();
        if (transactionReceipt.isStatusOK()) {
            log.info("ClaimUSDBYield success!");
        } else {
            log.error("ClaimUSDBYield failed!");
        }
        log.info("Block Number: {}, Transaction Hash: {}", transactionReceipt.getBlockNumber(), transactionReceipt.getTransactionHash());
    }
}
