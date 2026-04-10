<template>
    <div class="product-catalog">
       <!-- 浮窗按钮 -->
      <DraggableFloatButton :badge="cartCount" @click="toggleCart" >
      </DraggableFloatButton>

      <!-- 主内容区：左右分栏 -->
      <div class="main-content">
        <!-- 左侧：导航栏 -->
        <aside class="sidebar">
          <!-- 商品分类 -->
          <div class="category-section">
            <h3>商品分类</h3>
            <div class="categories">
              <button
                v-for="(name, key) in CATEGORY_MAP"
                :key="key"
                :class="{ active: searchForm.category === key }"
                @click="searchForm.category = (searchForm.category === key) ? null : key; 
                handleFilterChange()"
              >
                <span class="icon">{{ getCategoryIcon(key) }}</span>
                <span class="text">{{ name }}</span>
              </button>
            </div>
          </div>
  
          <!-- 月份筛选 -->
          <div class="filter-section">
            <h4>种植月份</h4>
            <div class="month-filter">
              <button
                v-for="month in PLANTING_MONTHS"
                :key="month"
                :class="{ active: searchForm.month === month }"
                @click="searchForm.month = (searchForm.month === month) ? null : month;
                handleFilterChange()"
              >
                {{ month }}
              </button>
            </div>
          </div>
  
          <!-- 地区筛选 -->
          <div class="filter-section">
            <h4>适宜地区</h4>
            <div class="region-filter">
              <button
                v-for="region in SUITABLE_REGIONS"
                :key="region"
                :class="{ active: searchForm.region === region }"
                @click="searchForm.region = (searchForm.region === region) ? null : region; 
                handleFilterChange()"
              >
                {{ region }}
              </button>
            </div>
          </div>
        </aside>
  
        <!-- 中间：商品展示区 -->
        <main class="content-panel">
          <!-- 搜索框 -->
          <div class="search-bar">
            <input
              v-model="searchForm.keyword"
              type="text"
              placeholder="搜索种子、植物或工具..."
              @input="onSearchInput"
            />
          </div>
  
          <!-- 商品卡片网格 -->
          <div class="product-grid">
            <div v-if="loading" class="empty-state">正在加载数据...</div>
            <div v-else-if="productList.length === 0" class="empty-state">
              <p>暂无符合条件的商品</p>
            </div>
            <div v-else class="grid">
              <div
                v-for="product in productList"
                :key="product.id"
                class="product-card"
              >
                <div class="product-image">
                  <img :src="product.imageUrl || product.image || 'https://via.placeholder.com/100x100?text=NoImg'" alt="商品图" />
                </div>
                <div class="product-info">
                  <h4>{{ product.name }}</h4>
                  <p class="category-tag">{{ CATEGORY_MAP[product.category] }}</p>
                  <p class="description">{{ product.shortDescription  }}</p>
  
                  <!-- 种植难度 -->
                  <div class="difficulty">
                    <span>难度：</span>
                    <div class="stars">
                      <span
                        v-for="i in 5"
                        :key="i"
                        class="star"
                        :class="{ filled: i <= product.difficulty }"
                      >★</span>
                    </div>
                    <small>{{ getDifficultyText(product.difficulty) }}</small>
                  </div>
  
                  <div class="price">¥{{ product.price.toFixed(2) }}</div>
                  <button class="btn-add-to-cart" @click="addToCart(product)">
                    加入购物车
                  </button>
                </div>
              </div>
            </div>
          </div>
        </main>

        <!-- 购物车浮窗面板 -->
        <div
        v-show="isCartOpen"
        class="cart-panel"
        :style="{ right: isDocked ? '0' : `${positionX}px`, top: `${positionY}px` }"
        @mousedown="startDrag"
        >
        <div class="cart-header">
            <h3>购物车</h3>
            <button class="remove-btn" @click="clearCart">🗑️</button>
        </div>
        <div class="cart-items">
          <div v-if="cart.items.length === 0" class="empty-cart">
            <p>购物车为空</p>
          </div>
          <div v-else class="items-list">
            <div v-for="item in cart.items" :key="item.id" class="cart-item">
                <img :src="item.imageUrl" alt="" />
                <div class="item-info">
                  <p>{{ item.name }}</p>
                  <div class="quantity-controls">
                    <button class="change-btn" @click="decreaseQty(item.id)">−</button>
                    <span>{{ item.quantity }}</span>
                    <button class="change-btn" @click="cart.add(item)">+</button>
                  </div>
                  <p class="total-price">¥{{ (item.price * item.quantity).toFixed(2) }}</p>
                </div>
                <button class="remove-btn" @click="removeFromCart(item.id)">×</button>
            </div>
          </div>
        </div>
        <div class="checkout-footer">
            <div class="total">
              <strong>合计：</strong> <span class=total-price>¥{{ totalAmount.toFixed(2) }}</span>
            </div>
            <button class="btn-checkout">去结算</button>
        </div>
        </div>
      </div>
       
  </div>
  </template>
  
<script setup>
  import { ref, computed, onMounted } from 'vue'
  import DraggableFloatButton from '../components/cartFloating.vue'
  import { useCart } from '@/stores/cartStore'
  import { getProductList } from '@/api/product'
  
  // ===== 引入配置常量 =====
  import { 
    CATEGORY_MAP, 
    CATEGORY_ICONS, 
    PLANTING_MONTHS, 
    SUITABLE_REGIONS,
    DIFFICULTY_MAP,
    DEFAULT_ICON
  } from '@/config/product'
  
  // ===== 状态定义 =====
  const searchForm = ref({
    keyword: '',
    category: null,
    month: null,
    region: null,
    page: 1,
    pageSize: 20
  })
  
  const productList = ref([])
  const loading = ref(false)
  const total = ref(0)
  
  // ===== 购物车逻辑 =====
  const cart = useCart()
  const isCartOpen = ref(false)
  
  const cartCount = computed(() => cart.count)
  const totalAmount = computed(() => cart.total)
  
  const toggleCart = () => {
    isCartOpen.value = !isCartOpen.value
  }
  
  const addToCart = (product) => {
    cart.add(product)
  }
  
  const removeFromCart = (productId) => {
    cart.remove(productId)
  }
  
  const decreaseQty = (productId) => {
    cart.decreaseQuantity(productId)
  }
  
  const clearCart = () => {
    cart.clear()
  }
  
  // ===== 拖拽浮窗状态 =====
  const positionX = ref(30)
  const positionY = ref(10)
  const isDragging = ref(false)
  const isDocked = ref(false)
  
  // ===== 核心数据获取逻辑 (已简化) =====
  const fetchData = async () => {
    loading.value = true
    try {
      // 👇 直接透传 searchForm，具体的清洗工作交给 api/product.js 处理
      console.log('🚀 发送请求，原始参数:', searchForm.value)
      
      const res = await getProductList(searchForm.value)
      
      console.log('📥 后端返回数据:', res)
  
      // request 拦截器已统一返回 payload，直接按数据结构处理
      handleDataStructure(res)
  
    } catch (error) {
      console.error('❌ 获取数据失败:', error)
      alert(`加载失败：${error.message || '请检查网络或后端服务'}`)
    } finally {
      loading.value = false
    }
  }
  
  // ===== 数据格式兼容处理 =====
  function handleDataStructure(data) {
    let dataList = []
    let totalCount = 0
  
    if (!data) {
      dataList = []
      totalCount = 0
    } else if (Array.isArray(data)) {
      dataList = data
      totalCount = data.length
    } else if (data.list) {
      // 常见分页格式 { list: [], total: 100 }
      dataList = data.list
      totalCount = data.total || 0
    } else if (data.records) {
      // MyBatis-Plus 分页格式 { records: [], total: 100 }
      dataList = data.records
      totalCount = data.total || 0
    } else {
      console.warn('⚠️ 未知数据结构:', data)
      dataList = []
    }
  
    productList.value = dataList
    total.value = totalCount
    console.log('✅ 渲染成功，共', dataList.length, '条')
  }
  
  // ===== 交互与过滤 =====
  const handleFilterChange = () => {
    searchForm.value.page = 1 // 切换条件重置页码
    fetchData()
  }
  
  // 搜索防抖
  let searchTimer = null
  const onSearchInput = () => {
    if (searchTimer) clearTimeout(searchTimer)
    searchTimer = setTimeout(() => {
      handleFilterChange()
    }, 500)
  }
  
  // ===== 辅助工具 =====
  const getCategoryIcon = (key) => CATEGORY_ICONS[key] || DEFAULT_ICON
  const getDifficultyText = (level) => DIFFICULTY_MAP[level] || '未知'
  
  // ===== 生命周期 =====
  onMounted(() => {
    fetchData()
  })
  </script>

<style scoped>
  .product-catalog {
    padding: 0;
    background-color: #f9faf8;
    min-height: 100vh;
  }

  .cart-toggle {
    position: relative;
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background: #fff;
    color: #7cb342;
    border: none;
    cursor: pointer;
    font-size: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 2px 6px rgba(0,0,0,0.1);
  }
  
  .cart-count {
    position: absolute;
    top: -8px;
    right: -8px;
    background: #f44336;
    color: white;
    font-size: 12px;
    width: 18px;
    height: 18px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .sidebar {
    flex: 0 0 220px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    padding: 16px;
  }
  
  .content-panel {
    flex: 1;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    padding: 20px;
  }
  
  .search-bar input {
    width: 100%;
    padding: 12px;
    border: 1px solid #ddd;
    border-radius: 24px;
    font-size: 16px;
    outline: none;
  }
  
  .product-grid {
    margin-top: 20px;
  }
  
  .grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 24px;
  }
  
  .empty-state {
    text-align: center;
    color: #999;
    padding: 40px 0;
  }
  
  .product-card {
    background-image: url('../assets/images/basket.png');
    border-radius: 12px;
    min-height: 300px; 
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transition: transform 0.2s;
  }
  
  .product-card:hover {
    transform: translateY(-4px);
  }
  
  .product-image img {
    width: 100%;
    height: 120px;
    object-fit: cover;
  }
  
  .product-info {
    padding: 12px;
  }
  
  .product-info h4 {
    margin: 0 0 8px;
    font-size: 16px;
  }
  
  .category-tag {
    font-size: 12px;
    color: #66bb6a;
    margin-bottom: 8px;
  }
  
  .description {
    font-size: 14px;
    color: #666;
    margin-bottom: 12px;
    line-height: 1.4;
  }
  
  .difficulty {
    display: flex;
    align-items: center;
    margin-bottom: 12px;
    font-size: 14px;
  }
  
  .stars {
    margin: 0 6px;
  }
  
  .star {
    color: #bdbdbd;
    font-size: 14px;
  }
  
  .star.filled {
    color: #ff9800;
  }
  
  .price {
    font-size: 18px;
    color: #2e7d32;
    font-weight: bold;
    margin: 8px 0;
  }
  
  .btn-add-to-cart {
    width: 100%;
    padding: 8px;
    background-color: #4caf50;
    color: white;
    border: none;
    border-radius: 6px;
    font-weight: bold;
    cursor: pointer;
  }
  
  .btn-add-to-cart:hover {
    background-color: #388e3c;
  }
  
  /* 购物车面板 */
  .cart-panel {
    position: fixed;
    top: 100px;
    right: 300px;
    width: 320px;
    height: calc(100vh - 100px);
    background: white;
    border-radius: 8px;
    box-shadow: -5px 0 15px rgba(0,0,0,0.1);
    z-index: 1000;
    overflow: hidden;
    cursor: move;
    transition: right 0.3s ease;
  }
  
  .cart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    border-bottom: 1px solid #eee;
  }
  
  .close-btn {
    background: none;
    border: none;
    font-size: 20px;
    cursor: pointer;
    color: #999;
  }
  
  .cart-items {
    padding: 16px;
    overflow-y: auto;
    height: calc(100% - 100px);
  }
  
  .empty-cart {
    text-align: center;
    color: #999;
    padding: 20px;
  }
  
  .items-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }
  
  .cart-item {
    display: flex;
    align-items: center;
    padding: 8px;
    border-bottom: 1px dashed #eee;
  }
  
  .cart-item img {
    width: 50px;
    height: 50px;
    object-fit: cover;
    border-radius: 4px;
  }
  
  .item-info {
    margin: 0 12px;
    flex: 1;
  }
  
  .total-price {
    color: #f44336;
    font-weight: bold;
  }
  

  
  /* 主内容区 */
  .main-content {
    display: flex;
    max-width: 1400px;
    margin: 0 auto;
    padding: 20px;
  }
  
  .sidebar {
    flex: 0 0 220px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    padding: 16px;
  }
  
  .category-section h3 {
    margin-bottom: 2px;
    color: #2e7d32;
    font-size: 16px;
  }
  
  .categories {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(60px, 1fr));
    align-items: center;
    justify-content: center;
    gap: 10px;
    padding: 0px;
  }
  
  .categories button {
    display: flex;
    flex-direction: column;
    padding: 6px 10px;
    border: 1px solid #ddd;
    background: #f5f5f5;
    border-radius: 8px;
    cursor: pointer;
    text-align: center;
    transition: all 0.2s;
    font-size: 14px;
  }
  
  .categories button:hover {
    background: #e8f5e9;
    border-color: #4caf50;
  }
  
  .categories button.active {
    background: #c8e6c9;
    border-color: #4caf50;
    color: #1b5e20;
    font-weight: bold;
  }
  
  .icon {
    font-size: 1.2rem;
    margin-bottom: 5px;
  }
  
  .text {
    font-size: 0.85rem;
  }
  
  .filter-section {
    margin-top: 24px;
  }
  
  .filter-section h4 {
    margin-bottom: 8px;
    color: #2e7d32;
    font-size: 14px;
  }
  
  .month-filter, .region-filter {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(40px, 1fr));
    flex-wrap: wrap;
    gap: 5px;
  }
  
  .month-filter button, .region-filter button {
    padding: 4px 8px;
    font-size: 12px;
    border: 1px solid #ccc;
    background: white;
    border-radius: 4px;
    cursor: pointer;
  }
  
  .month-filter button.active, .region-filter button.active {
    background: #c8e6c9;
    border-color: #4caf50;
    color: #1b5e20;
  }
  
  .content-panel {
    flex: 1;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    padding: 20px;
  }
  
  .search-bar input {
    width: 100%;
    padding: 12px;
    border: 1px solid #ddd;
    border-radius: 24px;
    font-size: 16px;
    outline: none;
  }
  
  .product-grid {
    margin-top: 20px;
  }
  
  .grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 24px;
  }
  
  .empty-state {
    text-align: center;
    color: #999;
    padding: 40px 0;
  }
  
  .product-card {
    background: white;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0,0,0,0.08);
    transition: transform 0.2s;
  }
  
  .product-card:hover {
    transform: translateY(-4px);
  }
  
  .product-image img {
    width: 100%;
    height: 120px;
    object-fit: cover;
  }
  
  .product-info {
    padding: 12px;
  }
  
  .product-info h4 {
    margin: 0 0 8px;
    font-size: 16px;
  }
  
  .category-tag {
    font-size: 12px;
    color: #66bb6a;
    margin-bottom: 8px;
  }
  
  .description {
    font-size: 14px;
    color: #666;
    margin-bottom: 12px;
    line-height: 1.4;
  }
  
  .difficulty {
    display: flex;
    align-items: center;
    margin-bottom: 12px;
    font-size: 14px;
  }
  
  .stars {
    margin: 0 6px;
  }
  
  .star {
    color: #bdbdbd;
    font-size: 14px;
  }
  
  .star.filled {
    color: #ff9800;
  }
  
  .price {
    font-size: 18px;
    color: #2e7d32;
    font-weight: bold;
    margin: 8px 0;
  }
  
  .btn-add-to-cart {
    width: 100%;
    padding: 8px;
    background-color: #4caf50;
    color: white;
    border: none;
    border-radius: 6px;
    font-weight: bold;
    cursor: pointer;
  }
  
  .btn-add-to-cart:hover {
    background-color: #388e3c;
  }
  
  /* 购物车浮层 */
  .cart-floating {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    background: white;
    border-top: 1px solid #eee;
    box-shadow: 0 -2px 10px rgba(0,0,0,0.1);
    padding: 12px 20px;
    z-index: 100;
  }
  
  .cart-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;
  }
  
  .cart-icon {
    font-size: 20px;
  }
  
  .cart-title {
    font-weight: bold;
    color: #2e7d32;
  }
  
  .cart-count {
    background: #f44336;
    color: white;
    padding: 4px 8px;
    border-radius: 12px;
    font-size: 12px;
  }
  
  .cart-items {
    max-height: 200px;
    overflow-y: auto;
    margin-bottom: 12px;
  }
  
  .cart-item {
    display: flex;
    align-items: center;
    padding: 8px 0;
    border-bottom: 1px dashed #eee;
  }
  
  .cart-item img {
    width: 50px;
    height: 50px;
    object-fit: cover;
    border-radius: 4px;
  }

  .change-btn{
    background: none;
    border: none;
    font-size: 18px;
    color: #a0a0a0;
    cursor: pointer;
    width: 24px;
    height: 24px;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    transition: background-color 0.2s;
  }
  
  .item-info {
    margin: 0 12px;
    flex: 1;
  }
  
  .total-price {
    color: #f44336;
    font-weight: bold;
  }
  
  .remove-btn {
  background: none;
  border: none;
  font-size: 18px;
  color: #f44336;
  cursor: pointer;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.2s;
}

.remove-btn:hover {
  background: #ffebee;
}
  
  .checkout-footer {
    padding: 16px;
    border-top: 1px solid #eee;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .checkout-btn {
    text-align: right;
  }
  
  .btn-checkout {
    background-color: #4caf50;
    color: white;
    border: none;
    padding: 8px 16px;
    border-radius: 6px;
    font-weight: bold;
    cursor: pointer;
  }
  
  /* 响应式 */
  @media (max-width: 900px) {
    .main-content {
      flex-direction: column;
    }
    .sidebar {
      flex: none;
    }
    .content-panel {
      padding: 16px;
    }
}
</style>