<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>教练排班管理</span>
          <div>
            <el-select v-model="coachFilter" placeholder="选择教练" style="width: 160px; margin-right: 10px" clearable>
              <el-option v-for="c in coachList" :key="c.id" :label="c.realName || c.name" :value="c.id" />
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
        <el-table-column prop="timeSlotName" label="时间段" width="120" />
        <el-table-column prop="onDutyName" label="值班状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isOnDuty === 1 ? 'success' : 'info'">{{ row.onDutyName || (row.isOnDuty === 1 ? '值班' : '休息') }}</el-tag>
          </template>
        </el-table-column>
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
            <el-option v-for="c in coachList" :key="c.id" :label="c.realName || c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="排班日期" prop="scheduleDate">
          <el-date-picker v-model="form.scheduleDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="时间段" prop="timeSlot">
          <el-select v-model="form.timeSlot" placeholder="选择时间段" style="width: 100%">
            <el-option label="上午" value="MORNING" />
            <el-option label="下午" value="AFTERNOON" />
            <el-option label="全天" value="FULLDAY" />
          </el-select>
        </el-form-item>
        <el-form-item label="值班状态" prop="isOnDuty">
          <el-select v-model="form.isOnDuty" placeholder="选择状态" style="width: 100%">
            <el-option label="值班" :value="1" />
            <el-option label="休息" :value="0" />
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
import { getCoachScheduleList, addSchedule, updateSchedule, deleteSchedule, getCoachList } from '../../api/coach'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const coachFilter = ref('')
const dateRange = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const coachList = ref([])

const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref()
const form = reactive({ coachId: '', scheduleDate: '', timeSlot: 'MORNING', isOnDuty: 1, remark: '' })
const rules = {
  coachId: [{ required: true, message: '请选择教练', trigger: 'change' }],
  scheduleDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
  timeSlot: [{ required: true, message: '请选择时间段', trigger: 'change' }],
  isOnDuty: [{ required: true, message: '请选择值班状态', trigger: 'change' }]
}

const timeSlotText = (s) => ({ MORNING: '上午', AFTERNOON: '下午', FULLDAY: '全天' }[s] || s)
const onDutyText = (s) => ({ 1: '值班', 0: '休息' }[s] || s)

const mockSchedules = [
  { id: 201, coachId: 1, coachName: '李教练', scheduleDate: '2026-06-18', timeSlot: 'MORNING', timeSlotName: '上午', isOnDuty: 1, onDutyName: '值班', remark: '初级课程' },
  { id: 202, coachId: 2, coachName: '王教练', scheduleDate: '2026-06-18', timeSlot: 'AFTERNOON', timeSlotName: '下午', isOnDuty: 1, onDutyName: '值班', remark: '进阶训练' },
  { id: 203, coachId: 1, coachName: '李教练', scheduleDate: '2026-06-19', timeSlot: 'FULLDAY', timeSlotName: '全天', isOnDuty: 1, onDutyName: '值班', remark: '' },
  { id: 204, coachId: 3, coachName: '赵教练', scheduleDate: '2026-06-20', timeSlot: 'MORNING', timeSlotName: '上午', isOnDuty: 0, onDutyName: '休息', remark: '请假' }
]

const loadCoachList = async () => {
  try {
    const res = await getCoachList()
    coachList.value = res.data || []
  } catch (e) {
    coachList.value = [
      { id: 1, name: '李教练', realName: '李教练' },
      { id: 2, name: '王教练', realName: '王教练' },
      { id: 3, name: '赵教练', realName: '赵教练' },
      { id: 4, name: '钱教练', realName: '钱教练' }
    ]
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { coachId: coachFilter.value, page: page.value, pageSize: pageSize.value }
    if (dateRange.value?.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await getCoachScheduleList(params)
    tableData.value = res.data?.records || []
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
  Object.assign(form, { coachId: '', scheduleDate: '', timeSlot: 'MORNING', isOnDuty: 1, remark: '' })
  dialogVisible.value = true
}

const openEdit = (row) => {
  isEdit.value = true
  editId.value = row.id
  Object.assign(form, { coachId: row.coachId, scheduleDate: row.scheduleDate, timeSlot: row.timeSlot, isOnDuty: row.isOnDuty, remark: row.remark || '' })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  const coachName = coachList.value.find(c => c.id === form.coachId)?.realName || coachList.value.find(c => c.id === form.coachId)?.name || ''
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
      if (t) Object.assign(t, form, { coachName, timeSlotName: timeSlotText(form.timeSlot), onDutyName: onDutyText(form.isOnDuty) })
    } else {
      tableData.value.unshift({ id: Date.now(), ...form, coachName, timeSlotName: timeSlotText(form.timeSlot), onDutyName: onDutyText(form.isOnDuty) })
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

onMounted(() => {
  loadCoachList()
  loadData()
})
</script>
