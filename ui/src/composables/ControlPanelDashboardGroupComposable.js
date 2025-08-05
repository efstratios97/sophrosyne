import { ref, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'

export const useControlPanelDashboardGroupComposable = () => {
  const axiosCore = inject('axios-core')
  const { t } = useI18n()
  const toast = useToast()

  const controlPanelDashboardGroups = ref([])
  const controlPanelDashboardGroup = ref({})

  const getControlPanelDashboardGroups = async () => {
    await axiosCore
      .get('/int/user/controlpaneldashboardgroups')
      .then((res) => {
        controlPanelDashboardGroups.value = res.data
      })
      .catch(() => {})
  }

  const getControlPanelDashboardGroupById = async (id) => {
    await axiosCore
      .get('/int/user/controlpaneldashboardgroup/' + id)
      .then((res) => {
        controlPanelDashboardGroup.value = res.data
      })
      .catch(() => {})
  }

  const createControlPanelDashboardGroup = async (controlPanelDashboardGroupObject) => {
    controlPanelDashboardGroupObject = prepareActions(controlPanelDashboardGroupObject)
    await axiosCore
      .post('/int/user/controlpaneldashboardgroup', controlPanelDashboardGroupObject)
      .then(() => {
        toast.add({
          severity: 'success',
          summary: t(
            'control_panel_menu.control_panel_dashboard_group.creation_form.btn.create.toast.success.message'
          ),
          detail: t(
            'control_panel_menu.control_panel_dashboard_group.creation_form.btn.create.toast.success.detail'
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
            'control_panel_menu.control_panel_dashboard_group.creation_form.btn.create.toast.error.message'
          ),
          detail: t(
            'control_panel_menu.control_panel_dashboard_group.creation_form.btn.create.toast.error.detail'
          ),
          life: 3000
        })
      })
  }

  const updateControlPanelDashboardGroup = async (controlPanelDashboardGroupObject) => {
    controlPanelDashboardGroupObject = prepareActions(controlPanelDashboardGroupObject)
    await axiosCore
      .put(
        '/int/user/controlpaneldashboardgroup/' + controlPanelDashboardGroupObject.id,
        controlPanelDashboardGroupObject
      )
      .then(() => {
        toast.add({
          severity: 'success',
          summary: t(
            'control_panel_menu.control_panel_dashboard_group.creation_form.btn.update.toast.success.message'
          ),
          detail: t(
            'control_panel_menu.control_panel_dashboard_group.creation_form.btn.update.toast.success.detail'
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
            'control_panel_menu.control_panel_dashboard_group.creation_form.btn.update.toast.error.message'
          ),
          detail: t(
            'control_panel_menu.control_panel_dashboard_group.creation_form.btn.update.toast.error.detail'
          ),
          life: 3000
        })
      })
  }

  const prepareActions = (controlPanelDashboardGroupObject) => {
    controlPanelDashboardGroupObject.associatedActions =
      controlPanelDashboardGroupObject.associatedActions.map((action) => {
        return action.id
      })
    controlPanelDashboardGroupObject.associatedDynamicActions =
      controlPanelDashboardGroupObject.associatedDynamicActions.map((dynamicAction) => {
        return dynamicAction.id
      })
    return controlPanelDashboardGroupObject
  }

  const deleteControlPanelDashboardGroup = async (controlPanelDashboardGroupObject) => {
    await axiosCore
      .delete('/int/user/controlpaneldashboardgroup/' + controlPanelDashboardGroupObject.id)
      .then(() => {
        toast.add({
          severity: 'success',
          summary: t(
            'control_panel_menu.control_panel_dashboard_group.deletion.confirmation_dialog.toast.success.message'
          ),
          detail: t(
            'control_panel_menu.control_panel_dashboard_group.deletion.confirmation_dialog.toast.success.detail'
          ),
          life: 3000
        })
      })
      .catch(() => {
        toast.add({
          severity: 'error',
          summary: t(
            'control_panel_menu.control_panel_dashboard_group.deletion.confirmation_dialog.toast.error.message'
          ),
          detail: t(
            'control_panel_menu.control_panel_dashboard_group.deletion.confirmation_dialog.toast.error.detail'
          ),
          life: 3000
        })
      })
  }

  return {
    getControlPanelDashboardGroups,
    controlPanelDashboardGroups,
    getControlPanelDashboardGroupById,
    controlPanelDashboardGroup,
    createControlPanelDashboardGroup,
    updateControlPanelDashboardGroup,
    deleteControlPanelDashboardGroup
  }
}
