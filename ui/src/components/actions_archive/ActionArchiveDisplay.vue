<template>
  <div v-if="userLoggedEmbedded">
    <Card style="overflow: auto; height: 100%">
      <template #title>
        {{ $t('actions_archive.datatable.title') }}
      </template>
      <template #subtitle>
        {{ $t('actions_archive.datatable.sub_title') }}
      </template>
      <template #content>
        <DataTable
          v-model:expandedRows="expandedRows"
          :value="actionsArchive"
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
          @rowExpand="getLogFileData"
        >
          <Column expander style="width: 5rem" />
          <Column
            field="actionId"
            :header="$t('actions_archive.datatable.column_header.id')"
          ></Column>
          <Column
            field="actionName"
            :header="$t('actions_archive.datatable.column_header.name')"
          ></Column>
          <Column
            field="actionCommand"
            :header="$t('actions_archive.datatable.column_header.command')"
          >
            <template #body="slotProps">
              <span class="sophrosyne-code">{{ slotProps.data.actionCommand }}</span>
            </template>
          </Column>
          <Column
            field="version"
            :header="$t('actions_archive.datatable.column_header.version')"
          ></Column>
          <Column
            field="executionStartPoint"
            :header="$t('actions_archive.datatable.column_header.execution_start_time')"
          >
            <template #body="slotProps">
              {{
                slotProps.data.executionStartPoint != null
                  ? slotProps.data.executionStartPoint.substring(
                      0,
                      slotProps.data.executionStartPoint.lastIndexOf('.')
                    )
                  : ''
              }}
            </template>
          </Column>
          <Column
            field="executionEndPoint"
            :header="$t('actions_archive.datatable.column_header.execution_end_time')"
          >
            <template #body="slotProps">
              {{
                slotProps.data.executionEndPoint != null
                  ? slotProps.data.executionEndPoint.substring(
                      0,
                      slotProps.data.executionEndPoint.lastIndexOf('.')
                    )
                  : ''
              }}
            </template>
          </Column>
          <Column
            field="exitCode"
            :header="$t('actions_archive.datatable.column_header.exit_code')"
          >
          </Column>
          <Column
            field="type"
            :header="$t('actions_archive.datatable.column_header.type')"
          ></Column>
          <template #expansion="slotProps">
            <div class="grid">
              <div class="col-6" style="min-height: 350px; max-height: 350px; overflow: auto">
                <ActionArchiveFileDisplay
                  :textData="actionLogFiles[slotProps.data.id].executionLogFileData"
                ></ActionArchiveFileDisplay>
              </div>
              <div class="col-6" style="min-height: 350px; max-height: 350px; overflow: auto">
                <ActionArchiveFileDisplay
                  :textData="
                    actionLogFiles[slotProps.data.id].postExecutionLogFileData == ''
                      ? $t('actions_archive.file_display.empty_post_log_file')
                      : actionLogFiles[slotProps.data.id].postExecutionLogFileData
                  "
                ></ActionArchiveFileDisplay>
              </div>
            </div>
          </template>
        </DataTable>
      </template>
    </Card>
  </div>
</template>

<script setup>
import { ref, onBeforeMount, inject } from 'vue'
import ActionArchiveFileDisplay from '@/components/actions_archive/ActionArchiveFileDisplay.vue'
import { useActionArchiveComposable } from '@/composables/ActionArchiveComposable.js'
import { useUserComposable } from '@/composables/UserComposable.js'

const { loginUser } = useUserComposable()
const userLoggedEmbedded = ref(false)
const { getActionArchive, actionsArchive, actionLogFiles } = useActionArchiveComposable()

const axiosCore = inject('axios-core')
const expandedRows = ref([])

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
</script>
