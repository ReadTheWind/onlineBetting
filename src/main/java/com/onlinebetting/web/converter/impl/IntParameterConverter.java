package com.onlinebetting.web.converter.impl;


import com.onlinebetting.web.converter.ParameterConverter;

import java.lang.reflect.Parameter;

public class IntParameterConverter implements ParameterConverter {

    @Override
    public boolean isMatch(Parameter parameter) {
        return parameter.getParameterizedType().getTypeName().equals("int") || parameter.getParameterizedType().getTypeName().equals("Integer");
    }

    @Override
    public Object convert(String parameterValue) {
        return "".equals(parameterValue) ? 0 : Integer.parseInt(parameterValue);
    }
}
