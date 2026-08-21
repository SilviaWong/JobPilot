import { defineNuxtPlugin } from '#app'
import { api, request } from '~/utils/request'

export default defineNuxtPlugin((nuxtApp) => {
  return {
    provide: {
      api,
      request,
    },
  }
})
