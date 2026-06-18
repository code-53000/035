<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>封场管理</span>
          <div>
            <el-select v-model="timeSlotFilter" placeholder="时段" style="width: 140px; margin-right: 10px" clearable>
              <el-option label="全天封场" value="FULLDAY" />
              <el-option label="上午封场" value="MORNING" />
              <el-option label="下午封场" value="AFTERNOON" />
            </el-select>
            <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" style="width: 300px; margin-right: 10px;" value-format="YYYY-MM-DD" />
            <el-button type="primary" @click="loadData">查询</el-button>
            <el-button type="success" :icon="Plus" style="margin-left: 10px" @click="openAdd">新增封场</el-button>
          </div>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="closureDate" label="封场日期" width="120" />
        <el-table-column prop="timeSlotName" label="封场时段" width="120" />
        <el-table-column prop="statusName" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ row.statusName || statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="封场原因" show-overflow-tooltip />
        <el-table-column prop="createdByName" label="创建人" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 1" type="danger" size="small" @click="handleDelete(row)">取消</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑封场' : '新增封场'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="封场日期" prop="closureDate">
          <el-date-picker v-model="form.closureDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="封场时段" prop="timeSlot">
          <el-select v-model="form.timeSlot" placeholder="选择时段" style="width: 100%">
            <el-option label="全天封场" value="FULLDAY" />
            <el-option label="上午封场" value="MORNING" />
            <el-option label="下午封场" value="AFTERNOON" />
          </el-select>
        </el-form-item>
        <el-form-item label="封场原因" prop="reason">
          <el-input v-model="form.reason" type="textarea" :rows="4" placeholder="请输入封场原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getClosureList, createClosure, updateClosure, deleteClosure } from '../../api/closure'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const timeSlotFilter = ref('')
const dateRange = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref()
const form = reactive({ closureDate: '', timeSlot: 'FULLDAY', reason: '' })
const rules = {
  closureDate: [{ required: true, message: '请选择封场日期', trigger: 'change' }],
  timeSlot: [{ required: true, message: '请选择封场时段', trigger: 'change' }],
  reason: [{ required: true, message: '请输入封场原因', trigger: 'blur' }]
}

const statusText = (s) => ({ 1: '生效', 0: '已取消' }[s] || s)
const statusTagType = (s) => ({ 1: 'success', 0: 'info' }[s] || '')
const timeSlotText = (s) => ({ FULLDAY: '全天封场', MORNING: '上午封场', AFTERNOON: '下午封场' }[s] || s)

const mockClosures = [
  { id: 301, closureDate: '2026-06-22', timeSlot: 'FULLDAY', timeSlotName: '全天封场', status: 1, statusName: '生效', reason: '台风预警', createdByName: '管理员', createTime: '2026-06-18 09:00:00' },
  { id: 302, closureDate: '2026-06-25', timeSlot: 'MORNING', timeSlotName: '上午封场', status: 1, statusName: '生效', reason: '设备检修', createdByName: '管理员', createTime: '2026-06-17 14:30:00' },
  { id: 303, closureDate: '2026-07-01', timeSlot: 'FULLDAY', timeSlotName: '全天封场', status: 0, statusName: '已取消', reason: '内部活动包场', createdByName: '管理员', createTime: '2026-06-16 10:15:00' }
]

const loadData = async () => {
  loading.value = true
  try {
    const params = { page: page.value, pageSize: pageSize.value }
    if (dateRange.value?.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await getClosureList(params)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    let list = JSON.parse(JSON.stringify(mockClosures))
    if (timeSlotFilter.value) list = list.filter(c => c.timeSlot === timeSlotFilter.value)
    total.value = list.length
    const start = (page.value - 1) * pageSize.value
    tableData.value = list.slice(start, start + pageSize.value)
  } finally {
    loading.value = false
  }
}

const openAdd = () => {
  isEdit.value = false
  editId.value = null
  Object.assign(form, { closureDate: '', timeSlot: 'FULLDAY', reason: '' })
  dialogVisible.value = true
}

const openEdit = (row) => {
  isEdit.value = true
  editId.value = row.id
  Object.assign(form, { closureDate: row.closureDate, timeSlot: row.timeSlot, reason: row.reason })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateClosure(editId.value, form)
      ElMessage.success('编辑成功')
    } else {
      await createClosure(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    if (isEdit.value) {
      const t = tableData.value.find(c => c.id === editId.value)
      if (t) Object.assign(t, form, { timeSlotName: timeSlotText(form.timeSlot) })
    } else {
      tableData.value.unshift({ id: Date.now(), ...form, timeSlotName: timeSlotText(form.timeSlot), status: 1, statusName: '生效', createdByName: '当前用户', createTime: new Date().toLocaleString('zh-CN') })
    }
    ElMessage.success(isEdit.value ? '编辑成功（模拟）' : '新增成功（模拟）')
    dialogVisible.value = false
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定取消封场 #${row.id} 吗？`, '提示', { type: 'warning' })
    await deleteClosure(row.id)
    ElMessage.success('取消成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      const t = tableData.value.find(c => c.id === row.id)
      if (t) { t.status = 0; t.statusName = '已取消' }
      ElMessage.success('取消成功（模拟）')
    }
  }
}

onMounted(loadData)
</script>
