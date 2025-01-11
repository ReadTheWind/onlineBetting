package com.onlinebetting.web.converter.impl;


import com.onlinebetting.web.converter.ParameterConverter;

import java.lang.reflect.Parameter;

public class LongParameterConverter implements ParameterConverter {

    @Override
    public boolean isMatch(Parameter parameter) {
        return parameter.getParameterizedType().getTypeName().equals("Long") || parameter.getParameterizedType().getTypeName().equals("long") ;
    }

    @Override
    public Object convert(String parameterValue) {
        return "".equals(parameterValue) ? 0L : Long.parseLong(parameterValue);
    }
}
