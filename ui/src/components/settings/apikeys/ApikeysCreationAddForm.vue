<template>
  <Card class="sophrosyne-card">
    <template #header><br /></template>
    <template #content>
      <div class="sophrosyne-form-wrapper">
        <form class="sophrosyne-form">
          <span class="sophrosyne-field-wrapper">
            <FloatLabel>
              <InputText
                class="sophrosyne-inputtext"
                id="apikeyname"
                v-model="apikeyname"
                type="text"
                :invalid="!checkValidApikeyName(apikeyname)"
              />
              <label for="apikeyname">{{
                $t('settings.apikeyscreationaddform.input_text_apikeyname')
              }}</label>
            </FloatLabel>
          </span>
          <span class="sophrosyne-field-wrapper">
            <FloatLabel>
              <InputText
                class="sophrosyne-inputtext"
                id="apikeydescription"
                v-model="apikeydescription"
                type="text"
              />
              <label for="apikeydescription">{{
                $t('settings.apikeyscreationaddform.input_text_apikeydescription')
              }}</label></FloatLabel
            >
          </span>
          <div class="grid">
            <div class="col-4 pt-1">
              {{ $t('settings.apikeyscreationaddform.input_text_apikeyactive') }}
            </div>
            <div class="col"><InputSwitch v-model="apikeyactive" /></div>
          </div>
          <Button label="Submit" @click="createApikey" :disabled="!isValidApipkeyName" />
          <ProgressBar v-if="submitting" mode="indeterminate" style="height: 6px"></ProgressBar>
        </form>
        <Toast />
      </div>
    </template>
  </Card>
</template>

<script setup>
import { ref, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'

const toast = useToast()
const { t } = useI18n()
const axiosCore = inject('axios-core')

const apikeyname = ref('')
const apikeydescription = ref('')
const apikeyactive = ref(true)
const submitting = ref(false)
const isValidApipkeyName = ref(true)

const checkValidApikeyName = (apikeyname) => {
  const regex = /^[^/\\%&=?#\s]*$/
  isValidApipkeyName.value = regex.test(apikeyname)
  return isValidApipkeyName.value
}

const createApikey = () => {
  submitting.value = true
  axiosCore
    .post('/int/admin/apikey', {
      apikeyname: apikeyname.value,
      apikeydescription: apikeydescription.value,
      apikeyactive: apikeyactive.value ? 1 : 0
    })
    .then(() => {
      toast.add({
        severity: 'success',
        summary: t('settings.apikeyscreationaddform.toast.success.message'),
        detail: t('settings.apikeyscreationaddform.toast.success.detail'),
        life: 3000
      })
      submitting.value = false
      setTimeout(function () {
        location.reload()
      }, 1500)
    })
    .catch(() => {
      toast.add({
        severity: 'error',
        summary: t('settings.apikeyscreationaddform.toast.error.message'),
        detail: t('settings.apikeyscreationaddform.toast.error.detail'),
        life: 3000
      })
      submitting.value = false
    })
}
</script>

<style></style>
