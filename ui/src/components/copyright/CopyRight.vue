<template>
  <footer class="flex justify-content-center align-items-center py-3">
    <small class="text-color-secondary"
      >&copy; {{ currentYear }}
      <a href="https://github.com/efstratios97" target="_blank">{{ $t('copyright.company') }}</a
      >{{ $t('copyright.rights') }}
      <a href="https://docs.sophrosyne.ep-monitoring.com/#/" target="_blank">{{
        $t('copyright.docs')
      }}</a></small
    >
  </footer>
</template>

<script setup>
import { computed } from 'vue'

const currentYear = computed(() => new Date().getFullYear())
</script>
