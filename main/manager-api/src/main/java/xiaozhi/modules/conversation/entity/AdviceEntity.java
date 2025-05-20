package xiaozhi.modules.conversation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("teacher_advice")
public class AdviceEntity {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;  // 主键

    private String content;

    @TableField("teacher_id") // 明确指定数据库字段名
    private String teacherId;

    @TableField("student_id")
    private String studentId;

    @TableField("create_date")
    private String createDate;
}


