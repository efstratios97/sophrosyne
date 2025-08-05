import { ref, inject } from 'vue'

export const useActionArchiveComposable = () => {
  const axiosCore = inject('axios-core')

  const actionsArchive = ref([])
  const actionLogFiles = ref({})
  const getActionArchive = async () => {
    await axiosCore.get('/int/client/archive/actions/metadata').then((res) => {
      actionsArchive.value = res.data
      actionsArchive.value.forEach((archive) => {
        actionLogFiles.value[archive.id] = {
          executionLogFileData: '',
          postExecutionLogFileData: ''
        }
      })
    })
  }

  return { getActionArchive, actionsArchive, actionLogFiles }
}
