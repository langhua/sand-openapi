# sand-openapi
本模块是一个OFBiz plugin，通过整合swagger-ui和swagger-editor，为Sandflower提供openapi文档编写环境。
尤其适合的三三制开发：一人编写openapi文档和测试，一人开发Vue3前端代码，一人开发OFBiz后端代码。当然，也可以一个人搞定所有事情。

本模块的功能包括：
1. openapi文档浏览：用户在浏览器中浏览存储在服务器端的openapi文档；
2. openapi文档编辑：用户在浏览器中编辑存储在服务器端的openapi文档；
3. 根据openapi文档生成前端代码：用户在浏览器中编辑openapi文档，并生成前端代码；
4. 根据openapi文档生成后端代码：用户在浏览器中编辑openapi文档，并生成后端代码。

本模块的架构图如下：






### 开发笔记
1. [sand-openapi开发环境搭建笔记](./docs/zh/envsetup.md)