<template>
  <aside class="left-sidebar" :class="{ 'open': isOpen, 'narrow': isNarrow }">
    <!-- 控制按钮 (仅在非桌面端显示，用于关闭) -->
    <button class="close-btn" @click="$emit('toggle')" v-if="!isDesktop">✕</button>

    <div class="sidebar-content">
      <h3 class="title" v-if="!isNarrow || isOpen">🌱 商品分类</h3>
      
      <nav class="category-grid">
        <button
          v-for="(icon, key) in CATEGORY_ICONS" 
          :key="key" 
          :class="['category-item', { active: selectedCategory === key }]"
        @click="selectCategory(key)"
        >
          <span class="icon">{{ icon }}</span>
          <span class="text" v-if="!isNarrow">{{ CATEGORY_MAP[key]}}</span>
        </button>
      </nav>
    </div>
  
    <!-- 购物车区域 (始终显示) -->
    <div class="cart-section">
        <div class="cart-header">
          <h4>🛒 购物车</h4>
          <span class="cart-count" v-if="cart.count > 0">{{ cart.count }}</span>
        </div>
            
        <!-- 购物车为空 -->
        <div class="cart-empty" v-if="cart.items.length === 0">
          <p>购物车是空的</p>
          <router-link to="/products" class="go-shopping">去逛逛</router-link>
        </div>
            
        <!-- 购物车商品列表 -->
        <div class="cart-list" v-else>
          <div 
            v-for="item in cart.items" 
            :key="item.id" 
            class="cart-item"
          >
            <div class="item-image">
              <img :src="item.imageUrl || '/default-product.png'" :alt="item.name" />
            </div>
            <div class="item-info">
              <h5 class="item-name">
                {{ item.name }}
                <span class="item-price">¥{{ item.price }}</span>
              </h5>
              <p class="item-specs">
                {{ item.specs ||'50粒' }} 
                <span class="item-quantity">×{{ item.quantity }}</span>
              </p>
              
              <div class="cart-total">
                  <span class="total-price">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
                  <router-link to="/cart" class="checkout-btn">结算</router-link>
              </div>
            </div>
            <div>
              <button class="remove-btn" @click="removeFromCart(item.id)">×</button>
            </div>
          </div>
          <!-- 购物车底部操作 -->
          <div class="cart-footer" v-if="cart.items.length > 0">
              <button class="clear-cart-btn" @click="clearCart">清空购物车</button>
            </div>
        </div>
    </div>
  </aside>
</template>

<script setup>
  import { computed, inject, ref } from 'vue'
  import { useCart } from '@/stores/cartStore'
  import { CATEGORY_MAP, CATEGORY_ICONS } from '@/config/product'
  
  const store = inject('productStore')
  const props = defineProps({
    isOpen: Boolean,
    isNarrow: Boolean
  })

  // 响应式状态
  const isNarrow = ref(false)
  const selectedCategory = ref(null)

  const emit = defineEmits(['toggle'])

  // 选择分类并触发筛选
  const selectCategory = (categoryKey) => {
    // 如果点击的是当前选中的分类，则取消选择
    if (selectedCategory.value === categoryKey) {
      selectedCategory.value = null
    } else {
      selectedCategory.value = categoryKey
    }
    
    // 通知 store 进行筛选
    if (store) {
      store.fetchProducts({ 
        category: selectedCategory.value,
        page: 1  // 重置到第一页
      })
    }
  }

  // 简单判断是否桌面，用于显示关闭按钮
  const isDesktop = computed(() => window.innerWidth > 1024)

  // 购物车操作逻辑
  const cart = useCart()
  const getCategoryPath = (key) => {
    return `/products?category=${key}`
  }
  // 移除商品
  const removeFromCart = (productId) => {
    cart.remove(productId)
  }

  const clearCart = () => {
    if(confirm('确定要清空购物车吗？')) {
      cart.clear()
    }
  }
</script>

<style scoped>
  .left-sidebar {
    background: #fff;
    border-right: 1px solid #eee;
    transition: all 0.3s ease;
    overflow: hidden;

    scrollbar-width: none; /* Firefox */
    -ms-overflow-style: none; /* IE/Edge */
    
    /* 桌面端默认样式 */
    width: 285px;
    min-width: 50px;
    max-width: 285px;
    
    transform: translateX(0);
  }
  


  /*电脑端*/
  @media (max-width: 1248px) {
    close-btn { display: block; } 
    .left-sidebar {
      z-index: 100;
      box-shadow: 2px 0 10px rgba(0,0,0,0.1);
      width: 27%;
      min-width: 230px;
      max-width: 285px;
    }
    .sidebar-content { padding: 12px; }

    .category-item {
      display: flex; 
      flex-direction: column; 
      align-items: center;
      text-decoration: none; 
      color: rgba(255, 255, 255, 0.1);
      background: #f9f9f9; 
      border-radius: 12px;
      padding: 10px 10px; 
      transition: all 0.2s;
   }
   .category-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(60px, 1fr));
      gap: 10px;
      padding: 4px 4px;
    }

  }

  /* 平板端逻辑 */
  @media (max-width: 768px) {
    .left-sidebar {
      z-index: 100;
      box-shadow: 5px 0 10px rgba(0,0,0,0.1);
      width: 12%;
      min-width: 50px;
      max-width: 285px;
    }
    .category-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(40px, 1fr));
      gap: 10px;
      padding: 4px 8px;
    }
    .title { display: none; }
    .icon { font-size: 1rem;  }
    .text { display: none; }
    .sidebar-content { padding: 10px 0; }
    .category-item { padding: 2px ; }
    .cart-section { display: none }

  }

  /* 移动端隐藏逻辑 */
  @media (max-width: 480px) {
    .left-sidebar {
      display: none;
    }
  }

  .close-btn {
    position: absolute;
    top: 10px; right: 10px;
    background: none; border: none;
    font-size: 1.2rem; cursor: pointer;
    display: none;
  }
 
  .title { 
    text-align: center; 
    margin-bottom: 6px; 
    color: #333; 
    border-bottom: 2px solid #80ab64; 
    padding-bottom: 5px; 
  }

  .category-item {
      display: flex; 
      flex-direction: column; 
      align-items: center;
      text-decoration: none; 
      color: #333;
      background: #f9f9f9; 
      border-radius: 12px;
      transition: all 0.3s;
      background: transparent; /* 1. 改为透明，或者改成和页面大背景一样的颜色 (如 #fff) */
      border: none;            /* 2. 强制移除任何实线边框 */
      outline: none;           /* 3. 移除点击时的蓝色/黑色轮廓线 */
      box-shadow: none; 
   }
  .category-item:hover { background: #e8f5e9; transform: translateY(-2px); }
  .category-item.active { background: #80ab64; color: white; }
  .icon { font-size: 1.2rem; margin-bottom: 4px; }
  .text { font-size: 0.85rem; text-align: center; }

  /* --- 购物车区域样式 --- */
  .cart-section {
    background: #f8f9fa;
    border-radius: 12px;
    padding: 12px;
    margin-top: auto; /* 推到底部 */
    border: 1px solid #e9ecef;
  }

  .cart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
    padding-bottom: 5px;
    border-bottom: 1px dashed #ddd;
  }

  .cart-header h4 {
    margin: 0;
    font-size: 1rem;
    color: #333;
  }

  .cart-count {
    background: #e74c3c;
    color: white;
    font-size: 0.75rem;
    padding: 2px 8px;
    border-radius: 10px;
    min-width: 20px;
    text-align: center;
  }

  .cart-empty {
    text-align: center;
    padding: 20px 0;
    color: #999;
  }

  .go-shopping {
    display: inline-block;
    margin-top: 10px;
    color: #80ab64;
    text-decoration: none;
    font-size: 0.9rem;
  }

  .go-shopping:hover {
    text-decoration: underline;
  }

  .cart-list {
    display: flex;
    flex-direction: column;
    gap: 10px; 

    overflow-y: auto;
    scrollbar-width: none; /* Firefox */
    -ms-overflow-style: none; /* IE/Edge */
    max-height: 45vh; 
  }

  .cart-item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 10px;
    background: white;
    border-radius: 8px;
    border: 1px solid #eee;
    position: relative;
  }

  .item-image {
    flex-shrink: 0;
  }

  .item-image img {
    width: 45px;
    height: 45px;
    object-fit: cover;
    border-radius: 6px;
    border: 1px solid #f0f0f0;
  }

  .item-info {
    flex: 1;
    min-width: 0; /* 防止文字溢出 */
  }

  .item-name {
    margin: 0 0 4px 0;
    font-size: 0.85rem;
    color: #333;
    white-space: nowrap;
  }

  .item-specs {
    margin: 0 0 6px 0;
    font-size: 0.75rem;
    color: #999;
  }

  .item-bottom {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .item-price {
    color: #e74c3c;
    font-weight: bold;
    font-size: 0.9rem;
  }

  .item-quantity {
    color: #666;
    font-size: 0.8rem;
  }

  .remove-btn {
    position: absolute;
    top: 5px;
    right: 5px;
    width: 20px;
    height: 20px;
    border-radius: 50%;
    border: none;
    background: #f0f0f0;
    color: #999;
    font-size: 1rem;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;
  }

  .remove-btn:hover {
    background: #e74c3c;
    color: white;
  }

  .cart-footer {
    margin-top: 2px;
    padding-top: 12px;
    border-top: 1px dashed #ddd;
  }

  .cart-total {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
    font-size: 0.9rem;
  }

  .total-price {
    color: #e74c3c;
    font-weight: bold;
    font-size: 1rem;
  }

  .checkout-btn {
    display: block;
    width: 60%;
    padding: 5px;
    background: #80ab64;
    color: white;
    text-align: center;
    text-decoration: none;
    border-radius: 6px;
    font-size: 0.7rem;
    transition: background-color 0.2s;
  }

  .checkout-btn:hover {
    background: #659146;
  }



</style>