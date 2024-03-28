package com.outrundao.backend.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class Web3Configuration {
    @Value("${client.rpc}")
    private String clientRPC;

    @Value("${caller.pk}")
    private String callerPK;

    @Value("${chain.id}")
    private long chainId;

    @Bean
    public Web3j getClient() {
        return Web3j.build(new HttpService(clientRPC));
    }

    @Bean
    public Credentials getCaller() {
        return Credentials.create(callerPK);
    }

    @Bean
    public long getChainId() {
        return chainId;
    }
}
