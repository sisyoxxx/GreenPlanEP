// src/stores/productStore.js
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getProductList, getProductDetail } from '@/api/product'

export const useProductStore = defineStore('product', () => {
  // ===== State (状态) =====
  const productList = ref([])       
  const total = ref(0)
  const loading = ref(false)
  const error = ref(null)
  
  // 记录当前筛选条件，方便其他组件知道当前状态
  const currentFilters = ref({
    page: 1,
    pageSize: 20,
    keyword: '',
    category: null,
    month: null,
    region: null
  })

  // ===== Actions (动作) =====
  
  /**
   * 获取商品列表
   * @param {Object} params 覆盖默认筛选条件的参数
   */
  async function fetchProducts(params = {}) {
    loading.value = true
    error.value = null
    
    try {
      // 1. 合并参数：使用当前存储的条件 + 新传入的条件
      const queryParams = { ...currentFilters.value, ...params }
      
      // 2. 更新当前记录的条件 (用于翻页时保持其他筛选条件不变)
      currentFilters.value = queryParams

      // 3. 调用 API (参数清洗在 api/product.js 中自动完成)
      const res = await getProductList(queryParams)

      // 4. 统一处理数据结构 (兼容多种后端返回格式)
      let dataList = []
      let totalCount = 0

      // 假设 request 拦截器已解包 data，如果没解包，可能需要 res.data
      let rawData = res
      
      // 如果后端返回包含 code 字段，提取 data
      if (rawData && typeof rawData.code !== 'undefined') {
        if (rawData.code !== 200 && rawData.code !== '200') {
          throw new Error(rawData.message || '业务错误')
        }
        rawData = rawData.data || rawData
      }

      // 解析列表和总数
      if (Array.isArray(rawData)) {
        dataList = rawData
        totalCount = rawData.length
      } else if (rawData && rawData.list) {
        dataList = rawData.list
        totalCount = rawData.total || 0
      } else if (rawData && rawData.records) {
        dataList = rawData.records
        totalCount = rawData.total || 0
      }

      // 5. 更新 State
      // 如果是第1页，替换列表；如果是加载更多，追加列表 (可选策略)
      if (queryParams.page === 1) {
        productList.value = dataList
      } else {
        productList.value = [...productList.value, ...dataList]
      }
      total.value = totalCount

      return { list: dataList, total: totalCount }

    } catch (err) {
      console.error('❌ [Store] 获取商品失败:', err)
      error.value = err.message
      throw err // 抛出错误让组件感知 (用于弹窗提示)
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取商品详情
   */
  async function fetchProductDetail(id) {
    loading.value = true
    try {
      const res = await getProductDetail(id)
      // 同样处理可能的 code 包裹
      let data = res
      if (res && typeof res.code !== 'undefined') {
        if (res.code !== 200) throw new Error(res.message)
        data = res.data || res
      }
      return data
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * 重置筛选条件并刷新
   */
  function resetAndFetch() {
    currentFilters.value = {
      page: 1,
      pageSize: 20,
      keyword: '',
      category: null,
      month: null,
      region: null
    }
    return fetchProducts({ page: 1 })
  }

  return {
    productList,
    total,
    loading,
    error,
    currentFilters,
    fetchProducts,
    fetchProductDetail,
    resetAndFetch
  }
})