package com.db.quoters.bpp;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

abstract public class AbstractProxyBeanPostProcessor implements BeanPostProcessor {

    //    !!!!!!!!!!!!!!!! Concurrent or not ?!!!!!!!!!!!!!!!!
//    beanName => beanClass
    private final Map<String, Class<?>> beanTypes = new ConcurrentHashMap<>();
    private final Class<? extends Annotation> annotation;

    public AbstractProxyBeanPostProcessor(Class<? extends Annotation> annotation) {
        this.annotation = annotation;
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (Stream.of(bean.getClass().getMethods())
                .anyMatch(method -> method.isAnnotationPresent(annotation))) {
            beanTypes.put(beanName, bean.getClass());
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (beanTypes.containsKey(beanName)) {
            return proxyCreation(bean, beanName, beanTypes.get(beanName));
        }
        return bean;
    }

    public abstract Object proxyCreation(Object bean, String beanName, Class<?> beanType);
}
