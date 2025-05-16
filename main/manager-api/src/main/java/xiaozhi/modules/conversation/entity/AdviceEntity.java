package xiaozhi.modules.conversation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("teacher_advice")
public class AdviceEntity {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;  // 主键

    private String content;
    private String teacherId;
    private String studentId;
    private String createDate;
}


