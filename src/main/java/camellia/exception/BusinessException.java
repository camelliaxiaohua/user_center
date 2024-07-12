package camellia.exception;

import camellia.common.ErrorCode;

/**
 * 定义自定义异常类 BusinessException，用于处理业务相关的异常情况。
 * @Datetime: 2024/7/12下午2:19
 * @author: Camellia.xioahua
 */
public class BusinessException extends RuntimeException {

    /**
     * 异常码
     */
    private final int code;

    /**
     * 描述
     */
    private final String description;

    /**
     * 接收异常消息 message、错误码 code 和描述 description，用于创建自定义异常。
     * @param message
     * @param code
     * @param description
     */
    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    /**
     * 接收 ErrorCode 枚举对象，用于创建异常，使用枚举中定义的错误码和描述。
     * @param errorCode
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    /**
     * 接收 ErrorCode 枚举对象和自定义描述 description，用于创建异常，保留枚举中定义的错误消息，但使用自定义的描述信息。
     * @param errorCode
     * @param description
     */
    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

    public int getCode() {
        return code;
    }


    public String getDescription() {
        return description;
    }
}
