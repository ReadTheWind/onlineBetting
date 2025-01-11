package com.onlinebetting.web;

import com.onlinebetting.web.converter.ParameterConverter;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.CopyOnWriteArrayList;

public class ParameterConverterFactory {

    private ParameterConverter defaultConverter;

    private final List<ParameterConverter> parameterConverters = new CopyOnWriteArrayList<>();

    public ParameterConverter getConverter(Parameter parameter) {
        for (ParameterConverter converter : parameterConverters) {
            if (converter.isMatch(parameter)) {
                return converter;
            }
        }
        return defaultConverter;
    }

    public void initConverters() {
        ServiceLoader<ParameterConverter> converters = ServiceLoader.load(ParameterConverter.class);
        for (ParameterConverter converter : converters) {
            parameterConverters.add(converter);
            defaultConverter = converter;
        }
    }

}
