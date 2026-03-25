// stores/cartStore.js
import { defineStore } from 'pinia'

export const useCart = defineStore('cart', {
  state: () => ({
    items: []
  }),
  getters: {
    count: (state) => state.items.reduce((sum, i) => sum + i.quantity, 0),
    total: (state) => state.items.reduce((sum, i) => sum + i.price * i.quantity, 0)
  },
  actions: {
    add(product) {
      const existing = this.items.find(i => i.id === product.id)
      if (existing) existing.quantity++
      else this.items.push({ ...product, quantity: 1 })
    },
    // ===== 移出购物车=====
    remove(productId) {
      // 根据商品 ID 删除整个条目
      this.items = this.items.filter(item => item.id !== productId)
    },

    // ===== 可选：减少数量（点“-”按钮用）=====
    decreaseQuantity(productId) {
      const item = this.items.find(i => i.id === productId)
      if (item) {
        if (item.quantity > 1) {
          item.quantity--
        } else {
          // 如果只剩 1 件，直接移除
          this.remove(productId)
        }
      }
    },

    // ===== 清空购物车 =====
    clear() {
      this.items = []
    }
  
  }
})