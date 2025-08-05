<template>
  <Card>
    <template #header><br /></template>
    <template #content>
      <div class="sophrosyne-form-wrapper">
        <form class="sophrosyne-form">
          <span class="sophrosyne-field-wrapper">
            <FloatLabel>
              <InputText
                class="sophrosyne-inputtext"
                id="username"
                v-model="newUser.username"
                type="text"
              />
              <label for="username">{{
                $t('settings.users.user_add_modal.form.fields.username')
              }}</label>
            </FloatLabel>
          </span>
          <span class="sophrosyne-field-wrapper">
            <FloatLabel>
              <InputText
                class="sophrosyne-inputtext"
                id="firstName"
                v-model="newUser.firstName"
                type="text"
              />
              <label for="firstName">{{
                $t('settings.users.user_add_modal.form.fields.firstName')
              }}</label>
            </FloatLabel>
          </span>
          <span class="sophrosyne-field-wrapper">
            <FloatLabel>
              <InputText
                class="sophrosyne-inputtext"
                id="lastName"
                v-model="newUser.lastName"
                type="text"
              />
              <label for="lastName">{{
                $t('settings.users.user_add_modal.form.fields.lastName')
              }}</label>
            </FloatLabel>
          </span>
          <span class="sophrosyne-field-wrapper">
            <FloatLabel>
              <InputText
                class="sophrosyne-inputtext"
                id="email"
                v-model="newUser.email"
                type="text"
              />
              <label for="email">{{ $t('settings.users.user_add_modal.form.fields.email') }}</label>
            </FloatLabel>
          </span>
          <span class="sophrosyne-field-wrapper">
            <Dropdown
              class="sophrosyne-inputtext"
              v-model="newUser.role"
              :options="roleOptions"
              optionLabel="role"
              filter
              :placeholder="$t('settings.users.user_add_modal.form.dropdown.placeholder')"
            />
          </span>
          <Button :label="$t('settings.users.user_add_modal.form.btn')" @click="createNewUser" />
          <ProgressBar v-if="submitting" mode="indeterminate" style="height: 6px"></ProgressBar>
        </form>
        <Dialog
          v-model:visible="toggleRevealUserInitPassword"
          modal
          maximizable
          :header="$t('settings.users.user_add_modal.response_modal.title')"
          v-if="toggleRevealUserInitPassword"
          @close="closeAllUserAddModals"
        >
          <p>{{ $t('settings.users.user_add_modal.response_modal.description') }}</p>
          <Panel
            :header="$t('settings.users.user_add_modal.response_modal.panel_title')"
            toggleable
          >
            <h4 class="white-space-normal" style="word-wrap: break-word">
              {{ newUser.intPassword }}
            </h4>
          </Panel>
        </Dialog>
        <Toast />
      </div>
    </template>
  </Card>
</template>

<script setup>
import { defineEmits, ref, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'
import { Dialog } from 'primevue'

const toast = useToast()
const { t } = useI18n()
const axiosCore = inject('axios-core')
const emit = defineEmits(['close', 'getUsers'])

const newUser = ref({})
const roleOptions = ref([{ role: 'Admin' }, { role: 'User' }, { role: 'Client' }])

const submitting = ref(false)

const closeAllUserAddModals = () => {
  emit('close')
}

const toggleRevealUserInitPassword = ref(false)
const revealUserInitPassword = () => {
  toggleRevealUserInitPassword.value = !toggleRevealUserInitPassword.value
}

const createNewUser = () => {
  submitting.value = true
  try {
    newUser.value.role = newUser.value.role.role.toUpperCase()
  } catch {
    /* empty */
  }
  axiosCore
    .post('/int/admin/user', newUser.value)
    .then((res) => {
      newUser.value.intPassword = res.data
      toast.add({
        severity: 'success',
        summary: t('settings.users.user_add_modal.toast.success.message'),
        detail: t('settings.users.user_add_modal.toast.success.detail'),
        life: 3000
      })
      submitting.value = false
      emit('getUsers')
      revealUserInitPassword()
    })
    .catch(() => {
      toast.add({
        severity: 'error',
        summary: t('settings.users.user_add_modal.toast.error.message'),
        detail: t('settings.users.user_add_modal.toast.error.detail'),
        life: 3000
      })
      submitting.value = false
    })
}
</script>

<style></style>
