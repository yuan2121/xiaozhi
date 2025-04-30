package xiaozhi.modules.conversation.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@TableName("ai_chat_message")
public class MessageEntity {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private Long userId;
    private String chatId;
    private String role;
    private String content;
    private String promptTokens;
    private String totalTokens;
    private String completionTokens;
    private String promptMs;
    private String totalMs;
    private String completionMs;
    private Long creator;
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private LocalDateTime createDate;
    private Long updater;
    @TableField(value = "update_date", fill = FieldFill.INSERT)
    private LocalDateTime updateDate;
}