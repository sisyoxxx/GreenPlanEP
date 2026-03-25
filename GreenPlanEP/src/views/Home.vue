<template>
  <div class="container">
    
    <!-- 1. 左侧分类栏 (首页特有) -->
    <div class="sidebar-wrapper"> 
      <LeftSidebar/>
    </div>
    

    <!-- 2. 中间主要内容区 -->
      <MainLayout/>

    <!-- 3. 右侧工具栏 (首页特有) -->
     <div class="right-wrapper">
      <RightSidebar/>
    </div>

    <!-- 4. 移动端遮罩层 -->
    <div v-if="(!isDesktop && (isLeftOpen || isRightOpen))" class="overlay" @click="closeAll"></div>

  </div>
</template>

<script setup>
  import { ref, reactive, computed, onMounted, onUnmounted, provide } from 'vue'
  import LeftSidebar from '../components/layout/LeftSidebar.vue'
  import RightSidebar from '../components/layout/RightSidebar.vue'
  import MainLayout from '../components/layout/MainLayout.vue'
  import { useCart } from '@/stores/cartStore'


  // ===== 1. 引入配置常量 (核心优化) =====
  import { 
    CATEGORY_MAP, 
    CATEGORY_ICONS 
  } from '@/config/product'

  // 当前激活的标签页
  const activeTab = ref('home')

  // ===== 2. 动态生成分类数据 (替代硬编码数组) =====
  // 将 config 中的对象转换为组件需要的数组格式 [{ id, name, icon }, ...]
  // 使用 index + 1 作为 id，或者如果后续需要映射到数据库 key，也可以存 key
  const categories = reactive(
    Object.keys(CATEGORY_MAP).map((key, index) => ({
      id: index + 1,          // 生成自增 ID (1, 2, 3...)
      key: key,               // 保留原始 Key (如 'FLOWER_SEEDS') 方便后续筛选
      name: CATEGORY_MAP[key],// 显示名称 (如 '花卉种子')
      icon: CATEGORY_ICONS[key] || '🌱' // 图标，如果没有则用默认
    }))
  )

  // ===== 3. 响应式布局状态管理 =====
  const windowWidth = ref(window.innerWidth)
  const isLeftOpen = ref(true)
  const isRightOpen = ref(true)
  const isLeftNarrow = ref(false)

  // 断点判断
  const isDesktop = computed(() => windowWidth.value > 1024) 

  // 初始化布局状态
  const initStatus = () => {
    if (isDesktop.value) {
      isLeftOpen.value = true
      isRightOpen.value = true
      isLeftNarrow.value = false
    } else {
      // 移动端默认关闭侧边栏
      isLeftOpen.value = false
      isRightOpen.value = false
    }
  }

  const updateWidth = () => {
    windowWidth.value = window.innerWidth
    initStatus()
  }

  const closeAll = () => {
    isLeftOpen.value = false
    isRightOpen.value = false
  }

  // ===== 5. 生命周期 =====
  onMounted(() => {
    window.addEventListener('resize', updateWidth)
    initStatus()
  })

  onUnmounted(() => {
    window.removeEventListener('resize', updateWidth)
  })

  // ===== 6. 购物车逻辑 =====
  const cart = useCart()
  // 提供给所有子组件 (如果子组件需要直接访问 cart)
  provide('cart', cart)
</script>

<style scoped>
  * {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
  }

  .container {
    height: 100%;
    display: flex;
    padding: 0;
    margin: 0;
    font-family: 'Microsoft YaHei', Arial, sans-serif;
  }

  /* 左侧栏：在LeftSidebar组件中定义 */
  .sidebar-wrapper {
    position: -webkit-sticky;
    position: sticky;
    top: 0px; 
    height: calc(100vh - 20px); 
    overflow: hidden; 
    flex-shrink: 0; /* 防止被中间内容挤压 */
  }

  .right-wrapper {
    position: -webkit-sticky;
    position: sticky;
    top: 0px; 
    height: calc(100vh - 20px); 
    overflow: hidden; 
    flex-shrink: 0; /* 防止被中间内容挤压 */
    display: flex;
  }

   ::v-deep(.left-sidebar) {
      height: 100%; 
      width: 100%;
      overflow-y: auto; 
      scrollbar-width: none;
    }
    ::v-deep(.left-sidebar)::-webkit-scrollbar {
      display: none;
    }


  /* 中间内容：自动填满剩余空间 */
  :deep(.main-layout) {
    flex: 1; /* 占据所有剩余空间 */
    overflow-x: hidden; /* 防止内容溢出 */
  }


  /* 页脚 */
  .footer {
    background: #2d3436;
    color: white;
    padding: 3rem 5% 1rem;
  }

  .footer-links {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 2rem;
    margin-bottom: 2rem;
  }

  .footer-section h4 {
    margin-bottom: 1rem;
    color: #ffd700;
  }

  .footer-section a {
    display: block;
    color: #bdc3c7;
    text-decoration: none;
    margin-bottom: 0.5rem;
    transition: color 0.3s;
  }

  .footer-section a:hover {
    color: white;
  }

  .copyright {
    text-align: center;
    padding-top: 2rem;
    border-top: 1px solid #444;
    color: #bdc3c7;
  }


  /* 响应式设计 */
  @media (max-width: 1024px) {
  /* 平板/手机端：侧边栏变为绝对定位 */
  
  /* 左侧栏样式由 HomeSidebar 内部控制 transform */
  
  /* 右侧栏默认隐藏，除非 open */
  /* HomeAside 内部需处理 absolute 定位 */
  }

  @media (max-width: 768px) {
    .header {
      flex-direction: column;
      gap: 1rem;
    }
  
  }
</style>