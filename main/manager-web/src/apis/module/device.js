import { getServiceUrl } from '../api';
import RequestService from '../httpRequest';

export default {
    // 已绑设备
    getAgentBindDevices(agentId, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/device/bind/${agentId}`)
            .method('GET')
            .success((res) => {
                RequestService.clearRequestTime();
                callback(res);
            })
            .fail((err) => {
                console.error('获取设备列表失败:', err);
                RequestService.reAjaxFun(() => {
                    this.getAgentBindDevices(agentId, callback);
                });
            }).send();
    },
    // 解绑设备
    unbindDevice(device_id, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/device/unbind`)
            .method('POST')
            .data({ deviceId: device_id })
            .success((res) => {
                RequestService.clearRequestTime();
                callback(res);
            })
            .fail((err) => {
                console.error('解绑设备失败:', err);
                RequestService.reAjaxFun(() => {
                    this.unbindDevice(device_id, callback);
                });
            }).send();
    },
    // 绑定设备
    bindDevice(agentId, deviceCode, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/device/bind/${agentId}/${deviceCode}`)
            .method('POST')
            .success((res) => {
                RequestService.clearRequestTime();
                callback(res);
            })
            .fail((err) => {
                console.error('绑定设备失败:', err);
                RequestService.reAjaxFun(() => {
                    this.bindDevice(agentId, deviceCode, callback);
                });
            }).send();
    },
    // 获取所有设备列表
    getAllDevices(callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/device/getAll`)
            .method('GET')
            .success((res) => {
                // 清除重试计时
                RequestService.clearRequestTime();
                // 调用上层回调
                callback(res);
            })
            .fail((err) => {
                console.error('获取所有设备失败:', err);
                // 失败后重试
                RequestService.reAjaxFun(() => {
                    this.getAllDevices(callback);
                });
            })
            .send();
    },

// 批量迁移设备到指定智能体
    updateBatchAgentId(agentId, deviceIds, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/device/updateBatchAgentId/${agentId}`)
            .method('PUT')
            .data(deviceIds)
            .success((res) => {
                RequestService.clearRequestTime();
                callback(res);
            })
            .fail((err) => {
                console.error('批量迁移设备失败:', err);
                this.$message.error(err.msg || '批量迁移设备失败');
                RequestService.reAjaxFun(() => {
                    this.updateBatchAgentId(agentId, deviceIds, callback);
                });
            })
            .send();
    }

}