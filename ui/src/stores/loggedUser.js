import { ref } from 'vue'
import { defineStore } from 'pinia'
import { useLocalStorage } from '@vueuse/core'

export const useLoggedUser = defineStore('loggedUser', () => {
  const loggedUserEmail = ref(useLocalStorage('loggedUserEmail', ''))
  const loggedUserUsername = ref(useLocalStorage('loggedUserUsername', ''))
  const loggedUserFirstName = ref(useLocalStorage('loggedUserFirstName', ''))
  const loggedUserLastName = ref(useLocalStorage('loggedUserLastName', ''))
  const loggedUserRole = ref(useLocalStorage('loggedUserRole', ''))
  const loggedUserToken = ref(useLocalStorage('loggedUserToken', ''))

  const changeToken = (token) => {
    loggedUserToken.value = token
  }

  return {
    loggedUserEmail,
    loggedUserUsername,
    loggedUserFirstName,
    loggedUserLastName,
    loggedUserRole,
    loggedUserToken,
    changeToken
  }
})
