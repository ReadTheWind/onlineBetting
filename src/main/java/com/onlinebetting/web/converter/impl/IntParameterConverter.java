package com.onlinebetting.web.converter.impl;


import com.onlinebetting.web.constants.WebConstant;
import com.onlinebetting.web.converter.ParameterConverter;

import java.lang.reflect.Parameter;

public class IntParameterConverter implements ParameterConverter {

    @Override
    public boolean isMatch(Parameter parameter) {
        return parameter.getParameterizedType().getTypeName().equals(WebConstant.BASIC_TYPE_INT) || parameter.getParameterizedType().getTypeName().equals(Integer.class.getName());
    }

    @Override
    public Object convert(String parameterValue) {
        return WebConstant.EMPTY_STRING.equals(parameterValue) ? WebConstant.INT_ZERO : Integer.parseInt(parameterValue);
    }
}
