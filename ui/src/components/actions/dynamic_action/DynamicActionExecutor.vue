<template>
  <Card class="sophrosyne-card">
    <template #header><br /></template>
    <template #content>
      <div v-for="dynamicParameter in dynamicParameters" :key="dynamicParameter">
        <div class="sophrosyne-form-wrapper">
          <form class="sophrosyne-form">
            <div
              class="grid sophrosyne-field-wrapper"
              v-if="dynamicParameter.hasMatchedDropdownOption"
            >
              <div class="col-10">
                <span
                  v-if="
                    !dynamicParameter.hasMatchedDropdownOption || dynamicParameter.showTextField
                  "
                >
                  <FloatLabel>
                    <InputText
                      :id="dynamicParameter.parameter"
                      v-model="userPassedParameters[dynamicParameter.parameter]"
                      type="text"
                      class="sophrosyne-inputtext"
                      @change="getCommandPreview"
                    />
                    <label :for="dynamicParameter.parameter">{{
                      dynamicParameter.parameter
                    }}</label>
                  </FloatLabel>
                </span>
                <span v-else class="sophrosyne-field-wrapper">
                  <FloatLabel>
                    <MultiSelect
                      v-if="
                        dynamicParameter.multiselect && dynamicParameter.dropdownOptions.length > 0
                      "
                      :id="dynamicParameter.dropdownOptionId"
                      v-model="userPassedParameters[dynamicParameter.dropdownOptionId]"
                      :options="dynamicParameter.dropdownOptions"
                      placeholder=""
                      class="w-full md:w-56"
                      display="chip"
                      filter
                      :maxSelectedLabels="5"
                      @change="getCommandPreview"
                    />

                    <Select
                      v-else
                      :id="dynamicParameter.dropdownOptionId"
                      v-model="userPassedParameters[dynamicParameter.parameter]"
                      :options="dynamicParameter.dropdownOptions"
                      placeholder=""
                      class="w-full md:w-56"
                      display="chip"
                      filter
                      :maxSelectedLabels="5"
                      @change="getCommandPreview"
                    >
                    </Select>
                    <label :for="dynamicParameter.parameter">{{
                      dynamicParameter.parameter
                    }}</label>
                  </FloatLabel>
                </span>
              </div>
              <div class="col-2">
                <ToggleButton
                  v-model="dynamicParameter.showTextField"
                  :onLabel="$t('actions.dynamic_action.dynamic_action_executor.dropdown.btn.on')"
                  :offLabel="$t('actions.dynamic_action.dynamic_action_executor.dropdown.btn.off')"
                  onIcon="pi pi-bars"
                  offIcon="pi pi-list-check"
                />
              </div>
            </div>
            <div v-if="!dynamicParameter.hasMatchedDropdownOption" class="sophrosyne-field-wrapper">
              <span class="sophrosyne-field-wrapper">
                <FloatLabel>
                  <InputText
                    :id="dynamicParameter.parameter"
                    v-model="userPassedParameters[dynamicParameter.parameter]"
                    type="text"
                    class="sophrosyne-inputtext"
                    @change="getCommandPreview"
                  />
                  <label :for="dynamicParameter.parameter">{{ dynamicParameter.parameter }}</label>
                </FloatLabel>
              </span>
            </div>
          </form>
        </div>
      </div>
      <div class="sophrosyne-form-wrapper">
        <form class="sophrosyne-form">
          <span
            class="sophrosyne-field-wrapper mt-8"
            v-tooltip="{
              value: commandPreview,
              showDelay: 100,
              hideDelay: 300
            }"
          >
            <FloatLabel>
              <InputText
                id="commandParameter"
                v-model="commandPreview"
                type="text"
                class="sophrosyne-inputtext"
                disabled
              />
              <label for="commandParameter">{{
                $t('actions.dynamic_action.action_dynamic_parameters_modal.command_preview')
              }}</label></FloatLabel
            >
          </span>
        </form>
      </div>
      <div class="grid">
        <div class="col-6">
          <Button
            :label="$t('actions.dynamic_action.action_control.btn.execute_action.label')"
            icon="pi pi-play"
            severity="success"
            rounded
            class="w-full"
            @click="
              executeAction(props.action.id, getMultiSelectParameters(userPassedParameters))
                ? emits('close')
                : undefined
            "
          />
        </div>
        <div class="col-6">
          <Button
            :label="$t('actions.dynamic_action.action_control.btn.save_as_action.label')"
            icon="pi pi-save"
            severity="primary"
            rounded
            class="w-full"
            @click="toggleCreateAction"
          />
        </div>
      </div>
    </template>
  </Card>
  <Dialog
    v-model:visible="showCreateAction"
    modal
    maximizable
    :header="$t('actions.action.action_creation_form.modal_header')"
    v-if="showCreateAction"
    @close="toggleCreateAction"
  >
    <ActionFormCreate :action="newAction" @getActions="toggleCreateAction"></ActionFormCreate>
  </Dialog>
</template>
<script setup>
import { ref, inject, defineProps, onMounted } from 'vue'
import ActionFormCreate from '@/components/actions/action/ActionFormCreate.vue'
import { Dialog } from 'primevue'
import { useDynamicActionComposable } from '@/composables/DynamicActionComposable.js'

const { executeAction } = useDynamicActionComposable()

const axiosCore = inject('axios-core')

const props = defineProps(['action'])
const emits = defineEmits(['close'])

const dynamicParameters = ref([])
const userPassedParameters = ref({})
onMounted(() => {
  getDynamicParameters()
})

const newAction = ref({ ...props.action })
const showCreateAction = ref(false)
const toggleCreateAction = () => {
  showCreateAction.value = !showCreateAction.value
}

const getMultiSelectParameters = (userPassedParameters) => {
  dynamicParameters.value.forEach((dynamicParameter) => {
    const selected = userPassedParameters?.[dynamicParameter.dropdownOptionId]
    if (Array.isArray(selected) && selected.length > 0) {
      const joined = selected.join(dynamicParameter.delimiter ?? '')
      userPassedParameters[dynamicParameter.parameter] = joined
      delete userPassedParameters[dynamicParameter.dropdownOptionId]
    }
  })
  return userPassedParameters
}

const getDynamicParameters = () => {
  axiosCore.get('/int/client/dynamicaction/' + props.action.id + '/parameters').then((res) => {
    dynamicParameters.value = res.data.parameters
    dynamicParameters.value?.forEach((dynamicParameter) => (dynamicParameter.showTextField = false))
  })
}

const commandPreview = ref('')
var userPassedParametersString = ref('')

const getCommandPreview = () => {
  userPassedParametersString.value = ''
  dynamicParameters.value.forEach((dynamicParameter) => {
    userPassedParametersString.value +=
      dynamicParameter.parameter +
      ':' +
      userPassedParameters.value[dynamicParameter.parameter] +
      ','
    if (dynamicParameter?.dropdownOptionId != undefined && dynamicParameter?.multiselect && !dynamicParameter.showTextField) {
      userPassedParametersString.value +=
        dynamicParameter.parameter +
        ':' +
        userPassedParameters.value[dynamicParameter.dropdownOptionId]?.join(
          dynamicParameter.delimiter ?? ''
        ) +
        ','
    }
  })
  userPassedParametersString.value = userPassedParametersString.value.slice(0, -1)
  axiosCore
    .post(
      '/int/client/dynamicaction/' + props.action.id + '/commandpreview',
      userPassedParametersString.value
    )
    .then((res) => {
      commandPreview.value = res.data
      newAction.value.command = res.data
    })
}
</script>
