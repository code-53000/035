<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>我的出海记录</span>
          <div>
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 300px; margin-right: 10px;"
              value-format="YYYY-MM-DD"
            />
            <el-button type="primary" @click="loadData">查询</el-button>
          </div>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="boatName" label="船只" />
        <el-table-column prop="coachName" label="教练" width="100" />
        <el-table-column prop="sailDate" label="出海日期" width="120" />
        <el-table-column prop="startTime" label="出海时间" width="120" />
        <el-table-column prop="endTime" label="返航时间" width="120" />
        <el-table-column prop="duration" label="时长(小时)" width="110" />
        <el-table-column prop="weather" label="天气" width="100" />
        <el-table-column prop="seaState" label="海况" width="100" />
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
import { getMyRecords } from '../../api/record'

const loading = ref(false)
const tableData = ref([])
const dateRange = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const mockRecords = [
  { id: 5001, boatName: '海风号', coachName: '李教练', sailDate: '2026-06-10', startTime: '08:30', endTime: '12:00', duration: 3.5, weather: '晴', seaState: '平稳', remark: '初学体验，风况良好' },
  { id: 5002, boatName: '飞翔号', coachName: '', sailDate: '2026-06-12', startTime: '14:00', endTime: '17:30', duration: 3.5, weather: '多云', seaState: '微浪', remark: '单人训练' },
  { id: 5003, boatName: '迅浪号', coachName: '王教练', sailDate: '2026-06-14', startTime: '09:00', endTime: '16:00', duration: 7, weather: '晴', seaState: '轻浪', remark: '全天训练课程' }
]

const loadData = async () => {
  loading.value = true
  try {
    const params = { page: page.value, pageSize: pageSize.value }
    if (dateRange.value?.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await getMyRecords(params)
    tableData.value = res.data?.list || res.data || []
    total.value = res.data?.total || tableData.value.length
  } catch (e) {
    tableData.value = mockRecords
    total.value = mockRecords.length
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>
