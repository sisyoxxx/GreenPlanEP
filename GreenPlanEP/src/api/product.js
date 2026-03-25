// src/api/product.js
import request from '@/utils/request'

/**
 * 获取产品列表 (带分页和筛选)
 * @param {Object} params 查询参数 { page, pageSize, keyword, category, month, region }
 */
export function getProductList(params) {
    // 参数清洗
  const cleanParams = {
    keyword: params.keyword ? params.keyword.trim() : undefined,
    category: params.category || undefined,
    // 前端传 "3月"，这里转成 "3"
    month: params.month ? params.month.replace('月', '') : undefined,
    // '全国' 视为不筛选
    region: params.region === '全国' ? undefined : (params.region || undefined),
    page: params.page,
    pageSize: params.pageSize
  }
  
  return request({
    url: '/api/products',
    method: 'get',
    params: cleanParams 
  })
}

/**
 * 获取产品详情
 */
export function getProductDetail(id) {
  return request({
    url: `/api/products/${id}`,
    method: 'get'
  })
}

/**
 * 新增产品
 */
export function addProduct(data) {
  return request({
    url: '/api/products',
    method: 'post',
    data
  })
}