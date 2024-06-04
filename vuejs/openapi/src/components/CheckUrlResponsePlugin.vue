<script lang="ts">
import router from '@/router'

const checkUrlResponsePlugin = (_system: any) => {
  return {
    afterLoad(_system: any) {
      fetch(_system.specSelectors.url()).then(response => {
        if (response && response.status && response.status == 401) {
          router.push({path: '/login'})
        }
      })
    },
  }
}

export default checkUrlResponsePlugin
</script>