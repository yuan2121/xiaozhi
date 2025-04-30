package xiaozhi.modules.conversation.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xiaozhi.modules.conversation.dto.MessageDto;
import xiaozhi.modules.conversation.dto.MessageListDto;
import xiaozhi.modules.conversation.service.MessageService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private MessageService messageService;

    // 增
    @Operation(summary = "新增对话信息")
    @PostMapping("/add")
    public String add(@RequestBody MessageDto messageDto) {
        messageService.add(messageDto);
        return "success!";
    }
    
    

    // 查（根据 ID）
    @Operation(summary = "获取对话信息")
    @GetMapping("/get/{id}")
    public MessageDto get(@PathVariable("id") String id) {
        MessageDto entity = messageService.getById(id);
        if (entity == null) {
            throw new RuntimeException("对话信息不存在: " + id);
        }
        MessageDto dto = new MessageDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Operation(summary = "根据 chatId 查询对话信息")
    @GetMapping("/list/{chatId}")
    public List<MessageListDto> listByAgent(@PathVariable("chatId") String chatId) {
        Map<String, Object> params = new HashMap<>();
        params.put("chatId", chatId);
        return messageService.list(params);
    }
}
