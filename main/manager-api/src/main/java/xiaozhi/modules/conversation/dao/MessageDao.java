package xiaozhi.modules.conversation.dao;

import org.apache.ibatis.annotations.Mapper;
import xiaozhi.common.dao.BaseDao;
import xiaozhi.modules.conversation.entity.MessageEntity;

//Repository
@Mapper
public interface MessageDao extends BaseDao<MessageEntity> {

}
