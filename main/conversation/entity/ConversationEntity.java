package xiaozhi.modules.conversation.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

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
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private LocalDateTime createDate;
    private Long updater;
    @TableField(value = "update_date", fill = FieldFill.INSERT)
    private LocalDateTime updateDate;

}