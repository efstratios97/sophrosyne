<template>
  <div class="surface-card p-4 shadow-2 border-round w-full lg:w-6">
    <div class="text-center mb-4">
      <img
        src="@/assets/images/logos/Sophrosyne_small.png"
        alt="Image"
        max-height="250px"
        class="mb-0"
      />
      <div class="text-900 text-3xl font-medium mb-3">{{ $t('login.main_text') }}</div>
      <span class="text-600 font-medium line-height-3">{{ $t('login.sub_text') }}</span>
      <a
        class="font-medium no-underline ml-2 text-blue-500 cursor-pointer"
        @click="toggleCreateAccount"
        >{{ $t('login.sub_text_link_text') }}</a
      >
    </div>

    <div>
      <label for="username1" class="block text-900 font-medium mb-2">{{
        $t('login.form_username')
      }}</label>
      <InputText
        v-model="username"
        id="username1"
        type="text"
        :placeholder="$t('login.form_username')"
        class="w-full mb-3"
      />

      <label for="password1" class="block text-900 font-medium mb-2">{{
        $t('login.form_password')
      }}</label>
      <InputText
        v-model="password"
        id="password1"
        type="password"
        :placeholder="$t('login.form_password')"
        class="w-full mb-3"
      />

      <div class="flex justify-content-start mb-6">
        <a
          class="font-medium no-underline ml-2 text-blue-500 text-right cursor-pointer"
          @click="toggleForgotPasswd"
          >{{ $t('login.forgot_password_link_text') }}</a
        >
      </div>
      <Button
        v-bind:label="$t('login.button_sign_in')"
        icon="pi pi-user"
        class="w-full"
        @click="
          loginUser(username, password)
            .then(() => {
              loginHandler()
            })
            .catch(() => {
              toast.add({
                severity: 'error',
                summary: t('login.toast.error.message'),
                detail: t('login.toast.error.detail'),
                life: 3000
              })
            })
        "
      ></Button>
    </div>
    <div v-if="showForgotPasswd">
      <LoginForgotPassword :visible="showForgotPasswd"></LoginForgotPassword>
    </div>
    <div v-if="showCreateAccount">
      <LoginCreateAccount :visible="showCreateAccount"></LoginCreateAccount>
    </div>
    <Toast />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useLoggedUser } from '@/stores/loggedUser'
import router from '@/router'
import LoginForgotPassword from '@/components/login/LoginForgotPassword.vue'
import LoginCreateAccount from '@/components/login/LoginCreateAccount.vue'
import { useUserComposable } from '@/composables/UserComposable.js'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'

const toast = useToast()
const { t } = useI18n()
const loggedUserStore = useLoggedUser()

const { loginUser } = useUserComposable()

const username = ref('')
const password = ref('')

onMounted(() => {
  loggedUserStore.loggedUserUsername = ''
  loggedUserStore.loggedUserFirstName = ''
  loggedUserStore.loggedUserLastName = ''
  loggedUserStore.loggedUserEmail = ''
  loggedUserStore.loggedUserRole = ''
  loggedUserStore.loggedUserToken = ''
})

const showForgotPasswd = ref(false)
const toggleForgotPasswd = () => {
  showForgotPasswd.value = !showForgotPasswd.value
}
const showCreateAccount = ref(false)
const toggleCreateAccount = () => {
  showCreateAccount.value = !showCreateAccount.value
}

const loginHandler = () => {
  setTimeout(() => {
    router.push({ path: '/home', replace: true })
  }, 1000)
}
</script>
