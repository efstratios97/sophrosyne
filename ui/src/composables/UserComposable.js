import { ref, inject } from 'vue'
import { useLoggedUser } from '@/stores/loggedUser'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'

export const useUserComposable = () => {
  const axiosCore = inject('axios-core')

  const loggedUserStore = useLoggedUser()
  const toast = useToast()
  const { t } = useI18n()

  const users = ref([])

  const getUsers = async () => {
    await axiosCore
      .get('/int/admin/users')
      .then((res) => {
        users.value = res.data
      })
      .catch(() => {})
  }

  const loginUser = (username, password) => {
    toast.add({
      severity: 'info',
      summary: t('login.toast.info.message'),
      detail: t('login.toast.info.detail'),
      life: 5000
    })
    loggedUserStore.loggedUserUsername = username
    return axiosCore
      .get('/auth/user/?username=' + username + '&password=' + password)
      .then((res) => {
        loggedUserStore.changeToken(res.data)
      })
      .then(() => {
        toast.add({
          severity: 'success',
          summary: t('login.toast.success.message'),
          detail: t('login.toast.success.detail'),
          life: 3000
        })
        axiosCore.get('/int/client/self/' + username).then((res) => {
          loggedUserStore.loggedUserFirstName = res.data.firstName
          loggedUserStore.loggedUserLastName = res.data.lastName
          loggedUserStore.loggedUserEmail = res.data.email
          loggedUserStore.loggedUserFirstName = res.data.firstName
          loggedUserStore.loggedUserLastName = res.data.lastName
          loggedUserStore.loggedUserRole = res.data.role
        })
      })
  }

  return { getUsers, users, loginUser }
}
