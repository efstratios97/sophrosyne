import { ref, inject } from 'vue'

export const useApikeysComposable = () => {
  const apikeys = ref([])
  const axiosCore = inject('axios-core')

  const getApikeys = async () => {
    await axiosCore
      .get('/int/user/apikeys')
      .then((res) => {
        apikeys.value = res.data
        apikeys.value.sort((a, b) => {
          let fa = a.apikeyname.toLowerCase(),
            fb = b.apikeyname.toLowerCase()
          if (fa < fb) {
            return -1
          }
          if (fa > fb) {
            return 1
          }
          return 0
        })
      })
      .then(() => {
        return apikeys.value
      })
  }
  return { getApikeys, apikeys }
}
