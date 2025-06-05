<template>
  <div class="container">
    <h1>高德地图示例</h1>
    <div id="map-container"></div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'

onMounted(() => {
  // 这里将添加高德地图的初始化代码
})
</script>

<style scoped>
.container {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
}

#map-container {
  width: 100%;
  height: calc(100vh - 60px);
  margin-top: 10px;
}

h1 {
  margin: 20px 0;
  color: #333;
}
</style> 