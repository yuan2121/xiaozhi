package xiaozhi.modules.conversation.dao;

import org.apache.ibatis.annotations.Mapper;
import xiaozhi.common.dao.BaseDao;
import xiaozhi.modules.conversation.entity.ConversationEntity;

//Repository
@Mapper
public interface ConversationDao extends BaseDao<ConversationEntity> {

}
