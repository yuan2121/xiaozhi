package xiaozhi.modules.sys.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import xiaozhi.modules.conversation.dto.ConversationListDto;
import xiaozhi.modules.sys.dto.SysUserDTO;
import xiaozhi.modules.sys.service.SysUserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Tag(name = "用户信息获取")
@AllArgsConstructor
public class UserController {
    private final SysUserService sysUserService;

    @GetMapping("/getUser/{userId}")
    public SysUserDTO getUserById(@PathVariable long userId) {
        return sysUserService.getByUserId(userId);
    }

    @PostMapping("/getUsers")
    public List<SysUserDTO> getUsersByIds(@RequestBody List<Long> userIds) {
        return sysUserService.getByUserIds(userIds);
    }

}
