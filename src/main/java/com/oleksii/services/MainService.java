package com.oleksii.services;

import com.oleksii.config.annotations.Autowired;
import com.oleksii.config.annotations.Bean;

@Bean
public class MainService {

    @Autowired
    private InnerService innerService;


    public void invokeInnerService() {
        innerService.invoke();
    }

}
