package camellia.model.request;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户登入请求体
 * @Datetime: 2024/7/11上午10:47
 * @author: Camellia.xioahua
 */
@Data
public class UserLoginRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 4541422015919271496L;

    private String userAccount;

    private String userPassword;

}
