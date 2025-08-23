<template>
  <ActionControlTemplate
    :lastActionStatus="lastActionStatus"
    @stopAction="stopAction"
    @executeAction="executeAction(props.id)"
    @updateRateLimit="emits('updateRateLimit', $event)"
  ></ActionControlTemplate>
</template>
<script setup>
import ActionControlTemplate from '@/components/actions/utils/ActionControlTemplate.vue'
import { ref, inject, onMounted, onUnmounted, defineProps } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'
import { useActionComposable } from '@/composables/ActionComposable.js'

const { getLastActionStatusInfo, lastActionStatus, executeAction } = useActionComposable()
const toast = useToast()
const { t } = useI18n()
const axiosCore = inject('axios-core')
const props = defineProps(['id'])
const emits = defineEmits(['updateRateLimit'])

var statusCaller = ref({})
onMounted(() => {
  getLastActionStatusInfo(props.id)
  statusCaller.value = setInterval(() => {
    getLastActionStatusInfo(props.id)
  }, 2000)
})

onUnmounted(() => {
  clearInterval(statusCaller)
})

const stopAction = () => {
  axiosCore
    .post('/int/client/executor/action/' + props.id + '/stop')
    .then(() => {
      toast.add({
        severity: 'success',
        summary: t('actions.action.action_control.btn.stop_action.toast.success.message'),
        detail: t('actions.action.action_control.btn.stop_action.toast.success.detail'),
        life: 3000
      })
      getLastActionStatusInfo(props.id)
    })
    .catch(() => {
      toast.add({
        severity: 'error',
        summary: t('actions.action.action_control.btn.stop_action.toast.error.message'),
        detail: t('actions.action.action_control.btn.stop_action.toast.error.detail'),
        life: 3000
      })
      getLastActionStatusInfo(props.id)
    })
}
</script>
