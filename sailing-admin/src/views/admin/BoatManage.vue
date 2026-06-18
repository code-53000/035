<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>船只管理</span>
          <div>
            <el-input v-model="keyword" placeholder="搜索船名/型号" style="width: 220px; margin-right: 10px" clearable />
            <el-button type="primary" @click="loadData">查询</el-button>
            <el-button type="success" :icon="Plus" style="margin-left: 10px" @click="openAdd">新增船只</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑船只' : '新增船只'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="船名" prop="name">
          <el-input v-model="form.name" placeholder="请输入船名" />
        </el-form-item>
        <el-form-item label="型号" prop="model">
          <el-input v-model="form.model" placeholder="请输入型号" />
        </el-form-item>
        <el-form-item label="容量" prop="capacity">
          <el-input-number v-model="form.capacity" :min="1" :max="50" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="选择状态" style="width: 100%">
            <el-option label="可用" value="AVAILABLE" />
            <el-option label="维护中" value="MAINTENANCE" />
            <el-option label="已预约" value="BOOKED" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="描述信息" />
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
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { onMounted } from 'vue'
import { getBoatList, createBoat, updateBoat, deleteBoat } from '../../api/boat'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const keyword = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const editId = ref(null)
const form = reactive({ name: '', model: '', capacity: 1, status: 'AVAILABLE', description: '' })
const rules = {
  name: [{ required: true, message: '请输入船名', trigger: 'blur' }],
  model: [{ required: true, message: '请输入型号', trigger: 'blur' }],
  capacity: [{ required: true, message: '请输入容量', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const statusText = (s) => ({ AVAILABLE: '可用', MAINTENANCE: '维护中', BOOKED: '已预约' }[s] || s)
const statusTagType = (s) => ({ AVAILABLE: 'success', MAINTENANCE: 'warning', BOOKED: 'info' }[s] || '')

const mockBoats = [
  { id: 1, name: '海风号', model: 'Hobie Cat 16', capacity: 4, status: 'AVAILABLE', description: '双体帆船，适合初学者' },
  { id: 2, name: '飞翔号', model: 'Laser', capacity: 1, status: 'AVAILABLE', description: '单人竞技帆船' },
  { id: 3, name: '蓝调号', model: 'Beneteau 40', capacity: 8, status: 'MAINTENANCE', description: '豪华巡航艇' },
  { id: 4, name: '迅浪号', model: '470', capacity: 2, status: 'AVAILABLE', description: '双人奥运级帆船' },
  { id: 5, name: '阳光号', model: 'Catalina 22', capacity: 6, status: 'BOOKED', description: '休闲巡航帆船' },
  { id: 6, name: '勇者号', model: 'Finn', capacity: 1, status: 'AVAILABLE', description: '单人重量级帆船' }
]

const loadData = async () => {
  loading.value = true
  try {
    const res = await getBoatList({ keyword: keyword.value, page: page.value, pageSize: pageSize.value })
    tableData.value = res.data?.list || res.data || []
    total.value = res.data?.total || tableData.value.length
  } catch (e) {
    let list = JSON.parse(JSON.stringify(mockBoats))
    if (keyword.value) list = list.filter(b => b.name.includes(keyword.value) || b.model.includes(keyword.value))
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
  Object.assign(form, { name: '', model: '', capacity: 1, status: 'AVAILABLE', description: '' })
  dialogVisible.value = true
}

const openEdit = (row) => {
  isEdit.value = true
  editId.value = row.id
  Object.assign(form, { name: row.name, model: row.model, capacity: row.capacity, status: row.status, description: row.description || '' })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateBoat(editId.value, form)
      ElMessage.success('编辑成功')
    } else {
      await createBoat(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    if (isEdit.value) {
      const t = tableData.value.find(b => b.id === editId.value)
      if (t) Object.assign(t, form)
    } else {
      tableData.value.unshift({ id: Date.now(), ...form })
    }
    ElMessage.success(isEdit.value ? '编辑成功（模拟）' : '新增成功（模拟）')
    dialogVisible.value = false
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除船只【${row.name}】吗？`, '提示', { type: 'warning' })
    await deleteBoat(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      tableData.value = tableData.value.filter(b => b.id !== row.id)
      ElMessage.success('删除成功（模拟）')
    }
  }
}

onMounted(loadData)
</script>
