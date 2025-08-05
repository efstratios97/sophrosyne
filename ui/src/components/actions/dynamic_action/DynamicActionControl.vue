<template>
  <div>
    <ActionControlTemplate
      :lastActionStatus="lastActionStatus"
      @stopAction="stopAction"
      @executeAction="toggleDynamicActionExecutor"
      @updateRateLimit="emits('updateRateLimit', $event)"
    ></ActionControlTemplate>
    <Dialog
      v-model:visible="showDynamicActionExecutor"
      modal
      maximizable
      v-if="showDynamicActionExecutor"
      @close="toggleDynamicActionExecutor"
      :style="{ width: '75vw', height: '75vh' }"
      :header="$t('actions.dynamic_action.action_dynamic_parameters_modal.title')"
    >
      <div>
        <DynamicActionExecutor
          :action="props.action"
          @close="toggleDynamicActionExecutor"
        ></DynamicActionExecutor>
      </div>
    </Dialog>
  </div>
</template>
<script setup>
import { ref, inject, onBeforeMount, onUnmounted, defineProps } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'
import ActionControlTemplate from '@/components/actions/utils/ActionControlTemplate.vue'
import DynamicActionExecutor from '@/components/actions/dynamic_action/DynamicActionExecutor.vue'
import { Dialog } from 'primevue'
import { useActionComposable } from '@/composables/ActionComposable.js'

const { getLastActionStatusInfo, lastActionStatus } = useActionComposable()

const toast = useToast()
const { t } = useI18n()
const axiosCore = inject('axios-core')
const props = defineProps(['action'])
const emits = defineEmits(['isDynamicActionExecutorToggled', 'updateRateLimit'])

var statusCaller = ref({})
onBeforeMount(() => {
  getLastActionStatusInfo(props.action.id)
  statusCaller.value = setInterval(() => {
    getLastActionStatusInfo(props.action.id)
  }, 2000)
})

onUnmounted(() => {
  clearInterval(statusCaller)
})

const showDynamicActionExecutor = ref(false)
const toggleDynamicActionExecutor = () => {
  showDynamicActionExecutor.value = !showDynamicActionExecutor.value
  emits('isDynamicActionExecutorToggled', showDynamicActionExecutor.value)
}

const stopAction = () => {
  axiosCore
    .post('/int/client/executor/dynamicaction/' + props.action.runningId + '/stop')
    .then(() => {
      toast.add({
        severity: 'success',
        summary: t('actions.action.action_control.btn.stop_action.toast.success.message'),
        detail: t('actions.action.action_control.btn.stop_action.toast.success.detail'),
        life: 3000
      })
      getLastActionStatusInfo(props.action.id)
    })
    .catch(() => {
      toast.add({
        severity: 'error',
        summary: t('actions.action.action_control.btn.stop_action.toast.error.message'),
        detail: t('actions.action.action_control.btn.stop_action.toast.error.detail'),
        life: 3000
      })
      getLastActionStatusInfo(props.action.id)
    })
}
</script>
