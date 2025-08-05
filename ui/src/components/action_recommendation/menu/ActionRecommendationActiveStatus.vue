<template>
  <Badge
    :value="actionRecommendationStatusInfo.isActiveText"
    size="large"
    :severity="actionRecommendationStatusInfo.isActive"
  ></Badge>
</template>
<script setup>
import { onMounted, onUnmounted, defineProps } from 'vue'
import { useActionRecommendationComposable } from '@/composables/ActionRecommendationComposable.js'

const { getActionRecommendationStatusInfo, actionRecommendationStatusInfo } =
  useActionRecommendationComposable()
const props = defineProps(['id'])

var statusCaller = null
onMounted(() => {
  getActionRecommendationStatusInfo(props.id)
  statusCaller = setInterval(() => {
    getActionRecommendationStatusInfo(props.id)
  }, 4000)

  onUnmounted(() => {
    clearInterval(statusCaller)
  })
})
</script>
