package xiaozhi.modules.conversation.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xiaozhi.modules.conversation.dto.SummaryDto;
import xiaozhi.modules.conversation.entity.SummaryEntity;
import xiaozhi.modules.conversation.service.SummaryService;

@RestController
@RequestMapping("/conversation/summary")
public class SummaryController {

    @Autowired
    private SummaryService summaryService;


    // 根据ID获取摘要
    @Operation(summary = "获取对话摘要")
    @GetMapping("/get/{chatId}")
    public SummaryDto getSummaryById(@PathVariable("chatId") String chatId) {
        SummaryEntity summaryEntity = summaryService.getSummaryByChatId(chatId);
        if (summaryEntity == null) {
            throw new RuntimeException("对话摘要不存在: " + chatId); // 如果没有找到摘要，抛出异常
        }

        // 将 SummaryEntity 转换为 SummaryDto
        SummaryDto summaryDto = new SummaryDto();
        BeanUtils.copyProperties(summaryEntity, summaryDto); // 复制属性
        return summaryDto; // 返回 SummaryDto
    }




    // 更新摘要内容
    @Operation(summary = "更新摘要内容")
    @PutMapping("/update/{chatId}")
    public SummaryDto updateSummary(@PathVariable String chatId, @RequestBody SummaryDto summaryDto) {
        // 获取当前摘要信息
        SummaryEntity existingSummary = summaryService.getSummaryByChatId(chatId);
        if (existingSummary == null) {
            throw new RuntimeException("对话摘要不存在: " + chatId);  // 如果没有找到对应的摘要，抛出异常
        }

        // 更新摘要信息
        existingSummary.setSummary(summaryDto.getSummary());  // 更新摘要内容
        summaryService.updateById(existingSummary);  // 使用 service 层更新摘要

        // 转换为 DTO 返回
        SummaryDto updatedSummaryDto = new SummaryDto();
        BeanUtils.copyProperties(existingSummary, updatedSummaryDto);
        return updatedSummaryDto;  // 返回更新后的摘要
    }


    // 删除摘要
    @Operation(summary = "删除摘要")
    @DeleteMapping("/delete/{chatId}")
    public ResponseEntity<Void> deleteSummary(@PathVariable String chatId) {
        // 获取摘要信息
        SummaryEntity existingSummary = summaryService.getSummaryByChatId(chatId);
        if (existingSummary == null) {
            throw new RuntimeException("对话摘要不存在: " + chatId);  // 如果没有找到对应的摘要，抛出异常
        }

        // 删除摘要
        summaryService.deleteById(chatId);

        // 返回响应
        return ResponseEntity.noContent().build();  // 返回204 No Content响应
    }

}
