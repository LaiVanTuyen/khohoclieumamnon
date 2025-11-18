# fix-intellij-cache.ps1
# Clean PowerShell script to stop Java, clear IDE and build caches, then run mvn wrapper

Write-Host "\n=== FIX INTELLIJ / Maven cache (NoSuchMethodError) ===\n"

Write-Host "[1/7] Stopping Java processes..." -ForegroundColor Yellow
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Write-Host "    Done stopping Java processes.\n" -ForegroundColor Green

Write-Host "[2/7] Removing target directory if exists..." -ForegroundColor Yellow
if (Test-Path "target") {
    Remove-Item -Path "target" -Recurse -Force -ErrorAction SilentlyContinue
    Write-Host "    Removed target/.\n" -ForegroundColor Green
} else {
    Write-Host "    target/ not found.\n" -ForegroundColor DarkYellow
}

Write-Host "[3/7] Removing .idea directory if exists..." -ForegroundColor Yellow
if (Test-Path ".idea") {
    Remove-Item -Path ".idea" -Recurse -Force -ErrorAction SilentlyContinue
    Write-Host "    Removed .idea/.\n" -ForegroundColor Green
} else {
    Write-Host "    .idea/ not found.\n" -ForegroundColor DarkYellow
}

Write-Host "[4/7] Removing .iml files if any..." -ForegroundColor Yellow
$imlFiles = Get-ChildItem -Path . -Filter "*.iml" -File -ErrorAction SilentlyContinue
if ($imlFiles -and $imlFiles.Count -gt 0) {
    $imlFiles | Remove-Item -Force -ErrorAction SilentlyContinue
    Write-Host "    Removed $($imlFiles.Count) .iml file(s).\n" -ForegroundColor Green
} else {
    Write-Host "    No .iml files found.\n" -ForegroundColor DarkYellow
}

Write-Host "[5/7] Optionally removing Spring Boot folder from local m2 (if present)..." -ForegroundColor Yellow
# Change the version below if you need to remove a different Spring Boot version
$sbVersionToRemove = "3.5.7"
$m2Path = Join-Path $env:USERPROFILE ".m2\repository\org\springframework\boot\spring-boot\$sbVersionToRemove"
if (Test-Path $m2Path) {
    Write-Host "    Found $m2Path - removing..." -ForegroundColor Yellow
    Remove-Item -Path $m2Path -Recurse -Force -ErrorAction SilentlyContinue
    Write-Host "    Removed Spring Boot $sbVersionToRemove from local repo.\n" -ForegroundColor Green
} else {
    Write-Host "    Local m2 Spring Boot $sbVersionToRemove not found.\n" -ForegroundColor DarkYellow
}

Write-Host "[6/7] Running Maven clean install (skip tests)..." -ForegroundColor Yellow
if (Test-Path ".\mvnw.cmd") {
    & .\mvnw.cmd clean install -DskipTests
} elseif (Get-Command mvn -ErrorAction SilentlyContinue) {
    mvn clean install -DskipTests
} else {
    Write-Host "    No mvnw.cmd and no mvn found in PATH. Please run build manually.\n" -ForegroundColor Red
    exit 1
}

if ($LASTEXITCODE -eq 0) {
    Write-Host "    Maven build finished successfully.\n" -ForegroundColor Green
} else {
    Write-Host "    Maven build failed (exit code $LASTEXITCODE).\n" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "[7/7] Optional: verify JAR for spring-boot version (if JAR exists)..." -ForegroundColor Yellow
$jarCandidate = Get-ChildItem -Path target -Filter "*.jar" -File -ErrorAction SilentlyContinue | Select-Object -First 1
if ($jarCandidate) {
    Write-Host "    Found jar: $($jarCandidate.FullName) - listing entries that mention 'spring-boot'..." -ForegroundColor Gray
    if (Get-Command jar -ErrorAction SilentlyContinue) {
        jar -tf $jarCandidate.FullName | Select-String "spring-boot" | Select-Object -First 5 | ForEach-Object { Write-Host "        $_" }
    } else {
        Write-Host "    'jar' command is not available in PATH; skipping jar check." -ForegroundColor DarkYellow
    }
} else {
    Write-Host "    No jar found in target/. Skipping jar check.\n" -ForegroundColor DarkYellow
}

Write-Host "\n=== DONE ===\n"
Write-Host "Next steps:\n  - Re-open IntelliJ and reimport the project.\n  - Run the application (mvnw spring-boot:run or from IDE).\n  - Open Swagger UI at: http://localhost:8080/api/v1/swagger-ui/index.html\n"
