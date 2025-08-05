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
                v-model="newAction.name"
                type="text"
                class="sophrosyne-inputtext"
              />
              <label for="name">{{
                $t('actions.' + I18nSource + '.action_creation_form.fields.name')
              }}</label></FloatLabel
            >
          </span>
          <span class="sophrosyne-field-wrapper">
            <FloatLabel>
              <InputText
                id="description"
                v-model="newAction.description"
                type="text"
                class="sophrosyne-inputtext"
              />
              <label for="description">{{
                $t('actions.' + I18nSource + '.action_creation_form.fields.description')
              }}</label></FloatLabel
            >
          </span>
          <span class="sophrosyne-field-wrapper">
            <FloatLabel>
              <InputText
                id="version"
                v-model="newAction.version"
                type="text"
                class="sophrosyne-inputtext"
              />
              <label for="version">{{
                $t('actions.' + I18nSource + '.action_creation_form.fields.version')
              }}</label></FloatLabel
            >
          </span>
          <InputGroup>
            <span
              class="sophrosyne-field-wrapper sophrosyne-inputtext"
              v-tooltip="{
                value: $t('actions.' + I18nSource + '.action_creation_form.tooltip.command'),
                showDelay: 100,
                hideDelay: 300
              }"
            >
              <FloatLabel>
                <InputText id="command" v-model="newAction.command" type="text" />
                <label for="command">{{
                  $t('actions.' + I18nSource + '.action_creation_form.fields.command')
                }}</label>
                <InputGroupAddon>
                  <Button
                    icon="pi pi-arrow-up-right-and-arrow-down-left-from-center"
                    severity="primary"
                    @click="toggleShowCommandTerminalDialog()"
                /></InputGroupAddon>
              </FloatLabel>
              <ActionCommandTemplate
                v-if="showCommandTerminalDialog"
                @submitCommand="setCommand($event)"
                @noCommand="toggleShowCommandTerminalDialog()"
                :command="newAction.command"
                :isDynamicAction="props.isDynamicAction"
              ></ActionCommandTemplate>
            </span>
          </InputGroup>

          <InputGroup>
            <span
              class="sophrosyne-field-wrapper sophrosyne-inputtext"
              v-tooltip="{
                value: dynamic_parameters_tooltip,
                showDelay: 100,
                hideDelay: 300
              }"
              v-if="props.isDynamicAction"
              ><FloatLabel>
                <InputText
                  id="dynamicParameters"
                  v-model="newAction.dynamicParameters"
                  type="text"
                />
                <label for="dynamicParameters">{{
                  $t('actions.dynamic_action.action_creation_form.fields.dynamic_parameters')
                }}</label>
                <InputGroupAddon>
                  <Button
                    icon="pi pi-arrow-up-right-and-arrow-down-left-from-center"
                    severity="primary"
                    @click="toggleShowDynamicCommandParameterTerminalDialog()"
                  />
                </InputGroupAddon>
              </FloatLabel>
              <ActionCommandTemplate
                v-if="showDynamicCommandParameterTerminalDialog"
                @submitCommand="setDynamicCommand($event)"
                @noCommand="toggleShowDynamicCommandParameterTerminalDialog()"
                :command="newAction.dynamicParameters"
                :isDynamicAction="props.isDynamicAction"
                :isDynamicActionParameters="true"
              ></ActionCommandTemplate>
            </span>
          </InputGroup>

          <span
            class="sophrosyne-field-wrapper"
            v-tooltip="{
              value: $t(
                'actions.' +
                  I18nSource +
                  '.action_creation_form.tooltip.post_execution_log_file_path'
              ),
              showDelay: 100,
              hideDelay: 300
            }"
            ><FloatLabel>
              <InputText
                id="postExecutionLogFilePath"
                v-model="newAction.postExecutionLogFilePath"
                type="text"
                class="sophrosyne-inputtext"
              />
              <label for="postExecutionLogFilePath">{{
                $t(
                  'actions.' +
                    I18nSource +
                    '.action_creation_form.fields.post_execution_log_file_path'
                )
              }}</label></FloatLabel
            >
          </span>

          <span
            class="sophrosyne-field-wrapper"
            v-tooltip="{
              value: $t(
                'actions.' + I18nSource + '.action_creation_form.tooltip.requires_confirmation'
              ),
              showDelay: 100,
              hideDelay: 300
            }"
          >
            <ToggleButton
              v-model="newAction.requiresConfirmation"
              :onLabel="
                $t(
                  'actions.' + I18nSource + '.action_creation_form.fields.requires_confirmation_on'
                )
              "
              :offLabel="
                $t(
                  'actions.' + I18nSource + '.action_creation_form.fields.requires_confirmation_off'
                )
              "
              onIcon="pi pi-lock"
              offIcon="pi pi-lock-open"
            />
          </span>
          <span
            class="sophrosyne-field-wrapper"
            v-tooltip="{
              value: $t(
                'actions.' + I18nSource + '.action_creation_form.tooltip.keep_latest_confirmation'
              ),
              showDelay: 100,
              hideDelay: 300
            }"
          >
            <ToggleButton
              v-if="props.isDynamicAction && newAction.requiresConfirmation"
              v-model="newAction.keepLatestConfirmationRequest"
              :onLabel="
                $t(
                  'actions.' +
                    I18nSource +
                    '.action_creation_form.fields.keep_latest_confirmation_on'
                )
              "
              :offLabel="
                $t(
                  'actions.' +
                    I18nSource +
                    '.action_creation_form.fields.keep_latest_confirmation_off'
                )
              "
              onIcon="pi pi-replay"
              offIcon="pi pi-list-check"
            />
          </span>

          <span
            class="sophrosyne-field-wrapper"
            v-tooltip="{
              value: $t('actions.' + I18nSource + '.action_creation_form.tooltip.muted'),
              showDelay: 100,
              hideDelay: 300
            }"
          >
            <ToggleButton
              v-model="newAction.muted"
              :onLabel="$t('actions.' + I18nSource + '.action_creation_form.fields.muted_on')"
              :offLabel="$t('actions.' + I18nSource + '.action_creation_form.fields.muted_off')"
              onIcon="pi pi-pause"
              offIcon="pi pi-play"
            />
          </span>

          <span
            class="sophrosyne-field-wrapper"
            v-tooltip="{
              value: $t('actions.' + I18nSource + '.action_creation_form.tooltip.allowed_apikeys'),
              showDelay: 100,
              hideDelay: 300
            }"
          >
            <MultiSelect
              v-model="newAction.allowedApikeysAsObject"
              :options="apikeys"
              filter
              optionLabel="apikeyname"
              :placeholder="
                $t('actions.' + I18nSource + '.action_creation_form.fields.allowed_apikeys')
              "
              class="w-full md:w-20rem"
              @change="getApikeys"
            />
          </span>
          <Button
            :label="$t('actions.' + I18nSource + '.action_creation_form.btn.create.label')"
            @click="emit('submitAction', newAction)"
          />
        </form>
      </div>
    </template>
    <template #footer>
      <ProgressBar
        v-if="props.settings.submitting"
        mode="indeterminate"
        style="height: 6px"
      ></ProgressBar>
    </template>
  </Card>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useApikeysComposable } from '@/composables/ApikeysComposable.js'
import { useI18n } from 'vue-i18n'
import ActionCommandTemplate from '@/components/actions/utils/ActionCommandTemplate.vue'

const { t } = useI18n()

const { getApikeys, apikeys } = useApikeysComposable()
const props = defineProps(['action', 'settings', 'isDynamicAction'])
const newAction = ref(props.action)
const I18nSource = props.isDynamicAction ? ref('dynamic_action') : ref('action')
const dynamic_parameters_tooltip = ref('')
onMounted(() => {
  getApikeys().then(() => {
    newAction.value['allowedApikeysAsObject'] = apikeys.value.filter((apikey) =>
      newAction.value['allowedApikeys'].includes(apikey.apikeyname)
    )
  })

  dynamic_parameters_tooltip.value = t(
    'actions.' + I18nSource.value + '.action_creation_form.tooltip.dynamic_parameters'
  )
    .replace(/&#123;/g, '{')
    .replace(/&#125;/g, '}')
})

const showCommandTerminalDialog = ref(false)
const toggleShowCommandTerminalDialog = () => {
  showCommandTerminalDialog.value = !showCommandTerminalDialog.value
}
const setCommand = (command) => {
  toggleShowCommandTerminalDialog()
  newAction.value['command'] = command
}

const showDynamicCommandParameterTerminalDialog = ref(false)
const toggleShowDynamicCommandParameterTerminalDialog = () => {
  showDynamicCommandParameterTerminalDialog.value = !showDynamicCommandParameterTerminalDialog.value
}
const setDynamicCommand = (command) => {
  toggleShowDynamicCommandParameterTerminalDialog()
  newAction.value['dynamicParameters'] = command
}

const emit = defineEmits(['submitAction'])
</script>
<style scoped>
.p-togglebutton {
  min-width: 100%;
}
</style>
