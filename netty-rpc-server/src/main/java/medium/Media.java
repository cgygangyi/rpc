package medium;

import com.alibaba.fastjson.JSONObject;
import handler.param.ServerRequest;
import utils.Response;
import utils.ResponseUtil;

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

    public Response process(ServerRequest request) {
        Response response = new Response();
        try {
            String command = request.getCommand();
            BeanMethod beanMethod = beanMap.get(command);
            if (beanMethod == null) {
                response = ResponseUtil.setErrorResponse("Command not found: " + command);
                response.setId(request.getId());
                return response;
            }

            Object bean = beanMethod.getBean();
            Method method = beanMethod.getMethod();
            Class parameterType = method.getParameterTypes()[0];
            Object content = request.getContent();
        
            Object args = JSONObject.parseObject(content.toString(), parameterType);
            response = (Response) method.invoke(bean, args);
            response.setId(request.getId());
            
            return response;
        } catch (Exception e) {
            response = ResponseUtil.setErrorResponse(e.getMessage());
            response.setId(request.getId());
            return response;
        }
    }
}
