// src/stores/gardenStore.js
import { defineStore } from 'pinia'
import { computed } from 'vue'
import { useStorage } from '@/composables/useStorage'

// 生长阶段枚举
const GROWTH_STAGES = ['种子期', '发芽期', '幼苗期', '生长期', '开花期', '结果期', '枯萎期']

export const useGardenStore = defineStore('garden', () => {
  // 使用自定义 Composable 进行持久化
  // 初始数据示例
  const initialData = [
    {
      id: 1,
      name: '小番茄',
      species: '樱桃红',
      sowDate: '2025-04-01',
      stage: '结果期',
      records: [
        { id: 101, date: '2025-04-05', note: '发芽了！两片嫩叶', image: 'https://via.placeholder.com/80x60?text=Sprout' }
      ]
    }
  ]

  const plants = useStorage('my-garden-plants', initialData)
  const selectedPlantId = useStorage('my-garden-selected-id', null)

  // Getters
  const selectedPlant = computed(() => 
    plants.value.find(p => p.id === selectedPlantId.value) || null
  )

  const growthStages = computed(() => GROWTH_STAGES)

  // Actions
  function addPlant(plantData) {
    const newPlant = {
      id: Date.now(),
      ...plantData,
      stage: '种子期', // 默认阶段
      records: []
    }
    plants.value.push(newPlant)
    selectedPlantId.value = newPlant.id // 自动选中
    return newPlant
  }

  function updatePlant(id, updates) {
    const plant = plants.value.find(p => p.id === id)
    if (plant) {
      Object.assign(plant, updates)
    }
  }

  function deletePlant(id) {
    const index = plants.value.findIndex(p => p.id === id)
    if (index !== -1) {
      plants.value.splice(index, 1)
      // 如果删除的是当前选中的，则清空选中状态
      if (selectedPlantId.value === id) {
        selectedPlantId.value = plants.value.length > 0 ? plants.value[0].id : null
      }
    }
  }

  function addRecord(plantId, recordData) {
    const plant = plants.value.find(p => p.id === plantId)
    if (plant) {
      const newRecord = {
        id: Date.now(),
        date: new Date().toISOString().split('T')[0],
        ...recordData
      }
      // 添加到开头
      plant.records.unshift(newRecord)
    }
  }

  function updateRecord(plantId, recordId, updates) {
    const plant = plants.value.find(p => p.id === plantId)
    if (plant) {
      const record = plant.records.find(r => r.id === recordId)
      if (record) {
        Object.assign(record, updates)
      }
    }
  }

  function deleteRecord(plantId, recordId) {
    const plant = plants.value.find(p => p.id === plantId)
    if (plant) {
      plant.records = plant.records.filter(r => r.id !== recordId)
    }
  }

  function selectPlant(id) {
    selectedPlantId.value = id
  }

  return {
    plants,
    selectedPlantId,
    selectedPlant,
    growthStages,
    addPlant,
    updatePlant,
    deletePlant,
    addRecord,
    updateRecord,
    deleteRecord,
    selectPlant
  }
})