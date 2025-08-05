<template>
  <div style="overflow: auto" class="background-scoped">
    <div class="flex justify-content-center flex-wrap m-8" style="overflow: auto">
      <LoginMask style="overflow: auto" />
    </div>
  </div>
</template>

<script setup>
import LoginMask from '@/components/login/LoginMask.vue'
</script>
<style scoped>
.background-scoped {
  background-image: url('@/assets/images/SOPHROSYNE_BACKGROUND.jpg');
  background-blend-mode: lighten;
  background-attachment: fixed;
  background-size: cover;
  background-repeat: no-repeat;
  height: 100vh;
  width: 100vw;
}
</style>
