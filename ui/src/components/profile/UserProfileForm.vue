<template>
  <div>
    <Card class="sophrosyne-card">
      <template #title> {{ $t('userprofile.title') }} </template>
      <template #subtitle> {{ $t('userprofile.sub_title') }} </template>
      <template #content>
        <form class="grid pt-4">
          <span class="col-3 col-offset-4 mb-3">
            <FloatLabel>
              <InputText
                class="sophrosyne-inputtext"
                id="username"
                v-model="user.username"
                type="text"
                :disabled="!isAdmin"
              />
              <label for="username">{{
                $t('settings.users.user_add_modal.form.fields.username')
              }}</label>
            </FloatLabel>
          </span>
          <span class="col-3 col-offset-4 mb-3">
            <FloatLabel>
              <InputText
                class="sophrosyne-inputtext"
                id="firstName"
                v-model="user.firstName"
                type="text"
                :disabled="!isAdmin"
              />
              <label for="firstName">{{
                $t('settings.users.user_add_modal.form.fields.firstName')
              }}</label>
            </FloatLabel>
          </span>
          <span class="col-3 col-offset-4 mb-3">
            <FloatLabel>
              <InputText
                class="sophrosyne-inputtext"
                id="lastName"
                v-model="user.lastName"
                type="text"
                :disabled="!isAdmin"
              />
              <label for="lastName">{{
                $t('settings.users.user_add_modal.form.fields.lastName')
              }}</label>
            </FloatLabel>
          </span>
          <span class="col-3 col-offset-4 mb-3">
            <FloatLabel>
              <InputText
                class="sophrosyne-inputtext"
                id="email"
                v-model="user.email"
                type="text"
                :disabled="!isAdmin"
              />
              <label for="email">{{ $t('settings.users.user_add_modal.form.fields.email') }}</label>
            </FloatLabel>
          </span>
          <span class="col-3 col-offset-4 mb-3">
            <FloatLabel>
              <InputText
                id="password"
                v-model="user.password"
                type="text"
                class="sophrosyne-inputtext"
              />
              <label for="password">{{ $t('userprofile.form.new_password') }}</label>
            </FloatLabel>
          </span>
          <span class="col-3 col-offset-4 mb-3">
            <Button label="Submit" @click="updateUser" class="w-full" />
            <ProgressBar v-if="submitting" mode="indeterminate" style="height: 6px"></ProgressBar>
          </span>
        </form>
      </template>
    </Card>
    <Toast />
  </div>
</template>

<script setup>
import { defineEmits, ref, inject, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'
import { useLoggedUser } from '@/stores/loggedUser'

const toast = useToast()
const { t } = useI18n()
const axiosCore = inject('axios-core')
const emit = defineEmits(['getUsers'])
const loggedUserStore = useLoggedUser()

const isAdmin = ref(loggedUserStore.loggedUserRole)
onMounted(() => {
  isAdmin.value == 'ADMIN' ? true : false
})

const user = ref({
  username: loggedUserStore.loggedUserUsername,
  firstName: loggedUserStore.loggedUserFirstName,
  lastName: loggedUserStore.loggedUserLastName,
  email: loggedUserStore.loggedUserEmail,
  password: ''
})

const submitting = ref(false)

const toggleRevealUserInitPassword = ref(false)
const revealUserInitPassword = () => {
  toggleRevealUserInitPassword.value = !toggleRevealUserInitPassword.value
}

const updateUser = () => {
  submitting.value = true
  axiosCore
    .put('/int/client/self/' + loggedUserStore.loggedUserUsername, user.value)
    .then((res) => {
      user.value.intPassword = res.data
      toast.add({
        severity: 'success',
        summary: t('userprofile.toast.success.message'),
        detail: t('userprofile.toast.success.detail'),
        life: 3000
      })
      submitting.value = false
      emit('getUsers')
      revealUserInitPassword()
    })
    .catch(() => {
      toast.add({
        severity: 'error',
        summary: t('userprofile.toast.error.message'),
        detail: t('userprofile.toast.error.detail'),
        life: 3000
      })
      submitting.value = false
    })
}
</script>

<style></style>
