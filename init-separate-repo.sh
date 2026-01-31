#!/bin/bash

# common-kotlin-lib를 독립적인 Git 저장소로 초기화

set -e

REPO_URL="https://github.com/passion-edu/s-class-common.git"

echo "🔧 common-kotlin-lib를 독립적인 Git 저장소로 초기화합니다..."
echo ""

# 현재 디렉토리 확인
if [ ! -f "build.gradle.kts" ]; then
    echo "❌ build.gradle.kts를 찾을 수 없습니다. common-kotlin-lib 디렉토리에서 실행해주세요."
    exit 1
fi

# .git이 이미 있으면 확인
if [ -d ".git" ]; then
    echo "⚠️  .git 디렉토리가 이미 존재합니다."
    read -p "기존 .git을 삭제하고 새로 초기화하시겠습니까? (y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo "🗑️  기존 .git 삭제 중..."
        rm -rf .git
    else
        echo "❌ 취소되었습니다."
        exit 1
    fi
fi

# Git 초기화
echo "🔧 Git 초기화 중..."
git init
git branch -M main

# Remote 추가
echo "🔗 Remote 설정 중..."
git remote add origin "$REPO_URL" 2>/dev/null || git remote set-url origin "$REPO_URL"

# 파일 추가
echo "➕ 파일 추가 중..."
git add .

# 커밋
echo "💾 커밋 중..."
git commit -m "Initial commit: S-Class Common Kotlin Library" || echo "⚠️  변경사항이 없습니다."

# 푸시 확인
echo ""
echo "✅ 초기화 완료!"
echo ""
echo "📋 다음 명령어로 푸시하세요:"
echo "   git push -u origin main --force"
echo ""
echo "⚠️  주의: --force 옵션으로 기존 내용을 모두 덮어씁니다."
echo ""
read -p "지금 푸시하시겠습니까? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "📤 푸시 중..."
    git push -u origin main --force
    echo "✅ 푸시 완료!"
else
    echo "⏸️  푸시를 건너뜁니다."
    echo "   수동으로 푸시하세요: git push -u origin main --force"
fi
