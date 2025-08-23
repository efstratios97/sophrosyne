<template>
  <div>
    <Card class="sophrosyne-card" style="min-height: 100%">
      <template #title>
        {{ $t('dashboard.timeline.title') }}
      </template>
      <template #subtitle>
        {{ $t('dashboard.timeline.sub_title') }}
      </template>
      <template #content>
        <Chart type="bar" :data="chartData" :options="chartOptions" class="h-30rem" />
      </template>
    </Card>
  </div>
</template>
<script setup>
import { ref, defineProps, onMounted } from 'vue'

const props = defineProps(['timeline'])
const chartData = ref({})
const chartOptions = ref({})

onMounted(() => {
  const documentStyle = getComputedStyle(document.documentElement)
  transformTochartData()
  chartData.value = {
    labels: chartDataTmp.value['total-x-axis'],
    datasets: [
      {
        type: 'line',
        label: 'Total run',
        borderColor: documentStyle.getPropertyValue('--p-blue-500'),
        borderWidth: 2,
        fill: false,
        tension: 0.4,
        data: chartDataTmp.value['total-success-y-values']
      },
      {
        type: 'bar',
        label: 'Total succeeded',
        backgroundColor: documentStyle.getPropertyValue('--p-green-500'),
        borderColor: 'white',
        borderWidth: 2,
        data: chartDataTmp.value['total-success-y-values']
      },
      {
        type: 'bar',
        label: 'total failed',
        backgroundColor: documentStyle.getPropertyValue('--p-red-500'),
        data: chartDataTmp.value['total-fail-y-values']
      }
    ]
  }

  const textColor = documentStyle.getPropertyValue('--text-color')
  const textColorSecondary = documentStyle.getPropertyValue('--text-color-secondary')
  const surfaceBorder = documentStyle.getPropertyValue('--surface-border')

  chartOptions.value = {
    maintainAspectRatio: false,
    aspectRatio: 0.6,
    plugins: {
      legend: {
        labels: {
          color: textColor
        }
      }
    },
    scales: {
      x: {
        ticks: {
          color: textColorSecondary
        },
        grid: {
          color: surfaceBorder
        }
      },
      y: {
        ticks: {
          color: textColorSecondary
        },
        grid: {
          color: surfaceBorder
        }
      }
    }
  }
})

const chartDataTmp = ref({})
function transformTochartData() {
  try {
    const result = {}

    props.timeline['total-x-axis'].forEach((date, index) => {
      if (!result[date]) {
        result[date] = {
          'total-x-axis': date,
          'total-y-values': 0,
          'total-success-y-values': 0,
          'total-fail-y-values': 0
        }
      }

      result[date]['total-y-values'] += props.timeline['total-y-values'][index]
      result[date]['total-success-y-values'] += props.timeline['total-success-y-values'][index]
      result[date]['total-fail-y-values'] +=
        props.timeline['total-y-values'][index] - props.timeline['total-success-y-values'][index]
    })
    chartDataTmp.value = Object.entries(result).reduce((result, [key, value]) => {
      Object.entries(value).forEach(([subKey, subValue]) => {
        if (!result[subKey]) {
          result[subKey] = []
        }
        result[subKey].push(subValue)
      })

      return result
    }, {})
  } catch (error) {
    /* empty */
  }
}
</script>
