<script setup lang="ts">
  import { ref } from 'vue'
  // import SwaggerUI from '@/components/SwaggerUI.vue'
  import SwaggerEditor from '@/components/SwaggerEditor.vue'
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
  const editorRef = ref()
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

  const saveEditorContent = () => {
    editorRef.value.saveContent()
    setTimeout(() => {
      editorMenuRef.value.cancelSelection()
  	}, 4000);
  }

  const handleFileUriMessage = (message: string) => {
    console.log("--editor view handleFileUriMessage--")
    if (message && message != fileUri.value) {
      console.log("--editor view handleFileUriMessage 2--")
      fileUri.value = message
      router.push({path: env.VITE_OPENAPI_BASE_URL + 'editor', query: {fileUri: fileUri.value}})
    }
  }

  defineExpose ({
    openFileDrawer,
    saveEditorContent
  })
</script>

<template>
  <EditorMenu ref="editorMenuRef"/>
  <SwaggerEditor ref="editorRef" :fileUri="fileUri" @fileUriMessage="handleFileUriMessage"/>

  <div>
    <el-drawer
      v-model="fileDrawer"
      title="Openapi Files"
      :direction="direction"
      :before-close="closeFileDrawer"
      style="top: 50px; height: auto;"
      size="50%"
      :show-close="false"
      @open="openDrawer"
    >
      <template #default>
        <Suspense>
          <FileTree />
        </Suspense>
      </template>
    </el-drawer>
  </div>
</template>

<style>
#swagger-editor {
  margin-top: 50px;
}
</style>

<style scoped>
:deep(.el-drawer__header) {
  margin-bottom: 0px;
  border-bottom: 1px solid darksalmon;
}
</style>
