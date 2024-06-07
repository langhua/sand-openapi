<template>
    <div id="swagger-ui"></div>
</template>

<script lang="ts">
import 'swagger-ui-dist/swagger-ui.css'
import 'swagger-ui-dist/index.css'
import { SwaggerUIBundle, SwaggerUIStandalonePreset } from 'swagger-ui-dist'
import { ref, watch } from 'vue'
import oauth2AndLogout from './SwaggerOAuth2Extend.vue'
import checkUrlResponsePlugin from './CheckUrlResponsePlugin.vue'

const uri = ref<string>('')

const showSwaggerUi = () => {
    const swaggerui = SwaggerUIBundle({
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
            SwaggerUIBundle.plugins.DownloadUrl,
            oauth2AndLogout,
            checkUrlResponsePlugin
        ],
        // 如果使用StandaloneLayout，会显示swagger-ui自带的页眉和页脚
        // layout: "StandaloneLayout"
    })

    if (window.location.host == 'localhost') {
        if (window.location.port == '8443') {
            swaggerui.initOAuth({
                appName: 'OFBiz CAS OAuth2',
                clientId: 'SandFlower',
                clientSecret: 'sandflower',
            });
        } else if (window.location.port.match(/517[3-9]/)) {
            swaggerui.initOAuth({
                appName: 'OFBiz CAS OAuth2',
                clientId: 'SandFlower1',
                clientSecret: 'sandflower',
            });
        } else if (window.location.port.match(/3[0-2]0[0-9]/)) {
            swaggerui.initOAuth({
                appName: 'OFBiz CAS OAuth2',
                clientId: 'SandFlower2',
                clientSecret: 'sandflower',
            });
        }
    }
}

export default {
    mounted() {
        showSwaggerUi()
    }
}
</script>

<script setup lang="ts">
const props = defineProps({
    fileUri: String
})

const emits = defineEmits ([
    'fileUriMessage'
])

if (props.fileUri != undefined && props.fileUri != '') {
    uri.value = props.fileUri
}

if (uri.value) {
    emits('fileUriMessage', uri.value)
}

watch(props, () => {
    uri.value = props.fileUri == undefined? '' : props.fileUri
    showSwaggerUi()
    emits('fileUriMessage', uri.value)
})
</script>
