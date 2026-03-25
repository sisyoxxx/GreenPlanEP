<template>
    <div class="community-layout">
      
      <!-- ================= 左侧导航栏 ================= -->
      <aside class="sidebar">
        <div class="sidebar-header">
          <h2>🌿 种植社区</h2>
        </div>
  
        <!-- 主要功能按钮 -->
        <nav class="main-nav">
          <button 
            class="nav-btn" 
            @click="switchView('post')"
            :class="{ active: currentView === 'post' }"
          >
            ✏️ 编辑发帖
          </button>
          
          <button 
            class="nav-btn" 
            @click="switchView('feed')"
            :class="{ active: currentView === 'feed' }"
          >
            📢 社区动态
          </button>

          <button 
            class="nav-btn" 
            @click="switchView('messages')"
            :class="{ active: currentView === 'messages' }"
          >
            💬 私信消息
            <span v-if="unreadMessages > 0" class="badge">{{ unreadMessages }}</span>
          </button>
          
          <button 
            class="nav-btn" 
            @click="switchView('notifications')"
            :class="{ active: currentView === 'notifications' }"
          >
            🔔 评论回复
            <span v-if="unreadNotifications > 0" class="badge">{{ unreadNotifications }}</span>
          </button>
          
          <button 
            class="nav-btn" 
            @click="switchView('likes')"
            :class="{ active: currentView === 'likes' }"
          >
            👍 点赞信息
            <span v-if="newLikes > 0" class="badge">{{ newLikes }}</span>
          </button>
        </nav>
  
        <!-- 分类筛选 -->
        <div class="categories">
          <h3>话题分类</h3>
          <ul>
            <li 
              v-for="cat in categories" 
              :key="cat.id"
              :class="{ active: selectedCategory === cat.id }"
              @click="selectCategory(cat.id)"
            >
              {{ cat.name }}
            </li>
          </ul>
        </div>
      </aside>
  
      <!-- ================= 右侧内容区 ================= -->
      <main class="content-area">
        
        <!-- 视图：帖子列表（默认） -->
        <div v-if="currentView === 'feed'" class="view-container">
          <div class="view-header">
            <h1>📢 社区动态</h1>
            <div class="filters">
              <select v-model="sortBy">
                <option value="latest">最新</option>
                <option value="hot">最热</option>
                <option value="discussed">最多讨论</option>
              </select>
            </div>
          </div>
  
          <!-- 帖子卡片列表 -->
          <div class="posts-list">
            <div 
              v-for="post in filteredPosts" 
              :key="post.id" 
              class="post-card"
              :class="{ official: post.isOfficial }"
            >
              <div class="post-header">
                <div class="post-meta">
                  <span class="author">{{ post.author }}</span>
                  <span class="date">{{ post.date }}</span>
                </div>
                <div class="post-tags">
                  <span v-if="post.isOfficial" class="tag official-tag">📢 官方话题</span>
                  <span v-else class="tag user-tag">💬 用户分享</span>
                  <span v-for="tag in post.tags" :key="tag" class="tag">#{{ tag }}</span>
                </div>
              </div>
  
              <h3 class="post-title">{{ post.title }}</h3>
              <p class="post-content">{{ post.content }}</p>
  
              <div class="post-actions">
                <button class="action-btn" @click="likePost(post)">
                  👍 {{ post.likes }}
                </button>
                <button class="action-btn" @click="openComments(post)">
                  💬 评论 ({{ post.comments }})
                </button>
                <button class="action-btn" @click="sharePost(post)">
                  🔗 分享
                </button>
              </div>
            </div>
          </div>
        </div>
  
        <!-- 视图：发帖编辑器 -->
        <div v-if="currentView === 'post'" class="view-container">
          <div class="view-header">
            <h1>✏️ 发布新帖</h1>
            <button class="back-btn" @click="switchView('feed')">← 返回社区</button>
          </div>
  
          <div class="post-editor">
            <input 
              v-model="newPost.title" 
              placeholder="请输入标题..." 
              class="title-input"
            />
            
            <textarea 
              v-model="newPost.content" 
              placeholder="分享你的种植经验或提问..." 
              rows="8"
              class="content-input"
            ></textarea>
  
            <div class="editor-options">
              <select v-model="newPost.category">
                <option value="" disabled>选择分类</option>
                <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                  {{ cat.name }}
                </option>
              </select>
  
              <input 
                v-model="newPost.tags" 
                placeholder="标签（用逗号分隔，如：浇水,施肥）" 
                class="tags-input"
              />
  
              <div class="image-upload">
                <input type="file" @change="handleImageUpload" accept="image/*" id="post-image" />
                <label for="post-image" class="upload-btn">📷 添加图片</label>
                <span v-if="newPost.image" class="image-preview">已选择图片</span>
              </div>
            </div>
  
            <button @click="submitPost" class="submit-btn" :disabled="!canSubmit">
              发布帖子
            </button>
          </div>
        </div>
  
        <!-- 视图：私信消息 -->
        <div v-if="currentView === 'messages'" class="view-container">
          <div class="view-header">
            <h1>💬 私信消息</h1>
          </div>
  
          <div class="messages-list">
            <div v-for="msg in messages" :key="msg.id" class="message-item" @click="openChat(msg)">
              <div class="message-avatar">👤</div>
              <div class="message-content">
                <div class="message-header">
                  <strong>{{ msg.from }}</strong>
                  <span class="message-time">{{ msg.time }}</span>
                </div>
                <p class="message-preview">{{ msg.preview }}</p>
              </div>
              <span v-if="msg.unread" class="unread-dot"></span>
            </div>
          </div>
  
          <!-- 聊天窗口（点击消息后显示） -->
          <div v-if="activeChat" class="chat-window">
            <div class="chat-header">
              <button @click="closeChat" class="back-btn">←</button>
              <h3>与 {{ activeChat.from }} 的对话</h3>
            </div>
            <div class="chat-messages">
              <div v-for="chat in activeChat.chats" :key="chat.id" class="chat-bubble" :class="chat.type">
                {{ chat.text }}
              </div>
            </div>
            <div class="chat-input">
              <input v-model="newMessage" placeholder="输入消息..." />
              <button @click="sendMessage">发送</button>
            </div>
          </div>
        </div>
  
        <!-- 视图：通知中心 -->
        <div v-if="currentView === 'notifications'" class="view-container">
          <div class="view-header">
            <h1>🔔 评论回复</h1>
          </div>
  
          <div class="notifications-list">
            <div v-for="notif in notifications" :key="notif.id" class="notification-item" :class="{ unread: notif.unread }">
              <div class="notif-icon">💬</div>
              <div class="notif-content">
                <p><strong>{{ notif.user }}</strong> 回复了你的帖子 "{{ notif.postTitle }}"</p>
                <p class="notif-text">{{ notif.text }}</p>
                <span class="notif-time">{{ notif.time }}</span>
              </div>
              <button v-if="notif.unread" @click="markAsRead(notif)" class="mark-read">✓</button>
            </div>
          </div>
        </div>
  
        <!-- 视图：点赞信息 -->
        <div v-if="currentView === 'likes'" class="view-container">
          <div class="view-header">
            <h1>👍 点赞信息</h1>
          </div>
  
          <div class="likes-list">
            <div v-for="like in likes" :key="like.id" class="like-item">
              <div class="like-icon">👍</div>
              <div class="like-content">
                <p><strong>{{ like.user }}</strong> 赞了你的帖子 "{{ like.postTitle }}"</p>
                <span class="like-time">{{ like.time }}</span>
              </div>
            </div>
          </div>
        </div>
  
      </main>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive, computed } from 'vue'
  
  // ================= 状态管理 =================
  const currentView = ref('feed') // feed, post, messages, notifications, likes
  const selectedCategory = ref('all')
  const sortBy = ref('latest')
  
  // 未读数量
  const unreadMessages = ref(3)
  const unreadNotifications = ref(5)
  const newLikes = ref(2)
  
  // 分类数据
  const categories = [
    { id: 'all', name: '全部话题' },
    { id: 'experience', name: '种植经验' },
    { id: 'questions', name: '求助问答' },
    { id: 'showcase', name: '成果展示' },
    { id: 'activities', name: '官方活动' }
  ]
  
  // 模拟帖子数据
  const posts = ref([
    {
      id: 1,
      title: '如何防止番茄徒长？',
      content: '最近我的番茄苗长得又细又高，是不是光照不够？求大神指点！',
      author: '花友小明',
      date: '2025-04-10',
      tags: ['浇水', '光照'],
      likes: 12,
      comments: 1,
      isOfficial: false,
      category: 'questions'
    },
    {
      id: 2,
      title: '【官方活动】春季播种挑战赛',
      content: '即日起，晒出你的播种过程，赢取种子礼包！活动持续到本月底，快来参加吧～',
      author: '管理员',
      date: '2025-04-08',
      tags: ['活动', '挑战'],
      likes: 89,
      comments: 0,
      isOfficial: true,
      category: 'activities'
    },
    {
      id: 3,
      title: '第一次种薄荷成功啦！',
      content: '按照教程一步步来，终于看到小芽冒出来了，太有成就感了！附上照片～',
      author: '新手小白',
      date: '2025-04-09',
      tags: ['薄荷', '新手'],
      likes: 24,
      comments: 3,
      isOfficial: false,
      category: 'showcase'
    }
  ])
  
  // 新帖子表单
  const newPost = reactive({
    title: '',
    content: '',
    category: '',
    tags: '',
    image: null
  })
  
  // 消息数据
  const messages = ref([
    {
      id: 1,
      from: '园艺达人',
      preview: '你好，看到你问番茄的问题，我有些经验可以分享...',
      time: '10分钟前',
      unread: true,
      chats: [
        { id: 1, text: '你好，看到你问番茄的问题，我有些经验可以分享...', type: 'received' },
        { id: 2, text: '真的吗？太感谢了！', type: 'sent' }
      ]
    },
    {
      id: 2,
      from: '植物医生',
      preview: '关于你上次咨询的病虫害问题...',
      time: '1小时前',
      unread: true,
      chats: []
    }
  ])
  
  // 通知数据
  const notifications = ref([
    {
      id: 1,
      user: '绿意盎然',
      postTitle: '如何防止番茄徒长？',
      text: '可能是缺光，建议每天保证6小时以上日照',
      time: '5分钟前',
      unread: true
    },
    {
      id: 2,
      user: '种菜老手',
      postTitle: '第一次种薄荷成功啦！',
      text: '恭喜！薄荷很好养的，接下来注意定期修剪',
      time: '20分钟前',
      unread: true
    }
  ])
  
  // 点赞数据
  const likes = ref([
    {
      id: 1,
      user: '阳光花园',
      postTitle: '第一次种薄荷成功啦！',
      time: '刚刚'
    },
    {
      id: 2,
      user: '自然之友',
      postTitle: '如何防止番茄徒长？',
      time: '15分钟前'
    }
  ])
  
  // 当前激活的聊天
  const activeChat = ref(null)
  const newMessage = ref('')
  
  // ================= 计算属性 =================
  const filteredPosts = computed(() => {
    let result = posts.value
    
    // 按分类筛选
    if (selectedCategory.value !== 'all') {
      result = result.filter(post => post.category === selectedCategory.value)
    }
    
    // 排序
    if (sortBy.value === 'latest') {
      // 按日期排序（简化处理）
    } else if (sortBy.value === 'hot') {
      result = [...result].sort((a, b) => b.likes - a.likes)
    }
    
    return result
  })
  
  const canSubmit = computed(() => {
    return newPost.title.trim() && newPost.content.trim()
  })
  
  // ================= 方法 =================
  
  // 切换视图
  const switchView = (view) => {
    currentView.value = view
  }
  
  // 选择分类
  const selectCategory = (categoryId) => {
    selectedCategory.value = categoryId
  }
  
  // 提交新帖子
  const submitPost = () => {
    if (!canSubmit.value) return
    
    const postObj = {
      id: Date.now(),
      title: newPost.title,
      content: newPost.content,
      author: '我',
      date: new Date().toISOString().split('T')[0],
      tags: newPost.tags.split(',').map(t => t.trim()).filter(t => t),
      likes: 0,
      comments: 0,
      isOfficial: false,
      category: newPost.category || 'experience'
    }
    
    posts.value.unshift(postObj)
    
    // 重置表单
    newPost.title = ''
    newPost.content = ''
    newPost.category = ''
    newPost.tags = ''
    newPost.image = null
    
    alert('✅ 帖子发布成功！')
    switchView('feed')
  }
  
  // 处理图片上传
  const handleImageUpload = (e) => {
    const file = e.target.files[0]
    if (file) {
      const reader = new FileReader()
      reader.onload = (event) => {
        newPost.image = event.target.result
      }
      reader.readAsDataURL(file)
    }
  }
  
  // 点赞帖子
  const likePost = (post) => {
    post.likes++
  }
  
  // 打开评论
  const openComments = (post) => {
    alert(`打开 "${post.title}" 的评论区（功能待实现）`)
  }
  
  // 分享帖子
  const sharePost = (post) => {
    alert(`分享 "${post.title}"（功能待实现）`)
  }
  
  // 打开聊天
  const openChat = (msg) => {
    activeChat.value = msg
    // 标记为已读
    msg.unread = false
    if (unreadMessages.value > 0) unreadMessages.value--
  }
  
  // 关闭聊天
  const closeChat = () => {
    activeChat.value = null
  }
  
  // 发送消息
  const sendMessage = () => {
    if (!newMessage.value.trim() || !activeChat.value) return
    
    activeChat.value.chats.push({
      id: Date.now(),
      text: newMessage.value,
      type: 'sent'
    })
    
    newMessage.value = ''
  }
  
  // 标记通知为已读
  const markAsRead = (notif) => {
    notif.unread = false
    if (unreadNotifications.value > 0) unreadNotifications.value--
  }
  </script>
  
  <style scoped>
  .community-layout {
    display: flex;
    min-height: calc(100vh - 60px);
    background: #f5f7fa;
  }
  
  /* ========== 左侧导航栏 ========== */
  .sidebar {
    width: 280px;
    background: white;
    border-right: 1px solid #e1e4e8;
    padding: 1.5rem;
    flex-shrink: 0;
    overflow-y: auto;
  }
  
  .sidebar-header h2 {
    margin: 0 0 1.5rem 0;
    color: #2c3e50;
    font-size: 1.5rem;
  }
  
  .main-nav {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    margin-bottom: 2rem;
  }
  
  .nav-btn {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0.75rem 1rem;
    background: #f8f9fa;
    border: 1px solid #e1e4e8;
    border-radius: 8px;
    cursor: pointer;
    font-size: 0.95rem;
    color: #495057;
    transition: all 0.2s;
    position: relative;
  }
  
  .nav-btn:hover {
    background: #e9ecef;
  }
  
  .nav-btn.active {
    background: #80ab64;
    color: white;
    border-color: #ffffff;
  }
  
  
  .badge {
    background: #dc3545;
    color: white;
    font-size: 0.7rem;
    padding: 2px 6px;
    border-radius: 10px;
    min-width: 18px;
    text-align: center;
  }
  
  .categories h3 {
    margin: 0 0 1rem 0;
    color: #6c757d;
    font-size: 0.9rem;
    text-transform: uppercase;
    letter-spacing: 0.5px;
  }
  
  .categories ul {
    list-style: none;
    padding: 0;
    margin: 0;
  }
  
  .categories li {
    padding: 0.5rem 0;
    cursor: pointer;
    color: #495057;
    border-radius: 4px;
    transition: background-color 0.2s;
  }
  
  .categories li:hover {
    background: #f8f9fa;
  }
  
  .categories li.active {
    color: #3a5c20;
    font-weight: bold;
  }
  
  /* ========== 右侧内容区 ========== */
  .content-area {
    flex: 1;
    padding: 2rem;
    overflow-y: auto;
  }
  
  .view-container {
    max-width: 800px;
    margin: 0 auto;
  }
  
  .view-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;
    padding-bottom: 1rem;
    border-bottom: 2px solid #e1e4e8;
  }
  
  .view-header h1 {
    margin: 0;
    color: #2c3e50;
    font-size: 1.8rem;
  }
  
  .back-btn {
    padding: 0.5rem 1rem;
    background: #6c757d;
    color: white;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-size: 0.9rem;
  }
  
  .back-btn:hover {
    background: #5a6268;
  }
  
  .filters select {
    padding: 0.5rem;
    border: 1px solid #ced4da;
    border-radius: 6px;
    font-size: 0.9rem;
  }
  
  /* ========== 帖子卡片 ========== */
  .posts-list {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }
  
  .post-card {
    background: white;
    border: 1px solid #e1e4e8;
    border-radius: 12px;
    padding: 1.5rem;
    box-shadow: 0 2px 8px rgba(0,0,0,0.05);
    transition: transform 0.2s, box-shadow 0.2s;
  }
  
  .post-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 16px rgba(0,0,0,0.1);
  }
  
  .post-card.official {
    border-left: 4px solid #ffc107;
    background: #fff8e1;
  }
  
  .post-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 1rem;
  }
  
  .post-meta {
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
  }
  
  .author {
    font-weight: bold;
    color: #2c3e50;
  }
  
  .date {
    font-size: 0.85rem;
    color: #6c757d;
  }
  
  .post-tags {
    display: flex;
    gap: 0.5rem;
    flex-wrap: wrap;
  }
  
  .tag {
    font-size: 0.75rem;
    padding: 2px 8px;
    border-radius: 12px;
    background: #e9ecef;
    color: #495057;
  }
  
  .official-tag {
    background: #ffc107;
    color: #856404;
  }
  
  .user-tag {
    background: #17a2b8;
    color: white;
  }
  
  .post-title {
    margin: 0 0 0.75rem 0;
    color: #2c3e50;
    font-size: 1.2rem;
  }
  
  .post-content {
    color: #495057;
    line-height: 1.6;
    margin-bottom: 1.5rem;
  }
  
  .post-actions {
    display: flex;
    gap: 1rem;
    border-top: 1px solid #e1e4e8;
    padding-top: 1rem;
  }
  
  .action-btn {
    background: none;
    border: none;
    color: #6c757d;
    cursor: pointer;
    font-size: 0.9rem;
    padding: 0.25rem 0.5rem;
    border-radius: 4px;
    transition: background 0.2s;
  }
  
  .action-btn:hover {
    background: #f8f9fa;
    color: #007bff;
  }
  
  /* ========== 发帖编辑器 ========== */
  .post-editor {
    background: white;
    border: 1px solid #e1e4e8;
    border-radius: 12px;
    padding: 2rem;
    box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  }
  
  .title-input {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid #ced4da;
    border-radius: 8px;
    font-size: 1.1rem;
    font-weight: bold;
    margin-bottom: 1rem;
    box-sizing: border-box;
  }
  
  .content-input {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid #ced4da;
    border-radius: 8px;
    font-family: inherit;
    font-size: 1rem;
    resize: vertical;
    margin-bottom: 1.5rem;
    box-sizing: border-box;
  }
  
  .editor-options {
    display: flex;
    flex-direction: column;
    gap: 1rem;
    margin-bottom: 1.5rem;
  }
  
  .editor-options select,
  .editor-options input {
    padding: 0.5rem;
    border: 1px solid #ced4da;
    border-radius: 6px;
    font-size: 0.9rem;
  }
  
  .tags-input {
    width: 100%;
    box-sizing: border-box;
  }
  
  .image-upload {
    display: flex;
    align-items: center;
    gap: 1rem;
  }
  
  #post-image {
    display: none;
  }
  
  .upload-btn {
    padding: 0.5rem 1rem;
    background: #e9ecef;
    border: 1px solid #ced4da;
    border-radius: 6px;
    cursor: pointer;
    font-size: 0.9rem;
  }
  
  .upload-btn:hover {
    background: #dee2e6;
  }
  
  .image-preview {
    color: #28a745;
    font-size: 0.9rem;
  }
  
  .submit-btn {
    width: 100%;
    padding: 0.75rem;
    background: #007bff;
    color: white;
    border: none;
    border-radius: 8px;
    font-size: 1rem;
    font-weight: bold;
    cursor: pointer;
    transition: background 0.2s;
  }
  
  .submit-btn:hover:not(:disabled) {
    background: #0056b3;
  }
  
  .submit-btn:disabled {
    background: #6c757d;
    cursor: not-allowed;
  }
  
  /* ========== 消息列表 ========== */
  .messages-list,
  .notifications-list,
  .likes-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }
  
  .message-item,
  .notification-item,
  .like-item {
    display: flex;
    align-items: center;
    gap: 1rem;
    padding: 1rem;
    background: white;
    border: 1px solid #e1e4e8;
    border-radius: 8px;
    cursor: pointer;
    transition: background 0.2s;
    position: relative;
  }
  
  .message-item:hover,
  .notification-item:hover,
  .like-item:hover {
    background: #f8f9fa;
  }
  
  .notification-item.unread {
    border-left: 3px solid #007bff;
    background: #f0f7ff;
  }
  
  .message-avatar,
  .notif-icon,
  .like-icon {
    font-size: 2rem;
    flex-shrink: 0;
  }
  
  .message-content,
  .notif-content,
  .like-content {
    flex: 1;
  }
  
  .message-header,
  .notif-content p:first-child,
  .like-content p {
    display: flex;
    justify-content: space-between;
    margin-bottom: 0.25rem;
  }
  
  .message-time,
  .notif-time,
  .like-time {
    font-size: 0.85rem;
    color: #6c757d;
  }
  
  .message-preview,
  .notif-text {
    color: #495057;
    font-size: 0.9rem;
    margin: 0;
  }
  
  .unread-dot {
    width: 8px;
    height: 8px;
    background: #dc3545;
    border-radius: 50%;
    flex-shrink: 0;
  }
  
  .mark-read {
    background: #28a745;
    color: white;
    border: none;
    border-radius: 50%;
    width: 24px;
    height: 24px;
    cursor: pointer;
    font-size: 0.8rem;
  }
  
  /* ========== 聊天窗口 ========== */
  .chat-window {
    margin-top: 2rem;
    background: white;
    border: 1px solid #e1e4e8;
    border-radius: 12px;
    overflow: hidden;
  }
  
  .chat-header {
    display: flex;
    align-items: center;
    gap: 1rem;
    padding: 1rem;
    background: #f8f9fa;
    border-bottom: 1px solid #e1e4e8;
  }
  
  .chat-header h3 {
    margin: 0;
    flex: 1;
  }
  
  .chat-messages {
    padding: 1rem;
    height: 300px;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .chat-bubble {
    max-width: 70%;
    padding: 0.75rem;
    border-radius: 12px;
    font-size: 0.9rem;
    line-height: 1.4;
  }
  
  .chat-bubble.received {
    background: #e9ecef;
    align-self: flex-start;
    border-bottom-left-radius: 4px;
  }
  
  .chat-bubble.sent {
    background: #007bff;
    color: white;
    align-self: flex-end;
    border-bottom-right-radius: 4px;
  }
  
  .chat-input {
    display: flex;
    gap: 0.5rem;
    padding: 1rem;
    border-top: 1px solid #e1e4e8;
  }
  
  .chat-input input {
    flex: 1;
    padding: 0.5rem;
    border: 1px solid #ced4da;
    border-radius: 6px;
    font-size: 0.9rem;
  }
  
  .chat-input button {
    padding: 0.5rem 1rem;
    background: #007bff;
    color: white;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-size: 0.9rem;
  }
  
  .chat-input button:hover {
    background: #0056b3;
  }
  
  /* ========== 响应式 ========== */
  @media (max-width: 768px) {
    .community-layout {
      flex-direction: column;
    }
    
    .sidebar {
      width: 100%;
      border-right: none;
      border-bottom: 1px solid #e1e4e8;
      max-height: 200px;
    }
    
    .content-area {
      padding: 1rem;
    }
    
    .view-header {
      flex-direction: column;
      gap: 1rem;
      align-items: flex-start;
    }
    
    .post-actions {
      flex-wrap: wrap;
    }
  }
  </style>