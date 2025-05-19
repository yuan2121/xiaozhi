package xiaozhi.modules.conversation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xiaozhi.common.service.impl.BaseServiceImpl;
import xiaozhi.modules.conversation.dao.ConversationDao;
import xiaozhi.modules.conversation.dto.ConversationDto;
import xiaozhi.modules.conversation.dto.ConversationListDto;
import xiaozhi.modules.conversation.entity.ConversationEntity;
import xiaozhi.modules.conversation.service.ConversationService;
import xiaozhi.modules.device.service.DeviceService;
import xiaozhi.modules.device.service.impl.DeviceServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ConversationServiceImpl extends BaseServiceImpl<ConversationDao, ConversationEntity> implements ConversationService {

    @Autowired
    private ConversationDao conversationDao;


    @Override
    public String add(ConversationDto conversation) {
        ConversationEntity conversationEntity = new ConversationEntity();
        BeanUtils.copyProperties(conversation, conversationEntity);


        int rows = conversationDao.insert(conversationEntity);
        if (rows <= 0) {
            throw new RuntimeException("新增会话失败");
        }
        return conversationEntity.getId();
    }

    @Override
    public int deleteById(String id) {
        // BaseServiceImpl 提供了 deleteById 方法
        return conversationDao.deleteById(id);
    }

    @Override
    public boolean update(ConversationDto conversation) {
        ConversationEntity entity = new ConversationEntity();
        BeanUtils.copyProperties(conversation, entity);
        return updateById(entity);
    }

    @Override
    public ConversationDto getById(String id) {
        // 调用父类方法获取实体
        ConversationEntity entity = conversationDao.selectById(id);
        if (entity == null) {
            return null;
        }
        ConversationDto dto = new ConversationDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public List<ConversationListDto> list(Map<String, Object> params) {
        QueryWrapper<ConversationEntity> wrapper = new QueryWrapper<>();
        if (params.get("agentId") != null) {
            wrapper.eq("agent_id", params.get("agentId"));
        }
        // 按 create_date 倒序
        wrapper.orderByDesc("create_date");
        List<ConversationEntity> entities = conversationDao.selectList(wrapper);
        return entities.stream().map(entity -> {
            ConversationListDto dto = new ConversationListDto();
            BeanUtils.copyProperties(entity, dto);
            // 如果实体类中的字段名和 DTO 中的字段名不同，可以手动映射
            return dto;
        }).collect(Collectors.toList());
    }

}
