<template>
  <div id="app" class="app-container">
    <!-- 全局头部组件 -->
    <AppHeader />
    
    <!-- 主内容区域：路由视图 -->
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <component :is="Component" />
      </router-view>
    </main>

    <!-- 全局页脚组件 -->
    <AppFooter />
  </div>
</template>

<script setup>
  import { defineAsyncComponent } from 'vue'

  // 异步加载全局组件，优化首屏速度
  const AppHeader = defineAsyncComponent(() => import('@/components/Header.vue'))
  const AppFooter = defineAsyncComponent(() => import('@/components/Footer.vue'))

</script>


<style>
  /* 全局重置样式 */
  * {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
  }
  html {
    scrollbar-width: none; /* 必须作用于 html */
  }

  /* 同时兼容其他浏览器 */
  html::-webkit-scrollbar {
    display: none;
  }

  body {
    font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', sans-serif;
    /*  固定背景，滚动页面时背景不动 */
    background-attachment: fixed;
    background-size: cover;          /* 覆盖全屏 */
    color: #333;
    -webkit-font-smoothing: antialiased;
    line-height: 1.6;   /*每一行的文字高度为该文字字体大小的1.6倍*/
  }

#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-content {
  flex: 1;
  width: 100%;
  margin: 0 auto;
  padding: 0;
}


/* 响应式设计 */
@media (max-width: 768px) {
  .main-content {
    padding: 0px;
  }
  
  body {
    font-size: 14px;
  }
}
</style>