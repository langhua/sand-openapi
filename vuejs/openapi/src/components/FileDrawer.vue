<script setup lang="ts">
  import { ref } from 'vue'
  const direction = ref('ltr')
  const fileDrawer = ref(false)

  const closeFileDrawer = ($parent: any) => {
    $parent.handleDrawerClose()
    fileDrawer.value = false
  }
  const openFileDrawer = () => {
    fileDrawer.value = true
  }

  defineExpose ({
    openFileDrawer
  })
</script>

<template>
  <el-drawer
    v-model="fileDrawer"
    title="I am the title"
    :direction="direction"
    :before-close="closeFileDrawer($parent)"
    style="top: 50px;"
    size="50%"
    >
    <span>Hi, there!</span>
  </el-drawer>
</template>