import { ref, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'

export const useDynamicActionComposable = () => {
  const axiosCore = inject('axios-core')
  const { t } = useI18n()
  const toast = useToast()
  const dynamicActions = ref([])

  const getDynamicActions = () => {
    axiosCore.get('/int/user/dynamicactions').then((res) => {
      dynamicActions.value = res.data
    })
  }

  const runningDynamicActions = ref([])

  const getRunningDynamicActions = async () => {
    await axiosCore.get('/int/client/executor/dynamicactions').then((res) => {
      runningDynamicActions.value = res.data
    })
  }

  const runningDynamicActionsById = ref([])

  const getRunningDynamicActionsById = async (id) => {
    await axiosCore.get('/int/client/executor/running/dynamicaction/' + id).then((res) => {
      runningDynamicActionsById.value = res.data
    })
  }

  const executeAction = async (actionId, userPassedParameters) => {
    return await axiosCore
      .post('/int/client/executor/dynamicaction/' + actionId, userPassedParameters)
      .then((result) => {
        const status = result?.status
        if (status === 200) {
          toast.add({
            severity: 'success',
            summary: t('actions.dynamic_action.action_control.btn.execute_action.toast.success.message'),
            detail: t('actions.dynamic_action.action_control.btn.execute_action.toast.success.detail'),
            life: 3000
          })
        } else if (status === 201) {
          toast.add({
            severity: 'warn',
            summary: t('actions.dynamic_action.action_control.btn.execute_action.toast.201.message'),
            detail: t('actions.dynamic_action.action_control.btn.execute_action.toast.201.detail'),
            life: 7000
          })
        }
        return true
      })
      .catch(error => {
        const status = error.response?.status
        if (status === 406) {
          toast.add({
            severity: 'warn',
            summary: t('actions.dynamic_action.action_control.btn.execute_action.toast.406.message'),
            detail: t('actions.dynamic_action.action_control.btn.execute_action.toast.406.detail'),
            life: 7000
          })
        } else if (status === 409) {
          toast.add({
            severity: 'warn',
            summary: t('actions.dynamic_action.action_control.btn.execute_action.toast.409.message'),
            detail: t('actions.dynamic_action.action_control.btn.execute_action.toast.409.detail'),
            life: 7000
          })
        } else {
          toast.add({
            severity: 'error',
            summary: t('actions.dynamic_action.action_control.btn.execute_action.toast.error.message'),
            detail: t('actions.dynamic_action.action_control.btn.execute_action.toast.error.detail'),
            life: 4000
          })
        }
        return false
      })
  }

  return {
    getDynamicActions,
    dynamicActions,
    getRunningDynamicActions,
    runningDynamicActions,
    getRunningDynamicActionsById,
    runningDynamicActionsById,
    executeAction
  }
}
