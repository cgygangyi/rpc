package proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import annotation.RemoteInvoke;
import core.ClientRequest;
import core.TcpClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.stereotype.Component;
import protocal.Response;

@Component
public class InvokeProxy implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("[Proxy] Checking bean: " + beanName);
        System.out.println("[Proxy] Bean class: " + bean.getClass().getName());

        Field[] fields = bean.getClass().getDeclaredFields();
        System.out.println("[Proxy] Found " + fields.length + " fields in bean");

        for (Field field : fields) {
            System.out.println("[Proxy] Checking field: " + field.getName() + " of type: " + field.getType());
            
            if (field.isAnnotationPresent(RemoteInvoke.class)) {
                System.out.println("[Proxy] Found @RemoteInvoke annotation on field: " + field.getName());
                field.setAccessible(true);

                final HashMap<Method, Class> methodMap = new HashMap<>();
                putMethodClass(methodMap, field);
                System.out.println("[Proxy] Created method map with " + methodMap.size() + " methods");
                
                Enhancer enhancer = getEnhancer(field, methodMap);
                System.out.println("[Proxy] Created enhancer for interface: " + field.getType().getName());
                
                try {
                    Object proxy = enhancer.create();
                    System.out.println("[Proxy] Successfully created proxy object");
                    field.set(bean, proxy);
                    System.out.println("[Proxy] Successfully injected proxy into field: " + field.getName());
                } catch (Exception e) {
                    System.err.println("[Proxy] Error creating proxy: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }

    private static Enhancer getEnhancer(Field field, HashMap<Method, Class> methodMap) {
        System.out.println("[Proxy] Creating enhancer for field: " + field.getName());
        
        Enhancer enhancer = new Enhancer();
        enhancer.setInterfaces(new Class[]{field.getType()});
        
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            System.out.println("[Proxy] Intercepting method call: " + method.getName());
            System.out.println("[Proxy] Method arguments: " + (args != null && args.length > 0 ? args[0] : "none"));
            
            ClientRequest request = new ClientRequest();
            String className = methodMap.get(method).getName() + "." + method.getName();
            request.setCommand(className);
            request.setContent(args[0]);
            
            System.out.println("[Proxy] Created request with command: " + className);
            System.out.println("[Proxy] Request ID: " + request.getId());
            
            Response response = TcpClient.send(request);
            System.out.println("[Proxy] Received response for request ID: " + response.getId());
            
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
        System.out.println("[Proxy] Scanning methods for field: " + field.getName());
        
        for(Method method : methods){
            System.out.println("[Proxy] Found method: " + method.getName() + 
                             " with return type: " + method.getReturnType().getName() +
                             " and parameter types: " + java.util.Arrays.toString(method.getParameterTypes()));
            methodMap.put(method, field.getType());
        }
    }
}
