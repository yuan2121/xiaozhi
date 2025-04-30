import { getServiceUrl } from '../api';
import RequestService from '../httpRequest';

export default {
    // 获取对话历史
    getHistory(params, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/conversation/history`)
            .method('GET')
            .params(params)
            .success((res) => {
                RequestService.clearRequestTime();
                callback(res);
            })
            .fail(() => {
                RequestService.reAjaxFun(() => {
                    this.getHistory(params, callback);
                });
            }).send();
    }
}