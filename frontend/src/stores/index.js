import { reactive } from 'vue'
import { defineStore } from 'pinia'

export const useStore = defineStore('counter', () => {
    const auth = reactive({
        user: null
    })

    return { auth }
})
