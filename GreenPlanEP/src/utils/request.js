// src/utils/request.js
import axios from 'axios'

// 创建 axios 实例
const request = axios.create({
  baseURL: 'http://localhost:8080', // 👈 你的后端 Spring Boot 地址
  timeout: 5000, // 请求超时时间
})

// 请求拦截器 (以后加 Token 在这里加)
request.interceptors.request.use(
  config => {
    // 例如：config.headers.Authorization = 'Bearer ' + token
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器 (处理后端统一返回格式 ApiResponse)
  request.interceptors.response.use(
    response => {
      const res = response.data

      // 如果后端返回的是 { code: xxx, message: xxx, data: xxx } 格式
      if (typeof res.code !== 'undefined') {
        if (res.code === 200 || res.code === '200') {
          return res.data // 只返回 data 部分，简化前端调用
        } else {
          // 业务错误，直接抛出，让前端 catch 捕获
          return Promise.reject(new Error(res.message || 'Request error'))
        }
      }

      // 如果没有 code 字段，直接返回整个 response.data
      return res
    },
    error => {
      return Promise.reject(error)
    }
  )

export default request