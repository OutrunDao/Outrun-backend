package com.outrundao.backend.service;

import com.outrundao.backend.contract.OutETHVault;
import com.outrundao.backend.contract.OutUSDBVault;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
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
public class AutoClaimYieldService {
    @Value("${contract.vault.eth}")
    private String outEthVaultAddress;

    @Value("${contract.vault.usdb}")
    private String outUsdbVaultAddress;

    private final Web3j CLIENT;

    private final Credentials CALLER;

    private final long CHAIN_ID;

    /**
     * @param client web3 client
     * @param caller caller to send transaction
     * @param chainId - chain Id
     */
    @Autowired
    public AutoClaimYieldService(Web3j client, Credentials caller, long chainId) {
        this.CLIENT = client;
        this.CALLER = caller;
        this.CHAIN_ID = chainId;
    }

    /**
     * Claim ETH native yield
     */
    private void claimETHNativeYield() throws Exception {
        log.info("Claiming ETH yield......");

        OutETHVault vault = OutETHVault.load(
                outEthVaultAddress,
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

    /**
     * Claim USDB native yield
     */
    private void claimUSDBNativeYield() throws Exception {
        log.info("Claiming USDB yield......");

        OutUSDBVault vault = OutUSDBVault.load(
                outUsdbVaultAddress,
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

    /**
     * Scheduled task: Automatic claim of native yield
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "UTC")
    private void autoClaimNativeYield() throws Exception {
        log.info("Auto claim native yield......");

        claimETHNativeYield();
        claimUSDBNativeYield();
    }
}
