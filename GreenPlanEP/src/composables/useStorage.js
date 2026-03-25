// src/composables/useStorage.js
// 同步数据至localStorage
import { ref, watch } from 'vue'

export function useStorage(key, initialValue) {
  // 1. 从 localStorage 读取初始值
  const storedValue = localStorage.getItem(key)
  const value = ref(storedValue ? JSON.parse(storedValue) : initialValue)

  // 2. 监听变化，自动写入 localStorage
  watch(
    value,
    (newValue) => {
      localStorage.setItem(key, JSON.stringify(newValue))
    },
    { deep: true } // 深度监听对象内部变化
  )

  return value
}