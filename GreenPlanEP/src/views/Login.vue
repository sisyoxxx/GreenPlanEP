<template>
    <div class="login-page">

      <div class="login-card">
        <!-- 头部 Logo -->
        <div class="login-header">
          <div class="logo-circle">🌱</div>
          <h2>{{ activeTab === 'login' ? '欢迎回来' : '创建账户' }}</h2>
        </div>
  
        <!-- ================ 登录表单 =============== -->
        <form v-if="activeTab === 'login'" @submit.prevent="handleLogin" class="login-form fade-in">
          <!-- 用户名输入 -->
          <div class="form-group">
            <div class="input-wrapper">
              <i class="fas fa-user input-icon"></i>
              <input 
                id="username"
                v-model="loginForm.username" 
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
                v-model="loginForm.password" 
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
              <input type="checkbox" v-model="loginForm.remember" />
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
  
        <!-- ================= 注册表单  ================= -->
        <form v-else @submit.prevent="handleRegister" class="login-form fade-in">
          <!-- 用户名 -->
          <div class="form-group">
            <div class="input-wrapper">
              <i class="fas fa-user input-icon"></i>
              <input 
                v-model="registerForm.username" 
                type="text" 
                placeholder="设置用户名"
                :class="{ 'error': errors.regUsername }"
                @blur="validateRegUsername"
              />
            </div>
            <span class="error-msg" v-if="errors.regUsername">{{ errors.regUsername }}</span>
          </div>

          <!-- 密码 -->
          <div class="form-group">
            <div class="input-wrapper">
              <i class="fas fa-lock input-icon"></i>
              <input 
                v-model="registerForm.password" 
                type="password" 
                placeholder="设置密码 (6位以上)"
                :class="{ 'error': errors.regPassword }"
                @blur="validateRegPassword"
              />
            </div>
            <span class="error-msg" v-if="errors.regPassword">{{ errors.regPassword }}</span>
          </div>

          <!-- 确认密码 (新增) -->
          <div class="form-group">
            <div class="input-wrapper">
              <i class="fas fa-check-circle input-icon"></i>
              <input 
                v-model="registerForm.confirmPassword" 
                type="password" 
                placeholder="确认密码"
                :class="{ 'error': errors.regConfirm }"
                @blur="validateRegConfirm"
              />
            </div>
            <span class="error-msg" v-if="errors.regConfirm">{{ errors.regConfirm }}</span>
          </div>

          <!-- 协议勾选 (新增) -->
          <div class="form-options" style="justify-content: flex-start;">
            <label class="checkbox-label agreement-label">
              <input type="checkbox" v-model="registerForm.agreed" />
              <span>我已阅读并同意 <a href="#" class="link-agreement">《用户服务协议》</a></span>
            </label>
          </div>
          <span class="error-msg" v-if="errors.regAgreed">{{ errors.regAgreed }}</span>

          <!-- 提交按钮 -->
          <button type="submit" class="btn-submit btn-register" :disabled="isLoading">
            <span v-if="!isLoading">立即注册</span>
            <span v-else class="loading-spinner">
              <i class="fas fa-circle-notch fa-spin"></i> 注册中...
            </span>
          </button>
        </form>

        <!-- ================ 底部切换提示 =============== -->
        <div class="login-footer">
          <p v-if="activeTab === 'login'">
            还没有账户？ <a href="#" class="link-register" @click.prevent="switchTab('register')">立即注册</a>
          </p>
          <p v-else>
            已有账号？ <a href="#" class="link-register" @click.prevent="switchTab('login')">去登录</a>
          </p>
      </div>
        
      </div>

    </div>
  </template>
  
<script setup>
  import { ref, reactive } from 'vue';
  import { useRouter } from 'vue-router';
  import '@fortawesome/fontawesome-free/css/all.min.css';
  import { useUserStore } from '@/api/user';
  import axios from 'axios';
  
  const router = useRouter();
  const userStore = useUserStore(); // ← 使用 Pinia store
  

  // ========================= 状态管理 =====================
  // 当前激活的标签页：'login' 或 'register'
  const activeTab = ref('login');
  const isLoading = ref(false);

  // 登录表单数据
  const loginForm = reactive({
    username: '',
    password: '',
    remember: true
  });
  
  // 注册表单数据 
  const registerForm = reactive({
    username: '',
    password: '',
    confirmPassword: '',
    agreed: false
  });

  // 错误信息对象 (合并了登录和注册的错误字段)
  const errors = reactive({
    username: '',
    password: '',
    regUsername: '',      // 注册用户名错误
    regPassword: '',      // 注册密码错误
    regConfirm: '',       // 确认密码错误
    regAgreed: ''         // 协议未勾选错误
  });
  

  // ======================== 验证逻辑 ========================
  // --- 登录验证 (保持原有) ---
  const validateUsername = () => {
    if (!loginForm.username.trim()) {
      errors.username = '账号不能为空';
      return false;
    }
    errors.username = '';
    return true;
  };
  
  const validatePassword = () => {
    if (!loginForm.password) {
      errors.password = '密码不能为空';
      return false;
    }
    if (loginForm.password.length < 6) {
      errors.password = '密码长度至少为6位';
      return false;
    }
    errors.password = '';
    return true;
  };
  
  // --- 注册验证  ---
  const validateRegUsername = () => {
    if (!registerForm.username.trim()) {
      errors.regUsername = '用户名不能为空';
      return false;
    }
    if (registerForm.username.length < 3) {
      errors.regUsername = '用户名至少3个字符';
      return false;
    }
    errors.regUsername = '';
    return true;
  };

  const validateRegPassword = () => {
    if (!registerForm.password) {
      errors.regPassword = '密码不能为空';
      return false;
    }
    if (registerForm.password.length < 6) {
      errors.regPassword = '密码长度至少为6位';
      return false;
    }
    errors.regPassword = '';
    return true;
  };

  const validateRegConfirm = () => {
    if (!registerForm.confirmPassword) {
      errors.regConfirm = '请再次输入密码';
      return false;
    }
    if (registerForm.password !== registerForm.confirmPassword) {
      errors.regConfirm = '两次输入的密码不一致';
      return false;
    }
    errors.regConfirm = '';
    return true;
  };


  // ========================== 业务逻辑 ======================
  // 登录处理
  const handleLogin = async () => {
    const isUserValid = validateUsername();
    const isPassValid = validatePassword();
    
    if (!isUserValid || !isPassValid) return;
  
    isLoading.value = true;
  
    try {
      // ✅ 调用 Pinia store 的 login 方法（已封装 axios + token 存储）
      await userStore.login({
        username: loginForm.username,
        password: loginForm.password
      });
      alert(`登录成功！欢迎 ${userStore.currentUser?.name || loginForm.username}`);
      router.replace('/user');
    } 
    catch (error) {
      console.error('登录失败:', error);
      alert('登录失败：' + (error.response?.data?.error || '用户名或密码错误'));
    } 
    finally {
      isLoading.value = false;
    }
  };

  // 注册处理
  const handleRegister = async () => {
    // 1. 执行所有注册字段的验证
    const isUserValid = validateRegUsername();
    const isPassValid = validateRegPassword();
    const isConfirmValid = validateRegConfirm();
    
    // 检查协议
    if (!registerForm.agreed) {
      errors.regAgreed = '请先同意用户服务协议';
      return;
    } else {
      errors.regAgreed = '';
    }

    if (!isUserValid || !isPassValid || !isConfirmValid) return;

    isLoading.value = true;

    try {
      // 2. 调用后端注册接口
      // 假设后端接口为 /api/auth/register
      const response = await axios.post('http://localhost:8080/api/auth/register', {
        username: registerForm.username,
        password: registerForm.password,
        confirmPassword: registerForm.confirmPassword,
        // 如果有邮箱或手机，也可以在这里添加
      });

      // 3. 处理成功响应
      if (response.data.code === 200 || response.data.success) {
        alert('注册成功！即将跳转到登录页面...');
        
        // 4. 自动切换回登录态，并预填用户名，提升体验
        switchTab('login');
        loginForm.username = registerForm.username;
        loginForm.password = ''; // 清空密码
        
        // 可选：清空注册表单
        registerForm.username = '';
        registerForm.password = '';
        registerForm.confirmPassword = '';
        registerForm.agreed = false;
      } else {
        // 后端业务错误 (如用户名已存在)
        alert('注册失败：' + (response.data.message || '未知错误'));
      }

    } catch (error) {
      console.error('注册异常:', error);
      // 网络错误或服务器崩溃
      const errMsg = error.response?.data?.message || '网络请求失败，请稍后重试';
      alert('注册失败：' + errMsg);
    } finally {
      isLoading.value = false;
    }
  };

  // 切换 Tab 时清空错误提示
  const switchTab = (tab) => {
    activeTab.value = tab;
    // 清空所有错误
    Object.keys(errors).forEach(key => errors[key] = '');
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
    display: flex;
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
    margin: 0 16px 0 0;
    display: inline-block;
  }
  
  .login-header h2 {
    margin: 10px 16px 0;
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

  /* 淡入动画：让 Tab 切换更平滑 */
  .fade-in {
    animation: fadeIn 0.4s ease-in-out;
  }

  @keyframes fadeIn {
    from {
      opacity: 0;
      transform: translateY(10px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  /* 注册按钮稍微不同的 hover 效果 (可选，保持统一也可) */
  .btn-register:hover:not(:disabled) {
    background: #3a5c20; 
    /* 如果想区分，可以改成 slightly different shade, e.g., #3d661e */
  }
  </style>