<template>
    <div class="user-page">
      <div class="user-container">
        <!-- 侧边栏 -->
        <aside class="sidebar">
          <div class="user-profile">
            <div class="avatar">
              <img :src="userInfo.avatar || '/default-avatar.png'" alt="头像">
            </div>
            <h3>{{ userInfo.nickname || userInfo.username }}</h3>
            <p class="level">🌱 种植等级: {{ userInfo.level }}</p>
          </div>
          
          <nav class="user-menu">
            <a href="#" :class="{ active: activeMenu === 'profile' }" @click.prevent="activeMenu = 'profile'">
              👤 个人信息
            </a>
            <a href="#" :class="{ active: activeMenu === 'orders' }" @click.prevent="activeMenu = 'orders'">
              📦 我的订单
            </a>
            <a href="#" :class="{ active: activeMenu === 'records' }" @click.prevent="activeMenu = 'records'">
              📝 种植记录
            </a>
            <a href="#" :class="{ active: activeMenu === 'favorites' }" @click.prevent="activeMenu = 'favorites'">
              ❤️ 我的收藏
            </a>
            <a href="#" :class="{ active: activeMenu === 'address' }" @click.prevent="activeMenu = 'address'">
              📍 收货地址
            </a>
            <a href="#" :class="{ active: activeMenu === 'settings' }" @click.prevent="activeMenu = 'settings'">
              ⚙️ 账户设置
            </a>
          </nav>
        </aside>
  
        <!-- 主内容区 -->
        <main class="main-content">
          <!-- 个人信息 -->
          <div v-if="activeMenu === 'profile'" class="content-section">
            <h2>个人信息</h2>
            <form @submit.prevent="updateProfile" class="profile-form">
              <div class="form-group">
                <label>用户名</label>
                <input type="text" v-model="userInfo.username" disabled>
              </div>
              <div class="form-group">
                <label>昵称</label>
                <input type="text" v-model="userInfo.nickname">
              </div>
              <div class="form-group">
                <label>手机号</label>
                <input type="tel" v-model="userInfo.phone">
              </div>
              <div class="form-group">
                <label>邮箱</label>
                <input type="email" v-model="userInfo.email">
              </div>
              <div class="form-group">
                <label>头像</label>
                <input type="file" @change="uploadAvatar" accept="image/*">
              </div>
              <button type="submit" class="submit-btn">保存修改</button>
            </form>
          </div>
  
          <!-- 我的订单 -->
          <div v-if="activeMenu === 'orders'" class="content-section">
            <h2>我的订单</h2>
            <div class="order-tabs">
              <button :class="{ active: orderTab === 'all' }" @click="orderTab = 'all'">全部</button>
              <button :class="{ active: orderTab === 'pending' }" @click="orderTab = 'pending'">待付款</button>
              <button :class="{ active: orderTab === 'shipping' }" @click="orderTab = 'shipping'">待发货</button>
              <button :class="{ active: orderTab === 'received' }" @click="orderTab = 'received'">待收货</button>
              <button :class="{ active: orderTab === 'completed' }" @click="orderTab = 'completed'">已完成</button>
            </div>
            
            <div class="order-list">
              <div class="order-card" v-for="order in orderList" :key="order.id">
                <div class="order-header">
                  <span class="order-no">订单号: {{ order.orderNo }}</span>
                  <span class="order-status" :class="order.status">{{ order.statusText }}</span>
                </div>
                <div class="order-items">
                  <div class="order-item" v-for="item in order.items" :key="item.id">
                    <img :src="item.image" :alt="item.name">
                    <div class="item-info">
                      <p class="name">{{ item.name }}</p>
                      <p class="specs">{{ item.specs }}</p>
                    </div>
                    <div class="item-quantity">×{{ item.quantity }}</div>
                    <div class="item-price">¥{{ item.price }}</div>
                  </div>
                </div>
                <div class="order-footer">
                  <span class="order-total">合计: ¥{{ order.total }}</span>
                  <div class="order-actions">
                    <button v-if="order.status === 'pending'" class="pay-btn">去付款</button>
                    <button v-if="order.status === 'received'" class="confirm-btn">确认收货</button>
                    <button v-if="order.status === 'completed'" class="review-btn">评价</button>
                    <button class="detail-btn">查看详情</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
  
          <!-- 种植记录 -->
          <div v-if="activeMenu === 'records'" class="content-section">
            <div class="section-header">
              <h2>种植记录</h2>
              <button class="add-btn" @click="showAddRecord = true">+ 添加记录</button>
            </div>
            
            <div class="record-grid">
              <div class="record-card" v-for="record in plantRecords" :key="record.id">
                <div class="record-image">
                  <img :src="record.image" :alt="record.plantName">
                </div>
                <div class="record-info">
                  <h4>{{ record.plantName }}</h4>
                  <p class="date">📅 {{ record.plantDate }}</p>
                  <p class="days">🌱 第 {{ record.growthDays }} 天</p>
                  <p class="status">状态: {{ record.status }}</p>
                </div>
                <div class="record-actions">
                  <button @click="editRecord(record)">编辑</button>
                  <button @click="deleteRecord(record.id)">删除</button>
                </div>
              </div>
            </div>
          </div>
  
          <!-- 收货地址 -->
          <div v-if="activeMenu === 'address'" class="content-section">
            <div class="section-header">
              <h2>收货地址</h2>
              <button class="add-btn" @click="showAddAddress = true">+ 新增地址</button>
            </div>
            
            <div class="address-list">
              <div class="address-card" :class="{ default: addr.isDefault }" v-for="addr in addressList" :key="addr.id">
                <div class="address-info">
                  <p class="name">{{ addr.receiver }} {{ addr.phone }}</p>
                  <p class="detail">{{ addr.province }}{{ addr.city }}{{ addr.district }} {{ addr.detail }}</p>
                </div>
                <div class="address-actions">
                  <span v-if="addr.isDefault" class="default-tag">默认地址</span>
                  <button @click="setDefaultAddress(addr.id)">设为默认</button>
                  <button @click="editAddress(addr)">编辑</button>
                  <button @click="deleteAddress(addr.id)">删除</button>
                </div>
              </div>
            </div>
          </div>
        </main>
      </div>
  
      <!-- 添加记录弹窗 -->
      <div class="modal" v-if="showAddRecord">
        <div class="modal-content">
          <h3>添加种植记录</h3>
          <form @submit.prevent="submitRecord">
            <div class="form-group">
              <label>植物名称</label>
              <input type="text" v-model="newRecord.plantName" required>
            </div>
            <div class="form-group">
              <label>种植日期</label>
              <input type="date" v-model="newRecord.plantDate" required>
            </div>
            <div class="form-group">
              <label>记录图片</label>
              <input type="file" @change="uploadRecordImage" accept="image/*">
            </div>
            <div class="form-group">
              <label>备注</label>
              <textarea v-model="newRecord.remark" rows="4"></textarea>
            </div>
            <div class="modal-actions">
              <button type="button" @click="showAddRecord = false">取消</button>
              <button type="submit">保存</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive, onMounted } from 'vue'
  import { useUserStore } from '@/api/user'
  
  const userStore = useUserStore()
  
  // 当前菜单
  const activeMenu = ref('profile')
  const orderTab = ref('all')
  
  // 用户信息
  const userInfo = reactive({
    username: '',
    nickname: '',
    phone: '',
    email: '',
    avatar: '',
    level: 1
  })
  
  // 订单列表
  const orderList = ref([])
  
  // 种植记录
  const plantRecords = ref([])
  
  // 地址列表
  const addressList = ref([])
  
  // 弹窗控制
  const showAddRecord = ref(false)
  const showAddAddress = ref(false)
  
  // 新记录
  const newRecord = reactive({
    plantName: '',
    plantDate: '',
    image: '',
    remark: ''
  })
  
  // 加载用户信息
  const loadUserInfo = async () => {
    try {
      const res = await userStore.getUserInfo()
      Object.assign(userInfo, res.data)
    } catch (error) {
      console.error('加载用户信息失败:', error)
    }
  }
  
  // 加载订单
  const loadOrders = async () => {
    try {
      const res = await userStore.getOrders({ status: orderTab.value })
      orderList.value = res.data
    } catch (error) {
      console.error('加载订单失败:', error)
    }
  }
  
  // 加载种植记录
  const loadRecords = async () => {
    try {
      const res = await userStore.getPlantRecords()
      plantRecords.value = res.data
    } catch (error) {
      console.error('加载记录失败:', error)
    }
  }
  
  // 加载地址
  const loadAddresses = async () => {
    try {
      const res = await userStore.getAddresses()
      addressList.value = res.data
    } catch (error) {
      console.error('加载地址失败:', error)
    }
  }
  
  // 更新个人信息
  const updateProfile = async () => {
    try {
      await userStore.updateUserInfo(userInfo)
      alert('保存成功!')
    } catch (error) {
      alert('保存失败:' + error.message)
    }
  }
  
  // 上传头像
  const uploadAvatar = async (event) => {
    const file = event.target.files[0]
    // 实现上传逻辑
  }
  
  // 提交种植记录
  const submitRecord = async () => {
    try {
      await userStore.addPlantRecord(newRecord)
      showAddRecord.value = false
      loadRecords()
    } catch (error) {
      alert('添加失败:' + error.message)
    }
  }
  
  // 删除记录
  const deleteRecord = async (id) => {
    if (confirm('确定删除这条记录吗?')) {
      await userStore.deletePlantRecord(id)
      loadRecords()
    }
  }
  
  // 设置默认地址
  const setDefaultAddress = async (id) => {
    await userStore.setDefaultAddress(id)
    loadAddresses()
  }
  
  // 删除地址
  const deleteAddress = async (id) => {
    if (confirm('确定删除这个地址吗?')) {
      await userStore.deleteAddress(id)
      loadAddresses()
    }
  }
  
  onMounted(() => {
    loadUserInfo()
    loadOrders()
    loadRecords()
    loadAddresses()
  })
  </script>
  
  <style scoped>
  .user-page {
    min-height: 100vh;
    background: #f5f5f5;
    padding: 2rem 5%;
  }
  
  .user-container {
    display: flex;
    gap: 2rem;
    max-width: 1200px;
    margin: 0 auto;
  }
  
  .sidebar {
    width: 250px;
    background: white;
    border-radius: 10px;
    padding: 1.5rem;
    box-shadow: 0 2px 10px rgba(0,0,0,0.08);
  }
  
  .user-profile {
    text-align: center;
    padding-bottom: 1.5rem;
    border-bottom: 1px solid #eee;
    margin-bottom: 1.5rem;
  }
  
  .avatar {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    overflow: hidden;
    margin: 0 auto 1rem;
    border: 3px solid #4a7c23;
  }
  
  .avatar img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .user-profile h3 {
    margin-bottom: 0.5rem;
    color: #333;
  }
  
  .level {
    color: #4a7c23;
    font-size: 0.9rem;
  }
  
  .user-menu a {
    display: block;
    padding: 0.8rem 1rem;
    color: #666;
    text-decoration: none;
    border-radius: 5px;
    margin-bottom: 0.5rem;
    transition: background 0.3s;
  }
  
  .user-menu a:hover,
  .user-menu a.active {
    background: #e8f5e9;
    color: #4a7c23;
  }
  
  .main-content {
    flex: 1;
    background: white;
    border-radius: 10px;
    padding: 2rem;
    box-shadow: 0 2px 10px rgba(0,0,0,0.08);
  }
  
  .content-section h2 {
    margin-bottom: 1.5rem;
    color: #333;
    padding-bottom: 1rem;
    border-bottom: 2px solid #4a7c23;
  }
  
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;
  }
  
  .add-btn {
    padding: 0.6rem 1.2rem;
    background: #4a7c23;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
  }
  
  .profile-form {
    max-width: 500px;
  }
  
  .form-group {
    margin-bottom: 1.5rem;
  }
  
  .form-group label {
    display: block;
    margin-bottom: 0.5rem;
    color: #666;
  }
  
  .form-group input,
  .form-group textarea {
    width: 100%;
    padding: 0.8rem;
    border: 1px solid #ddd;
    border-radius: 5px;
    font-size: 1rem;
  }
  
  .submit-btn {
    padding: 0.8rem 2rem;
    background: #4a7c23;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 1rem;
  }
  
  .order-tabs {
    display: flex;
    gap: 0.5rem;
    margin-bottom: 1.5rem;
    border-bottom: 1px solid #eee;
    padding-bottom: 1rem;
  }
  
  .order-tabs button {
    padding: 0.5rem 1.5rem;
    border: none;
    background: #f5f5f5;
    border-radius: 5px;
    cursor: pointer;
  }
  
  .order-tabs button.active {
    background: #4a7c23;
    color: white;
  }
  
  .order-card {
    border: 1px solid #eee;
    border-radius: 8px;
    margin-bottom: 1.5rem;
    overflow: hidden;
  }
  
  .order-header {
    display: flex;
    justify-content: space-between;
    padding: 1rem;
    background: #f9f9f9;
    border-bottom: 1px solid #eee;
  }
  
  .order-status {
    padding: 0.3rem 0.8rem;
    border-radius: 15px;
    font-size: 0.85rem;
  }
  
  .order-status.pending { background: #fff3cd; color: #856404; }
  .order-status.shipping { background: #d1ecf1; color: #0c5460; }
  .order-status.received { background: #d4edda; color: #155724; }
  .order-status.completed { background: #e2e3e5; color: #383d41; }
  
  .order-items {
    padding: 1rem;
  }
  
  .order-item {
    display: flex;
    align-items: center;
    gap: 1rem;
    padding: 0.8rem 0;
    border-bottom: 1px solid #eee;
  }
  
  .order-item:last-child {
    border-bottom: none;
  }
  
  .order-item img {
    width: 60px;
    height: 60px;
    object-fit: cover;
    border-radius: 5px;
  }
  
  .item-info {
    flex: 1;
  }
  
  .item-quantity,
  .item-price {
    color: #666;
  }
  
  .order-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem;
    background: #f9f9f9;
  }
  
  .order-total {
    font-size: 1.1rem;
    color: #e74c3c;
    font-weight: bold;
  }
  
  .order-actions {
    display: flex;
    gap: 0.5rem;
  }
  
  .order-actions button {
    padding: 0.5rem 1rem;
    border: 1px solid #ddd;
    border-radius: 5px;
    cursor: pointer;
    background: white;
  }
  
  .pay-btn { background: #e74c3c; color: white; border: none; }
  .confirm-btn { background: #4a7c23; color: white; border: none; }
  .review-btn { background: #3498db; color: white; border: none; }
  
  .record-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 1.5rem;
  }
  
  .record-card {
    border: 1px solid #eee;
    border-radius: 8px;
    overflow: hidden;
  }
  
  .record-image {
    height: 180px;
    background: #f0f0f0;
  }
  
  .record-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .record-info {
    padding: 1rem;
  }
  
  .record-info h4 {
    margin-bottom: 0.5rem;
  }
  
  .record-info p {
    font-size: 0.85rem;
    color: #666;
    margin-bottom: 0.3rem;
  }
  
  .record-actions {
    display: flex;
    padding: 0.8rem 1rem;
    border-top: 1px solid #eee;
    gap: 0.5rem;
  }
  
  .record-actions button {
    flex: 1;
    padding: 0.5rem;
    border: 1px solid #ddd;
    border-radius: 5px;
    cursor: pointer;
    background: white;
  }
  
  .address-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }
  
  .address-card {
    border: 1px solid #eee;
    border-radius: 8px;
    padding: 1rem;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .address-card.default {
    border-color: #4a7c23;
    background: #f0f9f0;
  }
  
  .address-info {
    flex: 1;
  }
  
  .address-info .name {
    font-weight: bold;
    margin-bottom: 0.5rem;
  }
  
  .address-info .detail {
    color: #666;
    font-size: 0.9rem;
  }
  
  .default-tag {
    background: #4a7c23;
    color: white;
    padding: 0.2rem 0.6rem;
    border-radius: 10px;
    font-size: 0.8rem;
    margin-right: 0.5rem;
  }
  
  .address-actions button {
    padding: 0.4rem 0.8rem;
    border: 1px solid #ddd;
    border-radius: 5px;
    cursor: pointer;
    background: white;
    margin-left: 0.5rem;
  }
  
  .modal {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0,0,0,0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
  }
  
  .modal-content {
    background: white;
    padding: 2rem;
    border-radius: 10px;
    width: 90%;
    max-width: 500px;
  }
  
  .modal-content h3 {
    margin-bottom: 1.5rem;
  }
  
  .modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
    margin-top: 1.5rem;
  }
  
  .modal-actions button {
    padding: 0.7rem 1.5rem;
    border-radius: 5px;
    cursor: pointer;
  }
  
  .modal-actions button[type="button"] {
    background: #f5f5f5;
    border: 1px solid #ddd;
  }
  
  .modal-actions button[type="submit"] {
    background: #4a7c23;
    color: white;
    border: none;
  }
  
  @media (max-width: 768px) {
    .user-container {
      flex-direction: column;
    }
    
    .sidebar {
      width: 100%;
    }
    
    .user-menu {
      display: flex;
      flex-wrap: wrap;
      gap: 0.5rem;
    }
    
    .user-menu a {
      flex: 1;
      text-align: center;
      min-width: 100px;
    }
  }
  </style>