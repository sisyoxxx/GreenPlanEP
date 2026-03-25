<template>
  <div class="orders-layout">
    
    <!-- ================= 左侧导航与列表区 ================= -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <h2>📦 我的订单</h2>
        <span class="order-count">共 {{ orders.length }} 单</span>
      </div>

      <!-- 核心功能导航 (新增部分) -->
      <nav class="function-nav">
        <button 
          class="nav-item" 
          :class="{ active: leftView === 'orders' }"
          @click="leftView = 'orders'"
        >
          <span class="icon">📋</span> 订单列表
        </button>
        <button 
          class="nav-item" 
          :class="{ active: leftView === 'service' }"
          @click="leftView = 'service'"
        >
          <span class="icon">💬</span> 订单客服
          <span class="badge" v-if="unreadService > 0">{{ unreadService }}</span>
        </button>
        <button 
          class="nav-item" 
          :class="{ active: leftView === 'notify' }"
          @click="leftView = 'notify'"
        >
          <span class="icon">🔔</span> 物流助手
          <span class="badge" v-if="unreadNotify > 0">{{ unreadNotify }}</span>
        </button>
      </nav>

      <!-- 订单筛选与列表 (仅在 "订单列表" 视图显示) -->
      <div v-if="leftView === 'orders'" class="order-list-container">
        <div class="filter-bar">
          <select v-model="statusFilter">
            <option value="all">全部订单</option>
            <option value="pending">待发货</option>
            <option value="shipping">配送中</option>
            <option value="completed">已完成</option>
            <option value="reviewed">已评论</option> <!-- 新增筛选 -->
          </select>
        </div>

        <div class="order-list">
          <div 
            v-for="order in filteredOrders" 
            :key="order.id"
            class="order-card"
            :class="{ active: selectedOrderId === order.id }"
            @click="selectOrder(order.id)"
          >
            <div class="card-top">
              <span class="status-tag" :class="order.status">{{ getStatusText(order.status) }}</span>
              <span class="date">{{ order.date }}</span>
            </div>
            <div class="card-main">
              <div class="info">
                <div class="id">#{{ order.orderNo }}</div>
                <div class="total">¥{{ order.total }}</div>
              </div>
              <!-- 简单的商品缩略图展示 -->
              <div class="thumb" :style="{ backgroundImage: `url(${order.thumb})` }"></div>
            </div>
            <!-- 如果已完成但未评论，显示提示点 -->
            <div v-if="order.status === 'completed' && !order.reviewed" class="review-dot" title="快去评价吧"></div>
          </div>
        </div>
      </div>

      <!-- 客服消息列表 (仅在 "订单客服" 视图显示) -->
      <div v-if="leftView === 'service'" class="message-list">
        <div class="empty-tip" v-if="false">暂无消息</div>
        <div v-for="chat in serviceChats" :key="chat.id" class="msg-item" @click="openChat(chat)">
          <div class="avatar">👩‍💼</div>
          <div class="content">
            <div class="name">{{ chat.merchant }}</div>
            <div class="preview">{{ chat.lastMsg }}</div>
          </div>
          <span class="time">{{ chat.time }}</span>
        </div>
      </div>

      <!-- 物流通知列表 (仅在 "物流助手" 视图显示) -->
      <div v-if="leftView === 'notify'" class="notify-list">
        <div v-for="note in notifications" :key="note.id" class="note-item" :class="{ unread: note.unread }">
          <div class="icon">{{ note.type === 'ship' ? '🚚' : '🏠' }}</div>
          <div class="text">
            <p class="title">{{ note.title }}</p>
            <p class="desc">{{ note.desc }}</p>
            <span class="time">{{ note.time }}</span>
          </div>
        </div>
      </div>
    </aside>

    <!-- ================= 右侧详情区 ================= -->
    <main class="main-content">
      
      <!-- 场景 1: 未选择订单时的占位图 -->
      <div v-if="!selectedOrder && leftView === 'orders'" class="empty-state">
        <div class="illustration">📦</div>
        <h3>请选择一个订单查看详情</h3>
        <p>左侧列表展示了你所有的购买记录</p>
      </div>

      <!-- 场景 2: 订单详情 (核心功能) -->
      <div v-if="selectedOrder && leftView === 'orders'" class="detail-panel">
        <header class="detail-header">
          <h2>订单详情 #{{ selectedOrder.orderNo }}</h2>
          <div class="actions">
            <button class="btn-outline">联系客服</button>
            <button v-if="selectedOrder.status === 'completed'" class="btn-primary" @click="openReviewModal">
              {{ selectedOrder.reviewed ? '查看评价' : '写评价' }}
            </button>
          </div>
        </header>

        <!-- 商品信息 -->
        <section class="product-section">
          <div class="product-card">
            <img :src="selectedOrder.thumb" alt="商品" class="product-img" />
            <div class="product-info">
              <h3>{{ selectedOrder.productName }}</h3>
              <p class="sku">规格：{{ selectedOrder.sku }}</p>
              <div class="price-row">
                <span class="price">¥{{ selectedOrder.total }}</span>
                <span class="count">x{{ selectedOrder.count }}</span>
              </div>
            </div>
          </div>
        </section>

        <!-- 物流进度条 (可视化增强) -->
        <section class="logistics-section">
          <h3>🚚 物流进度</h3>
          <div class="timeline">
            <div class="step" v-for="(step, index) in logisticsSteps" :key="index" :class="{ active: step.active, last: index === logisticsSteps.length - 1 }">
              <div class="dot"></div>
              <div class="content">
                <p class="status">{{ step.text }}</p>
                <p class="time">{{ step.time }}</p>
              </div>
            </div>
          </div>
        </section>

        <!-- 收货地址 -->
        <section class="address-section">
          <h3>📍 收货信息</h3>
          <p><strong>张三</strong> 138****1234</p>
          <p>上海市浦东新区某某路某某号 绿植种子平台仓库</p>
        </section>
      </div>

      <!-- 场景 3: 客服聊天窗口 -->
      <div v-if="leftView === 'service'" class="chat-panel">
        <div class="chat-header">
          <h3>💬 官方客服 / 商家咨询</h3>
        </div>
        <div class="chat-body">
          <div class="system-notice">您好，有什么可以帮您？关于订单问题请直接发送订单号。</div>
          <!-- 模拟消息 -->
          <div class="msg received">亲，您的种子已经发芽了吗？🌱</div>
          <div class="msg sent">还没有呢，大概多久能到？</div>
        </div>
        <div class="chat-input-area">
          <input type="text" placeholder="输入消息..." />
          <button>发送</button>
        </div>
      </div>

      <!-- 场景 4: 物流助手详情 -->
      <div v-if="leftView === 'notify'" class="notify-panel">
        <h3>🔔 订单小助手通知</h3>
        <p class="sub-text">这里记录了您所有订单的关键节点通知</p>
        <div class="notify-full-list">
           <!-- 复用左侧的数据，但在右侧展示更详细的内容 -->
           <div v-for="note in notifications" :key="note.id" class="full-note">
             <div class="icon-lg">{{ note.type === 'ship' ? '🚚' : '🏠' }}</div>
             <div>
               <h4>{{ note.title }}</h4>
               <p>{{ note.desc }}</p>
               <small>{{ note.time }}</small>
             </div>
           </div>
        </div>
      </div>

    </main>

    <!-- 评价弹窗 (简单模拟) -->
    <div v-if="showReviewModal" class="modal-overlay" @click.self="showReviewModal = false">
      <div class="modal">
        <h3>评价商品：{{ selectedOrder?.productName }}</h3>
        <textarea placeholder="分享一下您的种植体验吧..." rows="4"></textarea>
        <div class="stars">⭐⭐⭐⭐⭐</div>
        <div class="modal-footer">
          <button @click="showReviewModal = false">取消</button>
          <button class="primary" @click="submitReview">提交评价</button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'

// ================= 状态定义 =================
const leftView = ref('orders') // 左侧当前视图：orders(订单), service(客服), notify(通知)
const statusFilter = ref('all')
const selectedOrderId = ref(null)
const showReviewModal = ref(false)

// 模拟未读数
const unreadService = ref(1)
const unreadNotify = ref(2)

// ================= 模拟数据 =================

// 1. 订单数据 (增加了 reviewed 字段和 thumb 图片)
const orders = ref([
  { 
    id: 1, 
    orderNo: '202610150001', 
    status: 'pending', 
    date: '2026-10-15', 
    total: 18, 
    productName: '有机番茄种子',
    sku: '樱桃红 / 50粒',
    count: 1,
    thumb: 'https://img0.baidu.com/it/u=3245465403,3072413569&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=664',
    reviewed: false 
  },
  { 
    id: 2, 
    orderNo: '202610140002', 
    status: 'shipping', 
    date: '2026-10-14', 
    total: 17, 
    productName: '薄荷盆栽',
    sku: '留兰香 / 9cm盆',
    count: 1,
    thumb: 'https://img1.baidu.com/it/u=3223651107,638915656&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=1067',
    reviewed: false 
  },
  { 
    id: 3, 
    orderNo: '202610130003', 
    status: 'completed', 
    date: '2026-10-13', 
    total: 3, 
    productName: '育苗土营养土',
    sku: '通用型 / 5L',
    count: 1,
    thumb: 'https://t13.baidu.com/it/u=2334313952,938119821&fm=224&app=112&f=JPEG?w=500&h=500',
    reviewed: true // 已评论
  }
])

// 2. 客服消息
const serviceChats = ref([
  { id: 1, merchant: '绿植官方旗舰店', lastMsg: '亲，种子收到后建议先催芽...', time: '10:30' },
  { id: 2, merchant: '园艺工具店', lastMsg: '剪刀缺货了，可以换铲子吗？', time: '昨天' }
])

// 3. 物流通知
const notifications = ref([
  { id: 1, type: 'ship', title: '您的订单已发货', desc: '番茄种子已由顺丰速运承运', time: '10分钟前', unread: true },
  { id: 2, type: 'deliver', title: '快递员正在派送', desc: '请保持电话畅通', time: '2小时前', unread: true },
  { id: 3, type: 'ship', title: '薄荷盆栽已发出', desc: '注意查收哦', time: '昨天', unread: false }
])

// ================= 计算属性 =================

// 筛选订单逻辑 (包含“已评论”筛选)
const filteredOrders = computed(() => {
  return orders.value.filter(order => {
    if (statusFilter.value === 'all') return true
    if (statusFilter.value === 'reviewed') return order.status === 'completed' && order.reviewed
    return order.status === statusFilter.value
  })
})

// 获取当前选中的订单对象
const selectedOrder = computed(() => {
  return orders.value.find(o => o.id === selectedOrderId.value)
})

// 模拟物流步骤 (根据状态动态变化)
const logisticsSteps = computed(() => {
  if (!selectedOrder.value) return []
  const baseSteps = [
    { text: '订单已提交', time: selectedOrder.value.date + ' 10:00', active: true },
    { text: '商家已接单', time: selectedOrder.value.date + ' 11:00', active: true }
  ]
  
  if (selectedOrder.value.status === 'pending') {
    baseSteps.push({ text: '等待发货...', time: '预计今天内', active: false })
  } else if (selectedOrder.value.status === 'shipping') {
    baseSteps.push({ text: '已打包发货', time: '2026-10-14 15:00', active: true })
    baseSteps.push({ text: '运输中', time: '2026-10-15 08:00', active: true })
    baseSteps.push({ text: '正在派送', time: '今日 09:00', active: true })
  } else {
    baseSteps.push({ text: '已签收', time: '2026-10-13 14:00', active: true })
    baseSteps.push({ text: '交易完成', time: '2026-10-13 14:05', active: true })
  }
  return baseSteps
})

// ================= 方法 =================

const selectOrder = (id) => {
  selectedOrderId.value = id
}

const getStatusText = (status) => {
  const map = {
    pending: '待发货',
    shipping: '配送中',
    completed: '已完成',
    reviewed: '已评论'
  }
  return map[status] || status
}

const openReviewModal = () => {
  showReviewModal.value = true
}

const submitReview = () => {
  alert('评价提交成功！感谢您的反馈 🌟')
  showReviewModal.value = false
  // 实际逻辑中这里会更新订单状态为 reviewed
  if(selectedOrder.value) selectedOrder.value.reviewed = true
}

const openChat = (chat) => {
  // 这里可以跳转到具体的聊天详情页，或者在右侧打开聊天窗口
  alert(`正在打开与 ${chat.merchant} 的对话...`)
}
</script>

<style scoped>
/* 布局容器 */
.orders-layout {
  display: flex;
  height: calc(100vh - 60px); /* 减去顶部导航栏高度 */
  background: #f5f7fa;
  font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
}

/* ================= 左侧边栏 ================= */
.sidebar {
  width: 280px;
  background: white;
  border-right: 1px solid #e1e4e8;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.sidebar-header {
  padding: 1.5rem;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sidebar-header h2 {
  margin: 0;
  font-size: 1.25rem;
  color: #2c3e50;
}

.order-count {
  font-size: 0.8rem;
  color: #999;
  background: #f0f0f0;
  padding: 2px 8px;
  border-radius: 10px;
}

/* 功能导航 (新增) */
.function-nav {
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  background: #fafafa;
  border-bottom: 1px solid #e1e4e8;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 0.75rem 1rem;
  background: white;
  border: 1px solid #e1e4e8;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.9rem;
  color: #555;
  transition: all 0.2s;
  position: relative;
}

.nav-item:hover {
  background: #f0f7ff;
  border-color: #b3d8ff;
}

.nav-item.active {
  background: #e6f7ff;
  border-color: #1890ff;
  color: #1890ff;
  font-weight: bold;
}

.nav-item .icon {
  margin-right: 8px;
  font-size: 1.1rem;
}

.badge {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  background: #ff4d4f;
  color: white;
  font-size: 0.7rem;
  padding: 1px 6px;
  border-radius: 10px;
  min-width: 16px;
  text-align: center;
}

/* 订单列表区域 */
.order-list-container {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.filter-bar {
  padding: 1rem;
  background: white;
  border-bottom: 1px solid #f0f0f0;
}

.filter-bar select {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: #f9f9f9;
  font-size: 0.9rem;
  outline: none;
}

.order-list {
  padding: 0.5rem;
}

.order-card {
  background: white;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 1rem;
  margin-bottom: 0.75rem;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.order-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  transform: translateY(-2px);
}

.order-card.active {
  border-color: #1890ff;
  background: #e6f7ff;
}

.card-top {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
}

.status-tag {
  font-size: 0.75rem;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: bold;
}
.status-tag.pending { background: #fff7e6; color: #fa8c16; }
.status-tag.shipping { background: #e6f7ff; color: #1890ff; }
.status-tag.completed { background: #f6ffed; color: #52c41a; }

.date {
  font-size: 0.8rem;
  color: #999;
}

.card-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info .id {
  font-size: 0.85rem;
  color: #666;
  margin-bottom: 4px;
}

.info .total {
  font-weight: bold;
  color: #f5222d;
  font-size: 1rem;
}

.thumb {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  background-size: cover;
  background-position: center;
  border: 1px solid #eee;
}

.review-dot {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 8px;
  height: 8px;
  background: #fa8c16;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.2); }
  100% { opacity: 1; transform: scale(1); }
}

/* 消息与通知列表样式 */
.message-list, .notify-list {
  flex: 1;
  overflow-y: auto;
  padding: 0.5rem;
}

.msg-item, .note-item {
  display: flex;
  align-items: center;
  padding: 0.75rem;
  background: white;
  border-radius: 6px;
  margin-bottom: 0.5rem;
  cursor: pointer;
  border: 1px solid transparent;
}

.msg-item:hover, .note-item:hover {
  background: #f9f9f9;
}

.note-item.unread {
  background: #e6f7ff;
  border-left: 3px solid #1890ff;
}

.avatar, .icon {
  font-size: 1.5rem;
  margin-right: 10px;
  flex-shrink: 0;
}

.content {
  flex: 1;
  overflow: hidden;
}

.name, .title {
  font-weight: bold;
  font-size: 0.9rem;
  color: #333;
  margin: 0 0 4px 0;
}

.preview, .desc {
  font-size: 0.8rem;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin: 0;
}

.time {
  font-size: 0.75rem;
  color: #999;
  flex-shrink: 0;
  margin-left: 8px;
}

/* ================= 右侧内容区 ================= */
.main-content {
  flex: 1;
  padding: 2rem;
  overflow-y: auto;
  background: #f5f7fa;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #999;
}

.illustration {
  font-size: 4rem;
  margin-bottom: 1rem;
  opacity: 0.5;
}

/* 详情面板 */
.detail-panel {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
  max-width: 800px;
  margin: 0 auto;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
  padding-bottom: 1.5rem;
  margin-bottom: 1.5rem;
}

.detail-header h2 {
  margin: 0;
  font-size: 1.5rem;
  color: #333;
}

.actions {
  display: flex;
  gap: 1rem;
}

.btn-outline {
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  color: #666;
}

.btn-primary {
  padding: 0.5rem 1rem;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.btn-primary:hover {
  background: #40a9ff;
}

/* 商品卡片 */
.product-section {
  margin-bottom: 2rem;
}

.product-card {
  display: flex;
  gap: 1.5rem;
  padding: 1rem;
  background: #fafafa;
  border-radius: 8px;
  border: 1px solid #eee;
}

.product-img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 6px;
}

.product-info h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1.1rem;
}

.sku {
  color: #999;
  font-size: 0.9rem;
  margin-bottom: 1rem;
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  color: #f5222d;
  font-size: 1.2rem;
  font-weight: bold;
}

/* 物流时间轴 */
.logistics-section {
  margin-bottom: 2rem;
}

.timeline {
  position: relative;
  padding-left: 20px;
  margin-top: 1rem;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 6px;
  top: 5px;
  bottom: 5px;
  width: 2px;
  background: #eee;
}

.step {
  position: relative;
  margin-bottom: 1.5rem;
  display: flex;
  gap: 1rem;
}

.step:last-child {
  margin-bottom: 0;
}

.dot {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: #ddd;
  border: 2px solid white;
  z-index: 1;
  flex-shrink: 0;
  margin-top: 4px;
}

.step.active .dot {
  background: #52c41a;
  box-shadow: 0 0 0 2px #b7eb8f;
}

.step .content .status {
  font-weight: bold;
  color: #333;
  margin: 0 0 4px 0;
}

.step .content .time {
  font-size: 0.85rem;
  color: #999;
  margin: 0;
}

.address-section {
  background: #fafafa;
  padding: 1rem;
  border-radius: 8px;
  font-size: 0.9rem;
  color: #666;
  line-height: 1.6;
}

.address-section h3 {
  margin-top: 0;
  font-size: 1rem;
  color: #333;
}

/* 客服与通知面板 */
.chat-panel, .notify-panel {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  height: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.chat-body {
  flex: 1;
  background: #f9f9f9;
  margin: 1rem 0;
  border-radius: 8px;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.system-notice {
  align-self: center;
  background: #fffbe6;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.8rem;
  color: #fa8c16;
  border: 1px solid #ffe58f;
}

.msg {
  max-width: 70%;
  padding: 0.75rem;
  border-radius: 8px;
  font-size: 0.9rem;
  line-height: 1.4;
}

.msg.received {
  background: white;
  align-self: flex-start;
  border: 1px solid #eee;
}

.msg.sent {
  background: #1890ff;
  color: white;
  align-self: flex-end;
}

.chat-input-area {
  display: flex;
  gap: 0.5rem;
}

.chat-input-area input {
  flex: 1;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 6px;
}

.chat-input-area button {
  padding: 0.5rem 1.5rem;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.notify-full-list {
  margin-top: 1rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.full-note {
  display: flex;
  gap: 1rem;
  padding: 1rem;
  border: 1px solid #eee;
  border-radius: 8px;
}

.icon-lg {
  font-size: 2rem;
}

/* 模态框 */
.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  width: 400px;
}

.modal textarea {
  width: 100%;
  padding: 0.5rem;
  margin: 1rem 0;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-family: inherit;
  box-sizing: border-box;
}

.stars {
  color: #faad14;
  font-size: 1.5rem;
  margin-bottom: 1rem;
  cursor: pointer;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}

.modal-footer button {
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  background: white;
  border-radius: 6px;
  cursor: pointer;
}

.modal-footer button.primary {
  background: #1890ff;
  color: white;
  border: none;
}
</style>