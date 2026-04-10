<template>
  <main class="main-layout">
    <!-- 热门商品 -->
    <section class="featured-products">
      <h3>热门绿植种子</h3>
      
      <!-- 加载状态提示 -->
      <div v-if="loading" class="loading-tip">正在加载商品...</div>
      
      <!-- 空数据提示 -->
      <div v-else-if="products.length === 0" class="empty-tip">暂无商品数据</div>

      <div class="product-grid" v-else>
        <div class="product-card" v-for="product in products" :key="product.id">
          <div class="product-image" @click="goToDetail(product.id)">
            <!-- 注意：后端返回的是 imageUrl (如 /uploads/xxx.jpg)，需要拼接域名或直接使用 -->
            <!-- 如果后端返回的是相对路径，img src 可以直接用，因为 axios baseURL 只影响 ajax，不影响 img 标签 -->
            <!-- 但为了保险，如果图片是相对路径，可能需要拼上 http://localhost:8080 -->
            <img :src="getImageUrl(product.imageUrl)" :alt="product.name">
          </div>
          <div class="product-info">
            <h4 @click="goToDetail(product.id)" style="cursor: pointer;">{{ product.name }}</h4>
            <p class="price">¥{{ product.price }}</p>
            <!-- 后端如果没有 sales 字段，可以暂时隐藏或显示库存 -->
            <p class="sales">库存 {{ product.stock }}件</p> 
            <button class="buy-btn" @click="addToCart(product)">加入购物车</button>
          </div>
        </div>
      </div>
    </section>
  </main>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useCart } from '@/stores/cartStore'
import { getProductList } from '@/api/product' // 引入 API

const cart = useCart()

// 1. 定义响应式数据
const products = ref([]) // 使用 ref 而不是 reactive 数组，方便整体替换
const loading = ref(true)

// 2. 图片路径处理工具函数
const getImageUrl = (path) => {
  if (!path) return '/images/default.jpg'
  // 如果路径已经是 http 开头，直接返回
  if (path.startsWith('http')) return path
  // 否则拼接后端地址 (根据你的实际部署情况调整)
  return 'http://localhost:8080' + path
}

// 3. 获取数据的方法
const fetchProducts = async () => {
  try {
    loading.value = true
    // 调用 API，传入分页参数 (如果后端支持)
    const res = await getProductList({ page: 1, pageSize: 20 })
    
    // 兼容常见返回结构：数组 / { list } / { records }
    if (Array.isArray(res)) {
      products.value = res
    } else if (res?.list) {
      products.value = res.list
    } else if (res?.records) {
      products.value = res.records
    } else {
      products.value = []
      console.warn('商品列表数据结构异常:', res)
    }
    
  } catch (error) {
    console.error('获取商品失败:', error)
    alert('加载商品失败，请检查后端服务是否启动')
    products.value = []
  } finally {
    loading.value = false
  }
}

// 4. 加入购物车 (保持原有逻辑，确保 product 对象结构匹配 store)
const addToCart = (product) => {
  // 如果 store 里需要 image 字段，而数据库叫 imageUrl，这里做个映射
  const cartItem = {
    ...product,
    image: product.imageUrl // 适配 cartStore 可能的字段名
  }
  cart.add(cartItem)
  alert('已加入购物车')
}

// 5. 跳转详情页 (路由跳转)
const goToDetail = (id) => {
  // 假设你配置了路由 /product/:id
  // import { useRouter } from 'vue-router'
  // const router = useRouter()
  // router.push(`/product/${id}`)
  console.log('跳转到详情页，ID:', id)
  // TODO: 后续实现路由跳转
}

// 6. 生命周期：组件挂载时自动请求
onMounted(() => {
  fetchProducts()
})
</script>

<style scoped>
/* 原有的样式保持不变 */
.featured-products {
    background: rgb(255, 255, 255, 0.2);
    padding: 1rem;
    border-radius: 10px;
    margin-bottom: 2rem;
}
.product-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 2rem;
    margin-top: 1rem;
}
.product-card {
    background: rgb(255, 255, 255);
    border: 1px solid #eee;
    border-radius: 10px;
    overflow: hidden;
    transition: box-shadow 0.3s;
    cursor: pointer; /* 增加点击手感 */
}
.product-card:hover {
    box-shadow: 0 5px 20px rgba(0,0,0,0.1);
}
.product-image {
    height: 150px;
    background: #f0f0f0;
    display: flex;
    align-items: center;
    justify-content: center;
}
.product-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}
.product-info {
    padding: 1rem;
}
.product-info h4 {
    margin-bottom: 0.5rem;
    color: #333;
    font-size: 1rem;
    line-height: 1.4;
}
.price {
    color: #e74c3c;
    font-size: 1.2rem;
    font-weight: bold;
}
.sales {
    color: #999;
    font-size: 0.9rem;
    margin: 0.4rem 0;
}
.buy-btn {
    width: 100%;
    padding: 0.6rem;
    background: #4a7c23;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.3s;
    margin-top: 0.5rem;
}
.buy-btn:hover {
    background: #2d5016;
}
.loading-tip, .empty-tip {
    text-align: center;
    padding: 2rem;
    color: #666;
}
</style>