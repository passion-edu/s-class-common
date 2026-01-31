#!/bin/bash

# passion-edu/s-class-common 레포지토리를 common-kotlin-lib만 포함하도록 수정

set -e

REPO_URL="https://github.com/passion-edu/s-class-common.git"
TEMP_DIR="/tmp/s-class-common-fix-$(date +%s)"
SOURCE_DIR="/Users/seeun/project/s-class/common-kotlin-lib"

echo "🔧 passion-edu/s-class-common 레포지토리 수정 중..."
echo ""

# 1. 임시 디렉토리 생성
echo "📁 임시 디렉토리 생성: $TEMP_DIR"
mkdir -p "$TEMP_DIR"
cd "$TEMP_DIR"

# 2. 레포지토리 클론
echo "📥 레포지토리 클론 중..."
git clone "$REPO_URL" .
git checkout -b main 2>/dev/null || git checkout main

# 3. 기존 내용 모두 삭제 (common-kotlin-lib 내용만 남기기 위해)
echo "🗑️  기존 내용 삭제 중..."
# .git 디렉토리는 보존해야 함
git rm -rf . 2>/dev/null || true
# .git을 제외한 모든 파일/디렉토리 삭제
find . -mindepth 1 -maxdepth 1 ! -name '.git' -exec rm -rf {} + 2>/dev/null || true

# 4. common-kotlin-lib 내용만 복사
echo "📋 common-kotlin-lib 내용 복사 중..."
cp -r "$SOURCE_DIR"/* .
cp "$SOURCE_DIR"/.gitignore . 2>/dev/null || true

# .git은 복사하지 않음 (이미 존재하는 .git 사용)

# 불필요한 파일 제거
rm -f QUICK_SEPARATE.sh SEPARATE_REPO_GUIDE.md 2>/dev/null || true

# 5. 파일 추가
echo "➕ 파일 추가 중..."
git add .

# 6. 커밋
echo "💾 커밋 중..."
git commit -m "chore: keep only common-kotlin-lib content" || echo "⚠️  변경사항이 없습니다."

# 7. 푸시 확인
echo ""
echo "✅ 준비 완료!"
echo ""
echo "📋 다음 명령어로 푸시하세요:"
echo "   cd $TEMP_DIR"
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
    echo ""
    echo "📁 임시 디렉토리: $TEMP_DIR"
    echo "   필요시 삭제하세요: rm -rf $TEMP_DIR"
else
    echo "⏸️  푸시를 건너뜁니다."
    echo "📁 임시 디렉토리: $TEMP_DIR"
    echo "   수동으로 푸시하세요: cd $TEMP_DIR && git push -u origin main --force"
fi
