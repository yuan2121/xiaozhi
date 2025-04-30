package xiaozhi.modules.conversation.service;

import xiaozhi.common.service.BaseService;
import xiaozhi.modules.conversation.dto.SummaryDto;
import xiaozhi.modules.conversation.entity.MessageEntity;
import xiaozhi.modules.conversation.entity.SummaryEntity;

public interface SummaryService extends BaseService<SummaryEntity> {
    /**
     * 获取智能体的摘要
     *
     * @param chatId 智能体ID
     * @return 摘要
     */
    SummaryEntity getSummaryByChatId(String chatId);

    /**
     * 更新智能体的摘要
     *
     * @param summaryDto chatId与summary
     */
    void updateSummaryByChatId(SummaryDto summaryDto);
}
