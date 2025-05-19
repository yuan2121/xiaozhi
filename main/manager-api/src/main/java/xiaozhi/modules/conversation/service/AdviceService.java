package xiaozhi.modules.conversation.service;

import xiaozhi.common.service.BaseService;
import xiaozhi.modules.conversation.dto.AdviceListDto;
import xiaozhi.modules.conversation.dto.AdviceDto;
import xiaozhi.modules.conversation.entity.AdviceEntity;

import java.util.List;
import java.util.Map;

public interface AdviceService extends BaseService<AdviceEntity> {
    /**
     * 新增advice
     * @param adviceDto advice
     * @return 新增后生成的主键 ID
     */
    String add(AdviceDto adviceDto);

    /**
     * 查询建议列表
     * @param params  userId
     * @return advice DTO 列表
     */
    List<AdviceListDto> list(Map<String, Object> params);
}
