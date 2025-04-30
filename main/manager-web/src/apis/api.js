// 引入各个模块的请求
import admin from "./module/admin.js";
import agent from "./module/agent.js";
import device from "./module/device.js";
import model from "./module/model.js";
import timbre from "./module/timbre.js";
import user from "./module/user.js";
import ota from "./module/ota.js";
import conversation from "./module/conversation.js";
/**
 * 接口地址
 * 开发时自动读取使用.env.development文件
 * 编译时自动读取使用.env.production文件
 */
const DEV_API_SERVICE = process.env.VUE_APP_API_BASE_URL;

/**
 * 根据开发环境返回接口url
 * @returns {string}
 */
export function getServiceUrl() {
  return DEV_API_SERVICE;
}

/** request服务封装 */
export default {
  getServiceUrl,
  user,
  admin,
  agent,
  device,
  model,
  timbre,
  ota,
  conversation,
  // message: conversation,
};

// import axios from "axios";

// const Api = {
//   agent: {
//     // 获取聊天历史记录
//     getHistory(agentId) {
//       return axios.get(`/conversation/list/agent`, {
//         params: { agentId }, // 发送 agentId 作为查询参数
//       });
//     },

//     // 获取某个聊天记录的详细内容
//     getChatDetails(chatId) {
//       return axios.get(`/api/chat/details`, {
//         params: { chatId }, // 发送 chatId 作为查询参数
//       });
//     },
//   },

//   // 其他模块的 API 方法...
// };
