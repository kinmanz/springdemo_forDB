package com.db.quoters.bpp;

import com.db.quoters.Transactional;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;


public class TransactionalAnnotationBeanPostProcessor extends AbstractProxyBeanPostProcessor {

    private static final Class<? extends Annotation> annotation = Transactional.class;

    public TransactionalAnnotationBeanPostProcessor() {
        super(annotation);
    }

    @Override
    public Object proxyCreation(Object bean, String beanName, Class<?> beanType) {
        return  Proxy.newProxyInstance(beanType.getClassLoader(), beanType.getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Method classMethod = beanType.getMethod(method.getName(), method.getParameterTypes());
                if (classMethod.isAnnotationPresent(annotation)) {
                    return invokeWithTransaction(method, args, bean);
                }
                return method.invoke(bean, args);
            }
        });
    }

    // Do I have to throw exception farther?
    private Object invokeWithTransaction(Method method, Object[] args, Object bean) {
        System.out.println("TRANSACTION IS OPENED:");
        Object res = null;
        boolean flag = false;
        try {
            res = method.invoke(bean, args);
        } catch (Exception e) {
            flag = true;
            System.out.println("TRANSACTION ROLLED BACK: " + e);
        }
        if (!flag) System.out.println("TRANSACTION IS CLOSED:");
        return res;
    }
}
