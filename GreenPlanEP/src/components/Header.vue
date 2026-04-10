<template>
    <header class="site-header">
      <div class="header-inner">
        <!-- 左侧控制图标 (控制左边栏)，待扩展 -->
        

        <!-- Logo -->
        <router-link to="/" class="logo">
          <span class="icon">🌱</span>
        </router-link>
  
        <!-- 导航菜单 -->
        <nav class="main-nav">
          <router-link to="/" active-class="active">首页</router-link>
          <router-link to="/productcatalog" active-class="active">商品</router-link>
          <router-link to="/orders" active-class="active">订单</router-link>
          <router-link to="/planttutorial" active-class="active">教程</router-link>
          <router-link to="/plantingdiary" active-class="active">日记</router-link>
          <router-link to="/community" active-class="active">社区</router-link>
        </nav>
  
        <!-- 右侧工具栏 (登录) -->
        <div class="header-tools" @click="handleUserClick">
            <div class="nav-link"><i class="fas fa-user"></i></div>
            <span v-if="isLoggedIn" class="username-tooltip">{{ currentUser.name || currentUser.nickname || currentUser.username }}</span>
        </div>
      </div>
    </header>
  </template>
  
<script setup>
  import { ref, onMounted } from 'vue';
  import { useRouter } from 'vue-router';
  import '@fortawesome/fontawesome-free/css/all.min.css';

  // 1. 定义响应式数据 
  const isLoggedIn = ref(false);
  const currentUser = ref(null);

  // 2. 获取路由实例 
  const router = useRouter();

  // 3. 定义方法 (
  const checkLoginStatus = () => {
    const storedUser = localStorage.getItem('greenPlan_user');
    
    if (storedUser) {
      currentUser.value = JSON.parse(storedUser);
      isLoggedIn.value = true;
      console.log('已恢复登录状态:', currentUser.value.name);
    } else {
      isLoggedIn.value = false;
      currentUser.value = null;
    }
  };

  const handleUserClick = () => {
    if (isLoggedIn.value) {
      router.push('/user');
    } else {
      router.push('/login');
    }
  };

  // 供其他组件调用的登录成功处理函数 (如果需要暴露给父组件，可以使用 defineExpose)
  const performLogin = (userData) => {
    currentUser.value = userData;
    isLoggedIn.value = true;
    localStorage.setItem('greenPlan_user', JSON.stringify(userData));
    router.push('/user');
    alert(`欢迎回来，${userData.name}！状态已持久化。`);
  };

  const logout = () => {
    isLoggedIn.value = false;
    currentUser.value = null;
    localStorage.removeItem('greenPlan_user');
    localStorage.removeItem('greenPlan_token');
    router.push('/login');
  };

  // 4. 生命周期钩子 (替代 created)
  onMounted(() => {
    checkLoginStatus();
  });

  // 如果需要在模板中使用 performLogin 或 logout (例如通过 ref 引用子组件)，需要暴露它们
  defineExpose({
    performLogin,
    logout
  });

</script>
  
<style scoped>
  .site-header {
    background: #80ab64;
    box-shadow: 0 2px 8px rgba(0,0,0,0.05);
    width: 100%;
    height: 50px;
    z-index: 1000;
    position: relative; 
    top: 0;          /* 2. 紧贴顶部 */
  }
  
  .header-inner {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;
    height: 50px;   
  }

  
  
  .icon-btn {
    background: none;
    border: none;
    color: #ebebeb;
    font-size: 1.5rem;
    align-items: center;
    cursor: pointer;
  }

  .logo {
    display: flex;
    align-items: center;
    text-decoration: none;
    color: #2d5016;
    font-size: 1.2rem;
  }
  
  .logo .icon {
    margin-right: 4px;
    font-size: 1.5rem;
  }
  
  .main-nav {
    display: flex;
    gap: 30px;
  }
  
  .main-nav a {
    text-decoration: none;
    color: #ebebeb;
    font-weight: 500;
    position: relative;
  }
  
  .main-nav a.active {
    color: #3a5c20;
  }
  
  .main-nav a.active::after {
    content: '';
    position: absolute;
    bottom: -13px;
    left: 0;
    width: 100%;
    height: 3px;
    background: #4a7c23;
  }
  

  @media (max-width: 480px) {
    .logo .icon{
      font-size: 1.2rem;
    }
    .main-nav {
      font-size: 0.9rem;
    }
  }

  .header-tools {
    display: flex;
    align-items: center;
    gap: 0px;
  }
  
  .tool-item {
    position: relative;
    font-size: 1.2rem;
    text-decoration: none;
    color: #333;
  }
  
  .badge {
    position: absolute;
    top: -8px;
    right: -8px;
    background: #e74c3c;
    color: #fff;
    font-size: 0.7rem;
    padding: 2px 6px;
    border-radius: 10px;
  }
  
  .btn-login {
    padding: 6px 16px;
    border: 1px solid #4a7c23;
    color: #4a7c23;
    border-radius: 20px;
    text-decoration: none;
    font-size: 0.9rem;
  }
  .nav-link{
    color: white;
  }
  .nav-link:hover {
    background: rgba(255,255,255,0.2);
  }

  /* 当前页面高亮 */
  .nav-link.router-link-exact-active {
    color: #4a7c23;
  }
  .fa-user {
    display: inline-block;
    width: 35px;
    line-height: 24px;
    text-align: center;
  }

  </style>