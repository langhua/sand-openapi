<template>
    <div id="swagger-ui"></div>
</template>

<script lang="ts">
import 'swagger-ui-dist/swagger-ui.css'
import 'swagger-ui-dist/index.css'
import { SwaggerUIBundle, SwaggerUIStandalonePreset } from 'swagger-ui-dist'

export default {
    mounted() {
        SwaggerUIBundle({
            // 或者使用yaml样例文件: "https://petstore.swagger.io/v2/swagger.yaml"
            url: "https://petstore.swagger.io/v2/swagger.json",
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
}
</script>
