package xiaozhi.modules.conversation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xiaozhi.common.service.impl.BaseServiceImpl;
import xiaozhi.modules.conversation.dao.AdviceDao;
import xiaozhi.modules.conversation.dto.AdviceDto;
import xiaozhi.modules.conversation.dto.AdviceListDto;
import xiaozhi.modules.conversation.entity.AdviceEntity;
import xiaozhi.modules.conversation.service.AdviceService;
import xiaozhi.modules.sys.dao.SysUserDao;
import xiaozhi.modules.sys.entity.SysUserEntity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdviceServiceImpl extends BaseServiceImpl<AdviceDao, AdviceEntity> implements AdviceService {

    @Autowired
    private AdviceDao adviceDao;  // 注入 AdviceDao，它继承了 BaseDao
    @Autowired
    private SysUserDao sysUserDao;

    @Override
    public String add(AdviceDto adviceDto) {
        AdviceEntity adviceEntity = new AdviceEntity();
        BeanUtils.copyProperties(adviceDto, adviceEntity);
        int rows = adviceDao.insert(adviceEntity);
        if (rows <= 0) {
            throw new RuntimeException("新增会话失败");
        }
        return adviceEntity.getId();
    }


    @Override
    public List<AdviceListDto> list(Map<String, Object> params) {
        QueryWrapper<AdviceEntity> wrapper = new QueryWrapper<>();
        if (params.get("studentId") != null) {
            wrapper.eq("student_id", params.get("studentId"));
        }
        wrapper.orderByAsc("create_date");

        List<AdviceEntity> entities = adviceDao.selectList(wrapper);

        return entities.stream().map(entity -> {
            AdviceListDto dto = new AdviceListDto();
            BeanUtils.copyProperties(entity, dto);

            // 通过 studentId 查用户
            SysUserEntity user = sysUserDao.selectById(entity.getStudentId());
            if (user != null) {
                dto.setStudentName(user.getRealName());
            }

            return dto;
        }).collect(Collectors.toList());
    }

}
