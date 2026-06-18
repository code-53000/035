<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>我的排班</span>
          <div>
            <el-date-picker v-model="currentMonth" type="month" placeholder="选择月份" style="width: 200px; margin-right: 10px;" value-format="YYYY-MM" />
            <el-button type="primary" @click="loadData">查询</el-button>
          </div>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="scheduleDate" label="排班日期" width="120" />
        <el-table-column label="时间段" width="160">
          <template #default="{ row }">{{ timeSlotText(row.timeSlot) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assignedBoat" label="分配船只" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMySchedule } from '../../api/coach'

const loading = ref(false)
const tableData = ref([])
const currentMonth = ref(new Date().toISOString().slice(0, 7))
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const statusText = (s) => ({ SCHEDULED: '已排班', IN_PROGRESS: '进行中', COMPLETED: '已完成', CANCELLED: '已取消' }[s] || s)
const statusTagType = (s) => ({ SCHEDULED: '', IN_PROGRESS: 'primary', COMPLETED: 'success', CANCELLED: 'info' }[s] || '')
const timeSlotText = (s) => ({ MORNING: '上午 08:00-12:00', AFTERNOON: '下午 13:00-17:00', FULL: '全天 08:00-17:00' }[s] || s)

const mockSchedules = [
  { id: 201, scheduleDate: '2026-06-18', timeSlot: 'MORNING', status: 'IN_PROGRESS', assignedBoat: '海风号', remark: '初级课程' },
  { id: 202, scheduleDate: '2026-06-18', timeSlot: 'AFTERNOON', status: 'SCHEDULED', assignedBoat: '迅浪号', remark: '进阶训练' },
  { id: 203, scheduleDate: '2026-06-19', timeSlot: 'FULL', status: 'SCHEDULED', assignedBoat: '蓝调号', remark: '' },
  { id: 204, scheduleDate: '2026-06-20', timeSlot: 'MORNING', status: 'SCHEDULED', assignedBoat: '飞翔号', remark: '单人训练' },
  { id: 205, scheduleDate: '2026-06-15', timeSlot: 'AFTERNOON', status: 'COMPLETED', assignedBoat: '勇者号', remark: '' },
  { id: 206, scheduleDate: '2026-06-16', timeSlot: 'MORNING', status: 'COMPLETED', assignedBoat: '阳光号', remark: '休闲体验' }
]

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMySchedule({ month: currentMonth.value, page: page.value, pageSize: pageSize.value })
    tableData.value = res.data?.list || res.data || []
    total.value = res.data?.total || tableData.value.length
  } catch (e) {
    tableData.value = mockSchedules
    total.value = mockSchedules.length
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>
