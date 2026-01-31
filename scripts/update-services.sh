#!/bin/bash

# 각 서비스의 build.gradle.kts에 GitHub Packages repository 추가 스크립트
# 사용법: ./scripts/update-services.sh [GITHUB_ORG]
# 기본값: passion-edu

set -e

GITHUB_ORG=${1:-"passion-edu"}
GITHUB_REPO=${2:-"s-class-common"}
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

echo "🔧 Updating services to use GitHub Packages..."
echo "GitHub Organization: $GITHUB_ORG"
echo "Repository: $GITHUB_REPO"
echo "GitHub Packages URL: https://maven.pkg.github.com/$GITHUB_ORG/$GITHUB_REPO"
echo ""

# 서비스 목록
SERVICES=(
    "payment-service"
    "account-service"
    "auth"
    "lms-service"
    "notification-service"
    "supporters-service"
    "api-gateway"
    "formo"
)

for service in "${SERVICES[@]}"; do
    SERVICE_PATH="$PROJECT_ROOT/$service"
    
    if [ ! -d "$SERVICE_PATH" ]; then
        echo "⚠️  Skipping $service (directory not found)"
        continue
    fi
    
    BUILD_FILE="$SERVICE_PATH/build.gradle.kts"
    
    if [ ! -f "$BUILD_FILE" ]; then
        echo "⚠️  Skipping $service (build.gradle.kts not found)"
        continue
    fi
    
    echo "📝 Updating $service..."
    
    # mavenLocal() 제거 (있는 경우)
    if grep -q "mavenLocal()" "$BUILD_FILE"; then
        echo "  - Removing mavenLocal()"
        sed -i.bak '/mavenLocal()/d' "$BUILD_FILE"
        rm -f "$BUILD_FILE.bak"
    fi
    
    # GitHub Packages repository 추가 (이미 있으면 스킵)
    if ! grep -q "GitHubPackages" "$BUILD_FILE"; then
        echo "  - Adding GitHub Packages repository"
        
        # repositories 블록 찾기
        if grep -q "repositories {" "$BUILD_FILE"; then
            # repositories 블록 내부에 추가
            sed -i.bak "/repositories {/a\\
    \\
    // GitHub Packages (공통 라이브러리)\\
    // Organization: $GITHUB_ORG\\
    // Repository: $GITHUB_REPO\\
    maven {\\
        name = \"GitHubPackages\"\\
        url = uri(\"https://maven.pkg.github.com/$GITHUB_ORG/$GITHUB_REPO\")\\
        credentials {\\
            username = project.findProperty(\"gpr.user\") as String?\\
                ?: System.getenv(\"GITHUB_ACTOR\")\\
                ?: \"github\"\\
            password = project.findProperty(\"gpr.token\") as String?\\
                ?: System.getenv(\"GITHUB_TOKEN\")\\
                ?: \"\"\\
        }\\
    }
" "$BUILD_FILE"
            rm -f "$BUILD_FILE.bak"
        fi
    else
        echo "  - GitHub Packages repository already exists"
    fi
    
    # common-kotlin-lib 의존성 업데이트
    if grep -q "common-kotlin-lib" "$BUILD_FILE"; then
        echo "  - Updating common-kotlin-lib version"
        sed -i.bak 's/com\.s-class:common-kotlin-lib:[0-9.]*-SNAPSHOT/com.s-class:common-kotlin-lib:1.0.0/g' "$BUILD_FILE"
        sed -i.bak 's/com\.s-class:common-kotlin-lib:[0-9.]*/com.s-class:common-kotlin-lib:1.0.0/g' "$BUILD_FILE"
        rm -f "$BUILD_FILE.bak"
    fi
    
    echo "  ✅ $service updated"
    echo ""
done

echo "✨ All services updated!"
echo ""
echo "📋 Next steps:"
echo "1. Set up GitHub Personal Access Token:"
echo "   export GITHUB_TOKEN=your_token"
echo "   export GITHUB_ACTOR=your_username"
echo "2. Test build:"
echo "   cd payment-service && ./gradlew build"
echo ""
echo "📌 Current configuration:"
echo "   Organization: $GITHUB_ORG"
echo "   Repository: $GITHUB_REPO"
echo "   GitHub Packages URL: https://maven.pkg.github.com/$GITHUB_ORG/$GITHUB_REPO"
