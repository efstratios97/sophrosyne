import { Ref, ref, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'
import type { AxiosInstance } from 'axios'

export interface DropdownOption {
  id: ''
}

// --------------------------------------- //

export const useDropdownOptionComposable = () => {
  const axiosCore = inject<AxiosInstance>('axios-core')
  if (!axiosCore) throw new Error('axios-core injection missing')

  const { t } = useI18n()
  const toast = useToast()

  const dropdownOptions: Ref<object[]> = ref([])

  // ---- GET: dynamic actions ---- //
  const getDropdownOptions = async () => {
    const res = await axiosCore.get<DropdownOption[]>('/int/user/dropdownoptions')
    dropdownOptions.value = res.data
  }

  // ---- POST: create dropdownOption ---- //
  const createDropdownOption = async (dropdownOptionPassed: object) => {
    try {
      const res = await axiosCore.post<object>('/int/user/dropdownoption', dropdownOptionPassed)
      return res.status
    } catch (err: any) {
      return err.response.status
    }
  }

  // ---- PUT: update dropdownOption ---- //
  const updateDropdownOption = async (dropdownOptionPassed: DropdownOption) => {
    try {
      const res = await axiosCore.put<object>(
        '/int/user/dropdownoption/' + dropdownOptionPassed.id,
        dropdownOptionPassed
      )
      return res.status
    } catch (err: any) {
      return err.response.status
    }
  }

  // ---- PUT: update dropdownOption ---- //
  const deleteDropdownOption = async (dropdownOptionsPassed: DropdownOption[]) => {
    return dropdownOptionsPassed.map(async (dropdownOptionPassed) => {
      try {
        const res = await axiosCore.delete<object>(
          '/int/user/dropdownoption/' + dropdownOptionPassed.id
        )
        return res.status
      } catch (err: any) {
        return err.response.status
      }
    })
  }

  return {
    getDropdownOptions,
    dropdownOptions,
    createDropdownOption,
    updateDropdownOption,
    deleteDropdownOption
  }
}
