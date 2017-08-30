package com.db.quoters.bpp;

import com.db.quoters.Benchmark;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class BenchmarkAnnotationBeanPostProcessor extends AbstractProxyBeanPostProcessor {

    private static final Class<? extends Annotation> annotation = Benchmark.class;

    public BenchmarkAnnotationBeanPostProcessor() {
        super(annotation);
    }

    @Override
    public Object proxyCreation(Object bean, String beanName, Class<?> beanType) {
        return  Proxy.newProxyInstance(beanType.getClassLoader(), beanType.getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Method classMethod = beanType.getMethod(method.getName(), method.getParameterTypes());
                if (classMethod.isAnnotationPresent(annotation)) {
                    return invokeWithBenchmark(method, args, bean);
                }
                return method.invoke(bean, args);
            }

            @SneakyThrows
            private Object invokeWithBenchmark(Method method, Object[] args, Object bean) {
                System.out.println("BENCHMARK START: ");
                long start = System.nanoTime();

                Object res = method.invoke(bean, args);

                long end = System.nanoTime();
                System.out.println("BENCHMARK END: " + (end - start));
                return res;
            }
        });
    }
}
