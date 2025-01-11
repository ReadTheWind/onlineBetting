package com.onlinebetting.web.converter.impl;

import com.onlinebetting.web.converter.ParameterConverter;

import java.lang.reflect.Parameter;

public class StringParameterConverter implements ParameterConverter {

    @Override
    public boolean isMatch(Parameter parameter) {
        return parameter.getParameterizedType().getTypeName().equals(String.class.getName());
    }

    @Override
    public Object convert(String parameterValue) {
        return parameterValue;
    }
}
