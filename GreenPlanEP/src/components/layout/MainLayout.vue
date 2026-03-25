<template>
    <main class="main-layout">

        <HomeBannerSwiper />


      <!-- 热门商品 -->
      <section class="featured-products">
        <h3>热门绿植种子</h3>

        <!-- 显示加载状态 -->
        <div v-if="store.loading" class="loading">
            正在加载数据...
        </div>
        
        <!-- 显示错误信息 -->
        <div v-else-if="store.error" class="error">
            {{ store.error }}
        </div>

        <!-- 商品网格 -->
        <div v-else class="product-grid">
          <div 
            class="product-card" 
            v-for="product in store.productList" 
            :key="product.id"
          >
            <div class="product-image">
              <img :src="product.imageUrl" :alt="product.name">
            </div>
            <div class="product-info">
              <h4>{{ product.name }}</h4>
              <p class="price">¥{{ product.price }}</p>
              
              <button class="buy-btn" @click="addToCart(product)">加入购物车</button>
            </div>
          </div>
        </div>

      </section>
  </main>

</template>

<script setup>
    import { reactive, onMounted } from 'vue'
    import { useProductStore } from '@/stores/productStore'
    import { useCart } from '@/stores/cartStore'
    import HomeBannerSwiper from '@/components/HomeBannerSwiper.vue'



    //====== 商品展示逻辑 ======
    const store = useProductStore()
    const cart = useCart()

    // 筛选条件
    const filters = reactive({
    page: 1,
    pageSize: 12,
    keyword: '',
    category: '',
    month: '',
    region: ''
    })

    // 组件挂载时获取数据
    onMounted(() => {
        store.fetchProducts(filters)
    })

    // ✅ 1. 封装请求逻辑
    const handleSearch = () => {
        filters.page = 1 // 搜索时重置到第一页
        store.fetchProducts(filters)
    }

    const resetFilters = () => {
        filters.keyword = ''
        filters.category = ''
        filters.month = ''
        filters.region = ''
        handleSearch()
    }

    const changePage = (newPage) => {
        filters.page = newPage
        store.fetchProducts(filters)
        window.scrollTo({ top: 0, behavior: 'smooth' })
    }


    const addToCart = (product) =>{
        cart.add(product)
    }

</script>

<style scoped>
    .main-layout {
        background-image: url('../../assets/images/HomeBackground.png');
        background-size: 1105px auto;
        background-repeat: no-repeat;
        background-position: center bottom;
        background-attachment: fixed;
        padding: 1rem;
    }
    
 /* 商品区域 */
    .featured-products {
        background: rgba(255, 255, 255, 0.2);
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
        background-image: url('../../assets/images/basket.png');
        background: rgb(255, 255, 255, 0.5);
        border: 1px solid #eee;
        border-radius: 10px;
        overflow: hidden;
        transition: box-shadow 0.3s;
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
    }

    .buy-btn:hover {
        background: #2d5016;
    }
</style>
