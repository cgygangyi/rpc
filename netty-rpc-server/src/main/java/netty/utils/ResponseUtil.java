package netty.utils;

public class ResponseUtil {
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    public static Response setSuccessResponse() {
        Response response = new Response();
        response.setStatus(SUCCESS);
        return response;
    }

    public static Response setSuccessResponse(Object data) {
        Response response = new Response();
        response.setStatus(SUCCESS);
        response.setContent(data);
        return response;


    }

    public static Response setErrorResponse(String message) {
        Response response = new Response();
        response.setStatus(ERROR);
        response.setMessage(message);
        return response;
    }

}
