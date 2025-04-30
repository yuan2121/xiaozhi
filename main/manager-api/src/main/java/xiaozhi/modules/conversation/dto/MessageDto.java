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
public class MessageDto {
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
    private LocalDateTime createDate;
    private Long updater;
    private LocalDateTime updateDate;
}
