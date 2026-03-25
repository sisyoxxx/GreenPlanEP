<template>
    <div
      class="draggable-float-button"
      :style="{
        left: `${position.x}px`,
        top: `${position.y}px`,
        cursor: isDragging ? 'grabbing' : 'grab'
      }"
      @mousedown="startDrag"
      @touchstart="startDrag"
    >
      <!-- 按钮内容 -->
      <slot>
        <span>🛒</span>
      </slot>
      <!-- 可选：数量徽标 -->
      <span v-if="badge" class="badge">{{ badge }}</span>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive } from 'vue'
  import { useCart } from '@/stores/cartStore'
  
  const cart = useCart()
// 使用 cart.items, cart.count 等

  const props = defineProps({
    // 初始位置（可选）
    initialX: { type: Number, default: null },
    initialY: { type: Number, default: null },
    // 徽标数字
    badge: { type: [Number, String], default: null }
  })
  
  // 当前位置
  const position = reactive({
    x: props.initialX ?? window.innerWidth - 80,
    y: props.initialY ?? window.innerHeight - 120
  })
  
  const isDragging = ref(false)
  let offsetX = 0
  let offsetY = 0
  
  // 开始拖拽
  const startDrag = (e) => {
    const clientX = e.clientX || e.touches?.[0]?.clientX
    const clientY = e.clientY || e.touches?.[0]?.clientY
  
    if (!clientX || !clientY) return
  
    // 计算鼠标相对于按钮左上角的偏移
    offsetX = clientX - position.x
    offsetY = clientY - position.y
    isDragging.value = true
  
    // 监听移动和释放
    const moveHandler = (e) => {
      if (!isDragging.value) return
      const x = (e.clientX || e.touches?.[0]?.clientX) - offsetX
      const y = (e.clientY || e.touches?.[0]?.clientY) - offsetY
  
      // 边界限制：不能拖出视口
      position.x = Math.max(0, Math.min(window.innerWidth - 60, x))
      position.y = Math.max(0, Math.min(window.innerHeight - 60, y))
    }
  
    const stopHandler = () => {
      isDragging.value = false
      // 自动吸附到左右边缘
      const threshold = window.innerWidth / 3
      if (position.x < threshold) {
        position.x = 10 // 吸附到左边
      } else if (position.x > window.innerWidth - threshold) {
        position.x = window.innerWidth - 70 // 吸附到右边
      }
    }
  
    // 添加事件监听
    document.addEventListener('mousemove', moveHandler)
    document.addEventListener('mouseup', stopHandler)
    document.addEventListener('touchmove', moveHandler, { passive: false })
    document.addEventListener('touchend', stopHandler)
  
    // 清理函数（防止内存泄漏）
    const cleanup = () => {
      document.removeEventListener('mousemove', moveHandler)
      document.removeEventListener('mouseup', stopHandler)
      document.removeEventListener('touchmove', moveHandler)
      document.removeEventListener('touchend', stopHandler)
    }
  
    // 替换旧的 stopHandler 以包含清理
    const newStopHandler = () => {
      stopHandler()
      cleanup()
    }
  
    // 重新绑定带清理的 stopHandler
    document.removeEventListener('mouseup', stopHandler)
    document.removeEventListener('touchend', stopHandler)
    document.addEventListener('mouseup', newStopHandler)
    document.addEventListener('touchend', newStopHandler)
  }
  </script>
  
  <style scoped>
  .draggable-float-button {
    position: fixed;
    width: 60px;
    height: 60px;
    border-radius: 50%;
    background: #4caf50;
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
    z-index: 1000;
    user-select: none;
    transition: left 0.2s ease, top 0.2s ease;
  }
  
  .badge {
    position: absolute;
    top: -8px;
    right: -8px;
    background: #f44336;
    color: white;
    font-size: 12px;
    min-width: 18px;
    height: 18px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 2px;
  }
  </style>