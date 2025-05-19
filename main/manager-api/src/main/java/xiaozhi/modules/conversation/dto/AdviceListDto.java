package xiaozhi.modules.conversation.dto;

import lombok.Data;

@Data
public class AdviceListDto {
    private String Id;
    private String content;
    private String teacherId;
    private String teacherName;
    private String studentId;
    private String studentName;
    private String createDate;
}
