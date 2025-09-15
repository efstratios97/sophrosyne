<template>
  <div>
    <Card>
      <template #title> {{ $t('settings.configuration.export_config_data.title') }} </template>
      <template #subtitle>
        {{ $t('settings.configuration.export_config_data.sub_title') }}
      </template>
      <template #content>
        <div class="flex justify-content-center flex-wrap">
          <div
            class="flex align-items-center justify-content-center w-4rem h-4rem border-round m-2"
          >
            <Button text raised outlined class="border-2" @click="exportSophrosyneData('json')">
              <img src="@/assets/images/EXPORT_DATA_JSON.png" alt="Image" style="max-height: 5vh" />
            </Button>
          </div>
          <div
            class="flex align-items-center justify-content-center w-4rem h-4rem border-round m-2"
          >
            <Button text raised outlined class="border-2" @click="exportSophrosyneData('yaml')">
              <img src="@/assets/images/EXPORT_DATA_YAML.png" alt="Image" style="max-height: 5vh" />
            </Button>
          </div>
        </div>
      </template>
    </Card>
    <Toast />
  </div>
</template>

<script setup>
import { inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'primevue/usetoast'
import YAML from 'yaml'

const toast = useToast()
const { t } = useI18n()
const axiosCore = inject('axios-core')

const looksLikeJson = (s) => {
  if (typeof s !== 'string') return false
  const t = s.trim()
  return (t.startsWith('{') && t.endsWith('}')) || (t.startsWith('[') && t.endsWith(']'))
}
const tryParseJson = (v) => {
  if (typeof v !== 'string' || !looksLikeJson(v)) return v
  try {
    return JSON.parse(v)
  } catch {
    return v
  }
}
const normalizeExport = (val) => {
  if (Array.isArray(val)) {
    return val.map((v) => normalizeExport(tryParseJson(v)))
  }
  if (val && typeof val === 'object') {
    const out = {}
    for (const [k, v] of Object.entries(val)) {
      out[k] = normalizeExport(tryParseJson(v))
    }
    return out
  }
  return val
}

const exportSophrosyneData = (format = 'json') => {
  axiosCore
    .get('/int/admin/configuration/export')
    .then((res) => {
      // fix double-encoded sections coming from backend
      const normalized = normalizeExport(res.data)

      const isYaml = String(format).toLowerCase() === 'yaml'
      const data = isYaml ? YAML.stringify(normalized) : JSON.stringify(normalized, null, 2)

      const blob = new Blob([data], {
        type: isYaml ? 'application/x-yaml' : 'application/json'
      })

      const link = document.createElement('a')
      link.href = URL.createObjectURL(blob)
      link.download = isYaml ? 'sophrosyne.yaml' : 'sophrosyne.json'
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      setTimeout(() => URL.revokeObjectURL(link.href), 0)

      toast.add({
        severity: 'success',
        summary: t('settings.configuration.export_config_data.toast.success.message'),
        detail: t('settings.configuration.export_config_data.toast.success.detail'),
        life: 10000
      })
    })
    .catch(() => {
      toast.add({
        severity: 'error',
        summary: t('settings.configuration.export_config_data.toast.error.message'),
        detail: t('settings.configuration.export_config_data.toast.error.detail'),
        life: 3000
      })
    })
}
</script>

<style></style>
