<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>封场管理</span>
          <div>
            <el-select v-model="typeFilter" placeholder="类型" style="width: 140px; margin-right: 10px" clearable>
              <el-option label="全天封场" value="FULL" />
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
        <el-table-column label="封场时段" width="160">
          <template #default="{ row }">{{ typeText(row.type) }}</template>
        </el-table-column>
        <el-table-column prop="reason" label="封场原因" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="170" />
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑封场' : '新增封场'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="封场日期" prop="closureDate">
          <el-date-picker v-model="form.closureDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="封场类型" prop="type">
          <el-select v-model="form.type" placeholder="选择类型" style="width: 100%">
            <el-option label="全天封场" value="FULL" />
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
const typeFilter = ref('')
const dateRange = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref()
const form = reactive({ closureDate: '', type: 'FULL', reason: '' })
const rules = {
  closureDate: [{ required: true, message: '请选择封场日期', trigger: 'change' }],
  type: [{ required: true, message: '请选择封场类型', trigger: 'change' }],
  reason: [{ required: true, message: '请输入封场原因', trigger: 'blur' }]
}

const typeText = (s) => ({ FULL: '全天封场', MORNING: '上午封场', AFTERNOON: '下午封场' }[s] || s)

const mockClosures = [
  { id: 301, closureDate: '2026-06-22', type: 'FULL', reason: '台风预警', createTime: '2026-06-18 09:00:00' },
  { id: 302, closureDate: '2026-06-25', type: 'MORNING', reason: '设备检修', createTime: '2026-06-17 14:30:00' },
  { id: 303, closureDate: '2026-07-01', type: 'FULL', reason: '内部活动包场', createTime: '2026-06-16 10:15:00' }
]

const loadData = async () => {
  loading.value = true
  try {
    const params = { type: typeFilter.value, page: page.value, pageSize: pageSize.value }
    if (dateRange.value?.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await getClosureList(params)
    tableData.value = res.data?.list || res.data || []
    total.value = res.data?.total || tableData.value.length
  } catch (e) {
    let list = JSON.parse(JSON.stringify(mockClosures))
    if (typeFilter.value) list = list.filter(c => c.type === typeFilter.value)
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
  Object.assign(form, { closureDate: '', type: 'FULL', reason: '' })
  dialogVisible.value = true
}

const openEdit = (row) => {
  isEdit.value = true
  editId.value = row.id
  Object.assign(form, { closureDate: row.closureDate, type: row.type, reason: row.reason })
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
      if (t) Object.assign(t, form)
    } else {
      tableData.value.unshift({ id: Date.now(), ...form, createTime: new Date().toLocaleString('zh-CN') })
    }
    ElMessage.success(isEdit.value ? '编辑成功（模拟）' : '新增成功（模拟）')
    dialogVisible.value = false
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除封场 #${row.id} 吗？`, '提示', { type: 'warning' })
    await deleteClosure(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      tableData.value = tableData.value.filter(c => c.id !== row.id)
      ElMessage.success('删除成功（模拟）')
    }
  }
}

onMounted(loadData)
</script>
