<template>
  <Card class="sophrosyne-card">
    <template #header><br /></template>
    <template #content>
      <div class="sophrosyne-form-wrapper">
        <form class="sophrosyne-form">
          <span class="sophrosyne-field-wrapper">
            <FloatLabel>
              <InputText
                id="name"
                v-model="newDropdownOption.name"
                type="text"
                class="sophrosyne-inputtext"
              />
              <label for="name">{{
                $t('actions.dropdown_option.dropdown_option_template_form.fields.name')
              }}</label></FloatLabel
            >
          </span>
          <span class="sophrosyne-field-wrapper">
            <FloatLabel>
              <InputText
                id="description"
                v-model="newDropdownOption.description"
                type="text"
                class="sophrosyne-inputtext"
              />
              <label for="description">{{
                $t('actions.dropdown_option.dropdown_option_template_form.fields.description')
              }}</label></FloatLabel
            >
          </span>

          <span class="sophrosyne-field-wrapper">
            <FloatLabel>
              <InputText
                id="description"
                v-model="newDropdownOption.dynamicParameterToMatch"
                type="text"
                class="sophrosyne-inputtext"
              />
              <label for="description">{{
                $t(
                  'actions.dropdown_option.dropdown_option_template_form.fields.dynamic_parameter_to_match'
                )
              }}</label></FloatLabel
            >
          </span>

          <span
            class="sophrosyne-field-wrapper"
            v-tooltip="{
              value: $t(
                $t('actions.dropdown_option.dropdown_option_template_form.fields.type.field_name')
              ),
              showDelay: 100,
              hideDelay: 300
            }"
          >
            <ToggleButton
              v-model="newDropdownOption.type"
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
          <span class="sophrosyne-field-wrapper" v-if="!newDropdownOption.type">
            <FloatLabel>
              <InputText
                id="dynamic_input"
                v-model="newDropdownOption.getterDropdownOptionCallAddress"
                type="text"
                class="sophrosyne-inputtext"
              />
              <label for="dynamic_input">{{
                $t(
                  'actions.dropdown_option.dropdown_option_template_form.fields.type.dynamic.placeholder'
                )
              }}</label></FloatLabel
            >
          </span>
          <span class="sophrosyne-field-wrapper" v-else>
            <Select
              v-model="newOption"
              :options="newDropdownOption.dropdownOptions"
              :placeholder="
                $t(
                  'actions.dropdown_option.dropdown_option_template_form.fields.type.static.placeholder'
                )
              "
              editable
              class="w-full md:w-56"
            >
              <template #footer>
                <div class="p-3">
                  <Button
                    :label="
                      $t(
                        'actions.dropdown_option.dropdown_option_template_form.fields.type.static.btn.add'
                      )
                    "
                    fluid
                    severity="secondary"
                    variant="text"
                    size="small"
                    icon="pi pi-plus"
                    @click="addNewOption"
                  />
                  <Button
                    :label="
                      $t(
                        'actions.dropdown_option.dropdown_option_template_form.fields.type.static.btn.remove'
                      )
                    "
                    fluid
                    severity="warn"
                    variant="text"
                    size="small"
                    icon="pi pi-plus"
                    @click="clearNewOption"
                  />
                </div>
              </template>
            </Select>
          </span>

          <Button
            :label="$t('actions.dropdown_option.dropdown_option_template_form.btn.create.label')"
            @click="emitDropdownoption()"
          />
        </form>
      </div>
    </template>
    <template #footer>
      <ProgressBar
        v-if="props.metadata.submitting"
        mode="indeterminate"
        style="height: 6px"
      ></ProgressBar>
    </template>
  </Card>
</template>
<script setup lang="ts">
import { ref, onMounted, defineEmits } from 'vue'
import { useI18n } from 'vue-i18n'

const props = defineProps(['dropdownOption', 'metadata', 'create'])
const newDropdownOption = ref(props.dropdownOption)
const emits = defineEmits(['submitDropdownOption'])

onMounted(() => {
  if (!props.create) {
    if (newDropdownOption.value.type == 'STATIC') {
      newDropdownOption.value.type = true
    } else {
      newDropdownOption.value.type = false
    }
  }
})

const emitDropdownoption = () => {
  if (newDropdownOption.value.type) {
    newDropdownOption.value.type = 'STATIC'
  } else {
    newDropdownOption.value.type = 'DYNAMIC'
  }
  emits('submitDropdownOption', newDropdownOption.value)
}

const newOption = ref('')
const addNewOption = () => {
  if (newDropdownOption.value.dropdownOptions == undefined) {
    newDropdownOption.value.dropdownOptions = []
  }
  if (newOption.value == '' || newDropdownOption.value.dropdownOptions.includes(newOption.value)) {
    return
  }
  newDropdownOption.value.dropdownOptions.push(newOption.value)

  console.log(newDropdownOption.value.dropdownOptions)
  newDropdownOption.value.dropdownOptions
  newOption.value = ''
}

const clearNewOption = () => {
  newDropdownOption.value.dropdownOptions = newDropdownOption.value.dropdownOptions.filter(
    (obj: string) => obj !== newOption.value
  )
  newOption.value = ''
}
</script>
<style scoped>
.p-togglebutton {
  min-width: 100%;
}
</style>
