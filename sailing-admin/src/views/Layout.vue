<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
      <div class="logo">
        <el-icon v-if="isCollapse" :size="28"><Compass /></el-icon>
        <span v-else class="logo-text">帆船管理系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        :collapse="isCollapse"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/boats">
          <el-icon><Compass /></el-icon>
          <template #title>船只列表</template>
        </el-menu-item>
        <el-menu-item v-if="role === 'MEMBER'" index="/my-bookings">
          <el-icon><Tickets /></el-icon>
          <template #title>我的预约</template>
        </el-menu-item>
        <el-menu-item v-if="role === 'MEMBER'" index="/my-records">
          <el-icon><Document /></el-icon>
          <template #title>我的出海记录</template>
        </el-menu-item>
        <el-menu-item v-if="role === 'COACH'" index="/my-schedule">
          <el-icon><Calendar /></el-icon>
          <template #title>我的排班</template>
        </el-menu-item>
        <el-menu-item v-if="role === 'COACH'" index="/coach-bookings">
          <el-icon><Tickets /></el-icon>
          <template #title>我的预约</template>
        </el-menu-item>
        <el-sub-menu v-if="role === 'ADMIN'" index="admin">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>管理员</span>
          </template>
          <el-menu-item index="/admin/boats">船只管理</el-menu-item>
          <el-menu-item index="/admin/coaches">教练排班</el-menu-item>
          <el-menu-item index="/admin/bookings">预约审批</el-menu-item>
          <el-menu-item index="/admin/closures">封场管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" :size="20" @click="toggleCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><UserFilled /></el-icon>
              <span>{{ user?.name || user?.username || '用户' }}</span>
              <el-tag size="small" :type="roleTagType" style="margin-left: 8px">{{ roleText }}</el-tag>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  Compass, Tickets, Document, Calendar, Setting,
  UserFilled, ArrowDown, Fold, Expand
} from '@element-plus/icons-vue'
import { getUser, getRole, removeToken } from '../utils/auth'
import { logout } from '../api/auth'

const router = useRouter()
const route = useRoute()
const isCollapse = ref(false)

const user = computed(() => getUser())
const role = computed(() => getRole())
const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta?.title || '')

const roleText = computed(() => {
  const map = { MEMBER: '会员', COACH: '教练', ADMIN: '管理员' }
  return map[role.value] || '未知'
})
const roleTagType = computed(() => {
  const map = { MEMBER: '', COACH: 'success', ADMIN: 'danger' }
  return map[role.value] || ''
})

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleCommand = async (cmd) => {
  if (cmd === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' })
      try { await logout() } catch (e) {}
      removeToken()
      ElMessage.success('已退出登录')
      router.push('/login')
    } catch (e) {}
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.aside {
  background: #304156;
  transition: width 0.3s;
  overflow: hidden;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  background: #2b2f3a;
  border-bottom: 1px solid #1f2d3d;
}
.logo-text {
  margin-left: 8px;
}
:deep(.el-menu) {
  border-right: none;
}
.header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
}
.header-left {
  display: flex;
  align-items: center;
}
.collapse-btn {
  cursor: pointer;
  margin-right: 20px;
  color: #606266;
}
.header-right .user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #606266;
}
.header-right .user-info > * {
  margin-right: 6px;
}
.main {
  background: #f0f2f5;
  padding: 20px;
}
</style>
