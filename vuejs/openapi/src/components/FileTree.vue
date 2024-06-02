<script setup lang="ts">
import axios from 'axios'
import { CopyDocument,
         Document, DocumentAdd, DocumentDelete,
         Edit,
         Folder, FolderAdd, FolderDelete, 
         MoreFilled, Refresh } from '@element-plus/icons-vue'

import { Cut, Paste, FileHistory, Rename, View } from '@langhua/el-icon-ext'
import { ElMessage, ElMessageBox } from 'element-plus'
import type Node from 'element-plus/es/components/tree/src/model/node'
import { ref } from 'vue'
import router from '@/router'

const env = import.meta.env

const loadNode = (node: Node, resolve: (data: FileTree[]) => void) => {
  if (node.level == 0 || (node.data && node.data.type == 'Dir')) {
    setTimeout(async () => {
      let dataSource: FileTree[] = await axios({method: 'get',
                                                url: ((node.level != 0) ? node.data.path : env.VITE_SANDFLOWER_SANDDAV_FILETREE),
                                              })
                                              .then(response => {
                                                if (response.status == 200) {
                                                  return JSON.parse(JSON.stringify(response.data)).resources
                                                }
                                              })
                                              .catch(error => {
                                                if (error.response != undefined && error.response.status == 401) {
                                                  router.push({path: '/login'})
                                                }
                                              })

      if (node.level > 0 && dataSource[0] && dataSource[0].resources) {
        resolve(dataSource[0].resources)
      } else {
        resolve(dataSource)
      }
    }, 500)
  }
}

interface FileTree {
  name: string
  type: string
  path: string
  resources?: FileTree[]
  isLeaf: string
}

const fileTreeProps = {
  id: 'path',
  label: 'name',
  type: 'type',
  children: 'resources',
  isLeaf: function(data: FileTree) {
      return data == undefined || data.isLeaf == undefined || data.isLeaf == 'true'
    }
}

const handleNodeClick = () => {
}

const refreshFolder = (node: Node) => {
  // 重新展开节点就会间接重新触发load达到刷新效果
  node.loaded = false;
  node.expand();
}

const deleteNode = (node: Node, data: FileTree) => {
  var parentNode = node.parent
  ElMessageBox.confirm(
    decodeURIComponent(data.path) + ' will be deleted permanently. Continue?',
      'Warning',
      {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'warning',
      }
    )
    .then(async () => {
      await axios.delete(env.VITE_SANDFLOWER_SERVER + data.path)
           .then( response => {
              if (response.status == 204) {
                ElMessage({
                  type: 'success',
                  message: 'Delete completed.'
                })
                parentNode.loaded = false;
                parentNode.expand();
              } else {
                ElMessage({
                  type: 'error',
                  message: 'Delete failed',
                })
              }
            })
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: 'Delete canceled',
      })
    })
}

const newDocDialogVisible = ref(false)
const input = ref('')
const parentNodeForEdit = ref<Node>()
const parentNodePath = ref('')

const showNewDocDlg = (node: Node, data: FileTree) => {
  input.value = ''
  newDocDialogVisible.value = true
  parentNodeForEdit.value = node
  let tempPath = data.path.slice(1)
  tempPath = tempPath.slice(tempPath.indexOf('/'))
  parentNodePath.value = tempPath
}

const newDoc = ()  => {
  newDocDialogVisible.value = false
  setTimeout(async () => {
    await axios.put(parentNodeForEdit.value?.data.path + input.value + fileExtention.value)
               .then(response => {
                      if (response.status == 201) {
                        let tempPath = parentNodeForEdit.value?.data.path.slice(1)
                        tempPath = tempPath.slice(tempPath.indexOf('/'))
                        ElMessage({
                                    type: 'success',
                                    message: decodeURIComponent(tempPath) + input.value + fileExtention.value + ' has been created successfully.',
                                  })

                        if (parentNodeForEdit && parentNodeForEdit.value) {
                          parentNodeForEdit.value.loaded = false
                          parentNodeForEdit.value.expand()
                        }
                      }
                  })
  }, 500)
}

const fileExtention = ref('.yaml')
const fileExts = [
  {
    value: '.yaml',
    label: '.yaml',
  },
  {
    value: '.json',
    label: '.json',
  }
]

const newFolderDialogVisible = ref(false)
const showNewFolderDlg = (node: Node, data: FileTree) => {
  input.value = ''
  newFolderDialogVisible.value = true
  parentNodeForEdit.value = node
  let tempPath = data.path.slice(1)
  tempPath = tempPath.slice(tempPath.indexOf('/'))
  parentNodePath.value = tempPath
}

const newFolder = ()  => {
  newFolderDialogVisible.value = false
  setTimeout(async () => {
    await axios({method: 'mkcol',
                 url: parentNodeForEdit.value?.data.path + input.value,
                })
                .then(response => {
                      if (response.status == 201) {
                        let tempPath = parentNodeForEdit.value?.data.path.slice(1)
                        tempPath = tempPath.slice(tempPath.indexOf('/'))
                        ElMessage({
                                    type: 'success',
                                    message: decodeURIComponent(tempPath) + input.value + ' has been created successfully.',
                                  })

                        if (parentNodeForEdit && parentNodeForEdit.value) {
                          parentNodeForEdit.value.loaded = false
                          parentNodeForEdit.value.expand()
                        }
                      }
                  })
  }, 500)
}

const nodeForEdit = ref<Node>()
const renameFileDlgVisible = ref(false)
const renameFileExt = ref('')
const showRenameFileDlg = (node: Node, data: FileTree) => {
  input.value = data.name.slice(0, data.name.lastIndexOf('.'))
  renameFileExt.value = data.name.slice(data.name.lastIndexOf('.'))
  renameFileDlgVisible.value = true
  nodeForEdit.value = node
}

const renameFile = () => {
  renameFileDlgVisible.value = false
  let targetPath = nodeForEdit.value?.data.path.slice(0, nodeForEdit.value?.data.path.lastIndexOf('/') + 1) + input.value + renameFileExt.value
  setTimeout(async () => {
    await axios({method: 'move',
                 url: nodeForEdit.value?.data.path,
                 headers: {
                   Destination: encodeURIComponent(targetPath),
                 }
                })
                .then(response => {
                      if (response.status == 201) {
                        ElMessage({
                                    type: 'success',
                                    message: nodeForEdit.value?.data.path + ' has been moved to ' + targetPath + ' successfully.',
                                  })

                        if (nodeForEdit && nodeForEdit.value) {
                          nodeForEdit.value.parent.loaded = false
                          nodeForEdit.value.parent.expand()
                        }
                      }
                  })
  }, 500)
}

const emits = defineEmits([
  "closeFileDrawer"
])

const viewFile = (data: FileTree)  => {
  if (data.type == 'File') {
    let extension = data.name.slice(data.name.lastIndexOf('.'))
    let foundExt = false
    fileExts.forEach((ext) => {
      if (extension == ext.value) {
        foundExt = true
      }
    })
    if (extension != undefined && foundExt) {
      router.push({path: env.VITE_OPENAPI_BASE_URL + 'viewfile', query: {fileUri: data.path}})
      emits("closeFileDrawer", data.path)
    }
  }
}

const editFile = (data: FileTree)  => {
  if (data.type == 'File') {
    let extension = data.name.slice(data.name.lastIndexOf('.'))
    let foundExt = false
    fileExts.forEach((ext) => {
      if (extension == ext.value) {
        foundExt = true
      }
    })
    if (extension != undefined && foundExt) {
      router.push({path: env.VITE_OPENAPI_BASE_URL + 'editor', query: {fileUri: data.path}})
      emits("closeFileDrawer", data.path)
    }
  }
}

const resourceInCut = ref('')
const toggleCutResource = (data: FileTree) => {
  if (resourceInCut.value != '') {
    if (resourceInCut.value != data.path) {
      resourceInCut.value = data.path
    } else {
      resourceInCut.value = ''
    }
  } else {
    resourceInCut.value = data.path
  }
  resourceInCopy.value = ''
}

const resourceInCopy = ref('')
const toggleCopyResource = (data: FileTree) => {
  if (resourceInCopy.value != '') {
    if (resourceInCopy.value != data.path) {
      resourceInCopy.value = data.path
    } else {
      resourceInCopy.value = ''
    }
  } else {
    resourceInCopy.value = data.path
  }
  resourceInCut.value = ''
}

const pasteResource = (node: Node, data: FileTree) => {
  if (resourceInCopy.value != '' || resourceInCut.value != '') {
    let method = resourceInCopy.value != ''? 'copy' : 'move'
    let srcpathes: string[] = []
    if (resourceInCopy.value != '') {
      srcpathes = resourceInCopy.value.split('/')
    } else if (resourceInCut.value != '') {
      srcpathes = resourceInCut.value.split('/')
    }

    let destination = data.path
    if (srcpathes[srcpathes.length - 1] == '') {
      destination += srcpathes[srcpathes.length - 2] + '/'
    } else {
      destination += srcpathes[srcpathes.length - 1]
    }

    setTimeout(async () => {
      await axios({method: method,
                  url: resourceInCopy.value != ''? resourceInCopy.value : resourceInCut.value,
                  headers: {
                    Destination: destination,
                  }
                  })
                  .then(response => {
                        if (response.status > 200 && response.status <= 300) {
                          ElMessage({
                                      type: 'success',
                                      message: method + ' to ' + decodeURIComponent(data.path) + ' successfully.',
                                    })
                          resourceInCopy.value = ''
                          resourceInCut.value = ''
                          // node.parent.loaded = false
                          // node.parent.expand()

                          node.loaded = false
                          node.expand()
                        }
                    })
    }, 500)
  }
}
</script>

<template>
  <el-tree 
    lazy
    :load="loadNode"
    :props="fileTreeProps"
    node-key="path"
    @node-click="handleNodeClick"
    :default-expanded-keys="['/sanddav/']"
  >
    <template #default="{ node, data }">
      <span class="custom-tree-node">
        <el-icon v-if="data.type == 'File'" :class="resourceInCut == data.path? 'file-node-icon cutcolor' : (resourceInCopy == data.path? 'file-node-icon copycolor' : 'file-node-icon greencolor')"><Document /></el-icon>
        <el-icon v-else-if="data.type == 'Dir'" :class="resourceInCut == data.path? 'file-node-icon cutcolor' : (resourceInCopy == data.path? 'file-node-icon copycolor' : 'file-node-icon')"><Folder /></el-icon>
        <span class="tree-node-label" @dblclick="viewFile(data)"> {{ node.label }} </span>
        <span>
          <el-popover
            width="auto"
            popper-style="box-shadow: rgb(14 18 22 / 35%) 0px 10px 38px -10px, rgb(14 18 22 / 20%) 0px 10px 20px -15px; padding: 8px;"
          >
            <template #reference>
              <el-icon class="el-icon--right"><MoreFilled /></el-icon>
            </template>
            <template #default>
              <div style="display: flex; flex-direction: column; color: gray;">
                <span v-if="data.type == 'Dir'">
                  <a style="padding-right: 4px;">
                    <el-icon>
                      <el-tooltip class="box-item" effect="dark" placement="top"
                          content="New Document">
                        <DocumentAdd @click="showNewDocDlg(node, data)"/>
                      </el-tooltip>
                    </el-icon>
                  </a>
                  <a style="padding-right: 4px;">
                    <el-icon>
                      <el-tooltip class="box-item" effect="dark" placement="top"
                          content="New Folder">
                        <FolderAdd @click="showNewFolderDlg(node, data)"/>
                      </el-tooltip>
                    </el-icon>
                  </a>
                  <a v-if="node.level > 1" style="padding-right: 4px;">
                    <el-icon>
                      <el-tooltip class="box-item" effect="dark" placement="top-start"
                          content="Cut">
                        <Cut @click="toggleCutResource(data)"/>
                      </el-tooltip>
                    </el-icon>
                  </a>
                  <a style="padding-right: 4px;">
                    <el-icon>
                      <el-tooltip class="box-item" effect="dark" placement="top"
                          content="Copy Folder">
                        <CopyDocument @click="toggleCopyResource(data)"/>
                      </el-tooltip>
                    </el-icon>
                  </a>
                  <a style="padding-right: 4px;">
                    <el-icon>
                      <el-tooltip class="box-item" effect="dark" placement="top"
                          content="Paste">
                        <Paste @click="pasteResource(node, data)"/>
                      </el-tooltip>
                    </el-icon>
                  </a>
                  <a v-if="node.level > 1" style="padding-right: 4px;">
                    <el-icon>
                      <el-tooltip class="box-item" effect="dark" placement="top"
                          content="Rename">
                        <Rename />
                      </el-tooltip>
                    </el-icon>
                  </a>
                  <a v-if="node.level > 1" style="padding-right: 4px;">
                    <el-icon>
                      <el-tooltip class="box-item" effect="dark" placement="top"
                          content="Delete Folder">
                        <FolderDelete @click="deleteNode(node, data)"/>
                      </el-tooltip>
                    </el-icon>
                  </a>
                  <a>
                    <el-icon>
                      <el-tooltip class="box-item" effect="dark" placement="top-end"
                          content="Refresh">
                        <Refresh @click="refreshFolder(node)"/>
                      </el-tooltip>
                    </el-icon>
                  </a>
                </span>
                <span v-else-if="data.type == 'File'">
                  <a style="padding-right: 4px;">
                    <el-icon>
                      <el-tooltip class="box-item" effect="dark" placement="top-start"
                          content="Cut">
                        <Cut @click="toggleCutResource(data)"/>
                      </el-tooltip>
                    </el-icon>
                  </a>
                  <a style="padding-right: 4px;">
                    <el-icon>
                      <el-tooltip class="box-item" effect="dark" placement="top"
                          content="Copy Document">
                        <CopyDocument @click="toggleCopyResource(data)"/>
                      </el-tooltip>
                    </el-icon>
                  </a>
                  <a style="padding-right: 4px;">
                    <el-icon>
                      <el-tooltip class="box-item" effect="dark" placement="top"
                          content="View">
                        <View @click="viewFile(data)"/>
                      </el-tooltip>
                    </el-icon>
                  </a>
                  <a style="padding-right: 4px;">
                    <el-icon>
                      <el-tooltip class="box-item" effect="dark" placement="top"
                          content="Edit">
                        <Edit @click="editFile(data)"/>
                      </el-tooltip>
                    </el-icon>
                  </a>
                  <a style="padding-right: 4px;">
                    <el-icon>
                      <el-tooltip class="box-item" effect="dark" placement="top"
                          content="Rename">
                        <Rename @click="showRenameFileDlg(node, data)"/>
                      </el-tooltip>
                    </el-icon>
                  </a>
                  <a style="padding-right: 4px;">
                    <el-icon>
                      <el-tooltip class="box-item" effect="dark" placement="top"
                          content="Delete">
                        <DocumentDelete @click="deleteNode(node, data)"/>
                      </el-tooltip>
                    </el-icon>
                  </a>
                  <a>
                    <el-icon>
                      <el-tooltip class="box-item" effect="dark" placement="top-end"
                          content="History">
                        <FileHistory />
                      </el-tooltip>
                    </el-icon>
                  </a>
                </span>
              </div>
            </template>
          </el-popover>
        </span>
      </span>
    </template>
  </el-tree>

  <el-dialog v-model="newDocDialogVisible" title="New document" width="40%" draggable class="filedlg">
    <div>under {{ decodeURIComponent(parentNodePath) }}</div>
    <div>
      <el-input v-model="input" placeholder="filename"/>
      <el-select v-model="fileExtention" placeholder="file type">
        <el-option
          v-for="item in fileExts"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="newDocDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="newDoc">
          Create
        </el-button>
      </span>
    </template>
  </el-dialog>

  <el-dialog v-model="newFolderDialogVisible" title="New folder" width="40%" draggable>
    <div>under {{ decodeURIComponent(parentNodePath) }}</div>
    <div>
      <el-input v-model="input" placeholder="folder name"/>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="newFolderDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="newFolder">
          Create
        </el-button>
      </span>
    </template>
  </el-dialog>

  <el-dialog v-model="renameFileDlgVisible" title="Rename file" width="40%" draggable class="filedlg">
    <div>
      <el-input v-model="input" placeholder="new filename"/>
      <el-select v-model="renameFileExt" placeholder="file type">
        <el-option
          v-for="item in fileExts"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="renameFileDlgVisible = false">Cancel</el-button>
        <el-button type="primary" @click="renameFile">
          Rename
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style>
.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}

.tree-node-label {
  flex: auto;
  margin-left: 4px;
}

.file-node-icon {
  margin-left: -1.3em;
  margin-right: 4px;
}

span a:hover {
  color: #409EFF;
}

.filedlg .el-dialog__body {
  padding-top: 0.5em;
  padding-bottom: 3em;
}

.filedlg .el-input {
  width: 150px !important;
  float: left;
}

.filedlg .el-select {
  width: 120px;
  float: left;
}

.cutcolor {
  color: darksalmon;
}

.copycolor {
  color: deeppink;
}

.graycolor {
  color: gray;
}

.greencolor {
  color: green;
}
</style>
