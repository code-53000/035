<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="login-title">帆船管理系统</h2>
      <el-form ref="loginForm" :model="loginForm" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin" style="width: 100%">登 录</el-button>
        </el-form-item>
      </el-form>
      <div class="login-tips">
        <p>测试账号：</p>
        <p>会员：member / 123456</p>
        <p>教练：coach / 123456</p>
        <p>管理员：admin / 123456</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '../api/auth'

const router = useRouter()
const loginForm = reactive({
  username: '',
  password: ''
})
const loading = ref(false)
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  try {
    loading.value = true
    const res = await login(loginForm)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) {
    if (e.response?.status === 404 || !e.response) {
      mockLogin(loginForm.username, loginForm.password)
    }
  } finally {
    loading.value = false
  }
}

const mockLogin = (username, password) => {
  const users = {
    member: { role: 'MEMBER', name: '张三' },
    coach: { role: 'COACH', name: '李教练' },
    admin: { role: 'ADMIN', name: '管理员' }
  }
  if (users[username] && password === '123456') {
    const mockToken = 'mock_token_' + Date.now()
    const mockUser = { id: 1, username, ...users[username] }
    localStorage.setItem('sailing_token', mockToken)
    localStorage.setItem('sailing_user', JSON.stringify(mockUser))
    ElMessage.success('登录成功（模拟数据）')
    router.push('/')
  } else {
    ElMessage.error('用户名或密码错误')
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}
.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}
.login-tips {
  margin-top: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
  font-size: 12px;
  color: #909399;
  line-height: 1.8;
}
.login-tips p {
  margin: 0;
}
</style>
