<script setup lang="ts">
import axios from 'axios'
import { CopyDocument,
         Document, DocumentAdd, DocumentDelete,
         Folder, FolderAdd, FolderDelete, 
         Menu, Refresh } from '@element-plus/icons-vue'

import { Cut, Paste, FileHistory } from '@langhua/el-icon-ext'

const env = import.meta.env
const fileTree = await axios.get(env.VITE_SANDFLOWER_SERVER + env.VITE_SANDFLOWER_SANDDAV_FILETREE)
                            .then(response => {
                                // console.log(JSON.stringify(response.data))
                                return JSON.parse(JSON.stringify(response.data))
                            })

interface FileTree {
  name: string
  type: string
  path: string
  resources?: FileTree[]
}

const fileTreeProps = {
  value: 'path',
  label: 'name',
  children: 'resources',
}

const handleNodeClick = (data: FileTree) => {
  console.log(data)
}

// const append = (data: FileTree) => {
//   const newChild = { value: value + '_1', label: 'testtest', children: [] }
//   if (!data.resources) {
//     data.resources = []
//   }
//   data.resources.push(newChild)
//   fileTree.resources.value = [...fileTree.resources.value]
// }

// const remove = (node: Node, data: FileTree) => {
//   const parent = node.parent
//   const children: FileTree[] = parent.data.children || parent.data
//   const index = children.findIndex((d) => d.value === data.value)
//   children.splice(index, 1)
//   fileTree.resources.value = [...fileTree.resources.value]
// }
</script>

<template>
  <el-tree 
    :data="fileTree.resources"
    :props="fileTreeProps"
    node-key="path"
    @node-click="handleNodeClick"
    :default-expanded-keys="[fileTree.resources[0].path]"
  >
    <template #default="{ node, data }">
      <span class="custom-tree-node">
        <el-icon v-if="data.type == 'File'" class="file-node-icon" color="green"><Document /></el-icon>
        <el-icon v-else-if="data.type == 'Dir'" class="file-node-icon"><Folder /></el-icon>
        <span class="tree-node-label"> {{ node.label }} </span>
        <span>
          <el-popover
            width="auto"
            popper-style="box-shadow: rgb(14 18 22 / 35%) 0px 10px 38px -10px, rgb(14 18 22 / 20%) 0px 10px 20px -15px; padding: 8px;"
          >
            <template #reference>
              <el-icon class="el-icon--right"><Menu /></el-icon>
            </template>
            <template #default>
              <div
                class="demo-rich-conent"
                style="display: flex; flex-direction: column; color:gray;"
              >
                <span v-if="data.type == 'Dir'">
                  <a style="padding-right: 4px;">
                    <el-icon>
                      <el-tooltip class="box-item" effect="dark" placement="top-start"
                          content="Cut">
                        <Cut />
                      </el-tooltip>
                    </el-icon>
                  </a>
                  <a style="padding-right: 4px;">
                    <el-icon>
                      <el-tooltip class="box-item" effect="dark" placement="top"
                          content="Paste">
                        <Paste />
                      </el-tooltip>
                    </el-icon>
                  </a>
                  <a style="padding-right: 4px;">
                    <el-icon>
                      <el-tooltip class="box-item" effect="dark" placement="top"
                          content="New Folder">
                        <FolderAdd />
                      </el-tooltip>
                    </el-icon>
                  </a>
                  <a style="padding-right: 4px;">
                    <el-icon>
                      <el-tooltip class="box-item" effect="dark" placement="top"
                          content="New Document">
                        <DocumentAdd />
                      </el-tooltip>
                    </el-icon>
                  </a>
                  <a style="padding-right: 4px;">
                    <el-icon>
                      <el-tooltip class="box-item" effect="dark" placement="top"
                          content="Copy Document">
                        <CopyDocument />
                      </el-tooltip>
                    </el-icon>
                  </a>
                  <a style="padding-right: 4px;">
                    <el-icon>
                      <el-tooltip class="box-item" effect="dark" placement="top"
                          content="Delete Folder">
                        <FolderDelete />
                      </el-tooltip>
                    </el-icon>
                  </a>
                  <a>
                    <el-icon>
                      <el-tooltip class="box-item" effect="dark" placement="top-end"
                          content="Refresh">
                        <Refresh />
                      </el-tooltip>
                    </el-icon>
                  </a>
                </span>
                <span v-else-if="data.type == 'File'">
                  <a style="padding-right: 4px;">
                    <el-icon>
                      <el-tooltip class="box-item" effect="dark" placement="top-start"
                          content="Cut">
                        <Cut />
                      </el-tooltip>
                    </el-icon>
                  </a>
                  <a style="padding-right: 4px;">
                    <el-icon>
                      <el-tooltip class="box-item" effect="dark" placement="top"
                          content="Paste">
                        <Paste />
                      </el-tooltip>
                    </el-icon>
                  </a>
                  <a style="padding-right: 4px;">
                    <el-icon>
                      <el-tooltip class="box-item" effect="dark" placement="top"
                          content="New Document">
                        <DocumentAdd />
                      </el-tooltip>
                    </el-icon>
                  </a>
                  <a style="padding-right: 4px;">
                    <el-icon>
                      <el-tooltip class="box-item" effect="dark" placement="top"
                          content="Delete">
                        <DocumentDelete />
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
</template>

<style scoped>
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

span a img:hover, span a:hover {
  color: blue;
}

</style>
