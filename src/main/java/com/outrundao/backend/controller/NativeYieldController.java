package com.outrundao.backend.controller;

import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.outrundao.backend.utils.Result;
import com.outrundao.backend.service.interfaces.INativeYieldService;

@Slf4j
@RestController
@RequestMapping("/native/yield")
public class NativeYieldController {
    @Resource
    INativeYieldService nativeYieldService;

    /**
     * Query latest ETH Native yield APY
     * @return Result of APY
     */
    @GetMapping("/eth/apy/latest")
    @ResponseBody
    public Result<Double> queryLatestEthNativeYieldAPY() {
        double apy = nativeYieldService.getLatestEthNativeYieldAPY();

        if (apy != 0) {
            return Result.success("Successful query latest ETH native yield APY", apy);
        } else {
            return Result.error("Failure to query latest ETH native yield APY");
        }
    }

    /**
     * Query latest USDB Native yield APY
     * @return Result of APY
     */
    @GetMapping("/usdb/apy/latest")
    @ResponseBody
    public Result<Double> queryLatestUsdbNativeYieldAPY() {
        double apy = nativeYieldService.getLatestUsdbNativeYieldAPY();

        if (apy != 0) {
            return Result.success("Successful query latest USDB native yield APY", apy);
        } else {
            return Result.error("Failure to query latest USDB native yield APY");
        }
    }
}
