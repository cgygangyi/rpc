package netty.medium;

import com.alibaba.fastjson.JSONObject;
import netty.handler.param.ServerRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class Media {
    public static Map<String, BeanMethod> beanMap = new HashMap<>();

    private static Media m = null;

    public static Media getInstance() {
        if (m == null) {
            m = new Media();
        }
        return m;
    }

    public void registerBean(String beanName, BeanMethod beanMethod) {
        beanMap.put(beanName, beanMethod);
    }

    public BeanMethod getBeanMethod(String beanName) {
        return beanMap.get(beanName);
    }

    public Object process(ServerRequest request) throws InvocationTargetException, IllegalAccessException {
        String command = request.getCommand();
        BeanMethod beanMethod = beanMap.get(command);
        if (beanMethod == null) {
            throw new RuntimeException("Command not found: " + command);
        }
        Object bean = beanMethod.getBean();
        Method method = beanMethod.getMethod();
        Class parameterType = method.getParameterTypes()[0];
        Object content = request.getContent();
        
        Object args = JSONObject.parseObject(content.toString(), parameterType);
        return method.invoke(bean, args);
    }
}
