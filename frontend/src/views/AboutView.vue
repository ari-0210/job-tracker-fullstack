<template>
  <div class="max-w-4xl mx-auto p-6">
    <n-card title="DeadlineTracker" size="huge" class="rounded-3xl shadow-lg">
      <div class="space-y-6 text-gray-600">
        <p class="text-lg">这是一个进度管理工具，旨在帮助更高效的完成计划。</p>

        <n-divider title-placement="left">技术架构</n-divider>
        <div class="grid grid-cols-2 gap-4">
          <n-tag type="info" bordered>Spring Boot 3.x</n-tag>
          <n-tag type="info" bordered>Vue 3.x + Vite</n-tag>
          <n-tag type="info" bordered>Redis 7.x 缓存</n-tag>
          <n-tag type="info" bordered>MySQL 8.x 持久化</n-tag>
          <n-tag type="info" bordered>Tailwind CSS v4</n-tag>
          <n-tag type="info" bordered>Naive UI</n-tag>
        </div>

        <n-divider title-placement="left">核心功能</n-divider>
        <n-list>
          <n-list-item>🔥 实时可视化看板：一眼掌握投递进度</n-list-item>
          <n-list-item>⚡ Redis 高性能缓存：Dashboard 秒级响应</n-list-item>
          <n-list-item>📅 紧迫事项提醒：拒绝错过任何一个 DDL</n-list-item>
        </n-list>

        <div class="bg-blue-50 p-4 rounded-2xl border border-blue-100 text-sm">
          Developer:Ari
        </div>
      </div>
    </n-card>
  </div>
</template>
