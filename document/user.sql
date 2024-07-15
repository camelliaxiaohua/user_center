-- auto-generated definition
create table user
(
    id            bigint auto_increment comment 'id'
        primary key,
    username      varchar(255)  null comment '用户名称',
    user_account  varchar(255)  null comment '账号',
    avatar_url    varchar(1024) null comment '用户头像',
    gender        tinyint       null comment '性别',
    user_password varchar(255)  not null comment '密码',
    phone         varchar(255)  null comment '电话',
    email         varchar(255)  null comment '邮箱',
    user_status   int           null comment '状态',
    create_time   datetime      null comment '创建时间',
    update_time   datetime      null comment '更新时间',
    is_delete     tinyint       null comment '是否删除(逻辑删除)',
    user_role     int           null comment '0-普通用户，1-管理员',
    planet_code   varchar(512)  null comment '编号'
);

