package com.oleksii.services;

import com.oleksii.config.annotations.Bean;

@Bean
public class InnerService {

    public void invoke() {
        System.out.println("InnerService invocation method.");
    }
}
