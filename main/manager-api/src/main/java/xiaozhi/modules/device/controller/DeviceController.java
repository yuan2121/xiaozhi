package xiaozhi.modules.device.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import xiaozhi.common.exception.ErrorCode;
import xiaozhi.common.redis.RedisKeys;
import xiaozhi.common.redis.RedisUtils;
import xiaozhi.common.user.UserDetail;
import xiaozhi.common.utils.Result;
import xiaozhi.modules.agent.service.AgentService;
import xiaozhi.modules.device.dto.DeviceRegisterDTO;
import xiaozhi.modules.device.dto.DeviceUnBindDTO;
import xiaozhi.modules.device.entity.DeviceEntity;
import xiaozhi.modules.device.service.DeviceService;
import xiaozhi.modules.security.user.SecurityUser;
import org.springframework.transaction.annotation.Transactional;

@Tag(name = "设备管理")
@AllArgsConstructor
@RestController
@RequestMapping("/device")
public class DeviceController {
    private final DeviceService deviceService;
    private final AgentService agentService;

    private final RedisUtils redisUtils;

    @PostMapping("/bind/{agentId}/{deviceCode}")
    @Operation(summary = "绑定设备")
    @RequiresPermissions("sys:role:normal")
    public Result<Void> bindDevice(@PathVariable String agentId, @PathVariable String deviceCode) {
        deviceService.deviceActivation(agentId, deviceCode);
        return new Result<>();
    }

    @PostMapping("/register")
    @Operation(summary = "注册设备")
    public Result<String> registerDevice(@RequestBody DeviceRegisterDTO deviceRegisterDTO) {
        String macAddress = deviceRegisterDTO.getMacAddress();
        if (StringUtils.isBlank(macAddress)) {
            return new Result<String>().error(ErrorCode.NOT_NULL, "mac地址不能为空");
        }
        // 生成六位验证码
        String code = String.valueOf(Math.random()).substring(2, 8);
        String key = RedisKeys.getDeviceCaptchaKey(code);
        String existsMac = null;
        do {
            existsMac = (String) redisUtils.get(key);
        } while (StringUtils.isNotBlank(existsMac));

        redisUtils.set(key, macAddress);
        return new Result<String>().ok(code);
    }

    @GetMapping("/bind/{agentId}")
    @Operation(summary = "获取已绑定设备")
    @RequiresPermissions("sys:role:normal")
    public Result<List<DeviceEntity>> getUserDevices(@PathVariable String agentId) {
        UserDetail user = SecurityUser.getUser();
        List<DeviceEntity> devices = deviceService.getUserDevices(user.getId(), agentId);
        return new Result<List<DeviceEntity>>().ok(devices);
    }


    @GetMapping("/getAll")
    @Operation(summary = "获取所有设备")
//    @RequiresPermissions("sys:role:normal")
    public Result<List<DeviceEntity>> getAllDevices() {
        List<DeviceEntity> devices = deviceService.getAllDevices();
        return new Result<List<DeviceEntity>>().ok(devices);
    }

    @PostMapping("/unbind")
    @Operation(summary = "解绑设备")
    @RequiresPermissions("sys:role:normal")
    public Result<Void> unbindDevice(@RequestBody DeviceUnBindDTO unDeviveBind) {
        UserDetail user = SecurityUser.getUser();
        deviceService.unbindDevice(user.getId(), unDeviveBind.getDeviceId());
        return new Result<Void>();
    }

    @PutMapping("/enableOta/{id}/{status}")
    @Operation(summary = "启用/关闭OTA自动升级")
    @RequiresPermissions("sys:role:normal")
    public Result<Void> enableOtaUpgrade(@PathVariable String id, @PathVariable Integer status) {
        DeviceEntity entity = deviceService.selectById(id);
        if (entity == null) {
            return new Result<Void>().error("设备不存在");
        }
        entity.setAutoUpdate(status);
        deviceService.updateById(entity);
        return new Result<Void>();
    }



    @PutMapping("/updateBatchAgentId/{agentId}")
    @Operation(summary = "批量更改设备的agentId")
//    @RequiresPermissions("sys:role:normal")
    @Transactional(rollbackFor = Exception.class) // 添加事务控制,防止错误id导致仅更新部分内容，产生数据不一致的问题
    public Result<Void> updateBatchAgentId(@PathVariable String agentId, @RequestBody List<String> deviceIds) {
        // 先检查 agentId 是否存在
        if (agentService.getAgentById(agentId) == null) {
            return new Result<Void>().error("agentId 不存在: " + agentId);
        }

        List<DeviceEntity> updateList = new ArrayList<>();

        for (String deviceId : deviceIds) {
            DeviceEntity entity = deviceService.selectById(deviceId);
            if (entity == null) {
                // 抛出异常，触发事务回滚
                return new Result<Void>().error("device does not exist, ID: " + deviceId);
            }
            entity.setAgentId(agentId);
            updateList.add(entity);
        }

        // 执行批量更新，MyBatis-Plus 默认分批（每批1000）
        deviceService.updateBatchById(updateList);

        return new Result<Void>().ok(null);
    }


}