package xiaozhi.modules.conversation.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xiaozhi.common.service.impl.BaseServiceImpl;
import xiaozhi.modules.conversation.dao.SummaryDao;
import xiaozhi.modules.conversation.dto.ConversationDto;
import xiaozhi.modules.conversation.dto.SummaryDto;
import xiaozhi.modules.conversation.entity.ConversationEntity;
import xiaozhi.modules.conversation.entity.SummaryEntity;
import xiaozhi.modules.conversation.service.SummaryService;

@Service
public class SummaryServiceImpl extends BaseServiceImpl<SummaryDao, SummaryEntity> implements SummaryService {

    @Autowired
    private SummaryDao summaryDao;  // 注入 SummaryDao，它继承了 BaseDao

    @Override
    public String add(SummaryDto summaryDto) {
        SummaryEntity summaryEntity = new SummaryEntity();
        BeanUtils.copyProperties(summaryDto, summaryEntity);
        int rows = summaryDao.insert(summaryEntity);
        if (rows <= 0) {
            throw new RuntimeException("新增会话失败");
        }
        return summaryEntity.getId();
    }

    @Override
    public SummaryEntity getSummaryByChatId(String chatId) {
        // 使用 MyBatis-Plus 提供的查询方法，按 chatId 查找对应的 SummaryEntity
        return summaryDao.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SummaryEntity>()
                        .eq("chat_id", chatId)
        );
    }

    @Override
    public void updateSummaryByChatId(SummaryDto summaryDto) {
        String chatId = summaryDto.getChatId();
        String summary = summaryDto.getSummary();

        // 查找是否存在对应的 SummaryEntity
        SummaryEntity existingSummary = getSummaryByChatId(chatId);
        if (existingSummary != null) {
            // 如果存在，更新该条记录
            existingSummary.setSummary(summary);
            summaryDao.updateById(existingSummary);  // 使用 BaseDao 的 updateById 方法
        } else {
            // 如果没有找到该记录，创建新记录
            SummaryEntity newSummary = new SummaryEntity();
            newSummary.setChatId(chatId);
            newSummary.setSummary(summary);
            summaryDao.insert(newSummary);  // 使用 BaseDao 的 insert 方法
        }
    }
}
