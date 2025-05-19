<template>
  <div class="student-space-container">
    <header-bar />

    <div class="main-layout">
      <!-- 侧边导航栏 -->
      <div class="sidebar">
        <div class="logo">
          <img src="@/assets/student/student_logo.png" alt="Logo" class="logo-img" />
          <h2>学生空间</h2>
        </div>
        <ul class="nav-list">
          <li
              v-for="(item, index) in navItems"
              :key="index"
              :class="{ active: activeTab === item.key }"
              @click="switchTab(item.key)"
          >
            <i :class="item.icon"></i> {{ item.label }}
          </li>
        </ul>
      </div>

      <!-- 主体内容区 -->
      <div class="main-content">
        <div class="header">
          <div class="student-info">
            <h2>{{ studentName }} 的学习空间</h2>
            <p>学生ID: {{ formatStudentId(userId) }}</p>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="isLoading" class="loading-container">
          <div class="loading-spinner"></div>
          <p>加载中，请稍候...</p>
        </div>

        <!-- 内容展示区 -->
        <div v-else class="content-area">
          <!-- 学习概览 -->
          <div v-if="activeTab === 'overview'" />

          <!-- 学习记录 -->
          <div v-if="activeTab === 'records'" />

          <!-- 错题本 -->
          <div v-if="activeTab === 'mistakes'" />

          <!-- 学习建议区域 -->
          <div v-if="activeTab === 'advice'" class="tab-content advice-tab">
            <div class="tab-header">
              <h3>学习建议</h3>
              <el-button
                  type="primary"
                  size="small"
                  @click="showNewAdviceForm = !showNewAdviceForm"
                  class="add-advice-btn"
              >
                <i class="el-icon-plus"></i>
                {{ showNewAdviceForm ? '取消' : '添加新建议' }}
              </el-button>
            </div>

            <!-- 添加新建议表单 -->
            <div v-if="showNewAdviceForm" class="advice-form-container">
              <div class="advice-form">
                <h4>添加新建议</h4>
                <div class="form-group">
                  <label for="advice-content">建议内容：</label>
                  <el-input
                      type="textarea"
                      id="advice-content"
                      v-model="newAdvice.content"
                      :rows="4"
                      placeholder="请输入您对学生的学习建议..."
                      :maxlength="500"
                      show-word-limit
                  ></el-input>
                </div>
                <div class="form-actions">
                  <el-button @click="showNewAdviceForm = false">取消</el-button>
                  <el-button
                      type="primary"
                      @click="submitAdvice"
                      :loading="submitting"
                      :disabled="!newAdvice.content.trim()"
                  >
                    提交建议
                  </el-button>
                </div>
              </div>
            </div>

            <!-- 历史建议展示区 -->
            <div class="advice-section">
              <div class="section-header">
                <h4>历史建议</h4>
                <el-dropdown v-if="adviceList.length > 0" @command="handleSort" class="sort-dropdown">
                  <span class="el-dropdown-link">
                    {{ currentSort === 'newest' ? '最新优先' : '最早优先' }} <i class="el-icon-arrow-down"></i>
                  </span>
                  <el-dropdown-menu v-slot="dropdown">
                    <el-dropdown-item command="newest">最新优先</el-dropdown-item>
                    <el-dropdown-item command="oldest">最早优先</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </div>

              <el-empty v-if="adviceList.length === 0" description="暂无学习建议" />

              <!-- 教师名称加载中的状态 -->
              <div v-else-if="!isTeacherNamesLoaded && adviceList.length > 0" class="loading-teacher-names">
                <div class="loading-spinner small"></div>
                <p>正在加载教师信息...</p>
              </div>

              <div v-else class="advice-list">
                <el-card
                    v-for="(advice, index) in sortedAdviceList"
                    :key="index"
                    class="advice-item"
                    shadow="hover"
                >
                  <div class="advice-header">
                    <div class="advice-avatar">
                      <i class="el-icon-user"></i>
                    </div>
                    <div class="advice-info">
                      <span class="teacher-name">{{ getTeacherName(advice.teacherId) }}</span>
                      <span class="advice-date">{{ formatDate(advice.created_at || advice.createDate || new Date()) }}</span>
                    </div>
                  </div>
                  <div class="advice-content">
                    {{ advice.content }}
                  </div>
                </el-card>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Api from "@/apis/api";
import HeaderBar from "@/components/HeaderBar.vue";

export default {
  name: 'StudentSpace',
  components: {
    HeaderBar,
  },
  data() {
    return {
      isTeacherNamesLoaded: false, // 标记教师姓名是否加载完成
      isLoading: true,
      activeTab: 'advice', // 默认显示学习建议页面
      showNewAdviceForm: false,
      submitting: false,
      studentName: '加载中...', // 学生姓名
      userId: this.$route.query.studentId || '', // 从路由参数获取用户ID
      teacherId: null, // 当前教师ID
      adviceList: [], // 学习建议列表
      newAdvice: {
        content: '' // 新建议内容
      },
      teacherNames: {}, // 缓存教师姓名
      currentSort: 'newest', // 默认排序方式：最新优先
      navItems: [
        { key: 'overview', label: '学习概览', icon: 'el-icon-data-line' },
        { key: 'records', label: '学习记录', icon: 'el-icon-document' },
        { key: 'mistakes', label: '错题本', icon: 'el-icon-warning-outline' },
        { key: 'advice', label: '学习建议', icon: 'el-icon-chat-dot-square' }
      ]
    };
  },
  mounted() {
    // 组件挂载时获取用户信息
    Api.user.getUserInfo((res) => {
      //console.log(res);  // 打印返回的数据
      if (res.data.code === 0) {
        //console.log('API 返回的数据:', res.data);  // 确保 res.data 不为 undefined
        // 请求成功，获取用户ID
        this.teacherId = res.data.data.id;  // 保存用户ID
        //console.log('当前老师ID:', this.teacherId); // 在控制台输出用户ID
      } else {
        console.error("获取用户信息失败:", res.msg);
        //console.log('当前老师ID:', this.teacherId);
      }
    });
  },
  // Fix for the sortedAdviceList computed property
  computed: {
    sortedAdviceList() {
      const sorted = [...this.adviceList];

      // Unified date extraction function to handle different date formats
      const getDateValue = (advice) => {
        // Try different possible date fields
        const dateField = advice.created_at || advice.createDate || advice.createdDate;

        if (!dateField) {
          return new Date(0); // Default to epoch time if no date available
        }

        // If it's already a Date object
        if (dateField instanceof Date) {
          return dateField;
        }

        // Try to parse as date string
        const parsedDate = new Date(dateField);
        if (!isNaN(parsedDate.getTime())) {
          return parsedDate;
        }

        // Fallback to epoch time if parsing fails
        return new Date(0);
      };

      // Sort based on the current sort preference
      if (this.currentSort === 'newest') {
        // Newest first (descending order)
        return sorted.sort((a, b) => {
          return getDateValue(b).getTime() - getDateValue(a).getTime();
        });
      } else {
        // Oldest first (ascending order)
        return sorted.sort((a, b) => {
          return getDateValue(a).getTime() - getDateValue(b).getTime();
        });
      }
    }
  },
  created() {
    this.initialize();
  },
  // watch: {
  //   // 监听 teacherNames 变化
  //   'teacherNames': {
  //     handler(newVal) {
  //       if (newVal && Object.keys(newVal).length > 0) {
  //         this.isTeacherNamesLoaded = true;
  //       }
  //     },
  //     deep: true, // 深度监听
  //   }
  // },
  methods: {

    // 初始化数据
    async initialize() {
      this.isLoading = true;
      try {
        await Promise.all([
          this.fetchStudentName(),
          this.fetchAdviceList()

        ]);
      } catch (error) {
        console.error('初始化数据失败:', error);
        this.$message.error('加载数据失败，请刷新页面重试');
      } finally {
        this.isLoading = false;
      }
    },

    // 切换标签页
    switchTab(tab) {
      this.activeTab = tab;
      // 切换标签页时隐藏新建议表单
      this.showNewAdviceForm = false;
    },

    // 格式化学生ID，只显示前8位和后4位
    formatStudentId(id) {
      if (!id) return '';
      if (id.length > 12) {
        return `${id.substring(0, 8)}...${id.substring(id.length - 4)}`;
      }
      return id;
    },

    // 获取学生名称
    fetchStudentName() {
      const userId = this.$route.query.studentId;
      console.log("加载userId：学生id:", userId);
      return new Promise((resolve) => {
        Api.user.getUser(userId, (res) => {
          if (res && res.data) {
            // 请求成功，返回学生姓名
            this.studentName = res.data.realName;
          } else {
            this.studentName = "未知学生";
          }
          resolve();
        });
      });
    },

    // 获取学习建议列表
    fetchAdviceList() {
      return new Promise((resolve) => {
        Api.user.getAdviceList(this.userId,
            ({ data }) => {
              if (data) {
                console.log('API 返回的数据:', data);
                // 请求成功，处理学习建议数据
                this.adviceList = data || [];
                console.log('adviceList 返回的数据:', this.adviceList);
                // 获取所有教师的姓名
                const teacherIds = [...new Set(this.adviceList.map(advice => advice.teacherId))];
                console.log('教师ID列表:', teacherIds);
                if (teacherIds.length > 0) {
                  this.fetchTeacherNames(teacherIds).then(resolve);  // 批量获取教师姓名
                } else {
                  this.isTeacherNamesLoaded = true; // 如果没有教师ID，则标记为已加载完成
                  resolve();
                }
              } else {
                // 请求失败时处理
                console.error('获取学习建议失败:', data?.msg);
                this.adviceList = [];  // 设为默认空数组
                this.$message.error(data?.msg || '获取学习建议失败');  // 显示错误信息
                this.isTeacherNamesLoaded = true; // 标记为已加载完成，即使失败
                resolve();
              }
            },
            (error) => {
              // 请求发生错误时的处理
              console.error('获取学习建议失败:', error);
              this.adviceList = [];  // 设为默认空数组
              this.$message.error('获取学习建议失败');  // 显示错误信息
              this.isTeacherNamesLoaded = true; // 标记为已加载完成，即使失败
              resolve();
            }
        );
      });
    },

    // 获取教师姓名
    fetchTeacherNames(teacherIds) {
      // Api.user.getTeacherNames(
      //     { teacherIds },
      //     ({ data }) => {
      //       if (data && data.code === 0 && data.data) {
      //         const teachers = data.data || [];
      //         teachers.forEach(teacher => {
      //           this.teacherNames[teacher.id] = teacher.name;
      //         });
      //       } else {
      //         console.error('获取教师姓名失败:', data?.msg);
      //       }
      //     },
      //     (error) => {
      //       console.error('获取教师姓名失败:', error);
      //     }
      // );
      return new Promise((resolve) => {
            // 先重置标记
            this.isTeacherNamesLoaded = false;

            // 调用 getUserList 获取教师信息
      Api.user.getUserList(teacherIds, (res) => {
        if (res.data && res.data.length > 0) {
          const teachers = res.data || [];  // 假设返回的数据是用户信息数组
          console.log('后端返回的教师信息:', teachers);
          teachers.forEach(teacher => {
            // 将教师 ID 和姓名存储到 teacherNames 对象中
            this.teacherNames[teacher.id] = teacher.realName || '未知教师';  // 假设教师姓名字段为 realName
            console.log('教师名字缓存列表', this.teacherNames);
          });
          // 设置教师姓名已加载
          this.isTeacherNamesLoaded = true;
        } else {
          console.error('获取教师姓名失败:', res?.msg);
        }
        // 无论成功或失败，都标记为加载完成，并解析 Promise
        this.isTeacherNamesLoaded = true;
        resolve();
      });
      });
    },

    // 获取教师姓名，如果缓存中没有则显示ID
    getTeacherName(teacherId) {
      console.log('getTeacherName中传入的teacherId参数:', teacherId);
      if (!teacherId) {
        return '教师ID无效';  // 如果 teacherId 为 undefined 或 null，返回一个默认值
      }
      // 如果 teacherId 有值，使用 substring 获取前 8 位
      return this.teacherNames[teacherId] || `教师(ID: ${teacherId.substring(0, 8)}...)`;
    },

// 处理排序方式变更
    handleSort(command) {
      console.log('排序方式更改为:', command); // 添加日志便于调试
      this.currentSort = command;
    },

    // 提交新建议
    // submitAdvice() {
    //   if (!this.newAdvice.content.trim()) {
    //     this.$message.warning('请输入建议内容');
    //     return;
    //   }
    //
    //   this.submitting = true;
    //   const payload = {
    //     teacher_id: this.teacherId,
    //     user_id: this.userId,
    //     content: this.newAdvice.content.trim()
    //   };
    //
    //   Api.user.apisubmitAdvice(
    //       payload,
    //       ({ data }) => {
    //         this.submitting = false;
    //         if (data && data.code === 0) {
    //           // 添加到列表中
    //           this.adviceList.unshift({
    //             ...payload,
    //             id: Date.now(), // 临时ID
    //             created_at: new Date()
    //           });
    //
    //           // 清空输入并隐藏表单
    //           this.newAdvice.content = '';
    //           this.showNewAdviceForm = false;
    //
    //           this.$message.success('建议提交成功');
    //         } else {
    //           this.$message.error(data?.msg || '建议提交失败，请重试');
    //         }
    //       },
    //       (error) => {
    //         this.submitting = false;
    //         console.error('提交建议失败:', error);
    //         this.$message.error('建议提交失败，请重试');
    //       }
    //   );
    // },
    // 提交新建议
    submitAdvice() {
      if (!this.newAdvice.content.trim()) {
        this.$message.warning('请输入建议内容');
        return;
      }

      this.submitting = true;
      const payload = {
        teacher_id: this.teacherId,      // 教师ID
        user_id: this.userId,            // 学生ID
        content: this.newAdvice.content.trim(),  // 建议内容
      };

      // 调用 apisubmitAdvice 提交数据
      Api.user.apisubmitAdvice(payload, (res) => {
        this.submitting = false;
        if (res) {
          // 请求成功，将新建议添加到列表中
          this.adviceList.unshift({
            ...payload,
            id: Date.now(),           // 临时ID
            created_at: new Date(),   // 当前时间戳
            teacherId: this.teacherId // 确保teacherId字段存在
          });

          // 如果当前教师ID不在缓存中，则获取当前教师信息并添加到缓存
          if (this.teacherId && !this.teacherNames[this.teacherId]) {
            Api.user.getUserList([this.teacherId], (teacherRes) => {
              if (teacherRes.data && teacherRes.data.length > 0) {
                this.teacherNames[this.teacherId] = teacherRes.data[0].realName || '未知教师';
                console.log('已添加当前教师到缓存:', this.teacherNames);
              }
            });
          }

          // 清空输入框并隐藏表单
          this.newAdvice.content = '';
          this.showNewAdviceForm = false;

          // 提交成功后的提示
          this.$message.success('建议提交成功');
        } else {
          // 如果请求失败，弹出错误信息
          this.$message.error(res?.msg || '建议提交失败，请重试');
        }
      }, (error) => {
        // 如果请求出错，更新状态并显示错误提示
        this.submitting = false;
        console.error('提交建议失败:', error);
        this.$message.error('建议提交失败，请重试');
      });
    },


    // Modified formatDate method to properly handle various date formats
    formatDate(dateInput) {
      // If dateInput is null or undefined, use current date
      if (!dateInput) {
        return this.formatDate(new Date());
      }

      let date;

      // If dateInput is already a Date object
      if (dateInput instanceof Date) {
        date = dateInput;
      }
      // If dateInput is a string (from API)
      else if (typeof dateInput === 'string') {
        // Try parsing the date string
        date = new Date(dateInput);

        // Check if date is valid
        if (isNaN(date.getTime())) {
          // If invalid, try an alternative parsing approach for "YYYY-MM-DD HH:MM:SS" format
          const parts = dateInput.split(/[- :]/);
          if (parts.length >= 6) {
            // parts[0]=year, parts[1]=month, parts[2]=day, parts[3]=hour, parts[4]=minute, parts[5]=second
            date = new Date(parts[0], parts[1]-1, parts[2], parts[3], parts[4], parts[5]);
          } else {
            // If all parsing fails, use current date
            date = new Date();
          }
        }
      }
      // If dateInput is a timestamp number
      else if (typeof dateInput === 'number') {
        date = new Date(dateInput);
      }
      // Fallback to current date for any other type
      else {
        date = new Date();
      }

      // Format the date
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hour = String(date.getHours()).padStart(2, '0');
      const minute = String(date.getMinutes()).padStart(2, '0');
      const second = String(date.getSeconds()).padStart(2, '0');

      return `${year}-${month}-${day} ${hour}:${minute}:${second}`;
    }


  }
};
</script>

<style scoped>
.loading-teacher-names {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  text-align: center;
  color: #909399;
}

.loading-spinner.small {
  width: 20px;
  height: 20px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #409EFF;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 10px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 基础样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.student-space-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  color: #333;
  background-color: #f2f5fc;
}

.main-layout {
  display: flex;
  flex: 1;
  overflow: hidden;
}

/* 侧边栏样式 */
.sidebar {
  width: 220px;
  background: linear-gradient(to bottom, #4a90e2, #6772e5);
  color: #fff;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  z-index: 10;
}

.logo {
  padding: 20px 16px;
  display: flex;
  align-items: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-img {
  width: 32px;
  height: 32px;
  margin-right: 10px;
  border-radius: 4px;
}

.logo h2 {
  font-size: 1.1rem;
  font-weight: 500;
}

.nav-list {
  list-style: none;
  padding: 16px 0;
}

.nav-list li {
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.3s;
  margin: 2px 6px;
  display: flex;
  align-items: center;
  border-radius: 4px;
}

.nav-list li i {
  margin-right: 10px;
  font-size: 16px;
  width: 18px;
  text-align: center;
}

.nav-list li:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.nav-list li.active {
  background-color: rgba(255, 255, 255, 0.15);
  color: white;
  font-weight: 500;
}

/* 主内容区样式 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: #f2f5fc;
}

.header {
  padding: 14px 20px;
  background-color: white;
  border-bottom: 1px solid #e8eef7;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  z-index: 5;
}

.student-info h2 {
  font-size: 1.3rem;
  font-weight: 500;
  margin-bottom: 4px;
  color: #334d6e;
}

.student-info p {
  color: #7d8da8;
  font-size: 0.85rem;
}

.content-area {
  padding: 20px;
  flex: 1;
  overflow-y: auto;
}

/* 加载中样式 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
}

.loading-spinner {
  width: 36px;
  height: 36px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #4a90e2;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 标签内容样式 */
.tab-content {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.tab-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #ecf1f7;
  background-color: #fcfdff;
}

.tab-header h3 {
  font-size: 1.1rem;
  margin: 0;
  color: #334d6e;
  font-weight: 500;
}

.add-advice-btn {
  background-color: #4a90e2;
  border-color: #4a90e2;
  transition: all 0.3s;
}

.add-advice-btn:hover {
  background-color: #3a7bd5;
  border-color: #3a7bd5;
}

/* 学习建议样式 */
.advice-tab {
  display: flex;
  flex-direction: column;
}

.advice-form-container {
  padding: 0 20px;
  margin-bottom: 16px;
  border-bottom: 1px solid #ecf1f7;
}

.advice-form {
  padding: 16px;
  background-color: #f9fafd;
  border-radius: 6px;
  margin-bottom: 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.03);
}

.advice-form h4 {
  font-size: 1rem;
  margin-bottom: 14px;
  color: #334d6e;
  font-weight: 500;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 0 20px;
}

.section-header h4 {
  font-size: 1rem;
  color: #334d6e;
  font-weight: 500;
  margin: 0;
}

.sort-dropdown {
  font-size: 0.85rem;
}

.advice-section {
  padding: 16px 0;
}

.advice-list {
  padding: 0 20px;
}

.advice-item {
  margin-bottom: 16px;
  transition: all 0.3s;
  border-radius: 6px;
  overflow: hidden;
}

.advice-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.06);
}

.advice-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.advice-avatar {
  width: 38px;
  height: 38px;
  background: linear-gradient(120deg, #4a90e2, #6772e5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  margin-right: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.advice-info {
  display: flex;
  flex-direction: column;
}

.teacher-name {
  font-weight: 500;
  color: #334d6e;
  font-size: 0.9rem;
}

.advice-date {
  font-size: 0.8rem;
  color: #8899ac;
  margin-top: 2px;
}

.advice-content {
  color: #3e4b5b;
  line-height: 1.6;
  font-size: 0.9rem;
  white-space: pre-line;
}

/* 表单样式 */
.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #334d6e;
  font-size: 0.85rem;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* Element UI 下拉菜单样式 */
.el-dropdown-link {
  cursor: pointer;
  color: #4a90e2;
  display: flex;
  align-items: center;
  font-size: 0.85rem;
}

.el-dropdown-link i {
  margin-left: 4px;
}

/* 响应式设计 */
@media (max-width: 992px) {
  .main-layout {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    height: auto;
  }

  .nav-list {
    display: flex;
    padding: 10px;
    overflow-x: auto;
  }

  .nav-list li {
    padding: 8px 14px;
    margin-right: 10px;
    margin-bottom: 0;
    white-space: nowrap;
    border-radius: 16px;
  }

  .content-area {
    padding: 14px;
  }

  .advice-list {
    padding: 0 10px;
  }
}

@media (max-width: 576px) {
  .header {
    padding: 12px 14px;
  }

  .student-info h2 {
    font-size: 1.2rem;
  }

  .nav-list li {
    padding: 6px 12px;
    font-size: 0.85rem;
  }

  .nav-list li i {
    margin-right: 6px;
  }

  .tab-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
    padding: 14px 16px;
  }

  .section-header {
    padding: 0 16px;
  }

  .advice-form-container,
  .advice-list {
    padding: 0 16px;
  }

  .form-actions {
    flex-direction: column;
  }

  .form-actions button {
    width: 100%;
  }
}
</style>