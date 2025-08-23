import { ref, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'

export const useActionComposable = () => {
  const axiosCore = inject('axios-core')
  const { t } = useI18n()
  const toast = useToast()

  const runningStatus = ref({})
  const getActionStatusInfo = async (id) => {
    await axiosCore.get('/pull/user/executor/action/' + id + '/status').then((res) => {
      if (res.data.isRunning) {
        runningStatus.value.isRunning = 'success'
        runningStatus.value.isRunningText = 'running'
      } else {
        runningStatus.value.isRunning = 'info'
        runningStatus.value.isRunningText = 'idle'
      }
    })
  }

  const lastActionStatus = ref({
    status: { executionStartPoint: '', executionEndPoint: '' },
    exitCodeDisplayText: '',
    exitCodeDisplay: '',
    exitCodeDisplayTooltip: ''
  })

  const getLastActionStatusInfo = async (actionId) => {
    await axiosCore
      .get('/pull/user/archive/action/' + actionId + '/last')
      .then((res) => {
        lastActionStatus.value.status = res.data
        if (res.data.exitCode == 0) {
          lastActionStatus.value.exitCodeDisplay = 'success'
          lastActionStatus.value.exitCodeDisplayText = 'S'
          lastActionStatus.value.exitCodeDisplayTooltip = t(
            'actions.action.action_control.last_action_card.tooltip.execution_exit_code_success'
          )
        } else {
          lastActionStatus.value.exitCodeDisplay = 'danger'
          lastActionStatus.value.exitCodeDisplayText = 'F'
          lastActionStatus.value.exitCodeDisplayTooltip =
            t('actions.action.action_control.last_action_card.tooltip.execution_exit_code_error') +
            lastActionStatus.value.status.exitCode
        }
      })
      .catch((error) => {
        if (error.response.status) {
          switch (error.response.status) {
            case 404:
              lastActionStatus.value.exitCodeDisplayText = 'N'
              lastActionStatus.value.exitCodeDisplay = 'secondary'
              lastActionStatus.value.exitCodeDisplayTooltip = t(
                'actions.action.action_control.last_action_card.tooltip.execution_exit_code_never_run'
              )
              break
          }
        }
      })
  }

  const settings = ref({ submitting: false, succeeded: false })
  const createNewAction = async (event) => {
    settings.value.submitting = true
    event.allowedApikeys =
      event.allowedApikeysAsObject != undefined
        ? event.allowedApikeysAsObject.map((apikey) => {
          return apikey.apikeyname
        })
        : []
    event.requiresConfirmation = event.requiresConfirmation ? 1 : 0
    event.muted = event.muted ? 1 : 0
    await axiosCore
      .post('/int/user/action', event)
      .then(() => {
        toast.add({
          severity: 'success',
          summary: t('actions.action.action_creation_form.btn.create.toast.success.message'),
          detail: t('actions.action.action_creation_form.btn.create.toast.success.detail'),
          life: 3000
        })
        settings.value.succeeded = true
        settings.value.submitting = false
      })
      .catch(() => {
        toast.add({
          severity: 'error',
          summary: t('actions.action.action_creation_form.btn.create.toast.error.message'),
          detail: t('actions.action.action_creation_form.btn.create.toast.error.detail'),
          life: 3000
        })
        settings.value.succeeded = false
        settings.value.submitting = false
      })
  }
  const actions = ref([])

  const getActions = () => {
    axiosCore.get('/int/user/actions').then((res) => {
      actions.value = res.data
    })
  }

  const runningActions = ref([])
  const getRunningActions = async () => {
    await axiosCore.get('/int/client/executor/actions').then((res) => {
      runningActions.value = res.data
    })
  }

  const executeAction = async (actionId) => {
    return await axiosCore
      .post('/int/client/executor/action/' + actionId)
      .then((result) => {
        const status = result?.status
        if (status === 200) {
          toast.add({
            severity: 'success',
            summary: t('actions.action.action_control.btn.execute_action.toast.success.message'),
            detail: t('actions.action.action_control.btn.execute_action.toast.success.detail'),
            life: 3000
          })
        } else if (status === 201) {
          toast.add({
            severity: 'warn',
            summary: t('actions.action.action_control.btn.execute_action.toast.201.message'),
            detail: t('actions.action.action_control.btn.execute_action.toast.201.detail'),
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
            summary: t('actions.action.action_control.btn.execute_action.toast.406.message'),
            detail: t('actions.action.action_control.btn.execute_action.toast.406.detail'),
            life: 7000
          })
        } else if (status === 409) {
          toast.add({
            severity: 'warn',
            summary: t('actions.action.action_control.btn.execute_action.toast.409.message'),
            detail: t('actions.action.action_control.btn.execute_action.toast.409.detail'),
            life: 7000
          })
        } else {
          toast.add({
            severity: 'error',
            summary: t('actions.action.action_control.btn.execute_action.toast.error.message'),
            detail: t('actions.action.action_control.btn.execute_action.toast.error.detail'),
            life: 4000
          })
        }
        return false
      })
  }

  return {
    getRunningActions,
    runningActions,
    getActionStatusInfo,
    runningStatus,
    getLastActionStatusInfo,
    lastActionStatus,
    createNewAction,
    settings,
    getActions,
    actions,
    executeAction
  }
}
