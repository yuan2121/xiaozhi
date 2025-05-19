package xiaozhi.modules.conversation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xiaozhi.common.service.impl.BaseServiceImpl;
import xiaozhi.modules.conversation.dao.MessageDao;
import xiaozhi.modules.conversation.dto.MessageDto;
import xiaozhi.modules.conversation.dto.MessageListDto;
import xiaozhi.modules.conversation.entity.MessageEntity;
import xiaozhi.modules.conversation.service.MessageService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl extends BaseServiceImpl<MessageDao, MessageEntity> implements MessageService {

    @Autowired
    private MessageDao messageDao;

    // 新增消息
    @Override
    public String add(MessageDto message) {
        MessageEntity messageEntity = new MessageEntity();
        // 将 DTO 转换为 Entity
        BeanUtils.copyProperties(message, messageEntity);

        // 执行插入操作
        int rows = messageDao.insert(messageEntity);
        if (rows <= 0) {
            throw new RuntimeException("新增消息失败");
        }

        // 返回新增消息的 ID
        return messageEntity.getId();
    }

    // 根据 ID 删除消息
    @Override
    public int deleteById(String id) {
        // 使用 MyBatis-Plus 的 deleteById 方法进行删除
        return messageDao.deleteById(id);
    }

    // 更新消息
    @Override
    public boolean update(MessageDto message) {
        MessageEntity entity = new MessageEntity();
        // 将 DTO 转换为 Entity
        BeanUtils.copyProperties(message, entity);

        // 执行更新操作
        return updateById(entity);
    }

    // 根据 ID 查询消息
    @Override
    public MessageDto getById(String id) {
        // 查询消息实体
        MessageEntity entity = messageDao.selectById(id);
        if (entity == null) {
            return null;
        }

        // 将 Entity 转换为 DTO
        MessageDto dto = new MessageDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    // 列表查询消息
    @Override
    public List<MessageListDto> list(Map<String, Object> params) {
        // 创建 QueryWrapper 用于构建查询条件
        QueryWrapper<MessageEntity> wrapper = new QueryWrapper<>();

        // 根据传入的 params 动态设置查询条件
        if (params.get("chatId") != null) {
            wrapper.eq("chat_id", params.get("chatId"));
        }

        // 按 create_date 字段倒序排列
        wrapper.orderByAsc("create_date");

        // 执行查询
        List<MessageEntity> entities = messageDao.selectList(wrapper);

        // 将查询结果转换为 DTO 列表
        return entities.stream().map(entity -> {
            MessageListDto dto = new MessageListDto();
            BeanUtils.copyProperties(entity, dto);
            // 返回转换后的 DTO
            return dto;
        }).collect(Collectors.toList());
    }

    // 分页查询消息（如果需要分页功能，取消注释并实现）
    // @Override
    // public PageData<MessageDto> page(Map<String, Object> params) {
    //     // 获取分页对象
    //     IPage<MessageEntity> page = getPage(params);
    //     QueryWrapper<MessageEntity> wrapper = new QueryWrapper<>();
    //     if (params.get("chatId") != null) {
    //         wrapper.eq("chat_id", params.get("chatId"));
    //     }
    //     if (params.get("userId") != null) {
    //         wrapper.eq("user_id", params.get("userId"));
    //     }
    //     // 执行分页查询
    //     messageDao.selectPage(page, wrapper);

    //     // 转 DTO 列表
    //     List<MessageDto> dtoList = page.getRecords().stream().map(entity -> {
    //         MessageDto dto = new MessageDto();
    //         BeanUtils.copyProperties(entity, dto);
    //         dto.setCreateTime(entity.getCreateDate());
    //         dto.setUpdateTime(entity.getUpdateDate());
    //         return dto;
    //     }).collect(Collectors.toList());

    //     // 返回分页数据
    //     return new PageData<>(dtoList, page.getTotal());
    // }
}
