<template>
  <div>
    <Toolbar class="mb-4">
      <template #start>
        <Button
          label="New"
          icon="pi pi-plus"
          severity="success"
          class="mr-2"
          @click="toggleCreateForm"
        />
        <Button
          label="Update"
          icon="pi pi-replay"
          severity="primary"
          class="mr-2"
          @click="toggleUpdateForm"
          :disabled="
            selectedControlPanelObjects.length == 0 || selectedControlPanelObjects.length > 1
          "
        />
        <Button
          label="Delete"
          icon="pi pi-trash"
          severity="danger"
          @click="deleteControlPanelObjects"
          :disabled="!selectedControlPanelObjects || !selectedControlPanelObjects.length"
        />
      </template>

      <template #end> </template>
    </Toolbar>
    <div class="sophrosyne-card">
      <DataTable
        v-model:selection="selectedControlPanelObjects"
        :value="controlPanelObjectsView"
        dataKey="id"
        tableStyle="min-width: 60rem"
        scrollable
        scrollHeight="100vh"
      >
        <Column selectionMode="multiple" style="width: 3rem" :exportable="false"></Column>
        <Column
          field="id"
          :header="$t('control_panel_menu.' + I18nSource + '.menu.datatable.column_header.id')"
        ></Column>
        <Column
          field="name"
          :header="$t('control_panel_menu.' + I18nSource + '.menu.datatable.column_header.name')"
        ></Column>

        <Column
          field="description"
          :header="
            $t('control_panel_menu.' + I18nSource + '.menu.datatable.column_header.description')
          "
        >
        </Column>
        <Column
          field="associatedControlPanelDashboards"
          :header="
            $t(
              'control_panel_menu.' +
                I18nSource +
                '.menu.datatable.column_header.associated_control_panel_dashboard'
            )
          "
          v-if="props.isControlPanel"
        >
          <template #body="slotProps">
            <Dropdown
              :options="slotProps.data.associatedControlPanelDashboards"
              :placeholder="
                $t(
                  'control_panel_menu.' +
                    I18nSource +
                    '.menu.datatable.column_header.associated_control_panel_dashboard'
                )
              "
              class="w-full md:w-14rem"
            />
          </template>
        </Column>
        <Column
          field="associatedUsers"
          :header="
            $t(
              'control_panel_menu.' + I18nSource + '.menu.datatable.column_header.associated_users'
            )
          "
          v-if="props.isControlPanel"
        >
          <template #body="slotProps">
            <Dropdown
              :options="slotProps.data.associatedUsers"
              :placeholder="
                $t(
                  'control_panel_menu.' +
                    I18nSource +
                    '.menu.datatable.column_header.associated_users'
                )
              "
              class="w-full md:w-14rem"
              v-if="props.isControlPanel"
            />
          </template>
        </Column>
        <Column
          field="associatedControlPanelDashboardGroups"
          :header="
            $t(
              'control_panel_menu.' +
                I18nSource +
                '.menu.datatable.column_header.associated_control_panel_dashboard_groups'
            )
          "
          v-if="props.isControlPanelDashboard"
        >
          <template #body="slotProps">
            <Dropdown
              :options="slotProps.data.associatedControlPanelDashboardGroups"
              :placeholder="
                $t(
                  'control_panel_menu.' +
                    I18nSource +
                    '.menu.datatable.column_header.associated_control_panel_dashboard_groups'
                )
              "
              class="w-full md:w-14rem"
            />
          </template>
        </Column>
        <Column
          field="associatedActions"
          :header="
            $t(
              'control_panel_menu.' +
                I18nSource +
                '.menu.datatable.column_header.associated_actions'
            )
          "
          v-if="props.isControlPanelDashboardGroup"
        >
          <template #body="slotProps">
            <Dropdown
              :options="slotProps.data.associatedActions"
              :placeholder="
                $t(
                  'control_panel_menu.' +
                    I18nSource +
                    '.menu.datatable.column_header.associated_actions'
                )
              "
              class="w-full md:w-14rem"
            />
          </template>
        </Column>
        <Column
          field="associatedDynamicActions"
          :header="
            $t(
              'control_panel_menu.' +
                I18nSource +
                '.menu.datatable.column_header.associated_dynamic_actions'
            )
          "
          v-if="props.isControlPanelDashboardGroup"
        >
          <template #body="slotProps">
            <Dropdown
              :options="slotProps.data.associatedDynamicActions"
              :placeholder="
                $t(
                  'control_panel_menu.' +
                    I18nSource +
                    '.menu.datatable.column_header.associated_dynamic_actions'
                )
              "
              class="w-full md:w-14rem"
            />
          </template>
        </Column>
        <Column
          field="position"
          :header="
            $t('control_panel_menu.' + I18nSource + '.menu.datatable.column_header.position')
          "
          v-if="props.isControlPanelDashboard || props.isControlPanelDashboardGroup"
        />
      </DataTable>
      <ConfirmDialog :group="'deleteControlPanelObjects' + I18nSource">
        <template #message="slotProps">
          <div
            class="flex flex-column align-items-center w-full gap-3 border-bottom-1 surface-border"
          >
            <i :class="slotProps.message.icon" class="text-6xl text-red-500"></i>
            <p>{{ slotProps.message.message }}</p>
            <ul
              v-for="controlPanelObjects in selectedControlPanelObjects"
              :key="controlPanelObjects"
              class="list-disc"
            >
              <li>{{ controlPanelObjects.name }}</li>
            </ul>
          </div>
        </template>
      </ConfirmDialog>
      <Dialog
        v-model:visible="showCreateForm"
        modal
        maximizable
        :header="$t('control_panel_menu.' + I18nSource + '.creation_form.modal_header')"
        v-if="showCreateForm"
        @close="toggleCreateForm"
      >
        <CreationFormTemplate
          :arbitraryControlPanelObject="{}"
          :isControlPanel="props.isControlPanel"
          :isControlPanelDashboard="props.isControlPanelDashboard"
          :isControlPanelDashboardGroup="props.isControlPanelDashboardGroup"
          @getArbitraryControlPanelObjects="getCorrespondingControlPanelObjects"
          @close="toggleCreateForm"
        ></CreationFormTemplate>
      </Dialog>
      <Dialog
        v-model:visible="showUpdateForm"
        modal
        maximizable
        :header="$t('control_panel_menu.' + I18nSource + '.creation_form.modal_header_update')"
        v-if="showUpdateForm"
        @close="toggleUpdateForm"
      >
        <CreationFormTemplate
          :arbitraryControlPanelObject="
            controlPanelObjects.find(
              (controlPanelObject) => controlPanelObject.id === selectedControlPanelObjects[0].id
            )
          "
          :isControlPanel="props.isControlPanel"
          :isControlPanelDashboard="props.isControlPanelDashboard"
          :isControlPanelDashboardGroup="props.isControlPanelDashboardGroup"
          :isUpdate="true"
          @getArbitraryControlPanelObjects="getCorrespondingControlPanelObjects"
          @close="toggleUpdateForm"
        ></CreationFormTemplate
      ></Dialog>
      <Toast />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import { useControlPanelComposable } from '@/composables/ControlPanelComposable.js'
import { useControlPanelDashboardComposable } from '@/composables/ControlPanelDashboardComposable.js'
import { useControlPanelDashboardGroupComposable } from '@/composables/ControlPanelDashboardGroupComposable.js'
import { useDynamicActionComposable } from '@/composables/DynamicActionComposable.js'
import { useActionComposable } from '@/composables/ActionComposable.js'
import { useUserComposable } from '@/composables/UserComposable.js'
import CreationFormTemplate from '@/components/controlpanel/menu/utils/CreationFormTemplate.vue'

const toast = useToast()
const { t } = useI18n()
const axiosCore = inject('axios-core')
const controlPanelObjects = ref([])
const controlPanelObjectsView = ref([])
const selectedControlPanelObjects = ref([])
const { getControlPanels, controlPanels } = useControlPanelComposable()
const { getControlPanelDashboards, controlPanelDashboards } = useControlPanelDashboardComposable()
const { getControlPanelDashboardGroups, controlPanelDashboardGroups } =
  useControlPanelDashboardGroupComposable()
const { getUsers, users } = useUserComposable()
const { getDynamicActions, dynamicActions } = useDynamicActionComposable()
const { getActions, actions } = useActionComposable()

const props = defineProps([
  'arbitraryControlPanelObject',
  'forUpdate',
  'isControlPanel',
  'isControlPanelDashboard',
  'isControlPanelDashboardGroup'
])
const I18nSource = props.isControlPanel
  ? ref('control_panel')
  : props.isControlPanelDashboard
    ? ref('control_panel_dashboard')
    : ref('control_panel_dashboard_group')

onMounted(() => {
  getDynamicActions()
  getActions()
  getControlPanelDashboards()
  getControlPanelDashboardGroups().then(() => {
    getUsers().then(() => {
      getCorrespondingControlPanelObjects()
    })
  })
})

const showCreateForm = ref(false)
const toggleCreateForm = () => {
  showCreateForm.value = !showCreateForm.value
}

const showUpdateForm = ref(false)
const toggleUpdateForm = () => {
  showUpdateForm.value = !showUpdateForm.value
}

const getCorrespondingControlPanelObjects = () => {
  controlPanelObjects.value = []
  if (props.isControlPanel) {
    controlPanels.value = []
    getControlPanels().then(() => {
      Object.assign(controlPanelObjects.value, controlPanels.value)
      controlPanelObjectsView.value = JSON.parse(JSON.stringify(controlPanels.value))

      controlPanelObjectsView.value = controlPanelObjectsView.value.map((controlPanel) => {
        controlPanel.associatedUsers = controlPanel.associatedUsers.map((id) => {
          return users.value.find((user) => user.id === id)['username']
        })
        controlPanel.associatedControlPanelDashboards =
          controlPanel.associatedControlPanelDashboards.map((id) => {
            return controlPanelDashboards.value.find(
              (controlPanelDashboard) => controlPanelDashboard.id === id
            )['name']
          })
        return controlPanel
      })
    })
  } else if (props.isControlPanelDashboard) {
    getControlPanelDashboards().then(() => {
      Object.assign(controlPanelObjects.value, controlPanelDashboards.value)
      controlPanelObjectsView.value = JSON.parse(JSON.stringify(controlPanelDashboards.value))

      controlPanelObjectsView.value = controlPanelObjectsView.value.map((controlPanelDashboard) => {
        controlPanelDashboard.associatedControlPanelDashboardGroups =
          controlPanelDashboard.associatedControlPanelDashboardGroups.map((id) => {
            return controlPanelDashboardGroups.value.find(
              (controlPanelDashboardGroup) => controlPanelDashboardGroup.id === id
            )['name']
          })
        return controlPanelDashboard
      })
    })
  } else if (props.isControlPanelDashboardGroup) {
    getControlPanelDashboardGroups().then(() => {
      Object.assign(controlPanelObjects.value, controlPanelDashboardGroups.value)
      controlPanelObjectsView.value = JSON.parse(JSON.stringify(controlPanelDashboardGroups.value))

      controlPanelObjectsView.value = controlPanelObjectsView.value.map(
        (controlPanelDashboardGroup) => {
          controlPanelDashboardGroup.associatedActions =
            controlPanelDashboardGroup.associatedActions.map((id) => {
              return actions.value.find((action) => action.id === id)['name']
            })
          return controlPanelDashboardGroup
        }
      )

      controlPanelObjectsView.value = controlPanelObjectsView.value.map(
        (controlPanelDashboardGroup) => {
          controlPanelDashboardGroup.associatedDynamicActions =
            controlPanelDashboardGroup.associatedDynamicActions.map((id) => {
              return dynamicActions.value.find((dynamicAction) => dynamicAction.id === id)['name']
            })
          return controlPanelDashboardGroup
        }
      )
    })
  }
}

const confirm = useConfirm()
const deleteControlPanelObjects = () => {
  confirm.require({
    group: 'deleteControlPanelObjects' + I18nSource.value,
    header: t('control_panel_menu.' + I18nSource.value + '.deletion.confirmation_dialog.title'),
    message: t(
      'control_panel_menu.' +
        I18nSource.value +
        '.deletion.confirmation_dialog.confirmation.message'
    ),
    icon: 'pi pi-exclamation-circle',
    acceptIcon: 'pi pi-check',
    rejectIcon: 'pi pi-times',
    rejectClass: 'p-button-sm',
    acceptClass: 'p-button-outlined p-button-sm',
    accept: () => {
      var url = ''
      if (props.isControlPanel) {
        url = '/int/user/controlpanel/'
      } else if (props.isControlPanelDashboard) {
        url = '/int/user/controlpaneldashboard/'
      } else if (props.isControlPanelDashboardGroup) {
        url = '/int/user/controlpaneldashboardgroup/'
      }
      selectedControlPanelObjects.value.forEach((controlPanelObject) => {
        axiosCore
          .delete(url + controlPanelObject.id)
          .then(() => {
            toast.add({
              severity: 'success',
              summary: t(
                'control_panel_menu.' +
                  I18nSource.value +
                  '.deletion.confirmation_dialog.confirmation.toast.success.message'
              ),
              detail: t(
                'control_panel_menu.' +
                  I18nSource.value +
                  '.deletion.confirmation_dialog.confirmation.toast.success.detail'
              ),
              life: 3000
            })
            getCorrespondingControlPanelObjects()
            selectedControlPanelObjects.value = []
          })
          .catch(() => {
            toast.add({
              severity: 'error',
              summary: t(
                'control_panel_menu.' +
                  I18nSource.value +
                  '.deletion.confirmation_dialog.confirmation.toast.error.message'
              ),
              detail: t(
                'control_panel_menu.' +
                  I18nSource.value +
                  '.deletion.confirmation_dialog.confirmation.toast.error.detail'
              ),
              life: 3000
            })
            selectedControlPanelObjects.value = []
          })
      })
    },
    reject: () => {}
  })
}
</script>
