// src/apis/module/conversation.js
import { getServiceUrl } from "../api";
import RequestService from "../httpRequest";

export default {
  listByAgent(agentId, callback) {
    RequestService.sendRequest()
      .url(`${getServiceUrl()}/conversation/list/agent/${agentId}`)
      .method("GET")
      .success((res) => {
        RequestService.clearRequestTime();
        callback(res);
      })
      .fail((err) => {
        console.error("获取历史对话失败:", err);
        RequestService.reAjaxFun(() => {
          this.listByAgent(agentId, callback);
        });
      })
      .send();
  },
  listByChat(chatId, callback) {
    RequestService.sendRequest()
      .url(`${getServiceUrl()}/conversation/message/list/${chatId}`)
      .method("GET")
      .success((res) => {
        RequestService.clearRequestTime();
        callback(res);
      })
      .fail((err) => {
        console.error("获取对话信息失败:", err);
        RequestService.reAjaxFun(() => {
          this.listByChat(chatId, callback);
        });
      })
      .send();
  },
  // 获取对话总结（aiSummary）
  getConversationSummary(chatId, callback) {
  RequestService.sendRequest()
      .url(`${getServiceUrl()}/conversation/summary/get/${chatId}`)
      .method("GET")
      .success((res) => {
        RequestService.clearRequestTime(); // 清除请求时间记录
        callback(res);
      })
      .fail((err) => {
        console.error("获取对话总结失败:", err);
        RequestService.reAjaxFun(() => {
          // 如果请求失败，重新发起请求
          this.getConversationSummary(chatId, callback);
        });
      })
      .send();
}
};
