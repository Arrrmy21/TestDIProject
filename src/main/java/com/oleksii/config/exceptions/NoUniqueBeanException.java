package com.oleksii.config.exceptions;

public class NoUniqueBeanException extends Exception {
    public NoUniqueBeanException(String finalBeanName) {
        super(finalBeanName);
    }
}
