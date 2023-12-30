<template>
    <div id="swagger-ui"></div>
</template>

<script lang="ts">
import 'swagger-ui-dist/swagger-ui.css'
import 'swagger-ui-dist/index.css'
import { SwaggerUIBundle, SwaggerUIStandalonePreset } from 'swagger-ui-dist'
import { ref, watch } from 'vue'

const uri = ref<string>('')

const showSwaggerUi = () => {
    SwaggerUIBundle({
        // 或者使用yaml样例文件: "https://petstore.swagger.io/v2/swagger.yaml"
        // url: "https://petstore.swagger.io/v2/swagger.json",
        url: uri.value,
        dom_id: '#swagger-ui',
        deepLinking: true,
        presets: [
            SwaggerUIBundle.presets.apis,
            SwaggerUIStandalonePreset
        ],
        plugins: [
            SwaggerUIBundle.plugins.DownloadUrl
        ],
        // 如果使用StandaloneLayout，会显示swagger-ui自带的页眉和页脚
        // layout: "StandaloneLayout"
    })
}

export default {
    props: {
        fileUri: String
    },
    setup(props) {
        if (props.fileUri != undefined && props.fileUri != '') {
            uri.value = props.fileUri
        }

        watch(props, () => {
            uri.value = props.fileUri == undefined? '' : props.fileUri
            showSwaggerUi()
        })
    },
    mounted() {
        showSwaggerUi()
    }
}
</script>
