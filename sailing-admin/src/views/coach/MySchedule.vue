<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>我的排班</span>
          <div>
            <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" style="width: 300px; margin-right: 10px;" value-format="YYYY-MM-DD" />
            <el-button type="primary" @click="loadData">查询</el-button>
          </div>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="scheduleDate" label="排班日期" width="120" />
        <el-table-column prop="timeSlotName" label="时间段" width="120" />
        <el-table-column prop="onDutyName" label="值班状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isOnDuty === 1 ? 'success' : 'info'">{{ row.onDutyName || (row.isOnDuty === 1 ? '值班' : '休息') }}</el-tag>
          </template>
        </el-table-column>
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
const dateRange = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const mockSchedules = [
  { id: 201, scheduleDate: '2026-06-18', timeSlot: 'MORNING', timeSlotName: '上午', isOnDuty: 1, onDutyName: '值班', remark: '初级课程' },
  { id: 202, scheduleDate: '2026-06-18', timeSlot: 'AFTERNOON', timeSlotName: '下午', isOnDuty: 1, onDutyName: '值班', remark: '进阶训练' },
  { id: 203, scheduleDate: '2026-06-19', timeSlot: 'FULLDAY', timeSlotName: '全天', isOnDuty: 1, onDutyName: '值班', remark: '' },
  { id: 204, scheduleDate: '2026-06-20', timeSlot: 'MORNING', timeSlotName: '上午', isOnDuty: 1, onDutyName: '值班', remark: '单人训练' },
  { id: 205, scheduleDate: '2026-06-15', timeSlot: 'AFTERNOON', timeSlotName: '下午', isOnDuty: 1, onDutyName: '值班', remark: '' },
  { id: 206, scheduleDate: '2026-06-16', timeSlot: 'MORNING', timeSlotName: '上午', isOnDuty: 0, onDutyName: '休息', remark: '请假' }
]

const loadData = async () => {
  loading.value = true
  try {
    const params = { page: page.value, pageSize: pageSize.value }
    if (dateRange.value?.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await getMySchedule(params)
    tableData.value = res.data || []
    total.value = res.data?.length || 0
  } catch (e) {
    tableData.value = mockSchedules
    total.value = mockSchedules.length
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>
