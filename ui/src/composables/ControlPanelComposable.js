import { ref, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'

export const useControlPanelComposable = () => {
  const axiosCore = inject('axios-core')
  const { t } = useI18n()
  const toast = useToast()

  const controlPanels = ref([])
  const controlPanel = ref({})

  const getControlPanels = async () => {
    await axiosCore
      .get('/int/user/controlpanels')
      .then((res) => {
        controlPanels.value = res.data
      })
      .catch(() => {})
  }

  const getControlPanelById = async (id) => {
    await axiosCore
      .get('/int/user/controlpanel/' + id)
      .then((res) => {
        controlPanel.value = res.data
      })
      .catch(() => {})
  }

  const createControlPanel = async (controlPanelObject) => {
    controlPanelObject = prepareControlPanel(controlPanelObject)
    await axiosCore
      .post('/int/user/controlpanel', controlPanelObject)
      .then(() => {
        toast.add({
          severity: 'success',
          summary: t(
            'control_panel_menu.control_panel.creation_form.btn.create.toast.success.message'
          ),
          detail: t(
            'control_panel_menu.control_panel.creation_form.btn.create.toast.success.detail'
          ),
          life: 3000
        })
        setTimeout(function () {
          location.reload()
        }, 1500)
      })
      .catch(() => {
        toast.add({
          severity: 'error',
          summary: t(
            'control_panel_menu.control_panel.creation_form.btn.create.toast.error.message'
          ),
          detail: t('control_panel_menu.control_panel.creation_form.btn.create.toast.error.detail'),
          life: 3000
        })
      })
  }

  const updateControlPanel = async (controlPanelObject) => {
    controlPanelObject = prepareControlPanel(controlPanelObject)
    await axiosCore
      .put('/int/user/controlpanel/' + controlPanelObject.id, controlPanelObject)
      .then(() => {
        toast.add({
          severity: 'success',
          summary: t(
            'control_panel_menu.control_panel.creation_form.btn.create.toast.success.message'
          ),
          detail: t(
            'control_panel_menu.control_panel.creation_form.btn.create.toast.success.detail'
          ),
          life: 3000
        })
        setTimeout(function () {
          location.reload()
        }, 1500)
      })
      .catch(() => {
        toast.add({
          severity: 'error',
          summary: t('control_panel_menu.control_panel.update_form.btn.update.toast.error.message'),
          detail: t('control_panel_menu.control_panel.update_form.btn.update.toast.error.detail'),
          life: 3000
        })
      })
  }

  const prepareControlPanel = (controlPanelDashboardGroupObject) => {
    controlPanelDashboardGroupObject.associatedControlPanelDashboards =
      controlPanelDashboardGroupObject.associatedControlPanelDashboards.map(
        (controlPanelDashboard) => {
          return controlPanelDashboard.id
        }
      )
    controlPanelDashboardGroupObject.associatedUsers =
      controlPanelDashboardGroupObject.associatedUsers.map((user) => {
        return user.username
      })
    return controlPanelDashboardGroupObject
  }

  const deleteControlPanel = async (controlPanelObject) => {
    await axiosCore
      .delete('/int/user/controlpanel/' + controlPanelObject.id)
      .then(() => {
        toast.add({
          severity: 'success',
          summary: t(
            'control_panel_menu.control_panel.deletion.confirmation_dialog.toast.success.message'
          ),
          detail: t(
            'control_panel_menu.control_panel.deletion.confirmation_dialog.toast.success.detail'
          ),
          life: 3000
        })
      })
      .catch(() => {
        toast.add({
          severity: 'error',
          summary: t(
            'control_panel_menu.control_panel.deletion.confirmation_dialog.toast.error.message'
          ),
          detail: t(
            'control_panel_menu.control_panel.deletion.confirmation_dialog.toast.error.detail'
          ),
          life: 3000
        })
      })
  }

  return {
    getControlPanels,
    controlPanels,
    getControlPanelById,
    controlPanel,
    createControlPanel,
    updateControlPanel,
    deleteControlPanel
  }
}
