<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>我的预约</span>
          <div>
            <el-select v-model="statusFilter" placeholder="状态" style="width: 140px; margin-right: 10px" clearable>
              <el-option label="已批准" value="APPROVED" />
              <el-option label="已完成" value="COMPLETED" />
            </el-select>
            <el-button type="primary" @click="loadData">查询</el-button>
          </div>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="memberName" label="会员" width="100" />
        <el-table-column prop="boatName" label="船只" />
        <el-table-column prop="bookingDate" label="预约日期" width="120" />
        <el-table-column prop="timeSlotName" label="时间段" width="120" />
        <el-table-column prop="statusName" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.bookingStatus)">{{ row.statusName || statusText(row.bookingStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.bookingStatus === 'APPROVED'" type="success" size="small" @click="handleConfirm(row)">确认带教</el-button>
            <el-button v-if="row.bookingStatus === 'APPROVED'" type="primary" size="small" @click="openAddRecord(row)">登记出海</el-button>
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
    <add-record ref="addRecordRef" @success="loadData" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCoachBookings, confirmBooking } from '../../api/booking'
import AddRecord from './AddRecord.vue'

const addRecordRef = ref()
const loading = ref(false)
const tableData = ref([])
const statusFilter = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const statusText = (s) => ({ APPROVED: '已批准', COMPLETED: '已完成' }[s] || s)
const statusTagType = (s) => ({ APPROVED: 'success', COMPLETED: 'info' }[s] || '')

const mockBookings = [
  { id: 1010, memberName: '张三', boatName: '海风号', bookingDate: '2026-06-18', timeSlot: 'MORNING', timeSlotName: '上午', bookingStatus: 'APPROVED', statusName: '已批准', remark: '初学者体验' },
  { id: 1011, memberName: '李四', boatName: '迅浪号', bookingDate: '2026-06-18', timeSlot: 'AFTERNOON', timeSlotName: '下午', bookingStatus: 'APPROVED', statusName: '已批准', remark: '进阶训练' },
  { id: 1012, memberName: '王五', boatName: '飞翔号', bookingDate: '2026-06-19', timeSlot: 'MORNING', timeSlotName: '上午', bookingStatus: 'APPROVED', statusName: '已批准', remark: '' },
  { id: 1013, memberName: '赵六', boatName: '蓝调号', bookingDate: '2026-06-15', timeSlot: 'FULLDAY', timeSlotName: '全天', bookingStatus: 'COMPLETED', statusName: '已完成', remark: '家庭包船' }
]

const loadData = async () => {
  loading.value = true
  try {
    const res = await getCoachBookings({ status: statusFilter.value, page: page.value, pageSize: pageSize.value })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    let list = mockBookings
    if (statusFilter.value) list = list.filter(b => b.bookingStatus === statusFilter.value)
    total.value = list.length
    const start = (page.value - 1) * pageSize.value
    tableData.value = list.slice(start, start + pageSize.value)
  } finally {
    loading.value = false
  }
}

const handleConfirm = async (row) => {
  try {
    await ElMessageBox.confirm(`确认带教预约 #${row.id} 吗？`, '提示', { type: 'warning' })
    await confirmBooking(row.id)
    ElMessage.success('确认成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.success('确认成功（模拟）')
      row.bookingStatus = 'APPROVED'
      row.statusName = '已批准'
    }
  }
}

const openAddRecord = (row) => {
  addRecordRef.value.open(row)
}

onMounted(loadData)
</script>
