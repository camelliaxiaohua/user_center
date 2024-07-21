package camellia.model.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * <h6>队伍和用户信息封装类</h6>
 * @Datetime: 2024/7/21上午8:22
 * @author: Camellia.xioahua
 */
public class TeamUserVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7581736913448103683L;

    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 0-公开，1-私有，2-加密
     */
    private Integer status;


    /**
     * 创建时间
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

}
