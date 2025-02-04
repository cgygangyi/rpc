package netty.medium;

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
        if (bean.getClass().getAnnotation(Controller.class) != null) {
            Method[] methods = bean.getClass().getDeclaredMethods();
            for (Method method : methods) {
                String beanMethod = bean.getClass().getName() + "." + method.getName();
                System.out.println("Registering method: " + beanMethod);
                
                BeanMethod beanMethodObj = new BeanMethod();
                beanMethodObj.setBean(bean);
                beanMethodObj.setMethod(method);
                
                Media.getInstance().registerBean(beanMethod, beanMethodObj);
            }
        }
        return bean;
    }
}

