import { ref, inject } from 'vue'

export const useStatsComposable = () => {
  const axiosCore = inject('axios-core')

  const stats = ref({})

  const getStats = () => {
    axiosCore.get('/pull/user/dashboard/stats/all').then((res) => {
      stats.value = res.data
    })
  }

  return { getStats, stats }
}
