<template>
  <div class="grid" style="height: 100vh; overflow: auto">
    <!-- Navbar/Menu -->
    <div class="col-fixed" style="height: 95vh">
      <Menu style="height: 95vh"></Menu>
    </div>

    <div class="col" style="overflow: auto">
      <Card
        class="sophrosyne-card"
        style="height: 95vh; width: 99%; box-shadow: none !important; text-shadow: none !important"
      >
        <template #header>
          <div class="flex justify-content-end flex-wrap mr-8">
            <Button
              type="button"
              :label="$t('notification.action_confirmation.button')"
              icon="pi pi-bell"
              :badge="stats.requiring_confirmation"
              :severity="stats.requiring_confirmation == 0 ? 'primary' : 'danger'"
              size="large"
              class="mr-3"
              @click="router.push('/action-confirmation')"
            />
            <Button
              type="button"
              :label="$t('notification.active_recommendations.button')"
              icon="pi pi-bell"
              :badge="stats.active_recommendations"
              :severity="stats.active_recommendations == 0 ? 'primary' : 'danger'"
              size="large"
              @click="router.push('/action-recommendation')"
            />
          </div>
        </template>
        <template #content>
          <slot></slot>
        </template>
      </Card>
    </div>
  </div>
</template>
<script setup>
import { onBeforeMount } from 'vue'
import Menu from '@/components/navigation/Menu.vue'
import { useStatsComposable } from '@/composables/StatsComposable.js'
import router from '@/router'

const { getStats, stats } = useStatsComposable()

onBeforeMount(() => {
  getStats()
})
</script>
