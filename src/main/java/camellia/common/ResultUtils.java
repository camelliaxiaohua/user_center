package camellia.common;

/**
 *定义一个工具类 ResultUtils，用于生成 BaseResponse 对象，从而简化成功和失败响应的创建。
 * @Datetime: 2024/7/12下午12:38
 * @author: Camellia.xioahua
 */
public class ResultUtils {

    /**
     * 成功响应方法
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "成功");
    }

    /**
     * 接收一个 ErrorCode 对象，返回一个包含错误码信息的 BaseResponse 对象。
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 接收状态码、消息和描述参数，返回一个自定义错误信息的 BaseResponse 对象。
     *
     * @param code
     * @param message
     * @param description
     * @return
     */
    public static BaseResponse error(int code, String message, String description) {
        return new BaseResponse(code, null, message, description);
    }

    /**
     * 接收 ErrorCode 对象、自定义消息和描述，返回一个包含详细信息的 BaseResponse 对象。
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode, String message, String description) {
        return new BaseResponse(errorCode.getCode(), null, message, description);
    }


    /**
     * 接收 ErrorCode 对象和自定义描述，返回一个包含详细信息的 BaseResponse 对象。
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode, String description) {
        return new BaseResponse(errorCode.getCode(), errorCode.getMessage(), description);
    }
}
