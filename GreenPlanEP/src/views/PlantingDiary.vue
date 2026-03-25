<template>    
    <div class="my-garden-layout">
      
      <!-- ================= 左侧边栏 ================= -->
      <aside class="sidebar">
        <!-- 新增按钮 -->
        <button class="add-btn" @click="toggleAddForm">
          ➕ 新增种植日记
        </button>
  
        <!-- 新增表单 (下拉式) -->
        <div v-if="showAddForm" class="add-form">
          <h3>🌱 添加新植物</h3>
          <input v-model="newPlant.name" placeholder="植物名称" required />
          <input v-model="newPlant.species" placeholder="品种（如：番茄 - 樱桃红）" />
          <input type="date" v-model="newPlant.sowDate" required />
          <select v-model="newPlant.stage">
            <option v-for="stage in growthStages" :key="stage" :value="stage">{{ stage }}</option>
          </select>
          <button @click="addPlantFromForm">开始种植</button>
          <button @click="toggleAddForm" class="cancel-btn">取消</button>
        </div>
  
        <!-- 植物列表 -->
        <div class="plant-list">
          <h3>📚 我的植物 ({{ plants.length }})</h3>
          <ul>
            <li 
              v-for="plant in plants" 
              :key="plant.id"
              :class="{ active: selectedPlantId === plant.id }"
              @click="selectPlant(plant.id)"
            >
              <div class="plant-info">
                <span class="name">{{ plant.name }}</span>
                <span class="stage-badge" :class="getStageClass(plant.stage)">{{ plant.stage }}</span>
              </div>
              <small>{{ plant.species }}</small>
  
              <!-- 操作按钮 -->
              <div class="actions">
                <button class="icon-btn delete" @click.stop="confirmDeletePlant(plant.id)" title="删除植物">🗑️</button>
              </div>
            </li>
          </ul>
          <p v-if="plants.length === 0" class="empty-tip">暂无植物，点击上方“新增”开始种植吧～</p>
        </div>
      </aside>
  
      <!-- ================= 主内容区 ================= -->
      <main class="main-content">
        <!-- 只有当选中了植物才显示详情 -->
        <div v-if="currentSelectedPlant" class="plant-detail">
          
          <header class="detail-header">
            <div class="header-text">
              <h1>🌿 {{ currentSelectedPlant.name }} <small>({{ currentSelectedPlant.species }})</small></h1>
              <p class="meta">
                播种于：{{ currentSelectedPlant.sowDate }} 
                <span class="divider">|</span> 
                当前阶段：<strong :class="getStageClass(currentSelectedPlant.stage)">{{ currentSelectedPlant.stage }}</strong>
              </p>
            </div>
          </header>
  
          <!-- 今日打卡 -->
          <section class="daily-checkin">
            <h2>📅 今日打卡</h2>
            <textarea v-model="checkinForm.note" placeholder="今天它长得怎么样？"></textarea>
            
            <div class="upload-area">
              <input type="file" @change="handleCheckinImageUpload" accept="image/*" id="checkin-image-upload" ref="checkinFileInput" />
              <label for="checkin-image-upload" class="upload-label">
                {{ checkinForm.image ? '🖼️ 已选择图片' : '📷 上传今日照片' }}
              </label>
              <button v-if="checkinForm.image" class="clear-img" @click="clearCheckinImage">❌</button>
            </div>
            
            <button 
              @click="submitCheckin" 
              class="submit-btn" 
              :disabled="!checkinForm.note && !checkinForm.image"
            >
              提交记录
            </button>
          </section>
  
          <!-- 历史记录 -->
          <section class="history-timeline">
            <h2>📜 生长历程 ({{ currentSelectedPlant.records.length }})</h2>
            <ul v-if="currentSelectedPlant.records.length > 0">
              <li v-for="record in currentSelectedPlant.records" :key="record.id" class="timeline-item">
                <div class="timeline-dot"></div>
                <div class="timeline-content">
                  <strong class="date">{{ record.date }}</strong>
                  <p class="note">{{ record.note }}</p>
                  <img v-if="record.image" :src="record.image" alt="记录" class="record-img" />
                  <button class="text-btn danger" @click="confirmDeleteRecord(record.id)">删除此记录</button>
                </div>
              </li>
            </ul>
            <p v-else class="no-record">还没有记录哦，快去打卡吧！</p>
          </section>
        </div>
  
        <!-- 未选择植物时的欢迎页 -->
        <div v-else class="welcome-screen">
          <h1>🌼 欢迎来到你的种植日记</h1>
          <p>从左侧选择一个植物，或点击“新增种植日记”开始记录吧！</p>
          <div class="illustration">🌱💧️</div>
        </div>
      </main>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive, computed } from 'vue'
  
  // ================= 1. 本地数据定义 (替代 Pinia) =================
  
  // 生长阶段常量
  const growthStages = ['种子期', '发芽期', '幼苗期', '生长期', '开花期', '结果期', '枯萎期']
  
  // 模拟初始数据 (如果没有数据，就加载这些)
  const initialData = [
    {
      id: 1,
      name: '小番茄',
      species: '樱桃红',
      sowDate: '2025-04-01',
      stage: '发芽期',
      records: [
        { id: 101, date: '2025-04-05', note: '发芽了！两片嫩叶', image: 'https://via.placeholder.com/200x150?text=Sprout' }
      ]
    },
    {
      id: 2,
      name: '薄荷',
      species: '留兰香',
      sowDate: '2025-03-20',
      stage: '生长期',
      records: []
    }
  ]
  
  // 响应式数据：植物列表
  const plants = ref(initialData)
  
  // 响应式数据：当前选中的植物 ID (初始化为 null 或第一个植物的 ID)
  // 这里初始化为 null，通过逻辑控制，避免报错
  const selectedPlantId = ref(null)
  
  // 计算属性：获取当前选中的植物对象
  // 如果 selectedPlantId 为 null，则返回 null，模板中 v-if 会处理
  const currentSelectedPlant = computed(() => {
    if (!selectedPlantId.value) return null
    return plants.value.find(p => p.id === selectedPlantId.value) || null
  })
  
  // ================= 2. 状态管理：新增表单 =================
  const showAddForm = ref(false)
  const newPlant = reactive({
    name: '',
    species: '',
    sowDate: new Date().toISOString().split('T')[0],
    stage: '种子期'
  })
  
  const toggleAddForm = () => {
    showAddForm.value = !showAddForm.value
    if (!showAddForm.value) {
      // 重置表单
      newPlant.name = ''
      newPlant.species = ''
      newPlant.sowDate = new Date().toISOString().split('T')[0]
      newPlant.stage = '种子期'
    }
  }
  
  const addPlantFromForm = () => {
    if (!newPlant.name || !newPlant.sowDate) {
      alert('请填写名称和播种日期')
      return
    }
    
    const newId = Date.now()
    const plantObj = {
      id: newId,
      name: newPlant.name,
      species: newPlant.species,
      sowDate: newPlant.sowDate,
      stage: newPlant.stage,
      records: []
    }
    
    plants.value.push(plantObj)
    
    // 自动选中新添加的植物
    selectPlant(newId)
    
    toggleAddForm()
    alert('✅ 新植物添加成功！')
  }
  
  // ================= 3. 核心逻辑：选择与打卡 =================
  
  // 选择植物
  const selectPlant = (id) => {
    selectedPlantId.value = id
  }
  
  // 打卡表单
  const checkinForm = reactive({ 
    note: '', 
    image: null 
  })
  const checkinFileInput = ref(null)
  
  const handleCheckinImageUpload = (e) => {
    const file = e.target.files[0]
    if (file) {
      const reader = new FileReader()
      reader.onload = (event) => { 
        checkinForm.image = event.target.result
      }
      reader.readAsDataURL(file)
    }
  }
  
  const clearCheckinImage = () => {
    checkinForm.image = null
    if (checkinFileInput.value) checkinFileInput.value.value = ''
  }
  
  // ✅ 修复报错的关键：提交前严格检查 selectedPlantId
  const submitCheckin = () => {
    // 1. 检查是否选中植物
    if (!selectedPlantId.value) {
      alert('请先选择一个植物！')
      return
    }
  
    // 2. 检查内容
    if (!checkinForm.note && !checkinForm.image) {
      alert('请至少填写文字或上传图片')
      return
    }
  
    // 3. 找到当前植物并添加记录
    const plant = plants.value.find(p => p.id === selectedPlantId.value)
    if (plant) {
      const newRecord = {
        id: Date.now(),
        date: new Date().toISOString().split('T')[0],
        note: checkinForm.note,
        image: checkinForm.image
      }
      plant.records.unshift(newRecord) // 添加到开头
      
      // 重置表单
      checkinForm.note = ''
      checkinForm.image = null
      if (checkinFileInput.value) checkinFileInput.value.value = ''
      
      alert('✅ 今日打卡成功！')
    }
  }
  
  // ================= 4. 辅助功能 =================
  
  const getStageClass = (stage) => {
    const map = {
      '种子期': 'stage-seed',
      '发芽期': 'stage-sprout',
      '幼苗期': 'stage-seedling',
      '生长期': 'stage-grow',
      '开花期': 'stage-flower',
      '结果期': 'stage-fruit',
      '枯萎期': 'stage-wither'
    }
    return map[stage] || ''
  }
  
  const confirmDeletePlant = (id) => {
    if(confirm('确定要删除这株植物及其所有记录吗？')) {
      plants.value = plants.value.filter(p => p.id !== id)
      // 如果删除的是当前选中的，则清空选中状态
      if (selectedPlantId.value === id) {
        selectedPlantId.value = null
      }
    }
  }
  
  const confirmDeleteRecord = (recordId) => {
    if (!selectedPlantId.value) return
    
    const plant = plants.value.find(p => p.id === selectedPlantId.value)
    if (plant) {
      if(confirm('确定要删除这条记录吗？')) {
        plant.records = plant.records.filter(r => r.id !== recordId)
      }
    }
  }
  </script>
  
  <style scoped>
  /* 样式保持不变，直接复用之前的 CSS */
  .my-garden-layout { display: flex; height: calc(100vh - 60px); font-family: 'Microsoft YaHei', sans-serif; background: #f9f9f9; }
  .sidebar { width: 280px; background: white; border-right: 1px solid #eee; padding: 1.5rem; overflow-y: auto; flex-shrink: 0; }
  .add-btn { width: 100%; padding: 0.75rem; background: #4caf50; color: white; border: none; border-radius: 6px; font-size: 1rem; cursor: pointer; margin-bottom: 1.5rem; }
  .add-btn:hover { background: #43a047; }
  .add-form { background: #f0f9f0; padding: 1rem; border-radius: 6px; margin-bottom: 1.5rem; border: 1px dashed #4caf50; }
  .add-form h3 { margin-top: 0; color: #2e7d32; }
  .add-form input, .add-form select { width: 100%; padding: 0.5rem; margin-bottom: 0.75rem; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;}
  .add-form button { width: 100%; padding: 0.5rem; margin-bottom: 0.5rem; border: none; border-radius: 4px; cursor: pointer; }
  .add-form button:first-child { background: #4caf50; color: white; }
  .cancel-btn { background: #e0e0e0 !important; color: #333; }
  .plant-list h3 { margin-top: 0; color: #555; font-size: 1.1rem; }
  .plant-list ul { list-style: none; padding: 0; margin: 0; }
  .plant-list li { padding: 0.75rem; margin-bottom: 0.5rem; background: #f5f5f5; border-radius: 6px; cursor: pointer; transition: all 0.2s; border-left: 3px solid transparent; position: relative; }
  .plant-list li:hover { background: #e8f5e9; }
  .plant-list li.active { background: #c8e6c9; border-left-color: #4caf50; font-weight: bold; }
  .plant-info { display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.3rem; }
  .plant-info .name { font-weight: bold; font-size: 1rem; }
  .stage-badge { font-size: 0.7rem; padding: 2px 6px; border-radius: 4px; background: #eee; color: #666; }
  .stage-seed { background: #ffe0b2; color: #e65100; }
  .stage-sprout { background: #c8e6c9; color: #2e7d32; }
  .stage-seedling { background: #a5d6a7; color: #1b5e20; }
  .stage-grow { background: #81c784; color: #1b5e20; }
  .stage-flower { background: #f8bbd0; color: #880e4f; }
  .stage-fruit { background: #ffcc80; color: #e65100; }
  .stage-wither { background: #cfd8dc; color: #455a64; }
  .actions { position: absolute; right: 10px; top: 50%; transform: translateY(-50%); display: flex; gap: 5px; opacity: 0; transition: opacity 0.2s; }
  .plant-list li:hover .actions { opacity: 1; }
  .icon-btn { background: none; border: none; font-size: 0.9rem; cursor: pointer; padding: 2px; border-radius: 4px; }
  .icon-btn.delete:hover { background: #ffebee; color: #f44336; }
  .empty-tip { color: #999; font-size: 0.9rem; text-align: center; margin-top: 1rem; }
  .main-content { flex: 1; padding: 2rem; overflow-y: auto; }
  .welcome-screen { text-align: center; padding-top: 4rem; color: #666; }
  .welcome-screen h1 { color: #4caf50; margin-bottom: 1rem; }
  .illustration { font-size: 4rem; margin-top: 2rem; opacity: 0.6; }
  .plant-detail { max-width: 800px; margin: 0 auto; }
  .plant-detail h1 { color: #2e7d32; margin-bottom: 0.5rem; }
  .detail-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 2rem; background: white; padding: 1.5rem; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }
  .header-text h1 { margin: 0; font-size: 1.8rem; color: #2e7d32; }
  .header-text small { color: #666; font-weight: normal; }
  .meta { color: #666; margin-top: 0.5rem; }
  .divider { margin: 0 8px; color: #ddd; }
  section { margin-bottom: 2.5rem; padding-bottom: 2rem; border-bottom: 1px dashed #ddd; background: white; padding: 1.5rem; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }
  section:last-child { border-bottom: none; }
  section h2 { color: #333; margin-bottom: 1rem; font-size: 1.3rem; margin-top: 0; }
  .daily-checkin textarea { width: 100%; min-height: 80px; padding: 0.75rem; border: 1px solid #ccc; border-radius: 6px; resize: vertical; font-family: inherit; margin-bottom: 1rem; box-sizing: border-box;}
  .upload-area { display: flex; align-items: center; gap: 1rem; margin-bottom: 1rem; }
  #checkin-image-upload { display: none; }
  .upload-label { display: inline-block; padding: 0.5rem 1rem; background: #e3f2fd; color: #1976d2; border: 1px solid #bbdefb; border-radius: 4px; cursor: pointer; font-size: 0.9rem; }
  .clear-img { background: none; border: none; color: #f44336; cursor: pointer; }
  .submit-btn { padding: 0.75rem 2rem; background: #4caf50; color: white; border: none; border-radius: 6px; font-size: 1rem; cursor: pointer; }
  .submit-btn:hover { background: #43a047; }
  .submit-btn:disabled { background: #ccc; cursor: not-allowed; }
  .history-timeline ul { list-style: none; padding: 0; }
  .timeline-item { display: flex; gap: 1rem; margin-bottom: 1.5rem; position: relative; }
  .timeline-dot { width: 12px; height: 12px; background: #4caf50; border-radius: 50%; flex-shrink: 0; margin-top: 6px; z-index: 1; }
  .timeline-item::before { content: ''; position: absolute; left: 5px; top: 20px; bottom: -20px; width: 2px; background: #eee; z-index: 0; }
  .timeline-item:last-child::before { display: none; }
  .timeline-content { flex: 1; background: #fafafa; padding: 1rem; border-radius: 8px; border: 1px solid #eee; }
  .date { color: #2e7d32; font-weight: bold; display: block; margin-bottom: 0.5rem; }
  .note { margin: 0.5rem 0; color: #555; }
  .record-img { max-width: 200px; max-height: 150px; border-radius: 6px; margin-top: 0.5rem; display: block; object-fit: cover; }
  .text-btn { background: none; border: none; font-size: 0.8rem; cursor: pointer; color: #666; text-decoration: underline; }
  .text-btn.danger { color: #f44336; }
  .no-record { color: #999; font-style: italic; text-align: center; padding: 2rem; background: #f9f9f9; border-radius: 6px; }
  @media (max-width: 768px) { .my-garden-layout { flex-direction: column; } .sidebar { width: 100%; border-right: none; border-bottom: 1px solid #eee; max-height: 300px; } .main-content { padding: 1rem; } }
  </style>