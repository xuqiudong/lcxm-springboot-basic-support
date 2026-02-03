import {defineConfig} from "vite";
import vue from "@vitejs/plugin-vue";
import Icons from "unplugin-icons/vite";
import IconsResolver from "unplugin-icons/resolver";
// 自动导入工具
import AutoImport from "unplugin-auto-import/vite";
import Components from "unplugin-vue-components/vite";
import {ElementPlusResolver} from "unplugin-vue-components/resolvers";

export default defineConfig({
  plugins: [
    vue(),
    // 自动导入Element Plus的组件和API
    AutoImport({
      resolvers: [
        ElementPlusResolver(),
        // 自动导入图标组件
        IconsResolver({
          prefix: "Icon",
        }),
      ],
      // 自动导入Vue的ref、reactive等API
      imports: ["vue"],
    }),
    Components({
      resolvers: [
        // 自动注册图标组件
        IconsResolver({
          enabledCollections: ["ep"],
        }),
        ElementPlusResolver(),
      ],
    }),
    Icons({
      autoInstall: true
    })
  ],
  // 打包输出到后端resources/static/generator目录
  build: {
    outDir: "../src/main/resources/static/generator", // 相对路径，指向后端资源目录
    emptyOutDir: true, // 打包前清空目标目录
    assetsDir: "assets", // 静态资源（js/css）存放目录
  },
  // 开发环境跨域代理（解决前端调用后端接口的跨域问题）
  server: {
    port: 3000, // 前端开发服务器端口
    proxy: {
      "/api": {
        target: "http://localhost:8080", // 后端接口地址（根据实际修改）
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ""), // 去掉/api前缀
      },
    },
  },
});
