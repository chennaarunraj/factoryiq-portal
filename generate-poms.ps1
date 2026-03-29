# Run this from: manufacturing-portal\backend folder
# Creates pom.xml for all child microservices

Write-Host "Generating child POMs..." -ForegroundColor Cyan

function Write-ServicePom {
    param(
        [string]$ServiceName,
        [string]$PackageName,
        [string]$Description,
        [string]$ExtraDeps = ""
    )

    $pomContent = @"
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.portal</groupId>
        <artifactId>manufacturing-portal-parent</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <relativePath>../pom.xml</relativePath>
    </parent>

    <artifactId>$ServiceName</artifactId>
    <description>$Description</description>

    <dependencies>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
        </dependency>

        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>

        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>

        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-database-postgresql</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
$ExtraDeps
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
"@

    $path = "$ServiceName\pom.xml"
    Set-Content -Path $path -Value $pomContent -Encoding UTF8
    Write-Host "  [OK] $ServiceName\pom.xml" -ForegroundColor Green
}

# API Gateway - uses WebFlux (reactive) not Web
$gatewayPom = @"
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.portal</groupId>
        <artifactId>manufacturing-portal-parent</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <relativePath>../pom.xml</relativePath>
    </parent>

    <artifactId>api-gateway</artifactId>
    <description>API Gateway - routes and filters all incoming requests</description>

    <dependencies>

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
        </dependency>

    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
"@
Set-Content -Path "api-gateway\pom.xml" -Value $gatewayPom -Encoding UTF8
Write-Host "  [OK] api-gateway\pom.xml" -ForegroundColor Green

# Document Service - extra MinIO dependency
$minioExtra = @"

        <dependency>
            <groupId>io.minio</groupId>
            <artifactId>minio</artifactId>
        </dependency>
"@

# All standard services
Write-ServicePom "program-service"              "program"      "Program, Milestone, Jira sync"
Write-ServicePom "jira-sync-service"            "jirasync"     "Jira webhook sync service"
Write-ServicePom "production-service"           "production"   "Production visibility, WIP, yield"
Write-ServicePom "quality-service"              "quality"      "Quality management, NCR, CAPA, SPC"
Write-ServicePom "supply-chain-service"         "supplychain"  "Supply chain, PO, inventory, logistics"
Write-ServicePom "dynamics-integration-service" "dynamics"     "Microsoft Dynamics 365 integration"
Write-ServicePom "after-sales-service"          "aftersales"   "RMA, Repair, Warranty, Spare Parts"
Write-ServicePom "document-service"             "document"     "Document management, MinIO storage" $minioExtra
Write-ServicePom "notification-service"         "notification" "Event-driven notifications and alerts"
Write-ServicePom "analytics-service"            "analytics"    "Analytics, dashboards, reporting"

Write-Host ""
Write-Host "All POMs generated!" -ForegroundColor Cyan
Write-Host "Now run: mvn validate" -ForegroundColor Yellow