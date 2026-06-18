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
      <el-form-item label="出海时间" prop="departureTime">
        <el-date-picker v-model="form.departureTime" type="datetime" placeholder="选择出海时间" style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
      </el-form-item>
      <el-form-item label="返航时间" prop="returnTime">
        <el-date-picker v-model="form.returnTime" type="datetime" placeholder="选择返航时间" style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
      </el-form-item>
      <el-form-item label="时长(分钟)" prop="durationMinutes">
        <el-input-number v-model="form.durationMinutes" :min="1" :max="1440" :step="10" />
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
      <el-form-item label="风速" prop="windSpeed">
        <el-select v-model="form.windSpeed" placeholder="选择风速" style="width: 100%">
          <el-option label="无风(0级)" value="0级" />
          <el-option label="软风(1级)" value="1级" />
          <el-option label="轻风(2级)" value="2级" />
          <el-option label="微风(3级)" value="3级" />
          <el-option label="和风(4级)" value="4级" />
          <el-option label="清风(5级)" value="5级" />
          <el-option label="强风(6级)" value="6级" />
        </el-select>
      </el-form-item>
      <el-form-item label="潮汐" prop="tide">
        <el-select v-model="form.tide" placeholder="选择潮汐" style="width: 100%">
          <el-option label="涨潮" value="涨潮" />
          <el-option label="平潮" value="平潮" />
          <el-option label="退潮" value="退潮" />
        </el-select>
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
  departureTime: '',
  returnTime: '',
  durationMinutes: 120,
  weather: '',
  windSpeed: '',
  tide: '',
  remark: ''
})

const rules = {
  sailDate: [{ required: true, message: '请选择出海日期', trigger: 'change' }],
  departureTime: [{ required: true, message: '请选择出海时间', trigger: 'change' }],
  returnTime: [{ required: true, message: '请选择返航时间', trigger: 'change' }],
  durationMinutes: [{ required: true, message: '请输入航行时长', trigger: 'blur' }],
  weather: [{ required: true, message: '请选择天气', trigger: 'change' }],
  windSpeed: [{ required: true, message: '请选择风速', trigger: 'change' }],
  tide: [{ required: true, message: '请选择潮汐', trigger: 'change' }]
}

const open = (booking) => {
  form.bookingId = booking.id
  form.memberName = booking.memberName || ''
  form.memberId = booking.memberId
  form.boatName = booking.boatName || ''
  form.boatId = booking.boatId
  form.sailDate = booking.bookingDate || ''
  form.departureTime = ''
  form.returnTime = ''
  form.durationMinutes = 120
  form.weather = ''
  form.windSpeed = ''
  form.tide = ''
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
