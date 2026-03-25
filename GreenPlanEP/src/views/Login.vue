<template>
    <div class="login-page">
      <div class="login-card">
        <!-- 头部 Logo -->
        <div class="login-header">
          <div class="logo-circle">🌱</div>
          <h2>欢迎回来</h2>
          <p>请登录您的 GreenPlan 账户</p>
        </div>
  
        <!-- 登录表单 -->
        <form @submit.prevent="handleLogin" class="login-form">
          <!-- 用户名输入 -->
          <div class="form-group">
            <div class="input-wrapper">
              <i class="fas fa-user input-icon"></i>
              <input 
                id="username"
                v-model="form.username" 
                type="text" 
                placeholder="请输入用户名/手机号"
                :class="{ 'error': errors.username }"
                @blur="validateUsername"
              />
            </div>
            <span class="error-msg" v-if="errors.username">{{ errors.username }}</span>
          </div>
  
          <!-- 密码输入 -->
          <div class="form-group">
            <div class="input-wrapper">
              <i class="fas fa-lock input-icon"></i>
              <input 
                id="password"
                v-model="form.password" 
                type="password" 
                placeholder="请输入密码"
                :class="{ 'error': errors.password }"
                @blur="validatePassword"
              />
            </div>
            <span class="error-msg" v-if="errors.password">{{ errors.password }}</span>
          </div>
  
          <!-- 记住我 & 忘记密码 -->
          <div class="form-options">
            <label class="checkbox-label">
              <input type="checkbox" v-model="form.remember" />
              <span>7天内自动登录</span>
            </label>
            <a href="#" class="forgot-link">忘记密码？</a>
          </div>
  
          <!-- 提交按钮 -->
          <button type="submit" class="btn-submit" :disabled="isLoading">
            <span v-if="!isLoading">立即登录</span>
            <span v-else class="loading-spinner">
              <i class="fas fa-circle-notch fa-spin"></i> 登录中...
            </span>
          </button>
        </form>
  
        <!-- 底部提示 -->
        <div class="login-footer">
          <p>还没有账户？ <a href="#" class="link-register">立即注册</a></p>
        </div>
        

      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive } from 'vue';
  import { useRouter } from 'vue-router';
  import '@fortawesome/fontawesome-free/css/all.min.css';
  import { useUserStore } from '@/api/user';
  
  const router = useRouter();
  const userStore = useUserStore(); // ← 使用 Pinia store
  
  // 1. 表单数据
  const form = reactive({
    username: '',
    password: '',
    remember: true
  });
  
  // 2. 错误信息与状态
  const errors = reactive({
    username: '',
    password: ''
  });
  const isLoading = ref(false);
  
  // 3. 验证逻辑（保持不变）
  const validateUsername = () => {
    if (!form.username.trim()) {
      errors.username = '账号不能为空';
      return false;
    }
    errors.username = '';
    return true;
  };
  
  const validatePassword = () => {
    if (!form.password) {
      errors.password = '密码不能为空';
      return false;
    }
    if (form.password.length < 6) {
      errors.password = '密码长度至少为6位';
      return false;
    }
    errors.password = '';
    return true;
  };
  
  // 4. 登录处理
  const handleLogin = async () => {
    const isUserValid = validateUsername();
    const isPassValid = validatePassword();
    
    if (!isUserValid || !isPassValid) return;
  
    isLoading.value = true;
  
    try {
      // ✅ 调用 Pinia store 的 login 方法（已封装 axios + token 存储）
      await userStore.login({
        username: form.username,
        password: form.password
      });
  
      
      // ✅ 登录成功提示 + 跳转
      alert(`登录成功！欢迎 ${userStore.currentUser?.name || form.username}`);
      router.replace('/user');
  
    } catch (error) {
      console.error('登录失败:', error);
      // 根据后端返回的错误信息优化提示（可选）
      alert('登录失败：' + (error.response?.data?.error || '用户名或密码错误'));
    } finally {
      isLoading.value = false;
    }
  };
  </script>

  <style scoped>
  /* 页面背景 */
  .login-page {
    background-image: url('../assets/images/HomeBackground.png');
      background-size: 1105px auto;
      background-repeat: no-repeat;
      background-position: left bottom;
      background-attachment: fixed;
        
    min-height: 100vh;
    display: flex;
    justify-content: right;
    align-items: center;
    
    font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
  }
  
  /* 卡片主体 */
  .login-card {
    background: white;
    padding: 40px;
    border-radius: 16px;
    box-shadow: 0 10px 25px rgba(45, 80, 22, 0.15);
    width: 100%;
    max-width: 400px;
    text-align: center;
  }
  
  /* 头部 Logo 区 */
  .login-header {
    margin-bottom: 30px;
  }
  
  .logo-circle {
    width: 60px;
    height: 60px;
    background: #e8f5e9;
    color: #2d5016;
    font-size: 30px;
    line-height: 60px;
    border-radius: 50%;
    margin: 0 auto 15px;
    display: inline-block;
  }
  
  .login-header h2 {
    margin: 0;
    color: #2d5016;
    font-size: 24px;
  }
  
  .login-header p {
    margin: 8px 0 0;
    color: #666;
    font-size: 14px;
  }
  
  /* 表单样式 */
  .form-group {
    margin-bottom: 20px;
    text-align: left;
  }
  
  .form-group label {
    display: block;
    margin-bottom: 8px;
    color: #333;
    font-weight: 500;
    font-size: 14px;
  }
  
  .input-wrapper {
    position: relative;
  }
  
  .input-icon {
    position: absolute;
    left: 12px;
    top: 50%;
    transform: translateY(-50%);
    color: #aaa;
    font-size: 14px;
  }
  
  input {
    width: 100%;
    padding: 12px 12px 12px 38px; /* 左边距留给图标 */
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 14px;
    transition: all 0.3s;
    box-sizing: border-box; /* 关键：防止 padding 撑大宽度 */
  }
  
  input:focus {
    border-color: #4a7c23;
    outline: none;
    box-shadow: 0 0 0 3px rgba(74, 124, 35, 0.1);
  }
  
  input.error {
    border-color: #e74c3c;
  }
  
  .error-msg {
    color: #e74c3c;
    font-size: 12px;
    margin-top: 5px;
    display: block;
  }
  
  /* 选项区 */
  .form-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 25px;
    font-size: 13px;
  }
  
  .checkbox-label {
    display: flex;
    align-items: center;
    color: #666;
    cursor: pointer;
  }
  
  .checkbox-label input {
    width: auto;
    margin-right: 6px;
    padding: 0;
  }
  
  .forgot-link {
    color: #4a7c23;
    text-decoration: none;
  }
  
  .forgot-link:hover {
    text-decoration: underline;
  }
  
  /* 按钮 */
  .btn-submit {
    width: 100%;
    padding: 12px;
    background: #4a7c23;
    color: white;
    border: none;
    border-radius: 8px;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    transition: background-color 0.3s;
  }
  
  .btn-submit:hover:not(:disabled) {
    background: #3a5c20;
  }
  
  .btn-submit:disabled {
    background: #a3c293;
    cursor: not-allowed;
  }
  
  .loading-spinner i {
    margin-right: 8px;
  }
  
  /* 底部 */
  .login-footer {
    margin-top: 25px;
    font-size: 14px;
    color: #666;
  }
  
  .link-register {
    color: #4a7c23;
    font-weight: 600;
    text-decoration: none;
  }
  
  /* 测试提示 */
  .test-hint {
    margin-top: 20px;
    padding: 10px;
    background: #f0f7eb;
    border-radius: 6px;
    font-size: 12px;
    color: #558b2f;
  }
  </style>