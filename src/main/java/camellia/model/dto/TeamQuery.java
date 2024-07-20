package camellia.model.dto;

import camellia.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <h6>队伍查询封装类</h6>
 * <p>
 * 此类用于封装队伍查询的条件参数，继承自分页请求类 `PageRequest`。
 * 包含队伍的基本信息和查询条件，如队伍ID、名称、描述、最大人数、用户ID和队伍状态。
 * </p>
 *
 * @Datetime: 2024/7/20 上午9:39
 * @Author: Camellia.xioahua
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TeamQuery extends PageRequest {

    /**
     * 队伍ID
     * <p>
     * 用于精确查询特定队伍的唯一标识符。
     * </p>
     */
    private Long id;

    /**
     * 队伍名称
     * <p>
     * 用于根据队伍名称进行模糊查询。
     * </p>
     */
    private String name;

    /**
     * 队伍描述
     * <p>
     * 用于根据队伍描述进行模糊查询。
     * </p>
     */
    private String description;

    /**
     * 最大人数
     * <p>
     * 用于根据队伍的最大人数进行过滤查询。
     * </p>
     */
    private Integer maxNum;

    /**
     * 用户ID
     * <p>
     * 用于根据用户ID查询该用户创建或加入的队伍。
     * </p>
     */
    private Long userId;

    /**
     * 队伍状态
     * <p>
     * 用于根据队伍的公开状态进行过滤查询。
     * 0-公开，1-私有，2-加密。
     * </p>
     */
    private Integer status;
}
