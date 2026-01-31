#!/bin/bash

# common-kotlin-lib를 별도 레포지토리로 분리하는 스크립트

set -e

REPO_URL="https://github.com/passion-edu/s-class-common.git"
TEMP_DIR="/tmp/s-class-common-$(date +%s)"
SOURCE_DIR="/Users/seeun/project/s-class/common-kotlin-lib"

echo "🚀 common-kotlin-lib를 별도 레포지토리로 분리합니다..."
echo ""

# 1. 임시 디렉토리 생성
echo "📁 임시 디렉토리 생성: $TEMP_DIR"
mkdir -p "$TEMP_DIR"
cd "$TEMP_DIR"

# 2. 새 레포지토리 클론
echo "📥 새 레포지토리 클론 중..."
git clone "$REPO_URL" .
git checkout -b main 2>/dev/null || git checkout main

# 3. common-kotlin-lib 내용 복사
echo "📋 파일 복사 중..."
cp -r "$SOURCE_DIR"/* .
cp -r "$SOURCE_DIR"/.* . 2>/dev/null || true

# .git은 복사하지 않음
rm -rf .git 2>/dev/null || true

# 4. Git 초기화
echo "🔧 Git 초기화..."
git init
git branch -M main

# 5. 파일 추가
echo "➕ 파일 추가 중..."
git add .

# 6. 커밋
echo "💾 커밋 중..."
git commit -m "Initial commit: S-Class Common Kotlin Library" || echo "⚠️  변경사항이 없거나 이미 커밋되어 있습니다."

# 7. Remote 추가
echo "🔗 Remote 설정..."
git remote add origin "$REPO_URL" 2>/dev/null || git remote set-url origin "$REPO_URL"

# 8. 푸시 확인
echo ""
echo "✅ 준비 완료!"
echo ""
echo "📋 다음 명령어로 푸시하세요:"
echo "   cd $TEMP_DIR"
echo "   git push -u origin main --force"
echo ""
echo "⚠️  주의: --force 옵션은 기존 내용을 덮어씁니다."
echo ""
read -p "지금 푸시하시겠습니까? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "📤 푸시 중..."
    git push -u origin main --force
    echo "✅ 푸시 완료!"
    echo ""
    echo "📁 임시 디렉토리: $TEMP_DIR"
    echo "   필요시 삭제하세요: rm -rf $TEMP_DIR"
else
    echo "⏸️  푸시를 건너뜁니다."
    echo "📁 임시 디렉토리: $TEMP_DIR"
    echo "   수동으로 푸시하세요: cd $TEMP_DIR && git push -u origin main --force"
fi
