<template>
  <div class="chat-app">
    <HeaderBar />

    <div class="operation-bar">
      <div class="page-title">历史记录</div>
    </div>

    <div class="main-wrapper">
      <div class="content-panel">
        <!-- 左侧历史对话列表 -->
        <div class="history-sidebar">
          <div class="sidebar-header">
            <div class="header-content">
              <span class="agent-name">{{ agentInfo.name }}</span>
            </div>
          </div>
          <div class="chat-list">
            <div
              v-for="chat in chatHistory"
              :key="chat.id"
              class="chat-item"
              :class="{ active: selectedChat && chat.id === selectedChat.id }"
              @click="selectChat(chat)"
            >
              <div class="chat-item-content">
                <div class="chat-time">{{ formatDate(chat.timestamp) }}</div>
                <div class="message-count">{{ chat.messageCount || 0 }}</div>
                <div v-if="chat.creator" class="creator">{{ chat.creator }}</div>
              </div>
              <button class="delete-btn" @click.stop="deleteChat(chat.id)">
                <i class="delete-icon"></i>
              </button>
            </div>
            <div v-if="chatHistory.length === 0" class="empty-history">
              <div class="empty-icon">
                <img src="@/assets/header/robot.png" alt="机器人图标" />
              </div>
              <div class="empty-text">暂无历史对话</div>
            </div>
          </div>
        </div>

        <!-- 右侧对话详情 -->
        <div class="chat-detail">
          <div v-if="!selectedChat" class="empty-state">
            <div class="empty-icon">
              <img src="@/assets/header/user_management.png" alt="消息图标" />
            </div>
            <div class="empty-message">请选择一个对话</div>
          </div>

          <template v-else>
            <div class="chat-header">
              <div class="chat-title">
                对话详情 - {{ formatDate(selectedChat.timestamp) }}
              </div>
              <div v-if="selectedChat.deviceId" class="device-id">设备: {{ selectedChat.deviceId }}</div>
            </div>

            <div class="messages-container">
              <div
                v-for="(message, index) in selectedChat.messages"
                :key="index"
                :class="[
                  'message',
                  message.sender === 'user' ? 'user-message' : 'ai-message',
                ]"
              >
                <div class="message-header">
                  <span class="timestamp">{{
                    formatTime(message.timestamp)
                  }}</span>
                </div>

                <div class="message-bubble">
                  <div class="message-content">
                    <span v-if="message.emoji" class="emoji">{{
                      message.emoji
                    }}</span>
                    {{ message.content }}
                  </div>
                  <div v-if="message.sender === 'ai'" class="message-footer">
                    <small class="ai-generated">以上内容由AI生成</small>
                  </div>
                </div>
              </div>
            </div>
          </template>

          <!-- AI总结框 -->
          <div class="ai-summary">
            <div class="summary-title">AI总结</div>
            <div class="summary-content">
              {{ aiSummary || "暂无总结" }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import HeaderBar from "@/components/HeaderBar.vue";
import Api from "@/apis/api";

export default {
  name: "ChatHistoryPage",
  components: {
    HeaderBar,
  },
  data() {
    return {
      agentInfo: {
        id: "",
        name: "",
        deviceType: "",
        langModel: "",
        ttsModel: "",
      },
      selectedChat: null,
      chatHistory: [],
      aiSummary: "",  // 用来存储AI总结
    };
  },
  created() {
    this.loadAgentInfo();
    this.loadChatHistory();
  },
  methods: {
    loadAgentInfo() {
      const agentId = this.$route.query.agentId;
      if (!agentId) {
        this.$message.error("未找到智能体ID");
        return;
      }

      Api.agent.getDeviceConfig(agentId, ({ data }) => {
        if (data && data.code === 0 && data.data) {
          this.agentInfo = {
            id: agentId,
            name: data.data.agentName || "AI助手",
            deviceType: data.data.deviceType || "Edge语音合成",
            langModel: data.data.llmModelName || "EdgeTTS文本-映晓",
            ttsModel: data.data.ttsModelName || "EdgeTTS文本-映晓",
          };
        } else {
          this.$message.error(data?.msg || "获取智能体信息失败");
        }
      }, (error) => {
        this.$message.error(error?.message || "获取智能体信息失败");
      });
    },
    
    loadChatHistory() {
      const agentId = this.$route.query.agentId;
      console.log("加载历史记录，agentId:", agentId);
      if (!agentId) {
        console.log("未找到agentId，无法加载历史记录");
        return;
      }

      Api.conversation.listByAgent(
        agentId,
        (res) => {
          console.log("历史记录API响应:", res);
          if (res && res.data) {
            const list = Array.isArray(res.data) ? res.data : [];
            this.chatHistory = list.map(chat => ({
              id: chat.id,
              timestamp: chat.updateDate || new Date().toISOString(),
              messageCount: chat.messageCount || 0,
              deviceId: chat.deviceId || '',
              creator: chat.creator || '',
              messages: [],
            }));
            
            // 如果有记录，默认选择第一条
            if (this.chatHistory.length > 0 && !this.selectedChat) {
              this.selectChat(this.chatHistory[0]);
            }
          } else {
            this.chatHistory = [];
          }
        },
        (error) => {
          console.error("加载历史记录失败:", error);
          this.$message.error(error?.message || "加载历史记录失败");
        }
      );
    },

    selectChat(chat) {
      if (this.selectedChat && this.selectedChat.id === chat.id) return;

      // 第一次点击时 messages 为空，则拉取详情
      if (!chat.messages || chat.messages.length === 0) {
        Api.conversation.listByChat(
          chat.id,
          (res) => {
            if (res && res.data) {
              const msgs = Array.isArray(res.data) ? res.data : [];
              chat.messages = msgs.map(msg => ({
                id: msg.id,
                sender: msg.role || 'ai',  // 默认值防止空值
                content: msg.content || '',
                timestamp: msg.createDate || new Date().toISOString(),
                emoji: msg.emoji || null,
              }));
            } else {
              chat.messages = [];
            }
            this.selectedChat = { ...chat };
            this.loadAISummary(chat.id);  // 获取AI总结
          },
          (error) => {
            chat.messages = [];
            this.selectedChat = {...chat};
            this.$message.error(error?.message || "加载对话内容失败");
          }
        );
      } else {
        this.selectedChat = { ...chat };
        this.loadAISummary(chat.id);  // 获取AI总结
      }
    },
    loadAISummary(chatId) {
        // 调用 API 获取 AI 总结
        Api.conversation.getConversationSummary(chatId, (res) => {
          console.log("API 返回的数据：", res);  // 打印响应数据
          // 确保返回的数据包含 summary 字段
          if (res && res.data) {
            console.log('AI总结：', res.data.summary);
            this.aiSummary = res.data.summary;  // 将返回的总结内容赋值给 Vue 组件中的 aiSummary
          } else {
            this.aiSummary = "暂无总结";  // 如果没有总结，显示默认消息
          }
        });
    },
    
    deleteChat(chatId) {
      if (!this.$confirm) {
        // 如果不存在 $confirm 方法（可能使用的不是 Element UI），则使用 window.confirm
        if (window.confirm("确定要删除这条对话记录吗？")) {
          this.performDeleteChat(chatId);
        }
      } else {
        this.$confirm("确定要删除这条对话记录吗？", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        })
          .then(() => {
            this.performDeleteChat(chatId);
          })
          .catch(() => {
            // 取消删除
          });
      }
    },

    performDeleteChat(chatId) {
      // 实际应该调用API删除
      Api.conversation.delete(chatId, 
        (res) => {
          const index = this.chatHistory.findIndex(chat => chat.id === chatId);
          if (index !== -1) {
            this.chatHistory.splice(index, 1);
            if (this.selectedChat && this.selectedChat.id === chatId) {
              this.selectedChat = this.chatHistory.length > 0 ? {...this.chatHistory[0]} : null;
              // 如果选择了新的聊天，确保加载其消息
              if (this.selectedChat && (!this.selectedChat.messages || this.selectedChat.messages.length === 0)) {
                this.selectChat(this.selectedChat);
              }
            }
            this.$message.success("删除成功");
          }
        },
        (error) => {
          this.$message.error(error?.message || "删除失败");
        }
      );
    },

    formatDate(date) {
      if (!date) return "";
      try {
        const d = new Date(date);
        return `${d.getFullYear()}-${this.padZero(
          d.getMonth() + 1
        )}-${this.padZero(d.getDate())} ${this.padZero(
          d.getHours()
        )}:${this.padZero(d.getMinutes())}:${this.padZero(d.getSeconds())}`;
      } catch (e) {
        return "";
      }
    },
    
    formatTime(date) {
      return this.formatDate(date);
    },
    
    padZero(num) {
      return num.toString().padStart(2, "0");
    },
  },
};
</script>

<style scoped>
/* AI总结框样式 */
.ai-summary {
  padding: 16px;
  margin-top: 20px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}
.summary-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
}
.summary-content {
  font-size: 14px;
  color: #555;
  line-height: 1.5;
  word-wrap: break-word;
  white-space: pre-wrap; /* 保持换行 */
}


.chat-app {
  min-width: 900px;
  min-height: 506px;
  height: 100vh;
  display: flex;
  position: relative;
  flex-direction: column;
  background: linear-gradient(to bottom right, #dce8ff, #e4eeff, #e6cbfd);
  background-size: cover;
  -webkit-background-size: cover;
  -o-background-size: cover;
  overflow: hidden;
}

.operation-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid rgba(220, 223, 230, 0.3);
}

.page-title {
  font-size: 23px;
  font-weight: bold;
  color: #333;
}

.main-wrapper {
  margin: 5px 22px;
  border-radius: 15px;
  height: calc(100vh - 24vh);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  position: relative;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  flex-direction: column;
}

.content-panel {
  flex: 1;
  display: flex;
  overflow: hidden;
  height: 100%;
  border-radius: 15px;
  background: transparent;
  border: 1px solid #f0f0f0;
}

/* 左侧历史列表样式 */
.history-sidebar {
  width: 25%;
  min-width: 250px;
  background-color: #fff;
  border-right: 1px solid #e6e9ef;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sidebar-header {
  padding: 15px;
  border-bottom: 1px solid #e6e9ef;
  display: flex;
  align-items: center;
}

.header-content {
  display: flex;
  flex-direction: column;
}

.agent-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.chat-list {
  flex: 1;
  overflow-y: auto;
}

.chat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #e6e9ef;
  cursor: pointer;
  transition: background-color 0.2s;
}

.chat-item:hover {
  background-color: #f4f7ff;
}

.chat-item.active {
  background-color: #e8f0ff;
}

.chat-item-content {
  display: flex;
  justify-content: space-between;
  width: calc(100% - 35px);
}

.chat-time {
  font-size: 13px;
  color: #333;
}

.message-count {
  width: 24px;
  height: 24px;
  background-color: #8a8a8a;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}

.delete-btn {
  width: 24px;
  height: 24px;
  background: none;
  border: none;
  color: #ff6b6b;
  cursor: pointer;
  opacity: 0.6;
  transition: opacity 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.delete-btn:hover {
  opacity: 1;
  background-color: rgba(255, 107, 107, 0.1);
}

.delete-icon {
  display: inline-block;
  width: 16px;
  height: 16px;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='%23ff6b6b'%3E%3Cpath d='M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z'/%3E%3C/svg%3E");
}

.empty-history {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 20px;
  color: #909399;
}

.empty-icon {
  margin-bottom: 15px;
}

.empty-icon img {
  width: 60px;
  height: 60px;
  opacity: 0.6;
}

.empty-text {
  font-size: 14px;
}

/* 右侧聊天详情样式 */
.chat-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #f8f9fb;
  overflow: hidden;
  padding-bottom: 60px; /* 给总结框留出空间 */
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #e6e9ef;
  background-color: white;
}

.chat-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.device-id {
  font-size: 13px;
  color: #909399;
}

.messages-container {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.message {
  max-width: 75%;
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
}

.user-message {
  align-self: flex-end;
}

.ai-message {
  align-self: flex-start;
}

.message-header {
  margin-bottom: 5px;
  font-size: 12px;
  color: #909399;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 18px;
  position: relative;
}

.user-message .message-bubble {
  background-color: #5778ff;
  color: white;
  border-bottom-right-radius: 4px;
}

.ai-message .message-bubble {
  background-color: white;
  color: #333;
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.message-content {
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}

.emoji {
  margin-right: 8px;
}

.message-footer {
  margin-top: 8px;
  text-align: right;
}

.ai-generated {
  font-size: 11px;
  color: #999;
}

.empty-state {
  display: flex;
  flex-direction: column;
  flex: 1;
  align-items: center;
  justify-content: center;
  color: #909399;
}

.empty-state .empty-icon {
  margin-bottom: 20px;
}

.empty-state .empty-icon img {
  width: 80px;
  height: 80px;
  opacity: 0.6;
}

.empty-message {
  font-size: 16px;
}

.creator {
  font-size: 12px;
  color: #606266;
}
</style>