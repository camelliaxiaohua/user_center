package camellia.model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户注册请求体
 * @Datetime: 2024/7/11上午10:20
 * @author: Camellia.xioahua
 */
@Data
public class UserRegisterRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -5631184043973031395L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;

    private String planetCode;
}
