<template>
  <Card class="sophrosyne-card">
    <template #header><br /></template>
    <template #content>
      <div v-for="dynamicParameter in dynamicParameters" :key="dynamicParameter">
        <div class="sophrosyne-form-wrapper">
          <form class="sophrosyne-form">
            <span class="sophrosyne-field-wrapper">
              <FloatLabel>
                <InputText
                  :id="dynamicParameter"
                  v-model="userPassedParameters[dynamicParameter]"
                  type="text"
                  class="sophrosyne-inputtext"
                  @change="getCommandPreview"
                />
                <label :for="dynamicParameter">{{ dynamicParameter }}</label>
              </FloatLabel>
            </span>
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
              executeAction(props.action.id, userPassedParameters) ? emits('close') : undefined
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

const getDynamicParameters = () => {
  axiosCore.get('/int/client/dynamicaction/' + props.action.id + '/parameters').then((res) => {
    dynamicParameters.value = res.data.parameters
  })
}

const commandPreview = ref('')
var userPassedParametersString = ref('')

const getCommandPreview = () => {
  userPassedParametersString.value = ''
  dynamicParameters.value.forEach((dynamicParameter) => {
    userPassedParametersString.value +=
      dynamicParameter + ':' + userPassedParameters.value[dynamicParameter] + ','
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
