package xiaozhi.modules.conversation.controller;

import io.swagger.v3.oas.annotations.Operation;
import xiaozhi.modules.conversation.dto.ConversationDto;
import xiaozhi.modules.conversation.dto.ConversationListDto;
import xiaozhi.modules.conversation.entity.ConversationEntity;
import xiaozhi.modules.conversation.service.ConversationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/conversation")
public class ConversationController {
    @Autowired
    private ConversationService conversationService;

    // 增
    @Operation(summary = "新增会话")
    @PostMapping("/add")
    public String add(@RequestBody ConversationDto conversation) {
        conversationService.add(conversation);
        return "success!";
    }

//    // 删
//    @Operation(summary = "删除会话")
//    @DeleteMapping("/delete/{id}")
//    public String delete(@PathVariable("id") String id) {
//        boolean removed = conversationService.removeById(id);
//        if (!removed) {
//            throw new RuntimeException("删除会话失败: " + id);
//        }
//        return "success!";
//    }

    // 改
    @Operation(summary = "更新会话")
    @PutMapping("/update")
    public String update(@RequestBody ConversationDto conversation) {
        ConversationEntity entity = new ConversationEntity();
        BeanUtils.copyProperties(conversation, entity);
        boolean updated = conversationService.updateById(entity);
        if (!updated) {
            throw new RuntimeException("更新会话失败: " + conversation.getId());
        }
        return "success!";
    }

    // 查（根据 ID）
    @Operation(summary = "获取会话详情")
    @GetMapping("/get/{id}")
    public ConversationDto get(@PathVariable("id") String id) {
        ConversationDto entity = conversationService.getById(id);
        if (entity == null) {
            throw new RuntimeException("会话不存在: " + id);
        }
        ConversationDto dto = new ConversationDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Operation(summary = "根据 agentId 查询会话列表")
    @GetMapping("/list/agent/{agentId}")
    public List<ConversationListDto> listByAgent(@PathVariable("agentId") String agentId) {
        Map<String, Object> params = new HashMap<>();
        params.put("agentId", agentId);
        return conversationService.list(params);
    }
}
