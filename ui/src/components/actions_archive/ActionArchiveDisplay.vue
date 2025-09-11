<template>
  <div v-if="userLoggedEmbedded">
    <Toolbar class="mb-4">
      <template #start>
        <Button
          label="View Logs"
          icon="pi pi-book"
          severity="info"
          @click="toggleActionArchiveViewer"
          :disabled="selectedAction.length == 0 || selectedAction.length > 1"
        />
      </template>
    </Toolbar>
    <Card style="overflow: auto; height: 100%">
      <template #title>
        {{ $t('actions_archive.datatable.title') }}
      </template>
      <template #subtitle>
        {{ $t('actions_archive.datatable.sub_title') }}
      </template>
      <template #content>
        <DataTable
          :value="actionsArchive"
          v-model:selection="selectedAction"
          v-model:filters="filters"
          :globalFilterFields="['global']"
          filterDisplay="row"
          dataKey="id"
          tableStyle="min-width: 60rem"
          scrollable
          scrollHeight="100vh"
          :paginator="true"
          :rows="5"
          columnResizeMode="fit"
          paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
          :rowsPerPageOptions="[5, 10, 25, 50, 100, 250, 500]"
          currentPageReportTemplate="Showing {first} to {last} of {totalRecords} executed actions"
          @row-select="getLogFileData"
        >
          <Column selectionMode="single" headerStyle="width: 3rem"></Column>
          <Column
            v-for="(col, index) of columns"
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
            <template #body="slotProps">
              <span v-if="col.field == 'actionCommand'" class="sophrosyne-code">{{
                slotProps.data.actionCommand
              }}</span>
              <span
                v-else-if="col.field == 'executionStartPoint' || col.field == 'executionEndPoint'"
                >{{
                  slotProps.data[col.field] != null
                    ? slotProps.data[col.field].substring(
                        0,
                        slotProps.data[col.field].lastIndexOf('.')
                      )
                    : ''
                }}</span
              >
              <span v-else>{{ slotProps.data[col.field] }}</span>
            </template>
          </Column>
        </DataTable>
      </template>
    </Card>
    <Dialog
      v-model:visible="showActionArchiveViewer"
      modal
      maximizable
      :header="$t('actions.dynamic_action.action_running_selection.title')"
      v-if="showActionArchiveViewer"
      @close="toggleActionArchiveViewer"
    >
      <Splitter>
        <SplitterPanel :size="75">
          <ActionArchiveFileDisplay
            :textData="actionLogFiles[selectedAction.id].executionLogFileData"
          ></ActionArchiveFileDisplay>
        </SplitterPanel>
        <SplitterPanel :size="25">
          <ActionArchiveFileDisplay
            :textData="
              actionLogFiles[selectedAction.id].postExecutionLogFileData == ''
                ? $t('actions_archive.file_display.empty_post_log_file')
                : actionLogFiles[selectedAction.id].postExecutionLogFileData
            "
          ></ActionArchiveFileDisplay>
        </SplitterPanel>
      </Splitter>
    </Dialog>
  </div>
</template>

<script setup>
import { ref, onBeforeMount, inject } from 'vue'
import ActionArchiveFileDisplay from '@/components/actions_archive/ActionArchiveFileDisplay.vue'
import { useActionArchiveComposable } from '@/composables/ActionArchiveComposable.js'
import { useUserComposable } from '@/composables/UserComposable.js'
import { FilterMatchMode } from '@primevue/core/api'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const { loginUser } = useUserComposable()
const userLoggedEmbedded = ref(false)
const { getActionArchive, actionsArchive, actionLogFiles } = useActionArchiveComposable()

const axiosCore = inject('axios-core')
const selectedAction = ref({})

onBeforeMount(() => {
  const urlParams = new URLSearchParams(window.location.search)
  const username = urlParams.get('username')
  const password = urlParams.get('password')
  if (password != undefined) {
    loginUser(username, password).then(() => {
      userLoggedEmbedded.value = true
      getActionArchive()
    })
  } else {
    getActionArchive()
    userLoggedEmbedded.value = true
  }
})

const getLogFileData = (event) => {
  axiosCore.get('/int/client/archive/action/' + event.data.id + '/filedata').then((res) => {
    actionLogFiles.value[event.data.id] = res.data
  })
}

const showActionArchiveViewer = ref(false)
const toggleActionArchiveViewer = () => {
  if (!showActionArchiveViewer) {
    getLogFileData(selectedAction.id)
  }
  showActionArchiveViewer.value = !showActionArchiveViewer.value
}

const columns = ref([
  { field: 'actionId', header: t('actions_archive.datatable.column_header.id') },
  {
    field: 'actionName',
    header: t('actions_archive.datatable.column_header.name')
  },
  {
    field: 'actionCommand',
    header: t('actions_archive.datatable.column_header.command')
  },
  {
    field: 'actionVersion',
    header: t('actions_archive.datatable.column_header.version')
  },
  {
    field: 'executionStartPoint',
    header: t('actions_archive.datatable.column_header.execution_start_time')
  },
  {
    field: 'executionEndPoint',
    header: t('actions_archive.datatable.column_header.execution_end_time')
  },
  {
    field: 'exitCode',
    header: t('actions_archive.datatable.column_header.exit_code')
  },
  {
    field: 'type',
    header: t('actions_archive.datatable.column_header.type')
  }
])

const filters = ref({
  actionId: { value: null, matchMode: FilterMatchMode.CONTAINS },
  actionName: { value: null, matchMode: FilterMatchMode.CONTAINS },
  actionCommand: { value: null, matchMode: FilterMatchMode.CONTAINS },
  actionVersion: { value: null, matchMode: FilterMatchMode.CONTAINS },
  executionStartPoint: { value: null, matchMode: FilterMatchMode.CONTAINS },
  executionEndPoint: { value: null, matchMode: FilterMatchMode.CONTAINS },
  exitCode: { value: null, matchMode: FilterMatchMode.CONTAINS },
  type: { value: null, matchMode: FilterMatchMode.CONTAINS }
})
</script>
