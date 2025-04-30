package xiaozhi.modules.conversation.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 会话传输对象，与 ConversationEntity 字段一一对应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDto {
    /** 主键 UUID */
    private String id;

    /** 用户 ID */
    private Long userId;

    /** 代理/机器人 ID */
    private String agentId;

    /** 设备 ID */
    private String deviceId;

    /** 本次会话的消息数量 */
    private Integer messageCount;

    /** 创建人（通常与 userId 相同） */
    private Long creator;

    /** 创建时间字符串，例如 "2025-04-27 15:30:00" */
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    /** 更新人 */
    private Long updater;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateDate;


}
