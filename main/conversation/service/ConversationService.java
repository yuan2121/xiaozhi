package xiaozhi.modules.conversation.service;

import xiaozhi.common.service.BaseService;
import xiaozhi.modules.conversation.dto.ConversationDto;
import xiaozhi.modules.conversation.dto.ConversationListDto;
import xiaozhi.modules.conversation.entity.ConversationEntity;

import java.util.List;
import java.util.Map;

/**
 * 会话相关业务接口
 */
public interface ConversationService extends BaseService<ConversationEntity> {

    /**
     * 新增会话
     * @param conversation 会话 DTO
     * @return 新增后生成的主键 ID
     */
    String add(ConversationDto conversation);

    /**
     * 根据主键删除会话
     *
     * @param id 会话主键
     * @return 删除是否成功
     */
    int deleteById(String id);

    /**
     * 更新会话
     * @param conversation 包含主键 ID 的会话 DTO
     * @return 更新是否成功
     */
    boolean update(ConversationDto conversation);

    /**
     * 根据主键查询会话
     * @param id 会话主键
     * @return 会话 DTO，找不到返回 null
     */
    ConversationDto getById(String id);

    /**
     * 列表查询会话
     * @param params 查询参数，如 agentId、userId、日期范围等
     * @return 会话 DTO 列表
     */
    List<ConversationListDto> list(Map<String, Object> params);

//    /**
//     * 分页查询会话
//     * @param params 分页及过滤参数，需包含 page、limit、agentId 等
//     * @return 分页结果
//     */
//    PageData<ConversationDto> page(Map<String, Object> params);
}
