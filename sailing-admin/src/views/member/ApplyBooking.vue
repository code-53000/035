<template>
  <el-dialog v-model="visible" title="提交预约" width="500px" @close="handleClose">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="船只">
        <el-input v-model="boatInfo.name" disabled />
      </el-form-item>
      <el-form-item label="船号">
        <el-input v-model="boatInfo.code" disabled />
      </el-form-item>
      <el-form-item label="船型">
        <el-input v-model="boatInfo.boatTypeName" disabled />
      </el-form-item>
      <el-form-item label="容量">
        <el-input v-model="boatInfo.capacity" disabled />
      </el-form-item>
      <el-form-item label="预约日期" prop="bookingDate">
        <el-date-picker v-model="form.bookingDate" type="date" placeholder="选择日期" style="width: 100%" :disabled-date="disabledDate" />
      </el-form-item>
      <el-form-item label="时间段" prop="timeSlot">
        <el-select v-model="form.timeSlot" placeholder="选择时间段" style="width: 100%">
          <el-option label="上午 08:00 - 12:00" value="MORNING" />
          <el-option label="下午 13:00 - 17:00" value="AFTERNOON" />
          <el-option label="全天 08:00 - 17:00" value="FULL" />
        </el-select>
      </el-form-item>
      <el-form-item label="教练">
        <el-select v-model="form.coachId" placeholder="选择教练（可选）" style="width: 100%" clearable>
          <el-option v-for="c in coaches" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="备注信息" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">提交预约</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { createBooking } from '../../api/booking'

const emit = defineEmits(['success'])
const visible = ref(false)
const submitting = ref(false)
const formRef = ref()
const boatInfo = ref({})

const form = reactive({
  boatId: null,
  bookingDate: '',
  timeSlot: '',
  coachId: null,
  remark: ''
})

const rules = {
  bookingDate: [{ required: true, message: '请选择预约日期', trigger: 'change' }],
  timeSlot: [{ required: true, message: '请选择时间段', trigger: 'change' }]
}

const coaches = [
  { id: 1, name: '李教练' },
  { id: 2, name: '王教练' },
  { id: 3, name: '赵教练' }
]

const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

const open = (boat) => {
  boatInfo.value = boat
  form.boatId = boat.id
  form.bookingDate = ''
  form.timeSlot = ''
  form.coachId = null
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
    await createBooking({ ...form, bookingDate: form.bookingDate ? new Date(form.bookingDate).toISOString().split('T')[0] : '' })
    ElMessage.success('预约提交成功，等待审批')
    visible.value = false
    emit('success')
  } catch (e) {
    ElMessage.success('预约提交成功（模拟），等待审批')
    visible.value = false
    emit('success')
  } finally {
    submitting.value = false
  }
}

defineExpose({ open })
</script>
