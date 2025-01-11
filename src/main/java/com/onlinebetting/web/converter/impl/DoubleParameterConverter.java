package com.onlinebetting.web.converter.impl;


import com.onlinebetting.web.converter.ParameterConverter;

import java.lang.reflect.Parameter;

public class DoubleParameterConverter implements ParameterConverter {

    @Override
    public boolean isMatch(Parameter parameter) {
        return parameter.getParameterizedType().getTypeName().equals("double") || parameter.getParameterizedType().getTypeName().equals("Double");
    }

    @Override
    public Object convert(String parameterValue) {
        return "".equals(parameterValue) ? 0d : Double.parseDouble(parameterValue);
    }

}
