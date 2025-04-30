package xiaozhi.modules.conversation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 会话传输对象，与 ConversationEntity 字段一一对应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageListDto {
    private String id;
    private String role;
    private String content;
    private LocalDateTime createDate;
}


