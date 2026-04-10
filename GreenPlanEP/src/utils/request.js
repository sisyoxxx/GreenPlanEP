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
    if (config.method === 'post' || config.method === 'POST') {
      // 重要：确保后端能正确解析 JSON
      config.headers['Content-Type'] = 'application/json'
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器：统一只返回业务数据 response.data
request.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    // 只有网络错误（如 500, 404, 断网）才会进入这里
    return Promise.reject(error)
  }
)

export default request
