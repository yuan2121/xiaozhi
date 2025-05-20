import { getServiceUrl } from '../api'
import RequestService from '../httpRequest'


export default {
    // 登录
    login(loginForm, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/user/login`)
            .method('POST')
            .data(loginForm)
            .success((res) => {
                RequestService.clearRequestTime()
                callback(res)
            })
            .fail(() => {
                RequestService.reAjaxFun(() => {
                    this.login(loginForm, callback)
                })
            }).send()
    },
    // 获取验证码
    getCaptcha(uuid, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/user/captcha?uuid=${uuid}`)
            .method('GET')
            .type('blob')
            .header({
                'Content-Type': 'image/gif',
                'Pragma': 'No-cache',
                'Cache-Control': 'no-cache'
            })
            .success((res) => {
                RequestService.clearRequestTime();
                callback(res);
            })
            .fail((err) => {  // 添加错误参数

            }).send()
    },
    // 注册账号
    register(registerForm, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/user/register`)
            .method('POST')
            .data(registerForm)
            .success((res) => {
                RequestService.clearRequestTime()
                callback(res)
            })
            .fail(() => {
            }).send()
    },
    // 保存设备配置
    saveDeviceConfig(device_id, configData, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/user/configDevice/${device_id}`)
            .method('PUT')
            .data(configData)
            .success((res) => {
                RequestService.clearRequestTime();
                callback(res);
            })
            .fail((err) => {
                console.error('保存配置失败:', err);
                RequestService.reAjaxFun(() => {
                    this.saveDeviceConfig(device_id, configData, callback);
                });
            }).send();
    },
    // 用户信息获取
    getUserInfo(callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/user/info`)
            .method('GET')
            .success((res) => {
                RequestService.clearRequestTime()
                callback(res)
            })
            .fail((err) => {
                console.error('接口请求失败:', err)
                RequestService.reAjaxFun(() => {
                    this.getUserInfo(callback)
                })
            }).send()
    },
    // 修改用户密码
    changePassword(oldPassword, newPassword, successCallback, errorCallback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/user/change-password`)
            .method('PUT')
            .data({
                password: oldPassword,
                newPassword: newPassword,
            })
            .success((res) => {
                RequestService.clearRequestTime();
                successCallback(res);
            })
            .fail((error) => {
                RequestService.reAjaxFun(() => {
                    this.changePassword(oldPassword, newPassword, successCallback, errorCallback);
                });
            })
            .send();
    },
    // 修改用户状态
    changeUserStatus(status, userIds, successCallback) {
        console.log(555, userIds)
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/admin/users/changeStatus/${status}`)
            .method('put')
            .data(userIds)
            .success((res) => {
                RequestService.clearRequestTime()
                successCallback(res);
            })
            .fail((err) => {
                console.error('修改用户状态失败:', err)
                RequestService.reAjaxFun(() => {
                    this.changeUserStatus(status, userIds)
                })
            }).send()
    },
    // 获取公共配置
    getPubConfig(callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/user/pub-config`)
            .method('GET')
            .success((res) => {
                RequestService.clearRequestTime();
                callback(res);
            })
            .fail((err) => {
                console.error('获取公共配置失败:', err);
                RequestService.reAjaxFun(() => {
                    this.getPubConfig(callback);
                });
            }).send();
    },
    // getStudentName(userId, param2, param3) {
    //
    // },
    getAdviceList(userId, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/conversation/advice/list/user/${userId}`) // 使用传递的 userId 作为 URL 参数
            .method('GET')
            .success((res) => {
                // 请求成功，清理请求时间，并调用回调函数
                RequestService.clearRequestTime();
                callback(res);  // 将响应数据传递给回调函数
            })
            .fail((err) => {
                // 请求失败，输出错误信息并重试请求
                console.error('接口请求失败:', err);
                RequestService.reAjaxFun(() => {
                    this.getAdviceList(userId, callback);  // 重试调用 getAdviceList
                });
            })
            .send();
    },
    getUser(userId,callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/users/getUser/${userId}`) // 使用传递的 userId 作为 URL 参数
            .method('GET')
            .success((res) => {
                // 请求成功，清理请求时间，并调用回调函数
                RequestService.clearRequestTime();
                callback(res);  // 将响应数据传递给回调函数
            })
            .fail((err) => {
                // 请求失败，输出错误信息并重试请求
                console.error('接口请求失败:', err);
                RequestService.reAjaxFun(() => {
                    this.getUser(callback);  // 重试调用 getUser
                });
            })
            .send();
    },

// 获取用户信息列表
    getUserList(userIds, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/users/getUsers`)  // 请求的 URL
            .method('POST')  // 使用 POST 方法
            .data(userIds)  // 将 userIds 发送到服务器
            .success((res) => {
                RequestService.clearRequestTime();  // 清理请求时间
                callback(res);  // 请求成功后调用回调函数，返回用户信息列表
            })
            .fail((err) => {
                console.error('获取用户信息失败:', err);  // 输出错误信息
                this.$message.error(err.msg || '获取用户信息失败');  // 弹出错误提示
                // 如果请求失败，重试请求
                RequestService.reAjaxFun(() => {
                    this.getUserList(userIds, callback);  // 重试请求
                });
            })
            .send();  // 发送请求
    },

    apisubmitAdvice(adviceData, callback) {
        // 获取当前时间，并移除时区（去掉 'Z' 字符）

        // 获取当前时间，并调整为北京时间
        const currentDate = new Date();
        const beijingDate = new Date(currentDate.getTime() + (8 * 60 * 60 * 1000)).toISOString().replace('T', ' ').slice(0, 19);  //  转换为北京时间 (UTC + 8小时)格式：YYYY-MM-DD HH:mm:ss

        // 设置请求数据
        const postData = {
            content: adviceData.content,        // 建议内容
            teacherId: adviceData.teacher_id,  // 教师ID
            studentId: adviceData.user_id,        // 学生ID
            createDate: beijingDate,            // 当前时间，表示创建时间
        };

        // 发送请求
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/conversation/advice/add`)  // 请求的 URL
            .method('POST')  // 使用 POST 方法
            .data(postData)  // 将请求数据发送到服务器
            .success((res) => {
                RequestService.clearRequestTime();  // 清理请求时间
                callback(res);
            })
            .fail((err) => {
                console.error('提交学习建议失败:', err);  // 输出错误信息
                this.$message.error(err.msg || '提交学习建议失败');  // 弹出错误提示
                // 如果请求失败，重试请求
                RequestService.reAjaxFun(() => {
                    this.apisubmitAdvice(adviceData, callback);  // 重试请求
                });
            })
            .send();  // 发送请求
    },


}
