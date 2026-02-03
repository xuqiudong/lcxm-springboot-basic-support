## 代码生成前端部分:Vue 3 + TypeScript + Vite

> 本项目和后端项目 src同级

### 前端开发记录

> 很少少前端, 不甚熟悉, 记录下开发前端的流程
>
> 主要参加 [vite官网](https://vitejs.cn/vite3-cn/)

#### 1 创建项目

- 进入当前前端文件夹后: 
- `pnpm create vite@latest . -- --template vue`

- `pnpm install`
-   `pnpm run dev`

#### 2 引入element-plus

> [安装 | Element Plus](https://element-plus.org/zh-CN/guide/installation)

- `pnpm install element-plus`
- 自动导入 Element Plus 组件，避免全量引入:
  - `pnpm install -D unplugin-auto-import unplugin-vue-components` 
- 图标:`pnpm install @element-plus/icons-vue`

#### 3 配置 Vite（`vite.config.js`）

- 当前如下:

```js
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
})

```

- 修改为:

```js
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
// 自动导入工具
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

export default defineConfig({
  plugins: [
    vue(),
    // 自动导入Element Plus的组件和API
    AutoImport({
      resolvers: [ElementPlusResolver()],
      // 自动导入Vue的ref、reactive等API
      imports: ['vue']
    }),
    Components({
      resolvers: [ElementPlusResolver()]
    })
  ],
  // 打包输出到后端resources/static/generator目录
  build: {
    outDir: '../src/main/resources/static/generator', // 相对路径，指向后端资源目录
    emptyOutDir: true, // 打包前清空目标目录
    assetsDir: 'assets' // 静态资源（js/css）存放目录
  },
  // 开发环境跨域代理（解决前端调用后端接口的跨域问题）
  server: {
    port: 3000, // 前端开发服务器端口
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 后端接口地址（根据实际修改）
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '') // 去掉/api前缀
      }
    }
  }
})
```



#### 4 代码改造

##### 4.1 main.ts

初始化 Vue 实例、加载 Element Plus 组件和全局状态

##### 4.2 app.vue