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
                v-model="newActionRecommendation.name"
                type="text"
                class="sophrosyne-inputtext"
              />
              <label for="name">{{
                $t('action_recommendations.action_recommendation_creation_form.fields.name')
              }}</label>
            </FloatLabel>
          </span>
          <span
            class="sophrosyne-field-wrapper"
            v-tooltip="{
              value: $t(
                'action_recommendations.action_recommendation_creation_form.tooltip.description'
              ),
              showDelay: 100,
              hideDelay: 300
            }"
          >
            <FloatLabel>
              <Textarea
                id="description"
                v-model="newActionRecommendation.description"
                rows="10"
                cols="20"
                class="sophrosyne-inputtext"
              />
              <label for="description">{{
                $t('action_recommendations.action_recommendation_creation_form.fields.description')
              }}</label>
            </FloatLabel>
          </span>
          <span
            class="sophrosyne-field-wrapper"
            v-tooltip="{
              value: $t(
                'action_recommendations.action_recommendation_creation_form.tooltip.responsible_entity'
              ),
              showDelay: 100,
              hideDelay: 300
            }"
          >
            <FloatLabel>
              <InputText
                id="responsibleEntity"
                v-model="newActionRecommendation.responsibleEntity"
                type="text"
                class="sophrosyne-inputtext"
              />
              <label for="responsibleEntity">{{
                $t(
                  'action_recommendations.action_recommendation_creation_form.fields.responsible_entity'
                )
              }}</label>
            </FloatLabel>
          </span>
          <span
            class="sophrosyne-field-wrapper"
            v-tooltip="{
              value: $t(
                'action_recommendations.action_recommendation_creation_form.tooltip.contact_information'
              ),
              showDelay: 100,
              hideDelay: 300
            }"
          >
            <FloatLabel>
              <InputText
                id="contactInformation"
                v-model="newActionRecommendation.contactInformation"
                type="text"
                class="sophrosyne-inputtext"
              />
              <label for="contactInformation">{{
                $t(
                  'action_recommendations.action_recommendation_creation_form.fields.contact_information'
                )
              }}</label>
            </FloatLabel>
          </span>
          <span
            class="sophrosyne-field-wrapper"
            v-tooltip="{
              value: $t(
                'action_recommendations.action_recommendation_creation_form.tooltip.additional_documentation'
              ),
              showDelay: 100,
              hideDelay: 300
            }"
          >
            <div style="text-align: center; min-width: 100%">
              <ToggleButton
                v-model="showUpload"
                :onLabel="
                  $t(
                    'action_recommendations.action_recommendation_creation_form.fields.show_upload.update'
                  )
                "
                :offLabel="
                  $t(
                    'action_recommendations.action_recommendation_creation_form.fields.show_upload.keep'
                  )
                "
                offIcon="pi pi-save"
                onIcon="pi pi-sync"
                style="min-width: 100%"
                @click="setAdditionalDocumentation"
                v-if="props.isUpdate"
              />
              <FileUpload
                id="additionalDocumentation"
                accept="application/pdf"
                :maxFileSize="128000000"
                :multiple="false"
                url=""
                :showUploadButton="false"
                @select="onFileUpload($event)"
                @remove="removefile"
                @clear="removefile"
                @removeUploadedFile="removefile"
                v-if="props.isUpdate == null || props.isUpdate == false || showUpload"
              />
            </div>
          </span>
          <span
            class="sophrosyne-field-wrapper"
            v-tooltip="{
              value: $t(
                'action_recommendations.action_recommendation_creation_form.tooltip.allowed_apikeys'
              ),
              showDelay: 100,
              hideDelay: 300
            }"
          >
            <MultiSelect
              v-model="newActionRecommendation.allowedApikeysAsObject"
              :options="apikeys"
              filter
              optionLabel="apikeyname"
              :placeholder="
                $t(
                  'action_recommendations.action_recommendation_creation_form.fields.allowed_apikeys'
                )
              "
              class="w-full md:w-20rem"
              @change="getApikeys"
            />
          </span>
          <Button
            :label="
              $t('action_recommendations.action_recommendation_creation_form.btn.create.label')
            "
            @click="emit('submitActionRecommendation', newActionRecommendation)"
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

const { getApikeys, apikeys } = useApikeysComposable()

const props = defineProps(['actionRecommendation', 'settings', 'isUpdate'])

const newActionRecommendation = ref(props.actionRecommendation)
const oldActionRecommendation = { ...props.actionRecommendation }
const showUpload = ref(false)

onMounted(() => {
  getApikeys().then(() => {
    newActionRecommendation.value['allowedApikeysAsObject'] = apikeys.value.filter((apikey) =>
      newActionRecommendation.value['allowedApikeys'].includes(apikey.apikeyname)
    )
  })
})

const emit = defineEmits(['submitActionRecommendation'])

const onFileUpload = async (event) => {
  var file = event.files[0]

  const reader = new FileReader()
  reader.onload = function (event) {
    let binaryData = event.target.result
    let byteArray = new Uint8Array(binaryData)
    let blob = new Blob([byteArray])
    let readerBase64 = new FileReader()
    readerBase64.onload = function (event) {
      var base64String = event.target.result.split(',')[1]
      newActionRecommendation.value.additionalDocumentation = base64String
    }
    readerBase64.readAsDataURL(blob)
  }
  reader.readAsArrayBuffer(file)
}

const setAdditionalDocumentation = () => {
  showUpload.value
    ? (newActionRecommendation.value.additionalDocumentation =
        oldActionRecommendation.additionalDocumentation)
    : (newActionRecommendation.value.additionalDocumentation = null)
}

const removefile = () => {
  newActionRecommendation.value.additionalDocumentation = null
}
</script>
