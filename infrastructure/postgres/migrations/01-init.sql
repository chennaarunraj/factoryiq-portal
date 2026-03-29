-- Create keycloak schema
CREATE SCHEMA IF NOT EXISTS keycloak;

-- Create portal schema for our application
CREATE SCHEMA IF NOT EXISTS portal;

-- Grant permissions
GRANT ALL PRIVILEGES ON SCHEMA keycloak TO portaluser;
GRANT ALL PRIVILEGES ON SCHEMA portal TO portaluser;