<template>
  <div class="app-container">
    <el-container>
      <el-header>
        <h1>高德地图示例</h1>
      </el-header>
      <el-main>
        <div id="map-container" class="map-container"></div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import AMapLoader from '@amap/amap-jsapi-loader'

onMounted(async () => {
  const AMap = await AMapLoader.load({
    key: '', // 请在此处填入您的高德地图 Key
    version: '2.0',
    plugins: ['AMap.ToolBar', 'AMap.Scale']
  })

  const map = new AMap.Map('map-container', {
    zoom: 11,
    center: [116.397428, 39.90923]
  })

  map.addControl(new AMap.ToolBar())
  map.addControl(new AMap.Scale())
})
</script>

<style scoped>
.app-container {
  width: 100%;
  height: 100vh;
}

.map-container {
  width: 100%;
  height: 100%;
}

.el-header {
  background-color: #409EFF;
  color: white;
  text-align: center;
  line-height: 60px;
  padding: 0;
}

.el-main {
  padding: 0;
  height: calc(100vh - 60px);
}
</style> 