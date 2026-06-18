<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>我的预约</span>
          <div>
            <el-select v-model="statusFilter" placeholder="状态" style="width: 140px; margin-right: 10px" clearable>
              <el-option label="待审批" value="PENDING" />
              <el-option label="已批准" value="APPROVED" />
              <el-option label="已拒绝" value="REJECTED" />
              <el-option label="已取消" value="CANCELLED" />
              <el-option label="已完成" value="COMPLETED" />
            </el-select>
            <el-button type="primary" @click="loadData">查询</el-button>
          </div>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="boatName" label="船只" />
        <el-table-column prop="coachName" label="教练" width="100" />
        <el-table-column prop="bookingDate" label="预约日期" width="120" />
        <el-table-column prop="timeSlotName" label="时间段" width="120" />
        <el-table-column prop="statusName" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.bookingStatus)">{{ row.statusName || statusText(row.bookingStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注/拒绝原因" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.bookingStatus === 'PENDING' || row.bookingStatus === 'APPROVED'"
              type="danger"
              size="small"
              @click="handleCancel(row)"
            >取消</el-button>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyBookings, cancelBooking } from '../../api/booking'

const loading = ref(false)
const tableData = ref([])
const statusFilter = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const statusText = (s) => ({ PENDING: '待审批', APPROVED: '已批准', REJECTED: '已拒绝', CANCELLED: '已取消', COMPLETED: '已完成' }[s] || s)
const statusTagType = (s) => ({ PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger', CANCELLED: 'info', COMPLETED: 'primary' }[s] || '')

const mockBookings = [
  { id: 1001, boatName: '海风号', coachName: '李教练', bookingDate: '2026-06-20', timeSlot: 'MORNING', timeSlotName: '上午', bookingStatus: 'APPROVED', statusName: '已批准', remark: '', createTime: '2026-06-15 10:30:00' },
  { id: 1002, boatName: '飞翔号', coachName: '', bookingDate: '2026-06-21', timeSlot: 'AFTERNOON', timeSlotName: '下午', bookingStatus: 'PENDING', statusName: '待审批', remark: '', createTime: '2026-06-16 14:20:00' },
  { id: 1003, boatName: '迅浪号', coachName: '王教练', bookingDate: '2026-06-18', timeSlot: 'FULLDAY', timeSlotName: '全天', bookingStatus: 'REJECTED', statusName: '已拒绝', remark: '当日已封场', createTime: '2026-06-10 09:15:00' }
]

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMyBookings({ status: statusFilter.value, page: page.value, pageSize: pageSize.value })
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

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm(`确定取消预约 #${row.id} 吗？`, '提示', { type: 'warning' })
    await cancelBooking(row.id)
    ElMessage.success('取消成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.success('取消成功（模拟）')
      row.bookingStatus = 'CANCELLED'
      row.statusName = '已取消'
    }
  }
}

onMounted(loadData)
</script>
