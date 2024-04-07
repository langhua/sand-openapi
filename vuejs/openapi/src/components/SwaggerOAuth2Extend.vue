<script lang="ts">
const oauth2AndLogout = (_system: any) => {
  return {
    statePlugins: {
      auth: {
        wrapActions: {
          authorizeOauth2: (oriAction: (arg0: any) => any, _system: any) => (payload: { auth: { code: string } }) => {
            payload.auth.code = ""
            return oriAction(payload)
          },
          logout: (oriAction: (arg0: any) => any, _system: any) => (payload: any) => {
            // external logout
            fetch(window.location.origin + '/oauth/logout')
            return oriAction(payload)
          }
        }
      }
    }
  }
}

export default oauth2AndLogout
</script>