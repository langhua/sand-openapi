<script setup lang="ts">
  import router from '@/router';
  import { ref, watch } from 'vue';

  const env = import.meta.env

  const props = defineProps({
    fileUri: String
  })

  const activeIndex = ref('8')
  const cancelSelection = () => {
    activeIndex.value = activeIndex.value == '8' ? '9' : '8'
  }

  const menuEditDisabled = ref<boolean>(true)
  const menuItemDisabled = ref<boolean>(true)

  if (router.currentRoute.value.query.fileUri != undefined) {
    menuItemDisabled.value = false
    if (router.currentRoute.value.name == 'editor') {
      menuEditDisabled.value = true
    } else {
      menuEditDisabled.value = false
    }
  }

  const handleFileClick = ($parent: any) => {
    $parent.openFileDrawer()
  }

  const gotoEditor = () => {
    if (props.fileUri != undefined) {
      router.push({path: env.VITE_OPENAPI_BASE_URL + 'editor', query: {fileUri: props.fileUri}})
    } else {
      router.push({path: env.VITE_OPENAPI_BASE_URL + 'editor', query: {fileUri: router.currentRoute.value.query.fileUri}})
    }
  }

  const gotoView = () => {
    if (props.fileUri != undefined) {
      router.push({path: env.VITE_OPENAPI_BASE_URL + 'viewfile', query: {fileUri: props.fileUri}})
    } else {
      router.push({path: env.VITE_OPENAPI_BASE_URL + 'viewfile', query: {fileUri: router.currentRoute.value.query.fileUri}})
    }
  }

  const saveFile = ($parent: any) => {
    $parent.saveEditorContent()
  }

  watch(props, () => {
    if (props.fileUri != '') {
      menuItemDisabled.value = false
      if (router.currentRoute.value.name == 'editor') {
        menuEditDisabled.value = true
      } else {
        menuEditDisabled.value = false
      }
    } else {
      menuEditDisabled.value = true
      menuItemDisabled.value = true
    }
  })

  defineExpose ({
    cancelSelection,
    saveFile
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
    <el-menu-item v-if="router.currentRoute.value.name != 'editor'" index="2" @click="gotoEditor" :disabled="menuEditDisabled">Edit</el-menu-item>
    <el-menu-item v-if="router.currentRoute.value.name == 'editor'" index="3" :disabled="menuItemDisabled" @click="saveFile($parent)">Save</el-menu-item>
    <el-menu-item v-if="router.currentRoute.value.name == 'editor'" index="4" :disabled="menuItemDisabled" @click="gotoView">View</el-menu-item>
    <el-menu-item index="5" :disabled="menuItemDisabled">Generate Server</el-menu-item>
    <el-menu-item index="6" :disabled="menuItemDisabled">Generate Client</el-menu-item>
    <el-menu-item index="7">About</el-menu-item>
  </el-menu>
</template>

<style scoped>
.el-menu--horizontal {
  height: 50px;
  position: fixed;
  z-index: 10;
  top: 0;
  width: 100%;
}
</style>
