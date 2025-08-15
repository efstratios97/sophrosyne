<template>
  <div>
    <Toolbar class="mb-4">
      <template #start>
        <Button
          label="View"
          icon="pi pi-play"
          severity="info"
          @click="toggleActionExecutor"
          :disabled="selectedActions.length == 0 || selectedActions.length > 1"
        />
      </template>
    </Toolbar>
    <DataTable
      v-model:filters="filters"
      filterDisplay="row"
      :globalFilterFields="['global']"
      v-model:selection="selectedActions"
      :value="runningAnyTypeActions"
      scrollHeight="flex"
      dataKey="id"
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
    </DataTable>
    <Toast />
    <ActionExecutor
      :id="selectedActions[0].id"
      :action="selectedActions[0]"
      :isAction="selectedActions[0].actionType == 'action'"
      :isDynamicAction="selectedActions[0].actionType == 'dynamic'"
      v-if="showActionExecutor && selectedActions.length > 0"
      @close="(getRunningAnyTypeActions(), (showActionExecutor = false))"
    ></ActionExecutor>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useActionComposable } from '@/composables/ActionComposable.js'
import ActionExecutor from '@/components/actions/utils/ActionExecutor.vue'
import { FilterMatchMode } from '@primevue/core/api'
import { useDynamicActionComposable } from '@/composables/DynamicActionComposable.js'

const { getRunningActions, runningActions } = useActionComposable()
const { getRunningDynamicActions, runningDynamicActions } = useDynamicActionComposable()

const { t } = useI18n()
const selectedActions = ref([])

const columns = ref([
  {
    field: 'name',
    header: t('actions.action.action_menu.datatable.column_header.name')
  },
  {
    field: 'description',
    header: t('actions.action.action_menu.datatable.column_header.description')
  },
  {
    field: 'id',
    header: t('actions.action.action_menu.datatable.column_header.id')
  },
  {
    field: 'version',
    header: t('actions.action.action_menu.datatable.column_header.version')
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
  description: { value: null, matchMode: FilterMatchMode.CONTAINS }
})

onMounted(() => {
  getRunningAnyTypeActions()
})

const showActionExecutor = ref(false)
const toggleActionExecutor = () => {
  showActionExecutor.value = !showActionExecutor.value
}

const runningAnyTypeActions = ref(new Array())
const getRunningAnyTypeActions = () => {
  selectedActions.value = []
  runningAnyTypeActions.value = new Array()
  let dynamicActionToAdd = undefined

  getRunningActions()
    .then(() => {
      runningActions.value.forEach((action) => {
        action['actionType'] = 'action'
        runningAnyTypeActions.value.push(action)
      })
    })
    .then(() => {
      getRunningDynamicActions().then(() => {
        runningDynamicActions.value.forEach((dynamicAction) => {
          dynamicAction['actionType'] = 'dynamic'
          if (runningAnyTypeActions.value.length == 0) {
            dynamicActionToAdd = dynamicAction
          } else {
            runningAnyTypeActions.value.every((dynamicActionInAnyTypeAction) => {
              if (dynamicActionInAnyTypeAction.id != dynamicAction.id) {
                dynamicActionToAdd = dynamicAction
                return false
              }
              return true
            })
          }
          if (dynamicActionToAdd != undefined) {
            runningAnyTypeActions.value.push(dynamicActionToAdd)
          }
        })
      })
    })
}
</script>
