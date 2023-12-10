## 基于el-drawer实现侧边栏

### 功能
1. 点击菜单File，从左向右打开侧边栏
2. 点击侧边栏外，关闭侧边栏

### 引入el-drawer
1. 编辑vuejs/openapi/src/App.vue，代码如下：
```vue
<script setup lang="ts">
  import { ref } from 'vue'
  // import SwaggerUI from '@/components/SwaggerUI.vue'
  import SwaggerEditor from '@/components/SwaggerEditor.vue'
  import EditorMenu from '@/components/EditorMenu.vue'

  const direction = ref('ltr')
  const fileDrawer = ref(false)
  const editorMenuRef = ref()
  const closeFileDrawer = () => {
    if (editorMenuRef != null && editorMenuRef.value != null) {
      editorMenuRef.value.cancelSelection()
    }
    fileDrawer.value = false
  }
  const openFileDrawer = () => {
    if (fileDrawer != null && fileDrawer.value != null) {
      fileDrawer.value = true
    }
  }

  const openDrawer = () => {
  }

  defineExpose ({
    openFileDrawer
  })
</script>

<template>
  <!-- <SwaggerUI /> -->
  <EditorMenu ref="editorMenuRef"/>
  <SwaggerEditor/>

  <div>
    <el-drawer
      v-model="fileDrawer"
      title="Openapi Files"
      :direction="direction"
      :before-close="closeFileDrawer"
      style="top: 50px;"
      size="50%"
      :show-close="false"
      @open="openDrawer"
    >
      <span>Hi, there!</span>
    </el-drawer>
  </div>
</template>

<style scoped>
:deep(.el-drawer__header) {
  margin-bottom: 0px;
  border-bottom: 1px solid darksalmon;
}
</style>
```

### 参考资料
1. [侧边栏](https://element-plus.org/zh-CN/component/drawer.html)
2. 