// src/stores/user.js
import { defineStore } from 'pinia';
import { ref } from 'vue';
import axios from 'axios'; // ← 引入 axios

// 创建 axios 实例（可复用）
const api = axios.create({
  baseURL: 'http://localhost:8080/api', // 后端地址
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器：自动添加 JWT token
api.interceptors.request.use(config => {
  const token = localStorage.getItem('greenPlan_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, error => {
  return Promise.reject(error);
});

// 响应拦截器：统一处理错误
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      // token 失效，清除本地数据并跳转登录
      localStorage.removeItem('greenPlan_token');
      localStorage.removeItem('greenPlan_user');
      // 这里可以触发路由跳转（需配合 router）
      // router.push('/login');
    }
    return Promise.reject(error);
  }
);

export const useUserStore = defineStore('user', () => {
  const currentUser = ref(null);

  // 初始化：从 localStorage 恢复用户和 token
  const initUser = () => {
    const storedUser = localStorage.getItem('greenPlan_user');
    if (storedUser) {
      currentUser.value = JSON.parse(storedUser);
    }
  };
  initUser();

  // --- 真实 API 调用（替换 mock）---

  // 1. 获取用户信息
  const getUserInfo = async () => {
    try {
      const res = await api.get('/user/info'); // ← 调用真实接口
      const userInfo = res.data;
      
      // 更新本地存储
      currentUser.value = userInfo;
      localStorage.setItem('greenPlan_user', JSON.stringify(userInfo));
      
      return { data: userInfo };
    } catch (error) {
      console.error('获取用户信息失败:', error);
      throw error;
    }
  };

  // 2. 获取订单列表
  const getOrders = async ({ status = 'all' }) => {
    try {
      const res = await api.get(`/orders?status=${status}`);
      return { data: res.data };
    } catch (error) {
      console.error('获取订单失败:', error);
      throw error;
    }
  };

  // 3. 获取种植记录
  const getPlantRecords = async () => {
    try {
      const res = await api.get('/records/plant');
      return { data: res.data };
    } catch (error) {
      console.error('获取种植记录失败:', error);
      throw error;
    }
  };

  // 4. 获取地址列表
  const getAddresses = async () => {
    try {
      const res = await api.get('/addresses');
      return { data: res.data };
    } catch (error) {
      console.error('获取地址失败:', error);
      throw error;
    }
  };

  // 5. 更新用户信息
  const updateUserInfo = async (info) => {
    try {
      const res = await api.put('/user/info', info);
      const updatedUser = res.data;
      
      currentUser.value = updatedUser;
      localStorage.setItem('greenPlan_user', JSON.stringify(updatedUser));
      
      return { success: true };
    } catch (error) {
      console.error('更新用户信息失败:', error);
      throw error;
    }
  };

  // 6. 添加种植记录
  const addPlantRecord = async (record) => {
    try {
      await api.post('/records/plant', record);
      return { success: true };
    } catch (error) {
      console.error('添加种植记录失败:', error);
      throw error;
    }
  };

  // 7. 删除种植记录
  const deletePlantRecord = async (id) => {
    try {
      await api.delete(`/records/plant/${id}`);
      return { success: true };
    } catch (error) {
      console.error('删除种植记录失败:', error);
      throw error;
    }
  };

  // 8. 设置默认地址
  const setDefaultAddress = async (id) => {
    try {
      await api.patch(`/addresses/${id}/default`);
      return { success: true };
    } catch (error) {
      console.error('设置默认地址失败:', error);
      throw error;
    }
  };

  // 9. 删除地址
  const deleteAddress = async (id) => {
    try {
      await api.delete(`/addresses/${id}`);
      return { success: true };
    } catch (error) {
      console.error('删除地址失败:', error);
      throw error;
    }
  };

  // 10. 编辑地址
  const editAddress = async (address) => {
    try {
      await api.put(`/addresses/${address.id}`, address);
      return { success: true };
    } catch (error) {
      console.error('编辑地址失败:', error);
      throw error;
    }
  };

  // 🔑 新增：登录方法（用于保存 token 和用户）
  const login = async (credentials) => {
    try {
      const res = await axios.post('http://localhost:8080/api/login', credentials); 
      const { token, name, username } = res.data;

      // 保存 token 和用户信息
      localStorage.setItem('greenPlan_token', token);
      const user = { username, name };
      localStorage.setItem('greenPlan_user', JSON.stringify(user));
      currentUser.value = user;

      return { success: true, token, user };
    } catch (error) {
      console.error('登录失败:', error);
      throw error;
    }
  };

  // 🔑 新增：登出
  const logout = () => {
    localStorage.removeItem('greenPlan_token');
    localStorage.removeItem('greenPlan_user');
    currentUser.value = null;
  };

  return {
    currentUser,
    getUserInfo,
    getOrders,
    getPlantRecords,
    getAddresses,
    updateUserInfo,
    addPlantRecord,
    deletePlantRecord,
    setDefaultAddress,
    deleteAddress,
    editAddress,
    login,      // ← 暴露登录
    logout      // ← 暴露登出
  };
});