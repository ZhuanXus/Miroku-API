/* v1__init.sql */

/* 建表语句 */
CREATE TABLE response
(
    id      VARCHAR(64) PRIMARY KEY COMMENT '响应ID',
    object  VARCHAR(32)  NOT NULL,
    created BIGINT       NOT NULL COMMENT '创建时间',
    model   VARCHAR(128) NOT NULL COMMENT '模型名称'
) ENGINE = InnoDB
  DEFAULT CHARSET utf8mb4 COMMENT '响应表';

CREATE TABLE choices
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '单条详细回复ID',
    response_id     VARCHAR(64) NOT NULL COMMENT '关联的响应ID',
    index_          INT         NOT NULL COMMENT '索引',
    message_content TEXT        NOT NULL COMMENT '回复内容',
    message_role    VARCHAR(32) NOT NULL COMMENT '回复角色',
    finish_reason   VARCHAR(32) NOT NULL COMMENT '回复结束原因'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '单条详细回复表';