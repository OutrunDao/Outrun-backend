package com.outrundao.backend.service;

import com.outrundao.backend.contract.OutETHVault;
import com.outrundao.backend.contract.OutUSDBVault;
import com.outrundao.backend.service.interfaces.INativeYieldService;
import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class NativeYieldService implements INativeYieldService {
    private static final BigInteger BLOCK_INTERVAL = BigInteger.valueOf(43200L);

    private static final double DAY_RATE_RATIO = 10000000000D;

    private static final int YEAR = 365;

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
    public NativeYieldService(Web3j client, Credentials caller, long chainId) {
        this.CLIENT = client;
        this.CALLER = caller;
        this.CHAIN_ID = chainId;
    }

    /**
     * Get latest ETH native yield APY
     * @return ETH native yield APY
     */
    @Override
    public double getLatestEthNativeYieldAPY() {
        OutETHVault ethVault = OutETHVault.load(
                outEthVaultAddress,
                CLIENT,
                new RawTransactionManager(CLIENT, CALLER, CHAIN_ID),
                new DefaultGasProvider()
        );

        Disposable disposable = null;
        AtomicReference<Double> apy = new AtomicReference<>((double) 0);
        try {
            BigInteger latestBlockNumber = CLIENT.ethBlockNumber().send().getBlockNumber();
            disposable = ethVault.claimETHYieldEventFlowable(
                    DefaultBlockParameter.valueOf(latestBlockNumber.subtract(BLOCK_INTERVAL)),
                    DefaultBlockParameter.valueOf(latestBlockNumber)
            ).subscribe(eventResponse -> apy.set(Math.pow((eventResponse.dayRate.doubleValue() / DAY_RATE_RATIO + 1), YEAR) - 1), Throwable::printStackTrace);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (disposable != null) {
                disposable.dispose();
            }
        }

        return apy.get();
    }

    /**
     * Get latest USDB native yield APY
     * @return USDB native yield APY
     */
    @Override
    public double getLatestUsdbNativeYieldAPY() {
        OutUSDBVault usdbVault = OutUSDBVault.load(
                outUsdbVaultAddress,
                CLIENT,
                new RawTransactionManager(CLIENT, CALLER, CHAIN_ID),
                new DefaultGasProvider()
        );

        Disposable disposable = null;
        AtomicReference<Double> apy = new AtomicReference<>((double) 0);
        try {
            BigInteger latestBlockNumber = CLIENT.ethBlockNumber().send().getBlockNumber();
            disposable = usdbVault.claimUSDBYieldEventFlowable(
                    DefaultBlockParameter.valueOf(latestBlockNumber.subtract(BLOCK_INTERVAL)),
                    DefaultBlockParameter.valueOf(latestBlockNumber)
            ).subscribe(eventResponse -> apy.set(Math.pow((eventResponse.dayRate.doubleValue() / DAY_RATE_RATIO + 1), YEAR) - 1), Throwable::printStackTrace);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (disposable != null) {
                disposable.dispose();
            }
        }

        return apy.get();
    }
}
