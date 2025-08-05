import { ref, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'

export const useControlPanelDashboardComposable = () => {
  const axiosCore = inject('axios-core')
  const { t } = useI18n()
  const toast = useToast()

  const controlPanelDashboards = ref([])
  const controlPanelDashboard = ref({})

  const getControlPanelDashboards = async () => {
    await axiosCore
      .get('/int/user/controlpaneldashboards')
      .then((res) => {
        controlPanelDashboards.value = res.data
      })
      .catch(() => {})
  }

  const getControlPanelDashboardById = async (id) => {
    await axiosCore
      .get('/int/user/controlpaneldashboard/' + id)
      .then((res) => {
        controlPanelDashboard.value = res.data
      })
      .catch(() => {})
  }

  const createControlPanelDashboard = async (controlPanelDashboardObject) => {
    controlPanelDashboardObject = prepareDashboardGroups(controlPanelDashboardObject)
    await axiosCore
      .post('/int/user/controlpaneldashboard', controlPanelDashboardObject)
      .then(() => {
        toast.add({
          severity: 'success',
          summary: t(
            'control_panel_menu.control_panel_dashboard.creation_form.btn.create.toast.success.message'
          ),
          detail: t(
            'control_panel_menu.control_panel_dashboard.creation_form.btn.create.toast.success.detail'
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
            'control_panel_menu.control_panel_dashboard.creation_form.btn.create.toast.error.message'
          ),
          detail: t(
            'control_panel_menu.control_panel_dashboard.creation_form.btn.create.toast.error.detail'
          ),
          life: 3000
        })
      })
  }

  const updateControlPanelDashboard = async (controlPanelDashboardObject) => {
    controlPanelDashboardObject = prepareDashboardGroups(controlPanelDashboardObject)
    await axiosCore
      .put(
        '/int/user/controlpaneldashboard/' + controlPanelDashboardObject.id,
        controlPanelDashboardObject
      )
      .then(() => {
        toast.add({
          severity: 'success',
          summary: t(
            'control_panel_menu.control_panel_dashboard.creation_form.btn.update.toast.success.message'
          ),
          detail: t(
            'control_panel_menu.control_panel_dashboard.creation_form.btn.update.toast.success.detail'
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
            'control_panel_menu.control_panel_dashboard.creation_form.btn.update.toast.error.message'
          ),
          detail: t(
            'control_panel_menu.control_panel_dashboard.creation_form.btn.update.toast.error.detail'
          ),
          life: 3000
        })
      })
  }

  const prepareDashboardGroups = (controlPanelDashboardGroupObject) => {
    controlPanelDashboardGroupObject.associatedControlPanelDashboardGroups =
      controlPanelDashboardGroupObject.associatedControlPanelDashboardGroups.map(
        (controlPanelDashboardGroup) => {
          return controlPanelDashboardGroup.id
        }
      )
    return controlPanelDashboardGroupObject
  }

  const deleteControlPanelDashboard = async (controlPanelDashboardObject) => {
    await axiosCore
      .delete('/int/user/controlpaneldashboard/' + controlPanelDashboardObject.id)
      .then(() => {
        toast.add({
          severity: 'success',
          summary: t(
            'control_panel_menu.control_panel_dashboard.deletion.confirmation_dialog.toast.success.message'
          ),
          detail: t(
            'control_panel_menu.control_panel_dashboard.deletion.confirmation_dialog.toast.success.detail'
          ),
          life: 3000
        })
      })
      .catch(() => {
        toast.add({
          severity: 'error',
          summary: t(
            'control_panel_menu.control_panel_dashboard.deletion.confirmation_dialog.toast.error.message'
          ),
          detail: t(
            'control_panel_menu.control_panel_dashboard.deletion.confirmation_dialog.toast.error.detail'
          ),
          life: 3000
        })
      })
  }

  return {
    getControlPanelDashboards,
    controlPanelDashboards,
    getControlPanelDashboardById,
    controlPanelDashboard,
    createControlPanelDashboard,
    updateControlPanelDashboard,
    deleteControlPanelDashboard
  }
}
