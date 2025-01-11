package com.onlinebetting.web.converter;

import java.lang.reflect.Parameter;

public interface ParameterConverter {

    boolean isMatch(Parameter parameter);

    Object convert(String parameterValue);

}
