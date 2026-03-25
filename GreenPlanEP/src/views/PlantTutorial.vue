<template>
    <div class="tutorial-container">
      <!-- 1. 季节性推荐横幅 (动态显示) -->
      <div v-if="seasonalTip" class="seasonal-banner">
        <span class="icon">📅</span>
        <span class="text">{{ seasonalTip }}</span>
        <button class="view-btn" @click="filterBySeason(currentSeason)">查看相关教程</button>
      </div>
      
        <!-- 2. 顶部 Hero 区域 (可选，增加氛围感) -->
      <div class="hero-section">
        <h1>🌱 种植学院</h1>
        <p>从播种到收获，我们陪你一起见证生命的成长</p>
      </div>
  
      <!-- 3. 筛选栏 -->
      <div class="filter-bar">
        <div class="tabs">
          <button 
            v-for="tab in tabs" 
            :key="tab.id"
            :class="{ active: currentTab === tab.id }"
            @click="currentTab = tab.id"
          >
            {{ tab.name }}
          </button>
        </div>
        <div class="search-box">
          <input type="text" placeholder="搜索教程，例如：'薄荷'" v-model="searchQuery" />
        </div>
      </div>
  
      <!-- 4. 内容网格 (核心部分) -->
      <div class="content-grid">
        <div 
          v-for="item in filteredTutorials" 
          :key="item.id" 
          class="tutorial-card"
          @click="goToDetail(item.id)"
        >
          <!-- 封面区域 -->
          <div class="card-media">
            <img :src="item.coverImage" :alt="item.title" />
            <!-- 视频角标 -->
            <span v-if="item.type === 'video'" class="play-icon">▶️</span>
            <span v-if="item.type === 'tool'" class="tool-badge">🛠️ 工具</span>
            <span v-else class="type-badge article">📖 图文</span>
            <span class="duration" v-if="item.duration">{{ item.duration }}</span>
            <!-- ✅ 建议 1: 难度星级 (右上角) -->
            <div class="difficulty-badge" :title="getDifficultyText(item.difficulty)">
                {{ getStars(item.difficulty) }}
            </div>
          </div>
  
          <!-- 内容区域 -->
          <div class="card-content">
            <div class="meta-row">
                <span class="category-tag">{{ item.category }}</span>
                <!-- ✅ 交作业人数 (如果有) -->
                <span v-if="item.userSubmissions" class="submission-count">
                    📸 {{ item.userSubmissions }}人交作业
                </span>
            </div>
            <h3 class="card-title">{{ item.title }}</h3>
            <p class="card-excerpt">{{ item.excerpt }}</p>
            
            <div class="card-footer">
              <div class="author">
                <img :src="item.authorAvatar" class="avatar" />
                <span>{{ item.authorName }}</span>
              </div>
              <div class="stats">
                <span>👁️ {{ item.views }}</span>
                <span>❤️ {{ item.likes }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 空状态提示 -->
      <div v-if="filteredTutorials.length === 0" class="empty-state">
        暂无相关教程，快去发布第一个吧！
      </div>
    </div>
  </template>
  
<script setup>
  import { ref, computed, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  
  const router = useRouter()
  
  // 模拟当前季节 (实际可根据 new Date().getMonth() 自动判断)
  const currentSeason = 'spring' 
  const seasonalTip = ref('')
  // 初始化季节性提示
  onMounted(() => {
  const tips = {
      'spring': '🌸 春暖花开，正是种植球根和草花的好时节！',
      'summer': '☀️ 夏日炎炎，注意给植物遮阴防晒，预防病虫害。',
      'autumn': '🍂 秋高气爽，适合播种越冬植物和修剪枝条。',
      'winter': '❄️ 冬季休眠期，减少浇水，做好防寒保暖工作。'
    }
    seasonalTip.value = tips[currentSeason]
  })


  // 模拟数据 (实际应从 API 获取)
  const tutorials = ref([
    {
      id: 1,
      type: 'video', // 视频类型
      title: '新手必看：如何正确播种向日葵？',
      category: '新手入门',
      difficulty: 1,
      coverImage: 'https://images.unsplash.com/photo-1597848212624-a19eb35e2651?w=800',
      duration: '05:20',
      excerpt: '很多新手第一步就错了！本期视频详解浸种、催芽和覆土厚度的秘密。',
      authorName: '绿植达人',
      authorAvatar: '/avatars/user1.jpg',
      views: '1.2w',
      likes: 340
    },
    {
      id: 2,
      type: 'article', // 图文类型
      title: '薄荷疯长怎么办？修剪技巧全解析',
      category: '草本植物',
      difficulty: 2,
      coverImage: 'https://img2.baidu.com/it/u=1459122510,3626182329&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=753',
      excerpt: '薄荷越剪越茂盛？教你“打顶”和“重剪”两个关键动作，让盆栽爆盆。',
      authorName: '园艺师小王',
      authorAvatar: '/avatars/user2.jpg',
      views: '890',
      likes: 120
    },
    {
      id: 3,
      type: 'tool', // 工具教学
      title: '园艺铲子的三种隐藏用法',
      category: '工具使用',
      difficulty: 1,
      coverImage: 'https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=800',
      excerpt: '除了挖土，它还能用来松土、除草和测量深度。',
      authorName: '工具控',
      authorAvatar: '/avatars/user3.jpg',
      views: '500',
      likes: 88
    },
    {
        id: 4, type: 'article', 
        title: '多肉度夏指南：如何避免“黑腐”和“化水”？', 
        category: '进阶养护', 
        difficulty: 3,
        coverImage: 'https://img0.baidu.com/it/u=1481425006,2954952960&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=1067',
        excerpt: '夏天是多肉植物的“鬼门关”。本文详解控水、遮阴和通风的三大核心法则。',
        authorName: '多肉大神', 
        authorAvatar: '/avatars/user4.jpg', 
        views: '3.5w', 
        likes: 1200, 
        season: 'summer'
    },
    {
        id: 5, 
        type: 'tool', 
        title: '新手必学：3 分钟学会测试土壤酸碱度', 
        category: '工具使用', 
        difficulty: 1,
        coverImage: 'https://img0.baidu.com/it/u=1459621077,3449079989&fm=253&fmt=auto&app=120&f=JPEG?w=1200&h=800', 
        duration: '03:15',
        excerpt: '为什么你的花总是养不活？可能是土壤酸碱度不对！',
        authorName: '实验室园丁', 
        authorAvatar: '/avatars/user5.jpg', 
        views: '8,200', 
        likes: 450
    },
    {
        id: 6, 
        type: 'video', 
        title: '春天来了！郁金香种球埋多深才开花？', 
        category: '时令种植', 
        difficulty: 2,
        coverImage: 'https://img0.baidu.com/it/u=3847604231,2292094914&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=666', 
        duration: '06:45',
        excerpt: '春植球根黄金期！别再把种球埋错了，深度决定开花率。',
        authorName: '花田喜事', 
        authorAvatar: '/avatars/user6.jpg', 
        views: '1.1w', 
        likes: 680, 
        userSubmissions: 128, 
        season: 'spring'
    }
  ])
  
  const tabs = [
    { id: 'all', name: '全部' },
    { id: 'video', name: '🎬 视频教程' },
    { id: 'article', name: '📖 图文攻略' },
    { id: 'tool', name: '🛠️ 工具百科' }
  ]
  
  const currentTab = ref('all')
  const searchQuery = ref('')
  
  // 筛选逻辑
  const filteredTutorials = computed(() => {
    return tutorials.value.filter(item => {
      const matchTab = currentTab.value === 'all' || item.type === currentTab.value
      const matchSearch = item.title.includes(searchQuery.value) || item.excerpt.includes(searchQuery.value)
      return matchTab && matchSearch
    })
  })
  
  const goToDetail = (id) => {
    router.push(`/tutorials/${id}`)
  }



    // 辅助函数：生成星星
    const getStars = (level) => {
    return '★'.repeat(level) + '☆'.repeat(3 - level)
    }

    const getDifficultyText = (level) => {
    return ['简单', '中等', '困难'][level - 1]
    }

    const filterBySeason = (season) => {
    // 简单实现：切换 Tab 或设置搜索词，实际可调用 API
    searchQuery.value = season === 'spring' ? '春' : season
    currentTab.value = 'all'
    }
</script>
  
<style scoped>

  /* 简单的样式示意，实际项目中请使用你的全局样式 */
  .tutorial-container {

    max-width: 1200px; 
    margin: 0 auto; 
    padding: 20px; 
  }
  .hero-section { 
    text-align: center; 
    margin-bottom: 30px; 
    color: #000000; 
  }
  .filter-bar { 
    display: flex; 
    justify-content: space-between; 
    margin-bottom: 20px; 
    align-items: center; 
  }
  .tabs button { 
    background-color: rgb(255, 255, 255, 0.5); 
    border: none; 
    padding: 10px 20px; 
    cursor: pointer; 
    font-size: 1rem; 
    color: #666; 
  }
  .tabs button.active { 
    color: #80ab64; 
    font-weight: bold; 
    border-bottom: 2px solid #80ab64; 
  }
  .content-grid { 
    display: grid; 
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); 
    gap: 20px; 
  }

  /* ✅ 季节性横幅样式 */
.seasonal-banner {
  background: linear-gradient(90deg, #e8f5e9 0%, #f1fff2 100%);
  border-left: 5px solid #80ab64;
  padding: 15px 20px;
  border-radius: 8px;
  margin-bottom: 25px;
  display: flex;
  align-items: center;
  gap: 15px;
  color: #2e5c36;
}
.seasonal-banner .view-btn {
  margin-left: auto;
  background: #80ab64;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 0.85rem;
  transition: background-color 0.2s;
}
.seasonal-banner .view-btn:hover { background: #6b9652; }

/* ✅ 难度星级样式 */
.difficulty-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.6);
  color: #ffd700; /* 金色星星 */
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.8rem;
  letter-spacing: 1px;
  backdrop-filter: blur(2px);
}

/* ✅ 交作业计数样式 */
.submission-count {
  font-size: 0.75rem;
  color: #ff9800;
  background: #fff3e0;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: bold;
}

.meta-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.type-badge {
  position: absolute;
  bottom: 10px;
  left: 10px;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: bold;
  text-transform: uppercase;
}
.type-badge.video { background: rgba(231, 76, 60, 0.9); }
.type-badge.tool { background: rgba(243, 156, 18, 0.9); }
.type-badge.article { background: rgba(52, 152, 219, 0.9); }

/* 保持之前的卡片悬停效果等 */
.tutorial-card { 
  background: white; 
  border-radius: 12px; 
  overflow: hidden; 
  box-shadow: 0 2px 8px rgba(0,0,0,0.05); 
  transition: transform 0.2s; 
  cursor: pointer; 
  display: flex; 
  flex-direction: column;
}
  .tutorial-card:hover { 
    transform: translateY(-5px); 
    box-shadow: 0 8px 16px rgba(0,0,0,0.1); 
  }
  .card-media { 
    position: relative; 
    height: 180px; 
    flex-shrink: 0; 
  }
  .card-media img { 
    width: 100%; 
    height: 100%; 
    object-fit: cover; 
  }
  .card-content { 
    padding: 15px; 
    flex-grow: 1; 
    display: flex; 
    flex-direction: column; 
  }
  .card-title { 
    margin: 8px 0; 
    font-size: 1.05rem; 
    color: #333; 
    line-height: 1.4; 
    flex-grow: 1; 
  }
  .card-excerpt { 
    font-size: 0.85rem; 
    color: #666; 
    display: -webkit-box; 
    line-clamp: 2; 
    -webkit-box-orient: vertical; 
    overflow: hidden; 
    margin-bottom: 15px;
  }
  .card-footer { 
    display: flex; 
    justify-content: space-between; 
    align-items: center; 
    border-top: 1px solid #eee; 
    padding-top: 12px; 
    font-size: 0.8rem; 
    color: #999; 
  }
  .author { 
    display: flex; 
    align-items: center; 
    gap: 6px; 
  }
  .avatar { 
    width: 24px; 
    height: 24px; 
    border-radius: 50%; 
    object-fit: cover; 
  }
  
  /* 卡片样式 */

  .play-icon { 
    position: absolute; 
    bottom: 10px; 
    right: 10px; 
    background: rgba(0,0,0,0.6); 
    color: white; 
    padding: 4px 8px; 
    border-radius: 4px;
    font-size: 0.8rem; 
  }
  .tool-badge { 
    position: absolute; 
    top: 10px; 
    left: 10px; 
    background: #f39c12; 
    color: white; 
    padding: 4px 8px; 
    border-radius: 4px; 
    font-size: 0.8rem; 
  }
  .category-tag { 
    font-size: 0.75rem; 
    color: #80ab64; 
    background: #e8f5e9; 
    padding: 2px 6px; 
    border-radius: 4px; 
  }
  </style>