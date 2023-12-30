<script setup lang="ts">
</script>

<template>
  <Suspense>
    <RouterView :key="Date()"/>
    <template #fallback>
      Loading...
    </template>
  </Suspense>
</template>
