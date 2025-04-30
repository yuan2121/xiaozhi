package xiaozhi.modules.conversation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ai_chat_summary")
public class SummaryEntity {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;  // 主键

    private String summary;  // 摘要内容

    private String chatId;  // 外键，关联ai_chat_history表的id字段
}


