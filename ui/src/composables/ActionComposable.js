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

  const settings = ref({ submitting: false, succeded: false })
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
        settings.value.succeded = true
        settings.value.submitting = false
      })
      .catch(() => {
        toast.add({
          severity: 'error',
          summary: t('actions.action.action_creation_form.btn.create.toast.error.message'),
          detail: t('actions.action.action_creation_form.btn.create.toast.error.detail'),
          life: 3000
        })
        settings.value.succeded = false
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
    actions
  }
}
