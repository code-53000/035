<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>船只列表</span>
          <div>
            <el-input v-model="keyword" placeholder="搜索船名/型号" style="width: 220px; margin-right: 10px" clearable :prefix-icon="Search" />
            <el-select v-model="statusFilter" placeholder="状态" style="width: 120px; margin-right: 10px" clearable>
              <el-option label="可用" value="AVAILABLE" />
              <el-option label="维护中" value="MAINTENANCE" />
              <el-option label="已预约" value="BOOKED" />
            </el-select>
            <el-button type="primary" :icon="Search" @click="loadData">搜索</el-button>
          </div>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="船名" />
        <el-table-column prop="model" label="型号" />
        <el-table-column prop="capacity" label="容量(人)" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" :icon="Calendar" @click="openBooking(row)" :disabled="row.status !== 'AVAILABLE'">预约</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        style="margin-top: 20px; justify-content: flex-end; display: flex;"
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
      />
    </el-card>
    <apply-booking ref="applyRef" @success="loadData" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Calendar } from '@element-plus/icons-vue'
import { getBoatList } from '../../api/boat'
import ApplyBooking from './ApplyBooking.vue'

const applyRef = ref()
const loading = ref(false)
const tableData = ref([])
const keyword = ref('')
const statusFilter = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const statusText = (s) => ({ AVAILABLE: '可用', MAINTENANCE: '维护中', BOOKED: '已预约' }[s] || s)
const statusTagType = (s) => ({ AVAILABLE: 'success', MAINTENANCE: 'warning', BOOKED: 'info' }[s] || '')

const mockBoats = [
  { id: 1, name: '海风号', model: 'Hobie Cat 16', capacity: 4, status: 'AVAILABLE', description: '双体帆船，适合初学者' },
  { id: 2, name: '飞翔号', model: 'Laser', capacity: 1, status: 'AVAILABLE', description: '单人竞技帆船' },
  { id: 3, name: '蓝调号', model: 'Beneteau 40', capacity: 8, status: 'MAINTENANCE', description: '豪华巡航艇，维护保养中' },
  { id: 4, name: '迅浪号', model: '470', capacity: 2, status: 'AVAILABLE', description: '双人奥运级帆船' },
  { id: 5, name: '阳光号', model: 'Catalina 22', capacity: 6, status: 'BOOKED', description: '休闲巡航帆船' },
  { id: 6, name: '勇者号', model: 'Finn', capacity: 1, status: 'AVAILABLE', description: '单人重量级帆船' }
]

const loadData = async () => {
  loading.value = true
  try {
    const res = await getBoatList({ keyword: keyword.value, status: statusFilter.value, page: page.value, pageSize: pageSize.value })
    tableData.value = res.data?.list || res.data || []
    total.value = res.data?.total || tableData.value.length
  } catch (e) {
    let list = mockBoats
    if (keyword.value) list = list.filter(b => b.name.includes(keyword.value) || b.model.includes(keyword.value))
    if (statusFilter.value) list = list.filter(b => b.status === statusFilter.value)
    total.value = list.length
    const start = (page.value - 1) * pageSize.value
    tableData.value = list.slice(start, start + pageSize.value)
  } finally {
    loading.value = false
  }
}

const openBooking = (boat) => {
  applyRef.value.open(boat)
}

onMounted(loadData)
</script>
