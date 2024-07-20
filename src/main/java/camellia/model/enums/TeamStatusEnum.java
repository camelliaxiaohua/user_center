package camellia.model.enums;

/**
 * <h6>队伍状态枚举类</h6>
 * <p>
 * 此枚举类定义了队伍的不同状态，用于标识队伍的访问权限和公开程度。
 * 枚举值包括公开、私有和加密三种状态。
 * </p>
 *
 * @Datetime: 2024/7/20 上午11:37
 * @Author: Camellia.xioahua
 */
public enum TeamStatusEnum {

    /**
     * 公开状态：队伍对所有人可见和可加入。
     */
    PUBLIC(0, "公开"),

    /**
     * 私有状态：队伍仅对特定成员可见和可加入。
     */
    PRIVATE(1, "私有"),

    /**
     * 加密状态：队伍对特定成员可见，需要密码才能加入。
     */
    SECRET(2, "加密");

    /**
     * 状态的整数值，用于在数据库中存储和标识队伍状态。
     */
    private int value;

    /**
     * 状态的文本描述，用于前端展示和用户阅读。
     */
    private String text;



    /**
     * 私有构造函数，用于初始化枚举常量的值和文本描述。
     *
     * @param value 整数值，表示队伍状态。
     * @param text 文本描述，表示队伍状态的名称。
     */
    private TeamStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 根据整数值获取对应的枚举常量。
     *
     * @param value 整数值，表示队伍状态。
     * @return 对应的枚举常量，如果找不到则返回 null。
     */
    public static TeamStatusEnum getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        TeamStatusEnum[] values = TeamStatusEnum.values();
        for (TeamStatusEnum status : values) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return null;
    }

    /**
     * 获取枚举常量的整数值。
     *
     * @return 整数值，表示队伍状态。
     */
    public int getValue() {
        return value;
    }

    /**
     * 设置枚举常量的整数值。
     *
     * @param value 整数值，表示队伍状态。
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * 获取枚举常量的文本描述。
     *
     * @return 文本描述，表示队伍状态的名称。
     */
    public String getText() {
        return text;
    }

    /**
     * 设置枚举常量的文本描述。
     *
     * @param text 文本描述，表示队伍状态的名称。
     */
    public void setText(String text) {
        this.text = text;
    }
}
