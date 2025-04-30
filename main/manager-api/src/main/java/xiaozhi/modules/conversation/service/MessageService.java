package xiaozhi.modules.conversation.service;

import xiaozhi.common.service.BaseService;
import xiaozhi.modules.conversation.dto.MessageDto;
import xiaozhi.modules.conversation.dto.MessageListDto;
import xiaozhi.modules.conversation.entity.MessageEntity;

import java.util.List;
import java.util.Map;

/**
 * 消息相关业务接口
 */
public interface MessageService extends BaseService<MessageEntity> {

    /**
     * 新增消息
     * @param message 消息 DTO
     * @return 新增后生成的主键 ID
     */
    String add(MessageDto message);

    /**
     * 根据主键删除消息
     * @param id 消息主键
     * @return 删除影响行数
     */
    int deleteById(String id);

    /**
     * 更新消息
     * @param message 包含主键 ID 的消息 DTO
     * @return 更新是否成功
     */
    boolean update(MessageDto message);

    /**
     * 根据主键查询消息
     * @param id 消息主键
     * @return 消息 DTO，找不到返回 null
     */
    MessageDto getById(String id);

    /**
     * 列表查询消息
     * @param params 查询参数，如 agentId、userId、日期范围等
     * @return 消息列表 DTO
     */
    List<MessageListDto> list(Map<String, Object> params);
}
