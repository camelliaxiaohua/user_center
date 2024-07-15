package camellia.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 定义通用的返回类 BaseResponse，用于在 Java 项目中统一处理 API 的响应。
 * @Datetime: 2024/7/12下午12:22
 * @author: Camellia.xioahua
 */
@Data
public class BaseResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 5477638191965981955L;

    /**
     * 表示响应状态码
     */
    private int code;

    /**
     * 泛型类型的数据，表示响应的数据内容。
     */
    private T data;

    /**
     * 消息内容，用于描述响应的简短信息。
     */
    private String message;

    /**
     * 描述信息，用于提供更详细的响应说明。<br>
     * 根据需求，可删。
     */
    private String description;

    /**
     * 接收 code、data、message 和 description 参数，用于完全自定义响应。
     * @param code
     * @param data
     * @param message
     * @param description
     */
    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    /**
     * 省略 description 参数，将其默认为空字符串。
     * @param code
     * @param data
     * @param message
     */
    public BaseResponse(int code, T data, String message) {
        this(code, data, message, "");
    }

    /**
     * 省略 message 和 description 参数，将其默认为空字符串。
     * @param code
     * @param data
     */
    public BaseResponse(int code, T data) {
        this(code, data, "", "");
    }

    /**
     * 接受 ErrorCode 对象，用于处理错误响应。
     * @param errorCode
     */
    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }

}
