<template>
  <div class="px-4 py-5 md:px-6 lg:px-8">
    <div class="grid">
      <div class="col-3 sophrosyne-dashboard-stats-card">
        <DashboardStatsCard
          :icon_bg_color="'bg-blue-100'"
          :title="$t('dashboard.stats.current.title')"
          style="min-height: 80%"
        >
          <template #icon><i class="pi pi-bolt text-blue-500 text-xl"></i></template>
          <template #value>
            <div class="text-900 font-medium text-xl">{{ stats.current_running }}</div>
          </template>
          <template #subtitle>
            <span class="text-500">{{ $t('dashboard.stats.current.subtitle_text') }}</span>
          </template>
        </DashboardStatsCard>
      </div>
      <div v-if="lastActionStatus != undefined" class="col-3 sophrosyne-dashboard-stats-card">
        <DashboardStatsCard
          :icon_bg_color="'bg-orange-100'"
          :title="$t('dashboard.stats.last.title')"
          style="min-height: 80%"
        >
          <template #icon><i class="pi pi-book text-orange-500 text-xl"></i></template>
          <template #value>
            <div class="grid">
              <Badge
                :value="lastActionStatus.exitCodeDisplayText"
                size="large"
                :severity="lastActionStatus.exitCodeDisplay"
                v-tooltip="{
                  value: lastActionStatus.exitCodeDisplayTooltip,
                  showDelay: 100,
                  hideDelay: 300
                }"
              ></Badge>

              <div class="col-9">
                <h5 class="m-0">
                  {{ lastActionStatus.status.actionName }}
                </h5>
              </div>
            </div>
          </template>
          <template #subtitle>
            <span class="text-500"
              >{{ $t('dashboard.stats.last.subtitle_text_1') }}&nbsp;{{
                lastActionStatus.status.executionStartPoint != null
                  ? lastActionStatus.status.executionStartPoint.substring(
                      0,
                      lastActionStatus.status.executionStartPoint.lastIndexOf('.')
                    )
                  : ''
              }}</span
            >
          </template>
        </DashboardStatsCard>
      </div>
      <div class="col-3 sophrosyne-dashboard-stats-card">
        <DashboardStatsCard
          :icon_bg_color="'bg-cyan-100'"
          :title="$t('dashboard.stats.total.title')"
          style="min-height: 80%"
        >
          <template #icon><i class="pi pi-chart-bar text-cyan-500 text-xl"></i></template>
          <template #value>
            <div class="text-900 font-medium text-xl">{{ stats.total_run }}</div>
          </template>
          <template #subtitle>
            <span class="text-500 font-medium">{{ stats.total_external }}</span>
            <span class="text-500">&nbsp;{{ $t('dashboard.stats.total.subtitle_text_1') }}</span>
            &nbsp;|&nbsp;
            <span class="text-500 font-medium">{{ stats.total_internal }}</span>
            <span class="text-500">&nbsp;{{ $t('dashboard.stats.total.subtitle_text_2') }}</span>
          </template>
        </DashboardStatsCard>
      </div>
      <div class="col-3 sophrosyne-dashboard-stats-card">
        <DashboardStatsCard
          :icon_bg_color="'bg-green-100'"
          :title="$t('dashboard.stats.success.title')"
          style="min-height: 80%"
        >
          <template #icon><i class="pi pi-check text-green-500 text-xl"></i></template>
          <template #value>
            <div class="text-900 font-medium text-xl">
              {{ ((stats.total_success / stats.total_run) * 100).toFixed(2) }} %
            </div>
          </template>
          <template #subtitle>
            <span class="text-500 font-medium">{{ stats.total_success }}</span>
            <span class="text-500">&nbsp;{{ $t('dashboard.stats.success.subtitle_text_1') }}</span>
            &nbsp;|&nbsp;
            <span class="text-500 font-medium">{{ stats.total_fail }}</span>
            <span class="text-500">&nbsp;{{ $t('dashboard.stats.success.subtitle_text_2') }}</span>
          </template>
        </DashboardStatsCard>
      </div>
      <div v-if="stats.timeline" class="col-5">
        <DashboardTimeline :timeline="stats.timeline" />
      </div>
      <div class="col-7">
        <Card class="sophrosyne-card" style="min-height: 100%">
          <template #title>
            {{ $t('dashboard.runningActions.title') }}
          </template>
          <template #subtitle>
            {{ $t('dashboard.runningActions.sub_title') }}
          </template>
          <template #content>
            <RunningActionsDisplay></RunningActionsDisplay>
          </template>
        </Card>
      </div>
    </div>
  </div>
</template>
<script setup>
import { onBeforeMount, onMounted, onUnmounted } from 'vue'
import { useActionArchiveComposable } from '@/composables/ActionArchiveComposable.js'
import { useActionComposable } from '@/composables/ActionComposable.js'
import { useStatsComposable } from '@/composables/StatsComposable.js'
import DashboardStatsCard from '@/components/dashboard/DashboardStatsCard.vue'
import DashboardTimeline from '@/components/dashboard/DashboardTimeline.vue'
import RunningActionsDisplay from '@/components/actions/utils/RunningActionsDisplay.vue'

const { getActionArchive, actionsArchive } = useActionArchiveComposable()
const { getLastActionStatusInfo, lastActionStatus } = useActionComposable()
const { getStats, stats } = useStatsComposable()

var statusCaller = null
onBeforeMount(() => {
  getActionArchive()
  getStats()
  statusCaller = setInterval(() => {
    getStats()
  }, 5000)
})

onMounted(() => {
  getLastActionStats()
})

onUnmounted(() => {
  clearInterval(statusCaller)
})

const getLastActionStats = async () => {
  await getActionArchive()
  getLastActionStatusInfo(actionsArchive.value[0].actionId)
}
</script>
<style scoped>
.sophrosyne-dashboard-stats-card {
  min-height: 210px;
}
</style>
