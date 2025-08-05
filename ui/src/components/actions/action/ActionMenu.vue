<template>
  <div>
    <Toolbar class="mb-4">
      <template #start>
        <Button
          label="Execute"
          icon="pi pi-play"
          severity="info"
          @click="toggleActionExecutor"
          :disabled="selectedActions.length == 0 || selectedActions.length > 1"
        />
      </template>

      <template #end
        ><Button
          label="New"
          icon="pi pi-plus"
          severity="success"
          class="mr-2"
          @click="toggleCreateAction"
        />
        <Button
          label="Update"
          icon="pi pi-replay"
          severity="primary"
          class="mr-2"
          @click="toggleUpdateAction"
          :disabled="selectedActions.length == 0 || selectedActions.length > 1"
        />
        <Button
          label="Delete"
          icon="pi pi-trash"
          severity="danger"
          @click="deleteActions"
          :disabled="!selectedActions || !selectedActions.length"
        />
      </template>
    </Toolbar>
    <div class="sophrosyne-card">
      <DataTable
        v-model:filters="filters"
        filterDisplay="row"
        :globalFilterFields="['global']"
        v-model:selection="selectedActions"
        :value="actions"
        scrollHeight="flex"
        dataKey="name"
        paginator
        :rows="5"
        :rowsPerPageOptions="[5, 10, 20, 50]"
      >
        <template #header>
          <div style="text-align: left">
            <MultiSelect
              :modelValue="selectedColumns"
              :options="columns"
              optionLabel="header"
              @update:modelValue="onToggle"
              display="chip"
              placeholder="Select optional columns to display"
              :maxSelectedLabels="3"
            />
          </div>
        </template>
        <Column selectionMode="multiple" style="width: 3rem" :exportable="false"></Column>
        <Column
          v-for="(col, index) of selectedColumns"
          :field="col.field"
          :header="col.header"
          :key="col.field + '_' + index"
          :filterField="col.field"
          sortable
        >
          <template #filter="{ filterModel, filterCallback }">
            <InputText
              v-model="filterModel.value"
              type="text"
              @input="filterCallback()"
              placeholder="Search"
            />
          </template>
        </Column>
        <Column
          field="command"
          :header="$t('actions.action.action_menu.datatable.column_header.command')"
        >
          <template #body="slotProps">
            <span class="sophrosyne-code">{{ slotProps.data.command }}</span>
          </template>
        </Column>
        <Column
          field="requiresConfirmation"
          :header="$t('actions.action.action_menu.datatable.column_header.requires_confirmation')"
          sortable
        >
          <template #body="slotProps">
            <ToggleButton
              v-model="slotProps.data.requiresConfirmation"
              :onLabel="$t('actions.action.action_creation_form.fields.requires_confirmation_on')"
              :offLabel="$t('actions.action.action_creation_form.fields.requires_confirmation_off')"
              onIcon="pi pi-lock"
              offIcon="pi pi-lock-open"
              readonly
              disabled
            />
          </template>
        </Column>
        <Column
          field="muted"
          :header="$t('actions.action.action_menu.datatable.column_header.muted')"
          sortable
        >
          <template #body="slotProps">
            <ToggleButton
              v-model="slotProps.data.muted"
              :onLabel="$t('actions.action.action_creation_form.fields.muted_on')"
              :offLabel="$t('actions.action.action_creation_form.fields.muted_off')"
              onIcon="pi pi-pause"
              offIcon="pi pi-play"
              disabled
              readonly
            />
          </template>
        </Column>
        <Column
          field="allowedApikeys"
          :header="$t('actions.action.action_menu.datatable.column_header.allowed_apikeys')"
        >
          <template #body="slotProps">
            <Dropdown
              :options="slotProps.data.allowedApikeys"
              :placeholder="
                $t('actions.action.action_menu.datatable.column_header.allowed_apikeys')
              "
              class="w-full md:w-14rem"
            />
          </template>
        </Column>
      </DataTable>
      <ConfirmDialog group="deleteActions">
        <template #message="slotProps">
          <div
            class="flex flex-column align-items-center w-full gap-3 border-bottom-1 surface-border"
          >
            <i :class="slotProps.message.icon" class="text-6xl text-red-500"></i>
            <p>{{ slotProps.message.message }}</p>
            <ul v-for="action in selectedActions" :key="action" class="list-disc">
              <li>{{ action.name }}</li>
            </ul>
          </div>
        </template>
      </ConfirmDialog>
      <Dialog
        v-model:visible="showCreateAction"
        modal
        maximizable
        :header="$t('actions.action.action_creation_form.modal_header')"
        v-if="showCreateAction"
        @close="toggleCreateAction"
      >
        <ActionFormCreate
          @getActions="(getActions(), hideForms())"
          @close="toggleCreateAction"
        ></ActionFormCreate>
      </Dialog>
      <Dialog
        v-model:visible="showUpdateAction"
        modal
        maximizable
        :header="$t('actions.action.action_creation_form.modal_header_update')"
        v-if="showUpdateAction"
        @close="toggleUpdateAction"
      >
        <ActionFormUpdate
          :action="selectedActions[0]"
          @getActions="(getActions(), hideForms())"
          @close="toggleUpdateAction"
        ></ActionFormUpdate
      ></Dialog>
      <Toast />
    </div>
    <ActionExecutor
      :id="selectedActions[0].id"
      :action="selectedActions[0]"
      :isAction="true"
      v-if="showActionExecutor && selectedActions.length > 0"
      @close="toggleActionExecutor"
    ></ActionExecutor>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import { useActionComposable } from '@/composables/ActionComposable.js'
import ActionFormCreate from '@/components/actions/action/ActionFormCreate.vue'
import ActionFormUpdate from '@/components/actions/action/ActionFormUpdate.vue'
import ActionExecutor from '@/components/actions/utils/ActionExecutor.vue'
import { FilterMatchMode } from '@primevue/core/api'

import { Dialog } from 'primevue'

const { getActions, actions } = useActionComposable()

const toast = useToast()
const { t } = useI18n()
const axiosCore = inject('axios-core')
const selectedActions = ref([])

const columns = ref([
  {
    field: 'id',
    header: t('actions.action.action_menu.datatable.column_header.id')
  },
  {
    field: 'name',
    header: t('actions.action.action_menu.datatable.column_header.name')
  },
  {
    field: 'description',
    header: t('actions.action.action_menu.datatable.column_header.description')
  },
  {
    field: 'version',
    header: t('actions.action.action_menu.datatable.column_header.version')
  },
  {
    field: 'postExecutionLogFilePath',
    header: t('actions.action.action_menu.datatable.column_header.post_execution_log_file_path')
  }
])
const selectedColumns = ref(columns.value)
const onToggle = (val) => {
  selectedColumns.value = columns.value.filter((col) => val.includes(col))
}

const filters = ref({
  id: { value: null, matchMode: FilterMatchMode.CONTAINS },
  name: { value: null, matchMode: FilterMatchMode.CONTAINS },
  version: { value: null, matchMode: FilterMatchMode.CONTAINS },
  description: { value: null, matchMode: FilterMatchMode.CONTAINS },
  postExecutionLogFilePath: { value: null, matchMode: FilterMatchMode.CONTAINS }
})

onMounted(() => {
  getActions()
})

const showCreateAction = ref(false)
const toggleCreateAction = () => {
  showCreateAction.value = !showCreateAction.value
}

const showUpdateAction = ref(false)
const toggleUpdateAction = () => {
  showUpdateAction.value = !showUpdateAction.value
}

const showActionExecutor = ref(false)
const toggleActionExecutor = () => {
  showActionExecutor.value = !showActionExecutor.value
}

const hideForms = () => {
  showCreateAction.value = false
  showUpdateAction.value = false
  selectedActions.value = []
}

const confirm = useConfirm()
const deleteActions = () => {
  confirm.require({
    group: 'deleteActions',
    header: t('actions.action.action_deletion.title'),
    message: t('actions.action.action_deletion.message'),
    icon: 'pi pi-exclamation-circle',
    acceptIcon: 'pi pi-check',
    rejectIcon: 'pi pi-times',
    rejectClass: 'p-button-sm',
    acceptClass: 'p-button-outlined p-button-sm',
    accept: () => {
      selectedActions.value.forEach((action) => {
        axiosCore
          .delete('/int/user/action/' + action.id)
          .then(() => {
            toast.add({
              severity: 'success',
              summary: t('actions.action.action_deletion.toast.success.message'),
              detail: t('actions.action.action_deletion.toast.success.detail'),
              life: 3000
            })
            selectedActions.value = []
            getActions()
          })
          .catch(() => {
            toast.add({
              severity: 'error',
              summary: t('actions.action.action_deletion.toast.error.message'),
              detail: t('actions.action.action_deletion.toast.error.detail'),
              life: 3000
            })
            selectedActions.value = []
          })
      })
    },
    reject: () => {}
  })
}
</script>
