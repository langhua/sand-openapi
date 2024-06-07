<script setup lang="ts">
  import { ref } from 'vue'
  import SwaggerUI from '@/components/SwaggerUI.vue'
  import EditorMenu from '@/components/EditorMenu.vue'
  import FileTree from '@/components/FileTree.vue'
  import { useRoute } from 'vue-router'
  import router from '@/router';

  const env = import.meta.env

  const route = useRoute()
  const fileUri = ref<string>('')
  if (route != undefined && route.query != undefined && route.query.fileUri != undefined) {
    fileUri.value = route.query.fileUri.toString()
  }

  const direction = ref('ltr')
  const fileDrawer = ref(false)
  const editorMenuRef = ref()

  const closeFileDrawer = (path: string) => {
    if (editorMenuRef.value != null) {
      editorMenuRef.value.cancelSelection()
    }
    fileDrawer.value = false
    if (path != undefined && fileUri.value != path) {
      fileUri.value = path
    }
  }

  const openFileDrawer = () => {
    if (fileDrawer.value != null) {
      fileDrawer.value = true
    }
  }

  const openDrawer = () => {
  }

  const handleFileUriMessage = (message: string) => {
    if (message && message != fileUri.value) {
      fileUri.value = message
      router.push({path: env.VITE_OPENAPI_BASE_URL + 'viewfile', query: {fileUri: fileUri.value}})
    }
  }

  defineExpose ({
    openFileDrawer
  })
</script>

<template>
  <EditorMenu ref="editorMenuRef" :fileUri="fileUri"/>
  <SwaggerUI ref="swaggerUiRef" :fileUri="fileUri" @fileUriMessage="handleFileUriMessage"/>

  <div>
    <el-drawer
      v-model="fileDrawer"
      title="Openapi Files"
      :direction="direction"
      style="top: 50px; height: auto;"
      size="50%"
      :show-close="false"
      @open="openDrawer"
    >
      <template #default>
        <Suspense>
          <FileTree @closeFileDrawer="closeFileDrawer"/>
        </Suspense>
      </template>
    </el-drawer>
  </div>
</template>

<style scoped>
#swagger-ui {
  margin-top: 90px;
}

:deep(.el-drawer__header) {
  margin-bottom: 0px;
  border-bottom: 1px solid darksalmon;
}
</style>
