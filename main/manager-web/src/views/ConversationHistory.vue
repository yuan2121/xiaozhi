<template>
  <div class="welcome">
    <HeaderBar />
    
    <div class="operation-bar">
      <h2 class="page-title">对话历史</h2>
      <!-- <h2 class="page-title">对话历史 - {{ agentName }}</h2> -->
      <div class="right-operations">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="fetchHistory"
          :unlink-panels="true"
        />
        <el-button class="btn-search" @click="handleSearch">搜索</el-button>
      </div>
    </div>

    <div class="main-wrapper">
      <div class="content-panel">
        <div class="content-area">
          <el-card class="history-card" shadow="never">
            <el-table
              :data="historyList"
              class="transparent-table"
              :header-cell-class-name="headerCellClassName"
              height="calc(100vh - 350px)"
              style="width: 100%"
            >
              <el-table-column label="时间" prop="time" width="180" align="center"></el-table-column>
              <el-table-column label="用户提问" prop="question" align="left" min-width="300" :show-overflow-tooltip="false">
                <template slot-scope="scope">
                  <div class="cell-content">{{ scope.row.question }}</div>
                </template>
              </el-table-column>
              <el-table-column label="AI回复" prop="answer" align="left" min-width="300" :show-overflow-tooltip="false">
                <template slot-scope="scope">
                  <div class="cell-content">{{ scope.row.answer }}</div>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120" align="center">
                <template slot-scope="scope">
                  <el-button size="mini" type="text" @click="viewDetails(scope.row)">详情</el-button>
                </template>
              </el-table-column>
            </el-table>

            <!-- 删除分页部分 -->
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Api from '@/apis/api';
import HeaderBar from "@/components/HeaderBar.vue";

export default {
  name: 'ConversationHistory',
  components: { HeaderBar },
  data() {
    return {
      agentId: '',
      agentName: '加载中...', // 默认显示加载中
      historyList: [],
      searchKeyword: '',
      dateRange: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false // 新增加载状态
    }
  },
  created() {
    this.agentId = this.$route.query.agentId;
    if (!this.agentId) {
      this.$message.error('缺少智能体ID参数');
      this.$router.back();
      return;
    }
    this.fetchAgentInfo();
    this.fetchHistory();
  },
  methods: {
    fetchAgentInfo() {
      Api.agent.getDeviceConfig(this.agentId, ({ data }) => {
        if (data.code === 0 && data.data) {
          this.agentName = data.data.agentName || '未知智能体';
        } else {
          this.agentName = '获取智能体信息失败';
        }
      }).catch(() => {
        this.agentName = '获取智能体信息异常';
      });
    },
    fetchHistory() {
      this.loading = true;
      const params = {
        agentId: this.agentId,
        page: this.currentPage,
        size: this.pageSize,
        keyword: this.searchKeyword
      };
      
      if (this.dateRange && this.dateRange.length === 2) {
        params.startTime = this.dateRange[0].getTime();
        params.endTime = this.dateRange[1].getTime();
      }

      // 模拟数据
      if (process.env.NODE_ENV === 'development') {
        setTimeout(() => {
          this.loading = false;
          let mockData = [
            {
              time: '2023-05-15 10:30',
              question: '你好，我想了解一下产品功能',
              answer: '您好！我们的产品主要提供智能对话功能，可以帮助您解决各种问题。'
            },
            {
              time: '2023-05-15 11:45',
              question: '如何注册账号？',
              answer: '您可以在登录页面点击注册按钮，按照提示填写信息完成注册。'
            },
            {
              time: '2023-05-16 09:20',
              question: '忘记密码怎么办？',
              answer: '您可以在登录页面点击忘记密码，通过邮箱验证重置密码。'
            },
            {
              time: '2023-05-16 14:15',
              question: '产品支持哪些语言？',
              answer: '目前支持中文和英文，后续会逐步增加更多语言支持。'
            },
            {
              time: '2023-05-17 09:05',
              question: '如何联系客服？',
              answer: '您可以通过官网在线客服或拨打400-123-4567联系我们的客服团队。'
            },
            {
              time: '2023-05-17 11:30',
              question: '系统出现错误代码500怎么办？',
              answer: '请尝试刷新页面或稍后再试，如果问题持续，请联系技术支持。'
            },
            {
              time: '2023-05-18 10:00',
              question: '有移动端APP吗？',
              answer: '有的，您可以在应用商店搜索"小智AI"下载我们的移动应用。'
            },
            {
              time: '2023-05-18 15:45',
              question: '如何升级会员？',
              answer: '在个人中心页面点击"会员升级"，选择套餐后完成支付即可。'
            },
            {
              time: '2023-05-19 08:30',
              question: '发票如何申请？',
              answer: '在订单详情页面有"申请发票"按钮，填写发票信息后我们会邮件发送给您。'
            },
            {
              time: '2023-05-19 13:20',
              question: 'API文档在哪里查看？',
              answer: '登录后进入开发者中心，在文档中心可以查看完整的API文档。'
            },
            {
              time: '2023-05-20 09:15',
              question: '数据安全如何保障？',
              answer: '我们采用银行级加密技术，所有数据传输和存储都经过严格加密处理。'
            },
            {
              time: '2023-05-20 16:00',
              question: '可以定制开发吗？',
              answer: '可以的，我们有专业的企业定制团队，请联系商务洽谈具体需求。'
            },
            {
              time: '2024-03-10 14:30',
              question: '新版本什么时候发布？',
              answer: '我们计划在2024年第二季度发布新版本，届时会有更多创新功能。'
            },
            {
              time: '2024-06-25 09:45',
              question: '如何导出对话记录？',
              answer: '在对话历史页面右上角有导出按钮，支持导出为Excel或PDF格式。'
            },
            {
              time: '2024-09-15 11:20',
              question: '支持多语言切换吗？',
              answer: '是的，在个人设置中可以切换系统语言，目前支持5种语言。'
            },
            {
              time: '2024-12-05 16:10',
              question: '年终优惠活动有哪些？',
              answer: '我们推出了会员年费8折优惠，以及新用户首月免费试用活动。'
            },
            {
              time: '2025-01-08 10:15',
              question: '如何批量删除对话记录？',
              answer: '在对话历史页面可以多选记录，然后点击批量删除按钮进行操作。'
            },
            {
              time: '2025-04-20 13:50',
              question: 'AI模型更新了吗？',
              answer: '是的，我们刚刚升级了最新的GPT-5模型，响应速度和准确性都有提升。'
            },
            {
              time: '2025-07-30 15:30',
              question: '移动端支持离线模式吗？',
              answer: '目前支持部分功能的离线使用，完整离线模式将在下个版本推出。'
            },
            {
              time: '2025-10-12 09:25',
              question: '企业版有什么特别功能？',
              answer: '企业版提供专属客服、API调用额度提升和数据导出定制等功能。'
            }
          ];
          
          // 应用日期范围筛选
          if (this.dateRange && this.dateRange.length === 2) {
            const startTime = this.dateRange[0].getTime();
            const endTime = this.dateRange[1].getTime();
            mockData = mockData.filter(item => {
              const itemTime = new Date(item.time).getTime();
              return itemTime >= startTime && itemTime <= endTime;
            });
          }
          
          this.historyList = mockData;
          this.total = mockData.length;
        }, 500);
        return;
      }

      // 真实API调用
      Api.conversation.getHistory(params, ({ data }) => {
        this.loading = false;
        if (data.code === 0 && data.data) {
          this.historyList = data.data.list || [];
          this.total = data.data.total || 0;
        } else {
          this.$message.error(data.msg || '获取历史记录失败');
        }
      }).catch(err => {
        this.loading = false;
        this.$message.error('获取历史记录异常');
        console.error(err);
      });
    },
    handleSearch() {
      this.currentPage = 1;
      this.fetchHistory();
    },
    handlePageSizeChange(size) {
      this.pageSize = size;
      this.fetchHistory();
    },
    handlePageChange(page) {
      this.currentPage = page;
      this.fetchHistory();
    },
    viewDetails(row) {
      // 可以在这里实现查看详细对话的功能
      console.log('查看详情:', row);
    },
    headerCellClassName({ row, column, rowIndex, columnIndex }) {
      if (rowIndex === 0) {
        return 'custom-header-cell';
      }
      return '';
    }
  }
}
</script>

<style scoped>
.history-card {
  background: white;
  border: none;
  box-shadow: none;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 200px); /* 固定卡片高度 */
}

.transparent-table {
  flex: 1;
  display: flex;
  flex-direction: column;
  /* 移除 max-height 改为使用 el-table 的 height 属性控制 */
}

.transparent-table >>> .el-table__header-wrapper {
  position: sticky;
  top: 0;
  z-index: 2;
  background: white;
}

.transparent-table >>> .el-table__body-wrapper {
  overflow-y: auto;
}

.transparent-table >>> .el-table__header-wrapper .el-table__header th {
  padding: 18px 0; /* 增加内边距 */
  height: 70px; /* 增加表头高度 */
}

.cell-content {
  white-space: normal;
  word-break: break-word;
  padding: 8px 0;
}

.table_bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
  padding-bottom: 10px;
}

.custom-pagination {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>