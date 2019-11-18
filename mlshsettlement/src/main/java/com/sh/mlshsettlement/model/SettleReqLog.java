package com.sh.mlshsettlement.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author jinghui.yu
 * @since 2019-11-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SettleReqLog implements Serializable {

    private static final long serialVersionUID = 1L;

    public SettleReqLog() {
    }

    public SettleReqLog(Integer type, String keyField, String request, String response, String exceptionMsg) {
        this.type = type;
        this.keyField = keyField;
        this.request = request;
        this.response = response;
        this.exceptionMsg = exceptionMsg;
    }

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 1:商户提现  2：用户提现  
     */
    @TableField("type")
    private Integer type;

    /**
     * 关键字段
     */
    @TableField("key_field")
    private String keyField;

    /**
     * 请求参数
     */
    @TableField("request")
    private String request;

    /**
     * 响应
     */
    @TableField("response")
    private String response;

    @TableField("exception_msg")
    private String exceptionMsg;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;


}
