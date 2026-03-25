/**
 * 商品相关静态配置
 * 包含：分类映射、图标、月份、地区等不常变动的数据
 */

// 1. 商品分类映射 (Key 对应数据库 category 字段，Value 对应显示名称)
export const CATEGORY_MAP = {
    FLOWER_SEEDS: '花卉种子',
    VEGETABLE_SEEDS: '蔬菜种子',
    HERB_PLANTS: '草本植物',
    SUCCULENT_PLANTS: '多肉植物',
    PLANTING_TOOLS: '种植工具',
    NUTRIENT_FERTILIZERS: '营养肥料'
  }
  
// 2. 分类图标映射 (Key 必须与 CATEGORY_MAP 的 Key 一致)
export const CATEGORY_ICONS = {
  FLOWER_SEEDS: '🌸',
  VEGETABLE_SEEDS: '🥦',
  HERB_PLANTS: '🌿',
  SUCCULENT_PLANTS: '🌵',
  PLANTING_TOOLS: '🛠️',
  NUTRIENT_FERTILIZERS: '💊'
}
  
// 3. 种植月份列表
export const PLANTING_MONTHS = [
  '1月', '2月', '3月', '4月', '5月', '6月', 
  '7月', '8月', '9月', '10月', '11月', '12月'
]
  
// 4. 适宜地区列表
export const SUITABLE_REGIONS = [
  '华北', '华东', '华南', '华中', '西南', '西北', '东北', '全国'
]
  
// 5. 难度等级文本映射 (辅助工具)
export const DIFFICULTY_MAP = {
  1: '简单',
  2: '较易',
  3: '中等',
  4: '较难',
  5: '困难'
}
  
// 6. 默认图标 (当分类没有匹配到图标时使用)
export const DEFAULT_ICON = '🌱'

