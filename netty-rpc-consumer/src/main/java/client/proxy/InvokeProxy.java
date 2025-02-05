package client.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import client.annotation.RemoteInvoke;
import client.core.ClientRequest;
import client.core.TcpClient;
import client.core.param.Response;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.stereotype.Component;

@Component
public class InvokeProxy implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("[Proxy] Checking bean: " + beanName);

        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(RemoteInvoke.class)) {
                field.setAccessible(true);

                final HashMap<Method, Class> methodMap = new HashMap<>();
                putMethodClass(methodMap, field);
                Enhancer enhancer = getEnhancer(field, methodMap);
                
                try {
                    field.set(bean, enhancer.create());
                } catch (Exception e) {
                    System.err.println("[Proxy] Error creating proxy: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }

    private static Enhancer getEnhancer(Field field, HashMap<Method, Class> methodMap) {
        Enhancer enhancer = new Enhancer();
        enhancer.setInterfaces(new Class[]{field.getType()});
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            ClientRequest request = new ClientRequest();
            String className = methodMap.get(method).getName().replace("client.", "netty.");
            request.setCommand(className + "." + method.getName());
            request.setContent(args[0]);
            Response response = TcpClient.send(request);
            return response;
        });
        return enhancer;
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private void putMethodClass(HashMap<Method, Class> methodMap, Field field) {
        Method[] methods = field.getType().getDeclaredMethods();
        for(Method method : methods){
            methodMap.put(method, field.getType());
        }
    }
}
