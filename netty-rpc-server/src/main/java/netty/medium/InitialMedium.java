package netty.medium;

import netty.annotation.Remote;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Method;

@Component
public class InitialMedium implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (bean.getClass().getAnnotation(Remote.class) != null) {
            Method[] methods = bean.getClass().getDeclaredMethods();

            String className = bean.getClass().getInterfaces()[0].getName();

            for (Method method : methods) {
                String key = className + "." + method.getName();
                System.out.println("Registering method: " + key);

                BeanMethod beanMethod = new BeanMethod();
                beanMethod.setBean(bean);
                beanMethod.setMethod(method);

                Media.getInstance().registerBean(key, beanMethod);
            }
        }
        return bean;
    }
}

