import Keycloak from 'keycloak-js'

const keycloak = new Keycloak({
  url: 'http://localhost:8080',
  realm: 'manufacturing-portal',
  clientId: 'portal-frontend'
})

export default keycloak