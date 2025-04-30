package xiaozhi.modules.conversation.entity;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("ai_chat_history")
public class ConversationEntity {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private Long userId;
    private String agentId;
    private String deviceId;
    private int messageCount;
    private Long creator;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    private Long updater;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateDate;



}