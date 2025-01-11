package com.onlinebetting.web.converter.impl;


import com.onlinebetting.web.constants.WebConstant;
import com.onlinebetting.web.converter.ParameterConverter;

import java.lang.reflect.Parameter;

public class LongParameterConverter implements ParameterConverter {

    @Override
    public boolean isMatch(Parameter parameter) {
        return parameter.getParameterizedType().getTypeName().equals(Long.class.getName()) || parameter.getParameterizedType().getTypeName().equals(WebConstant.BASIC_TYPE_LONG);
    }

    @Override
    public Object convert(String parameterValue) {
        return WebConstant.EMPTY_STRING.equals(parameterValue) ? WebConstant.LONG_ZERO : Long.parseLong(parameterValue);
    }
}
