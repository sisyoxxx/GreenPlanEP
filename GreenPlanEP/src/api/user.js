// src/stores/useUserStore.js (或 user.js)

import { defineStore } from 'pinia';
import { useStorage } from '@/composables/useStorage'; // 引入你的持久化工具
import request from '@/utils/request'; // 假设你的 request.js 在 utils 文件夹

// 1. 定义并导出 Store
export const useUserStore = defineStore('user', () => {
  // 使用 useStorage 自动同步到 localStorage
  const currentUser = useStorage('greenPlan_user', null);
  const token = useStorage('greenPlan_token', '');

  // 2. 登录方法 (重点修改部分)
  // ✅ 修改思路：
  // - 不要让 Axios 拦截器直接 reject 业务错误 (401/402)。
  // - 让 login 方法总是返回一个 { code, msg, data } 格式的对象。
  // - 无论成功还是失败，都在 Store 层处理数据结构，保证组件层不会报错。
  const login = async ({ username, password }) => {
    try {
      const response = await request.post('/api/login', {
        username,
        password
      }, {
        headers: {
          'Content-Type': 'application/json'
        }
      });

      // ✅ 关键点1：假设拦截器已经放行，我们拿到的是标准数据
      const { code, msg, data } = response;

      if (code === 200) {
        // 登录成功逻辑
        const userToken = data?.token || '';
        const userDetails = data?.user || null;
        currentUser.value = userDetails;
        token.value = userToken;

        // 返回成功结果给组件
        return { code, msg, data };
      } else {
        // ✅ 关键点2：业务失败，不抛出 Error，而是返回错误信息对象
        // 这样组件的 try...catch 不会被触发，避免进入错误处理分支导致逻辑混乱
        return { code, msg, data: null };
      }

    } catch (error) {
      // ✅ 关键点3：网络错误处理
      // 如果是网络请求失败（如超时、断网），Axios 会进入这里
      // 我们构造一个标准的错误格式返回，防止组件层报错
      console.error('登录网络错误:', error);

      return {
        code: 500,
        msg: error.message || '网络请求失败，请检查网络',
        data: null
      };
    }
  };

  // 3. 注册方法 (保持不变，仅做微调以符合统一风格)
  const register = async ({ username, password, confirmPassword }) => {
    try {
      const response = await request.post('/api/register', { username, password });

      // 假设 register 的拦截器逻辑和 login 一样
      const { code, msg, data } = response;

      if (code === 200) {
        return { code, msg, data };
      } else {
        // 注册失败，返回错误信息
        return { code, msg, data: null };
      }

    } catch (error) {
      console.error('注册网络错误:', error);
      return {
        code: 500,
        msg: error.message || '注册网络错误',
        data: null
      };
    }
  };

  // 4. 登出方法 (保持不变)
  const logout = () => {
    currentUser.value = null;
    token.value = '';
  };

  return {
    currentUser,
    token,
    login,
    register,
    logout
  };
});
