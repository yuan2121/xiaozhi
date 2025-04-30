package xiaozhi.modules.conversation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * 会话传输对象，与 ConversationEntity 字段一一对应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationListDto {
    /** 代理/机器人 ID */
    private String id;
    private String agentId;
    private LocalDateTime updateDate;
    private int messageCount;
}
