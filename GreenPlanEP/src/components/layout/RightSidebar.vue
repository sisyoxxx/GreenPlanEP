<!--  智能模块(待写)
      根据登录和购物状态提供智能和个性化服务
      登录前：定位推荐
      登录后：定位推荐
      购买后：种植日记
      未定：种植教程、私信提醒
-->

<template>
  <div class="smart-module-container">
    <!-- 主卡片 -->
    <div class="smart-card">
      
      <!-- 头部：状态标识 -->
      <div class="card-header">
        <div class="status-badge guest">
          <span class="icon">👋</span>
          <span>游客模式</span>
        </div>
        <span class="refresh-btn" @click="refreshData" title="刷新推荐">
          ↻
        </span>
      </div>

      <!-- 核心展示区 -->
      <div class="card-body">
        <h2 class="main-title">{{ mockData.title }}</h2>
        <p class="sub-title">{{ mockData.description }}</p>

        <!-- 推荐列表 -->
        <div class="recommend-list">
          <div 
            v-for="(item, index) in mockData.recommendations" 
            :key="index" 
            class="list-item"
            :style="{ animationDelay: `${index * 0.1}s` }"
          >
            <div class="item-icon">{{ item.icon }}</div>
            <div class="item-content">
              <h4>{{ item.headline }}</h4>
              <p>{{ item.detail }}</p>
            </div>
            <div class="item-arrow">›</div>
          </div>
        </div>
      </div>

      <!-- 底部行动点 -->
      <div class="card-footer">
        <button class="action-btn primary">立即登录解锁更多</button>
        <button class="action-btn secondary">浏览热门种子</button>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { ref, onMounted } from 'vue';

  // --- 模拟数据 (未登录状态) ---
  const mockData = ref({
    title: "发现适合您所在地的绿植",
    description: "检测到您尚未登录，我们根据您的大致位置（模拟）为您推荐当地热销且易养的植物种子。",
    recommendations: [
      {
        icon: "📍",
        headline: "本地热销：薄荷种子",
        detail: "适应性强，新手首选，当前地区购买人数最多。"
      },
      {
        icon: "🌱",
        headline: "当季推荐：向日葵",
        detail: "春季播种正当时，预计60天即可开花。"
      },
      {
        icon: "🎁",
        headline: "新人专属福利",
        detail: "注册即送满50减10元优惠券，首单包邮。"
      }
    ]
  });

  // 模拟刷新功能
  const refreshData = () => {
    // 实际开发中这里会调用 API
    const originalTitle = mockData.value.title;
    mockData.value.title = "正在为您重新计算推荐...";
    
    setTimeout(() => {
      mockData.value.title = originalTitle;
      // 可以在这里打乱一下数组顺序模拟变化
      mockData.value.recommendations.sort(() => Math.random() - 0.5);
    }, 800);
  };

  onMounted(() => {
    console.log("智能模块已加载（模拟未登录状态）");
  });
</script>




<style scoped>
/* 容器：居中或放在右侧栏 */
  .smart-module-container {
    width: 100%;
    min-width: 50px;
    max-width: 240px; /* 限制最大宽度，适合侧边栏 */
    z-index: 100;
    font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', sans-serif;
    overflow-y: hidden;
  }

  /* 卡片主体 */
  .smart-card {
    background: #ffffff;
    border-radius: 16px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
    transform: translateY(-5px);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    border: 1px solid rgba(0,0,0,0.05);
  }

  .smart-card:hover {
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.12);
  }


    /* 窄屏逻辑 */
    @media (max-width: 950px) {
      .smart-module-container {
        display: flex;
        flex-direction: column; 
        overflow: hidden; 
        z-index: 100;
        box-shadow: 2px 0 10px rgba(0,0,0,0.1);
        width: 120px;
      }
      .card-footer { 
        display: flex;
        flex-direction: column; 
      }
      .sub-title {display: none; }
      .main-title { display: none;}
      .card-body { 
        display: flex;
        flex-direction: column;  
      }
    }
    /* 平板端逻辑 */
    @media (max-width: 905px) {
      .smart-module-container {
        display: none;
      }
    }

  /* 头部 */
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 0 auto;  
    padding: 18px 20px 8px 15px;

    max-width: 220px;
    
    border-bottom: 2px solid #80ab64;
  }

  .status-badge {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;
    font-weight: 600;
    padding: 4px 10px 4px 10px;
    border-radius: 15px;
  }

  .status-badge.guest {
    background-color: #e0f2fe;
    color: #64ab95;
  }

  .refresh-btn {
    cursor: pointer;
    color: #9ca3af;
    font-size: 18px;
    transition: color 0.2s, transform 0.3s;
  }

  .refresh-btn:hover {
    color: #64ab95;
    transform: rotate(180deg);
  }

  /* 内容区 */
  .card-body {
    padding: 8px 20px;
  }

  .main-title {
    font-size: 18px;
    font-weight: 700;
    color: #1f2937;
    margin: 0 0 8px 0;
    line-height: 1.4;
  }

  .sub-title {
    font-size: 13px;
    color: #6b7280;
    margin: 0 0 20px 0;
    line-height: 1.6;
  }

  /* 列表样式 */
  .recommend-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .list-item {
    display: flex;
    align-items: center;
    padding: 12px;
    background: #f9fafb;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.2s ease;
    border: 1px solid transparent;
    animation: fadeInUp 0.5s ease backwards;
  }

  @keyframes fadeInUp {
    from {
      opacity: 0;
      transform: translateY(10px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  .list-item:hover {
    background: #fff;
    border-color: #10b981;
    box-shadow: 0 4px 12px rgba(16, 185, 129, 0.1);
    transform: translateX(4px);
  }

  .item-icon {
    font-size: 24px;
    margin-right: 6px;
    flex-shrink: 0;
    background: #fff;
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    box-shadow: 0 2px 5px rgba(0,0,0,0.05);
  }

  .item-content {
    flex: 1;
    min-width: 0; /* 防止文本溢出 */
  }

  .item-content h4 {
    font-size: 14px;
    font-weight: 600;
    color: #374151;
    margin: 0 0 4px 0;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .item-content p {
    font-size: 12px;
    color: #9ca3af;
    margin: 0;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .item-arrow {
    color: #d1d5db;
    font-size: 20px;
    margin-left: 8px;
    transition: color 0.2s;
  }

  .list-item:hover .item-arrow {
    color: #10b981;
  }

  /* 底部按钮 */
  .card-footer {
    padding: 0 20px 20px 20px;
    display: flex;
    gap: 10px;
  }

  .action-btn {
    flex: 1;
    padding: 10px 8px;
    border: none;
    border-radius: 8px;
    font-size: 13px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
  }

  .action-btn.primary {
    background: #80ab64;
    color: white;
    box-shadow: 0 4px 10px rgba(16, 185, 129, 0.3);
  }

  .action-btn.primary:hover {
    background: #059669;
    transform: translateY(-2px);
  }

  .action-btn.secondary {
    background: #f3f4f6;
    color: #4b5563;
  }

  .action-btn.secondary:hover {
    background: #e5e7eb;
    transform: translateY(-2px);
  }
</style>


