package camellia.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * <h6>队伍添加请求封装类</h6>
 * <p>
 * 此类用于封装添加新队伍的请求参数，包含队伍的基本信息，如名称、描述、最大人数、过期时间、用户ID、状态和密码。
 * </p>
 *
 * @Datetime: 2024/7/20 下午2:16
 * @Author: Camellia.xioahua
 */
@Data
public class TeamAddRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -4145157075157899310L;

    /**
     * 队伍名称
     * <p>
     * 用于指定队伍的名称。
     * </p>
     */
    private String name;

    /**
     * 队伍描述
     * <p>
     * 用于描述队伍的详细信息。
     * </p>
     */
    private String description;

    /**
     * 最大人数
     * <p>
     * 指定队伍的最大成员人数。
     * </p>
     */
    private Integer maxNum;

    /**
     * 过期时间
     * <p>
     * 指定队伍的过期时间，到期后队伍将不再有效。
     * </p>
     */
    private Date expireTime;

    /**
     * 用户ID
     * <p>
     * 用于标识创建队伍的用户。
     * </p>
     */
    private Long userId;

    /**
     * 队伍状态
     * <p>
     * 指定队伍的公开状态，0 表示公开，1 表示私有，2 表示加密。
     * </p>
     */
    private Integer status;

    /**
     * 密码
     * <p>
     * 如果队伍状态为加密，则需要指定队伍的访问密码。
     * </p>
     */
    private String password;
}
