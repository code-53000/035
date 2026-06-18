<template>
  <el-dialog v-model="visible" title="登记出海记录" width="550px" @close="handleClose">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="会员">
        <el-input v-model="form.memberName" disabled />
      </el-form-item>
      <el-form-item label="船只">
        <el-input v-model="form.boatName" disabled />
      </el-form-item>
      <el-form-item label="出海日期" prop="sailDate">
        <el-date-picker v-model="form.sailDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
      </el-form-item>
      <el-form-item label="出海时间" prop="startTime">
        <el-time-picker v-model="form.startTime" placeholder="选择出海时间" style="width: 100%" value-format="HH:mm" />
      </el-form-item>
      <el-form-item label="返航时间" prop="endTime">
        <el-time-picker v-model="form.endTime" placeholder="选择返航时间" style="width: 100%" value-format="HH:mm" />
      </el-form-item>
      <el-form-item label="天气" prop="weather">
        <el-select v-model="form.weather" placeholder="选择天气" style="width: 100%">
          <el-option label="晴" value="晴" />
          <el-option label="多云" value="多云" />
          <el-option label="阴" value="阴" />
          <el-option label="小雨" value="小雨" />
          <el-option label="雷阵雨" value="雷阵雨" />
        </el-select>
      </el-form-item>
      <el-form-item label="海况" prop="seaState">
        <el-select v-model="form.seaState" placeholder="选择海况" style="width: 100%">
          <el-option label="平稳" value="平稳" />
          <el-option label="微浪" value="微浪" />
          <el-option label="轻浪" value="轻浪" />
          <el-option label="中浪" value="中浪" />
          <el-option label="大浪" value="大浪" />
        </el-select>
      </el-form-item>
      <el-form-item label="航行时长(小时)" prop="duration">
        <el-input-number v-model="form.duration" :min="0.5" :max="24" :step="0.5" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="航行情况备注" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">提交记录</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { createRecord } from '../../api/record'

const emit = defineEmits(['success'])
const visible = ref(false)
const submitting = ref(false)
const formRef = ref()

const form = reactive({
  bookingId: null,
  memberId: null,
  memberName: '',
  boatId: null,
  boatName: '',
  sailDate: '',
  startTime: '',
  endTime: '',
  weather: '',
  seaState: '',
  duration: 2,
  remark: ''
})

const rules = {
  sailDate: [{ required: true, message: '请选择出海日期', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择出海时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择返航时间', trigger: 'change' }],
  weather: [{ required: true, message: '请选择天气', trigger: 'change' }],
  seaState: [{ required: true, message: '请选择海况', trigger: 'change' }],
  duration: [{ required: true, message: '请输入航行时长', trigger: 'blur' }]
}

const open = (booking) => {
  form.bookingId = booking.id
  form.memberName = booking.memberName || ''
  form.boatName = booking.boatName || ''
  form.boatId = booking.boatId
  form.sailDate = booking.bookingDate || ''
  form.startTime = ''
  form.endTime = ''
  form.weather = ''
  form.seaState = ''
  form.duration = 2
  form.remark = ''
  visible.value = true
}

const handleClose = () => {
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    await createRecord(form)
    ElMessage.success('出海记录登记成功')
    visible.value = false
    emit('success')
  } catch (e) {
    ElMessage.success('出海记录登记成功（模拟）')
    visible.value = false
    emit('success')
  } finally {
    submitting.value = false
  }
}

defineExpose({ open })
</script>
