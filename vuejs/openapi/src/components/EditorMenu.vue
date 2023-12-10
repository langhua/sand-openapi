<script setup lang="ts">
  import { ref } from 'vue'
  const activeIndex = ref('8')
  const handleFileClick = ($parent: any) => {
    $parent.openFileDrawer()
  }
  const cancelSelection = () => {
    activeIndex.value = activeIndex.value == '8' ? '9' : '8'
  }
  defineExpose ({
    cancelSelection
  })
</script>

<template>
  <el-menu
    :default-active="activeIndex"
    mode="horizontal"
    background-color="#1b1b1b"
    text-color="#fff"
    active-text-color="#ffd04b"
  >
    <el-menu-item index="1" @click="handleFileClick($parent)">File</el-menu-item>
    <el-sub-menu index="2">
      <template #title>Edit</template>
      <el-menu-item index="2-1">item one</el-menu-item>
      <el-menu-item index="2-2">item two</el-menu-item>
      <el-menu-item index="2-3">item three</el-menu-item>
      <el-sub-menu index="2-4">
      <template #title>item four</template>
      <el-menu-item index="2-4-1">item one</el-menu-item>
      <el-menu-item index="2-4-2">item two</el-menu-item>
      <el-menu-item index="2-4-3">item three</el-menu-item>
      </el-sub-menu>
    </el-sub-menu>
    <el-menu-item index="3">Generate Server</el-menu-item>
    <el-menu-item index="4">Generate Client</el-menu-item>
    <el-menu-item index="5">About</el-menu-item>
  </el-menu>
</template>

<style scoped>
.el-menu--horizontal {
  height: 50px;
}
</style>
