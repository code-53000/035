<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>教练排班管理</span>
          <div>
            <el-select v-model="coachFilter" placeholder="选择教练" style="width: 160px; margin-right: 10px" clearable>
              <el-option v-for="c in coachList" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
            <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" style="width: 300px; margin-right: 10px;" value-format="YYYY-MM-DD" />
            <el-button type="primary" @click="loadData">查询</el-button>
            <el-button type="success" :icon="Plus" style="margin-left: 10px" @click="openAdd">新增排班</el-button>
          </div>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="coachName" label="教练" width="100" />
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
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑排班' : '新增排班'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="教练" prop="coachId">
          <el-select v-model="form.coachId" placeholder="选择教练" style="width: 100%">
            <el-option v-for="c in coachList" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="排班日期" prop="scheduleDate">
          <el-date-picker v-model="form.scheduleDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="时间段" prop="timeSlot">
          <el-select v-model="form.timeSlot" placeholder="选择时间段" style="width: 100%">
            <el-option label="上午 08:00 - 12:00" value="MORNING" />
            <el-option label="下午 13:00 - 17:00" value="AFTERNOON" />
            <el-option label="全天 08:00 - 17:00" value="FULL" />
          </el-select>
        </el-form-item>
        <el-form-item label="分配船只">
          <el-select v-model="form.boatId" placeholder="选择船只" style="width: 100%" clearable>
            <el-option v-for="b in boatList" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" placeholder="选择状态" style="width: 100%">
            <el-option label="已排班" value="SCHEDULED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="备注信息" />
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
import { addSchedule, updateSchedule, deleteSchedule } from '../../api/coach'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const coachFilter = ref('')
const dateRange = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const coachList = [
  { id: 1, name: '李教练' },
  { id: 2, name: '王教练' },
  { id: 3, name: '赵教练' },
  { id: 4, name: '钱教练' }
]
const boatList = [
  { id: 1, name: '海风号' },
  { id: 2, name: '飞翔号' },
  { id: 3, name: '蓝调号' },
  { id: 4, name: '迅浪号' }
]

const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref()
const form = reactive({ coachId: '', scheduleDate: '', timeSlot: 'MORNING', boatId: '', status: 'SCHEDULED', remark: '' })
const rules = {
  coachId: [{ required: true, message: '请选择教练', trigger: 'change' }],
  scheduleDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
  timeSlot: [{ required: true, message: '请选择时间段', trigger: 'change' }]
}

const statusText = (s) => ({ SCHEDULED: '已排班', IN_PROGRESS: '进行中', COMPLETED: '已完成', CANCELLED: '已取消' }[s] || s)
const statusTagType = (s) => ({ SCHEDULED: '', IN_PROGRESS: 'primary', COMPLETED: 'success', CANCELLED: 'info' }[s] || '')
const timeSlotText = (s) => ({ MORNING: '上午 08:00-12:00', AFTERNOON: '下午 13:00-17:00', FULL: '全天 08:00-17:00' }[s] || s)

const mockSchedules = [
  { id: 201, coachId: 1, coachName: '李教练', scheduleDate: '2026-06-18', timeSlot: 'MORNING', status: 'IN_PROGRESS', boatId: 1, assignedBoat: '海风号', remark: '初级课程' },
  { id: 202, coachId: 2, coachName: '王教练', scheduleDate: '2026-06-18', timeSlot: 'AFTERNOON', status: 'SCHEDULED', boatId: 4, assignedBoat: '迅浪号', remark: '进阶训练' },
  { id: 203, coachId: 1, coachName: '李教练', scheduleDate: '2026-06-19', timeSlot: 'FULL', status: 'SCHEDULED', boatId: 3, assignedBoat: '蓝调号', remark: '' },
  { id: 204, coachId: 3, coachName: '赵教练', scheduleDate: '2026-06-20', timeSlot: 'MORNING', status: 'SCHEDULED', boatId: 2, assignedBoat: '飞翔号', remark: '单人训练' }
]

const loadData = async () => {
  loading.value = true
  try {
    const params = { coachId: coachFilter.value, page: page.value, pageSize: pageSize.value }
    if (dateRange.value?.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await (async () => { throw new Error('mock') })()
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (e) {
    let list = JSON.parse(JSON.stringify(mockSchedules))
    if (coachFilter.value) list = list.filter(s => s.coachId === coachFilter.value)
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
  Object.assign(form, { coachId: '', scheduleDate: '', timeSlot: 'MORNING', boatId: '', status: 'SCHEDULED', remark: '' })
  dialogVisible.value = true
}

const openEdit = (row) => {
  isEdit.value = true
  editId.value = row.id
  Object.assign(form, { coachId: row.coachId, scheduleDate: row.scheduleDate, timeSlot: row.timeSlot, boatId: row.boatId || '', status: row.status, remark: row.remark || '' })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  const coachName = coachList.find(c => c.id === form.coachId)?.name || ''
  const assignedBoat = boatList.find(b => b.id === form.boatId)?.name || ''
  try {
    if (isEdit.value) {
      await updateSchedule(editId.value, form)
      ElMessage.success('编辑成功')
    } else {
      await addSchedule(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    if (isEdit.value) {
      const t = tableData.value.find(s => s.id === editId.value)
      if (t) Object.assign(t, form, { coachName, assignedBoat })
    } else {
      tableData.value.unshift({ id: Date.now(), ...form, coachName, assignedBoat })
    }
    ElMessage.success(isEdit.value ? '编辑成功（模拟）' : '新增成功（模拟）')
    dialogVisible.value = false
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除排班 #${row.id} 吗？`, '提示', { type: 'warning' })
    await deleteSchedule(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      tableData.value = tableData.value.filter(s => s.id !== row.id)
      ElMessage.success('删除成功（模拟）')
    }
  }
}

onMounted(loadData)
</script>
