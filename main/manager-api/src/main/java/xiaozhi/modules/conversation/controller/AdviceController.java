package xiaozhi.modules.conversation.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xiaozhi.modules.conversation.dto.AdviceDto;
import xiaozhi.modules.conversation.dto.AdviceListDto;
import xiaozhi.modules.conversation.service.AdviceService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/conversation/advice")
public class AdviceController {

    @Autowired
    private AdviceService adviceService;


    // 增
    @Operation(summary = "新增摘要")
    @PostMapping("/add")
    public String add(@RequestBody AdviceDto advice) {
        adviceService.add(advice);
        return "success!";
    }

    @Operation(summary = "根据 学生userId 查询建议列表")
    @GetMapping("/list/user/{studentId}")
    public List<AdviceListDto> listByUser(@PathVariable("studentId") String userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("studentId", userId);
        return adviceService.list(params);
    }

}
