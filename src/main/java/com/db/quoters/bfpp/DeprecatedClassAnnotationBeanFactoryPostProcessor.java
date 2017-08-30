package com.db.quoters.bfpp;

import com.db.quoters.DeprecatedClass;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by kinmanz on 30.08.17.
 */
public class DeprecatedClassAnnotationBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        Stream.of(beanFactory.getBeanNamesForAnnotation(DeprecatedClass.class))
                .map(s -> {
                    return new Object[]{beanFactory.getBeanDefinition(s),
                            beanFactory.getBean(s)
                                    .getClass()
                                    .getAnnotation(DeprecatedClass.class).value()};

                })
                .forEach(objects -> {
                    BeanDefinition beanDef = (BeanDefinition) objects[0];
                    Class<?> newClass = (Class<?>) objects[1];
                    beanDef.setBeanClassName(newClass.getName());
//                    beanFactory.re
                    System.out.println(beanDef.getBeanClassName());
                });

    }
}
