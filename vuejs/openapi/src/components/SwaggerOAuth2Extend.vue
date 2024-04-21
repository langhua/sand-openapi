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
            // external logout for openapi 3.0 only
            var logoutServer = ""
            var logoutUri = ""
            if (_system.specSelectors.isOAS3()) {
              const scheme = _system.specSelectors.specJson().get("components").get("securitySchemes").get(payload[0]).get("scheme")
              const server = _system.oas3Selectors.selectedServer()
              if (server != undefined && server.length > 0) {
                if (server.startsWith('/')) {
                  // this is an uri
                  const uriRegex = /(\/[-a-zA-Z0-9()@:%_\+.~#?&=]*)([-a-zA-Z0-9()@:%_\+.~#?&//=]*)/g
                  const groups = uriRegex.exec(server)
                  if (groups != null && groups.length >= 2 && groups[1].length > 1) {
                    if (scheme == "basic") {
                      logoutServer = window.location.origin
                      logoutUri = groups[1] + "/control/logout"
                    }
                  }
                } else {
                  // this is an url
                  const urlRegex = /(https?:\/\/[-a-zA-Z0-9@:%._\+~#=]{1,256}[\.a-zA-Z0-9()]{1,6}\b)(\/?[-a-zA-Z0-9()@:%_\+.~#?&=]*)([-a-zA-Z0-9()@:%_\+.~#?&//=]*)/gi
                  const groups = urlRegex.exec(server)
                  if (groups != null && groups.length >= 3 && groups[1].length > 1 && groups[2].length > 1) {
                    if (scheme == "basic") {
                      logoutServer = groups[1]
                      logoutUri = groups[2] + "/control/logout"
                    }
                  }
                }
              }
            }
            if (logoutServer == "" || logoutUri == "") {
              console.log("No logout url will be fetched.")
            } else {
              console.log("Logout: " + logoutServer + logoutUri)
              fetch(logoutServer + logoutUri)
            }
            return oriAction(payload)
          }
        }
      }
    }
  }
}

export default oauth2AndLogout
</script>