import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/products',
    name: 'Products',
    component: () => import('@/views/Products.vue'),
    meta: { title: '商品' }
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: () => import('@/views/ProductDetail.vue'),
    meta: { title: '商品详情' }
  },
  {
    path: '/community',
    name: 'Community',
    component: () => import('@/views/Community.vue'),
    meta: { title: '种植社区' }
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('@/views/Orders.vue'),
    meta: { title: '订单管理' }
  },
  {
    path: '/user',
    name: 'User',
    component: () => import('@/views/User.vue'),
    meta: { title: '用户中心', requiresAuth: true }
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('@/views/Cart.vue'),
    meta: { title: '购物车' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/planttutorial',
    name: 'PlantTutorial',
    component: () => import('@/views/PlantTutorial.vue'),
    meta: { title: '种植教程' }
  },
  {
    path: '/productcatalog',
    name: 'ProductCatalog',
    component: () => import('@/views/ProductCatalog.vue'),
    meta: { title: '商品区' }
  },
  {
    path: '/plantingdiary',
    name: 'PlantingDiary',
    component:() => import('@/views/PlantingDiary.vue'),
    meta: { title: '我的花园' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// 路由守卫
router.beforeEach((to, from) => {
  // 1. 设置标题
  const title = to.meta?.title;
  document.title = title ? `${title} - 绿植种子平台` : '绿植种子平台';

  // 2. 读取正确的 Key
  // 对应 user.js 中的 'greenPlan_token'
  const token = localStorage.getItem('greenPlan_token'); 
  const storedUser = localStorage.getItem('greenPlan_user'); 
  
  // 判断是否已登录：有 token 或者 有用户信息 都算登录
  const isAuth = !!(token || storedUser);

  // 3. 检查路由是否需要认证
  const requiresAuth = to.matched.some(record => record.meta?.requiresAuth);

  // 4. 逻辑判断
  if (requiresAuth && !isAuth) {
    // 未登录 -> 重定向到登录页，并带上原路径
    return {
      path: '/login',
      query: { redirect: to.fullPath }
    };
  }

  // 已登录 或 不需要认证 -> 放行
  return true;
});

export default router