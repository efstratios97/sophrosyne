<template>
  <div class="grid" style="height: 96vh; overflow: auto">
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
          <div class="flex align-items-center justify-content-between flex-wrap w-full">
            <div class="flex align-items-center">
              <FreeVersionBanner></FreeVersionBanner>
            </div>

            <div class="flex justify-content-end flex-wrap mr-3 mt-1">
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
import FreeVersionBanner from '@/components/infomercial/FreeVersionBanner.vue'
import { useStatsComposable } from '@/composables/StatsComposable.js'
import router from '@/router'

const { getStats, stats } = useStatsComposable()

onBeforeMount(() => {
  getStats()
})
</script>
