<template>
  <Card>
    <template #header><br /></template>
    <template #content>
      <div class="sophrosyne-form-wrapper">
        <form class="sophrosyne-form">
          <span class="sophrosyne-field-wrapper">
            <FloatLabel>
              <InputText
                id="name"
                v-model="newArbitraryControlPanelObject.name"
                type="text"
                class="sophrosyne-inputtext"
              />
              <label for="name">{{
                $t('control_panel_menu.' + I18nSource + '.creation_form.fields.name')
              }}</label>
            </FloatLabel>
          </span>
          <span class="sophrosyne-field-wrapper">
            <FloatLabel>
              <InputText
                id="description"
                v-model="newArbitraryControlPanelObject.description"
                type="text"
                class="sophrosyne-inputtext"
              />
              <label for="description">{{
                $t('control_panel_menu.' + I18nSource + '.creation_form.fields.description')
              }}</label>
            </FloatLabel>
          </span>
          <span
            class="sophrosyne-field-wrapper"
            v-tooltip="{
              value: $t(
                'control_panel_menu.' + I18nSource + '.creation_form.tooltip.associated_users'
              ),
              showDelay: 100,
              hideDelay: 300
            }"
            v-if="isControlPanel"
          >
            <MultiSelect
              v-model="newArbitraryControlPanelObject.associatedUsers"
              :options="users"
              filter
              optionLabel="username"
              :placeholder="
                $t('control_panel_menu.' + I18nSource + '.creation_form.fields.associated_users')
              "
              class="w-full md:w-20rem"
              @change="getUsers"
            />
          </span>
          <span
            class="sophrosyne-field-wrapper"
            v-tooltip="{
              value: $t(
                'control_panel_menu.' +
                  I18nSource +
                  '.creation_form.tooltip.associated_control_panel_dashboard'
              ),
              showDelay: 100,
              hideDelay: 300
            }"
            v-if="isControlPanel"
          >
            <MultiSelect
              v-model="newArbitraryControlPanelObject.associatedControlPanelDashboards"
              :options="controlPanelDashboards"
              filter
              optionLabel="name"
              :placeholder="
                $t(
                  'control_panel_menu.' +
                    I18nSource +
                    '.creation_form.fields.associated_control_panel_dashboard'
                )
              "
              class="w-full md:w-20rem"
              @change="getControlPanelDashboards"
            />
          </span>
          <span
            class="sophrosyne-field-wrapper"
            v-tooltip="{
              value: $t(
                'control_panel_menu.' +
                  I18nSource +
                  '.creation_form.tooltip.associated_control_panel_dashboard_groups'
              ),
              showDelay: 100,
              hideDelay: 300
            }"
            v-if="isControlPanelDashboard"
          >
            <MultiSelect
              v-model="newArbitraryControlPanelObject.associatedControlPanelDashboardGroups"
              :options="controlPanelDashboardGroups"
              filter
              optionLabel="name"
              :placeholder="
                $t(
                  'control_panel_menu.' +
                    I18nSource +
                    '.creation_form.fields.associated_control_panel_dashboard_groups'
                )
              "
              class="w-full md:w-20rem"
              @change="getControlPanelDashboardGroups"
            />
          </span>
          <span class="sophrosyne-field-wrapper" v-if="isControlPanelDashboardGroup">
            <MultiSelect
              v-model="newArbitraryControlPanelObject.associatedActions"
              :options="actions"
              filter
              optionLabel="name"
              :placeholder="
                $t('control_panel_menu.' + I18nSource + '.creation_form.fields.associated_actions')
              "
              class="w-full md:w-20rem"
              @change="getActions"
            />
          </span>
          <span class="sophrosyne-field-wrapper" v-if="isControlPanelDashboardGroup">
            <MultiSelect
              v-model="newArbitraryControlPanelObject.associatedDynamicActions"
              :options="dynamicActions"
              filter
              optionLabel="name"
              :placeholder="
                $t(
                  'control_panel_menu.' +
                    I18nSource +
                    '.creation_form.fields.associated_dynamic_actions'
                )
              "
              class="w-full md:w-20rem"
              @change="getDynamicActions"
            />
          </span>
          <span
            class="flex flex-column align-items-center py-2 gap-2 sophrosyne-field-wrapper"
            v-if="isControlPanelDashboardGroup || isControlPanelDashboard"
            v-tooltip="{
              value: $t('control_panel_menu.' + I18nSource + '.creation_form.tooltip.position'),
              showDelay: 100,
              hideDelay: 300
            }"
          >
            <FloatLabel>
              <InputNumber
                id="position"
                v-model="newArbitraryControlPanelObject.position"
                showButtons
                buttonLayout="horizontal"
                :min="0"
              >
                <template #incrementbuttonicon>
                  <span class="pi pi-plus" />
                </template>
                <template #decrementbuttonicon>
                  <span class="pi pi-minus" />
                </template>
              </InputNumber>
              <label for="position">{{
                $t('control_panel_menu.' + I18nSource + '.creation_form.fields.position')
              }}</label>
            </FloatLabel>
          </span>

          <Button
            :label="$t('control_panel_menu.' + I18nSource + '.creation_form.btn.create.label')"
            @click="submitArbitraryControlPanelObject(newArbitraryControlPanelObject)"
          />
        </form>
      </div>
    </template>
    <template #footer>
      <ProgressBar v-if="submitting" mode="indeterminate" style="height: 6px"></ProgressBar>
    </template>
  </Card>
</template>
<script setup>
import { ref, onMounted, onBeforeMount } from 'vue'
import { useUserComposable } from '@/composables/UserComposable.js'
import { useControlPanelComposable } from '@/composables/ControlPanelComposable.js'
import { useControlPanelDashboardComposable } from '@/composables/ControlPanelDashboardComposable.js'
import { useControlPanelDashboardGroupComposable } from '@/composables/ControlPanelDashboardGroupComposable.js'
import { useDynamicActionComposable } from '@/composables/DynamicActionComposable.js'
import { useActionComposable } from '@/composables/ActionComposable.js'

const { createControlPanel, updateControlPanel } = useControlPanelComposable()
const {
  createControlPanelDashboard,
  updateControlPanelDashboard,
  getControlPanelDashboards,
  controlPanelDashboards
} = useControlPanelDashboardComposable()
const {
  createControlPanelDashboardGroup,
  updateControlPanelDashboardGroup,
  getControlPanelDashboardGroups,
  controlPanelDashboardGroups
} = useControlPanelDashboardGroupComposable()
const { getUsers, users } = useUserComposable()
const { getDynamicActions, dynamicActions } = useDynamicActionComposable()
const { getActions, actions } = useActionComposable()

const emits = defineEmits(['getArbitraryControlPanelObjects'])
const props = defineProps([
  'arbitraryControlPanelObject',
  'isUpdate',
  'isControlPanel',
  'isControlPanelDashboard',
  'isControlPanelDashboardGroup'
])
const newArbitraryControlPanelObject = ref({ ...props.arbitraryControlPanelObject })
const I18nSource = props.isControlPanel
  ? ref('control_panel')
  : props.isControlPanelDashboard
    ? ref('control_panel_dashboard')
    : ref('control_panel_dashboard_group')

onBeforeMount(() => {
  getActions()
  getDynamicActions()
})

onMounted(() => {
  getControlPanelDashboards().then(() => {
    getControlPanelDashboardGroups().then(() => {
      getUsers().then(() => {
        setCorrectFieldsPerObject()
      })
    })
  })
})

const submitting = ref(false)

const setCorrectFieldsPerObject = () => {
  if (Object.keys(newArbitraryControlPanelObject.value).length == 0) {
    if (props.isControlPanel) {
      newArbitraryControlPanelObject.value.name = ''
      newArbitraryControlPanelObject.value.description = ''
      newArbitraryControlPanelObject.value.associatedUsers = []
      newArbitraryControlPanelObject.value.associatedControlPanelDashboards = []
    }
    if (props.isControlPanelDashboard) {
      newArbitraryControlPanelObject.value.name = ''
      newArbitraryControlPanelObject.value.description = ''
      newArbitraryControlPanelObject.value.associatedControlPanelDashboardGroups = []
      newArbitraryControlPanelObject.value.position = 0
    }
    if (props.isControlPanelDashboardGroup) {
      newArbitraryControlPanelObject.value.name = ''
      newArbitraryControlPanelObject.value.description = ''
      newArbitraryControlPanelObject.value.associatedActions = []
      newArbitraryControlPanelObject.value.associatedDynamicActions = []
      newArbitraryControlPanelObject.value.position = 0
    }
  } else {
    if (props.isControlPanel) {
      newArbitraryControlPanelObject.value.associatedUsers =
        newArbitraryControlPanelObject.value.associatedUsers.map((id) => {
          return users.value.find((user) => user.id === id)
        })
      newArbitraryControlPanelObject.value.associatedControlPanelDashboards =
        newArbitraryControlPanelObject.value.associatedControlPanelDashboards.map((id) => {
          return controlPanelDashboards.value.find(
            (controlPanelDashboard) => controlPanelDashboard.id === id
          )
        })
    }
    if (props.isControlPanelDashboard) {
      newArbitraryControlPanelObject.value.associatedControlPanelDashboardGroups =
        newArbitraryControlPanelObject.value.associatedControlPanelDashboardGroups.map((id) => {
          return controlPanelDashboardGroups.value.find(
            (controlPanelDashboardGroup) => controlPanelDashboardGroup.id === id
          )
        })
    }
    if (props.isControlPanelDashboardGroup) {
      newArbitraryControlPanelObject.value.associatedActions =
        newArbitraryControlPanelObject.value.associatedActions.map((id) => {
          return actions.value.find((action) => action.id === id)
        })
      newArbitraryControlPanelObject.value.associatedDynamicActions =
        newArbitraryControlPanelObject.value.associatedDynamicActions.map((id) => {
          return dynamicActions.value.find((dynamicAction) => dynamicAction.id === id)
        })
    }
  }
}

const submitArbitraryControlPanelObject = (newArbitraryControlPanelObject) => {
  submitting.value = true
  if (props.isControlPanel) {
    props.isUpdate
      ? updateControlPanel(newArbitraryControlPanelObject)
      : createControlPanel(newArbitraryControlPanelObject)
  } else if (props.isControlPanelDashboard) {
    props.isUpdate
      ? updateControlPanelDashboard(newArbitraryControlPanelObject)
      : createControlPanelDashboard(newArbitraryControlPanelObject)
  } else if (props.isControlPanelDashboardGroup) {
    props.isUpdate
      ? updateControlPanelDashboardGroup(newArbitraryControlPanelObject)
      : createControlPanelDashboardGroup(newArbitraryControlPanelObject)
  }
  emits('getArbitraryControlPanelObjects')
  submitting.value = false
}
</script>
