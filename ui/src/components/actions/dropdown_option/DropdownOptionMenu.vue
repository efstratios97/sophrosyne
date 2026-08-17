<template>
  <div>
    <Toolbar class="mb-4">
      <template #start>
        <Button
          label="New"
          icon="pi pi-plus"
          severity="success"
          class="mr-2"
          @click="toggleCreateDropdownOption"
        />
        <Button
          label="Update"
          icon="pi pi-replay"
          severity="primary"
          class="mr-2"
          @click="toggleUpdateDropdownOption"
          :disabled="selectedDropdownOptions.length == 0 || selectedDropdownOptions.length > 1"
        />
        <Button
          label="Delete"
          icon="pi pi-trash"
          severity="danger"
          @click="deleteDropdownOptions"
          :disabled="!selectedDropdownOptions || !selectedDropdownOptions.length"
        />
      </template>
    </Toolbar>
    <Card style="overflow: auto; height: 100%">
      <template #title>
        {{ $t('actions.dropdown_option.dropdown_option_menu.datatable.title') }}
      </template>
      <template #subtitle>
        {{ $t('actions.dropdown_option.dropdown_option_menu.datatable.sub_title') }}
      </template>
      <template #content>
        <DataTable
          :value="dropdownOptions"
          v-model:selection="selectedDropdownOptions"
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
            <template #body="slotProps">
              <span v-if="col.field == 'type'">
                <ToggleButton
                  disabled
                  :modelValue="slotProps.data[col.field] == 'STATIC'"
                  :onLabel="
                    $t(
                      'actions.dropdown_option.dropdown_option_template_form.fields.type.static.field_name'
                    )
                  "
                  :offLabel="
                    $t(
                      'actions.dropdown_option.dropdown_option_template_form.fields.type.dynamic.field_name'
                    )
                  "
                  onIcon="pi pi-receipt"
                  offIcon="pi pi-arrow-right-arrow-left"
                />
              </span>
              <span v-else-if="slotProps.data.type == 'STATIC' && col.field == 'dropdownOptions'"
                ><Select
                  :options="slotProps.data[col.field]"
                  :placeholder="
                    $t(
                      'actions.dropdown_option.dropdown_option_menu.datatable.column_header.dropdown_options.placeholder_dropdown'
                    )
                  "
                  class="w-full md:w-56"
              /></span>
              <span v-else-if="slotProps.data.type == 'DYNAMIC' && col.field == 'dropdownOptions'">
                <InputGroup>
                  <InputGroupAddon> <i class="pi pi-globe"></i></InputGroupAddon>
                  <InputText :modelValue="slotProps.data['dynamicParameterToMatch']" disabled />
                </InputGroup>
              </span>
              <span v-else>{{ slotProps.data[col.field] }}</span>
            </template>
          </Column>
        </DataTable>
      </template>
    </Card>
    <Dialog
      v-model:visible="showCreateDropdownOption"
      modal
      maximizable
      :header="$t('actions.dropdown_option.dropdown_option_menu.datatable.sub_title')"
      v-if="showCreateDropdownOption"
      @close="toggleCreateDropdownOption"
    >
      <DropdownOptionFormTemplate
        :dropdownOption="{}"
        :metadata="{ ...metadata, create: true }"
        @close="toggleCreateDropdownOption"
        @submitDropdownOption="createDropdownOption($event)"
      ></DropdownOptionFormTemplate>
    </Dialog>
    <Dialog
      v-model:visible="showUpdateDropdownOption"
      modal
      maximizable
      :header="$t('actions.action.action_creation_form.modal_header')"
      v-if="showUpdateDropdownOption"
      @close="toggleUpdateDropdownOption"
    >
      <DropdownOptionFormTemplate
        :dropdownOption="selectedDropdownOptions[0]"
        :metadata="{ ...metadata, create: false }"
        @close="toggleUpdateDropdownOption"
        @submitDropdownOption="updateDropdownOption($event)"
      ></DropdownOptionFormTemplate>
    </Dialog>
    <ConfirmDialog group="deleteDropdownOptions">
      <template #message="slotProps">
        <div
          class="flex flex-column align-items-center w-full gap-3 border-bottom-1 surface-border"
        >
          <i :class="slotProps.message.icon" class="text-6xl text-red-500"></i>
          <p>{{ slotProps.message.message }}</p>
          <ul
            v-for="dropdownOption in selectedDropdownOptions"
            :key="dropdownOption"
            class="list-disc"
          >
            <li>{{ dropdownOption.name }}</li>
          </ul>
        </div>
      </template>
    </ConfirmDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { FilterMatchMode } from '@primevue/core/api'
import { useI18n } from 'vue-i18n'
import DropdownOptionFormTemplate from './utils/DropdownOptionFormTemplate.vue'
import {
  useDropdownOptionComposable,
  DropdownOption
} from '../../../composables/DropdownOptionComposable'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'

const { t } = useI18n()
const toast = useToast()
const confirm = useConfirm()

const selectedDropdownOptions = ref(<any>[])
const dropdownOptions = ref(<any>[])
const metadata = ref({ submitting: false })

const dropdownComposoble = useDropdownOptionComposable()

onMounted(() => {
  getDropdownOptions()
})

const getDropdownOptions = () => {
  dropdownComposoble.getDropdownOptions().then(() => {
    dropdownOptions.value = dropdownComposoble.dropdownOptions.value
  })
}

const showCreateDropdownOption = ref(false)
const toggleCreateDropdownOption = () => {
  showCreateDropdownOption.value = !showCreateDropdownOption.value
}

const showUpdateDropdownOption = ref(false)
const toggleUpdateDropdownOption = () => {
  showUpdateDropdownOption.value = !showUpdateDropdownOption.value
}

const createDropdownOption = async (dropdownOption: object) => {
  metadata.value.submitting = true
  let status = await dropdownComposoble.createDropdownOption(dropdownOption)
  responseHandleHelper(status, true)
  metadata.value.submitting = false
}

const updateDropdownOption = async (dropdownOption: DropdownOption) => {
  metadata.value.submitting = true
  let status = await dropdownComposoble.updateDropdownOption(dropdownOption)
  responseHandleHelper(status, false)
  metadata.value.submitting = false
}

const responseHandleHelper = (status: number, create: boolean) => {
  let I18n_string = create ? 'create' : 'update'
  if (status == 200 || status == 201) {
    toast.add({
      severity: 'success',
      summary: t(
        'actions.dropdown_option.dropdown_option_template_form.btn.' +
          I18n_string +
          '.toast.success.message'
      ),
      detail: t(
        'actions.dropdown_option.dropdown_option_template_form.btn.' +
          I18n_string +
          '.toast.success.detail'
      ),
      life: 3000
    })
    create ? toggleCreateDropdownOption() : toggleUpdateDropdownOption()
    selectedDropdownOptions.value = []
    getDropdownOptions()
  } else if (status == 409) {
    toast.add({
      severity: 'error',
      summary: t(
        'actions.dropdown_option.dropdown_option_template_form.btn.' +
          I18n_string +
          '.toast.error.409.message'
      ),
      detail: t(
        'actions.dropdown_option.dropdown_option_template_form.btn.' +
          I18n_string +
          '.toast.error.409.detail'
      ),
      life: 5000
    })
  } else {
    toast.add({
      severity: 'error',
      summary: t(
        'actions.dropdown_option.dropdown_option_template_form.btn.' +
          I18n_string +
          '.toast.error.other.message'
      ),
      detail: t(
        'actions.dropdown_option.dropdown_option_template_form.btn.' +
          I18n_string +
          '.toast.error.other.detail'
      ),
      life: 3000
    })
  }
}

const deleteDropdownOptions = async () => {
  confirm.require({
    group: 'deleteDropdownOptions',
    header: t('actions.dropdown_option.dropdown_option_deletion.title'),
    message: t('actions.dropdown_option.dropdown_option_deletion.message'),
    icon: 'pi pi-exclamation-circle',
    acceptIcon: 'pi pi-check',
    rejectIcon: 'pi pi-times',
    rejectClass: 'p-button-sm',
    acceptClass: 'p-button-outlined p-button-sm',
    accept: async () => {
      let statuses = await dropdownComposoble.deleteDropdownOption(selectedDropdownOptions.value)
      statuses.forEach(async (status) => {
        if ((await status) == 200) {
          toast.add({
            severity: 'success',
            summary: t('actions.dropdown_option.dropdown_option_deletion.toast.success.message'),
            detail: t('actions.dropdown_option.dropdown_option_deletion.toast.success.detail'),
            life: 3000
          })
          selectedDropdownOptions.value = []
          getDropdownOptions()
        } else {
          toast.add({
            severity: 'error',
            summary: t('actions.dropdown_option.dropdown_option_deletion.toast.error.message'),
            detail: t('actions.dropdown_option.dropdown_option_deletion.toast.error.detail'),
            life: 3000
          })
        }
      })
    },
    reject: () => {}
  })
}

const columns = ref([
  {
    field: 'id',
    header: t('actions.dropdown_option.dropdown_option_menu.datatable.column_header.id')
  },
  {
    field: 'name',
    header: t('actions.dropdown_option.dropdown_option_menu.datatable.column_header.name')
  },
  {
    field: 'description',
    header: t('actions.dropdown_option.dropdown_option_menu.datatable.column_header.description')
  },
  {
    field: 'dynamicParameterToMatch',
    header: t(
      'actions.dropdown_option.dropdown_option_menu.datatable.column_header.dynamic_parameter_to_match'
    )
  },
  {
    field: 'type',
    header: t('actions.dropdown_option.dropdown_option_menu.datatable.column_header.type')
  },
  {
    field: 'dropdownOptions',
    header: t(
      'actions.dropdown_option.dropdown_option_menu.datatable.column_header.dropdown_options.name'
    )
  }
])

const selectedColumns = ref(columns.value)
const onToggle = (val: any) => {
  selectedColumns.value = columns.value.filter((col) => val.includes(col))
}

const filters = ref({
  id: { value: null, matchMode: FilterMatchMode.CONTAINS },
  name: { value: null, matchMode: FilterMatchMode.CONTAINS },
  description: { value: null, matchMode: FilterMatchMode.CONTAINS },
  dynamicParameterToMatch: { value: null, matchMode: FilterMatchMode.CONTAINS },
  type: { value: null, matchMode: FilterMatchMode.CONTAINS },
  dropdownOptions: { value: null, matchMode: FilterMatchMode.CONTAINS }
})
</script>
