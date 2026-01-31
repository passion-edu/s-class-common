# common-kotlin-lib 별도 레포지토리 분리 가이드

## 현재 상황

- `common-kotlin-lib`가 부모 레포지토리(`s-class`)에 포함되어 있음
- 부모 레포지토리의 remote가 `passion-edu/s-class-common`으로 설정되어 있음
- `common-kotlin-lib`만 별도 레포지토리로 분리 필요

## 분리 방법

### 방법 1: Git Subtree 사용 (권장)

부모 레포지토리에서 `common-kotlin-lib`만 별도 레포지토리로 분리:

```bash
# 1. 부모 레포지토리에서
cd /Users/seeun/project/s-class

# 2. common-kotlin-lib를 별도 브랜치로 분리
git subtree push --prefix=common-kotlin-lib origin common-kotlin-lib

# 3. common-kotlin-lib 디렉토리로 이동
cd common-kotlin-lib

# 4. 새로운 레포지토리로 초기화
rm -rf .git  # 부모의 git 정보 제거 (주의!)
git init
git branch -M main

# 5. 파일 추가 및 커밋
git add .
git commit -m "Initial commit: S-Class Common Kotlin Library"

# 6. 별도 레포지토리에 연결
git remote add origin https://github.com/passion-edu/s-class-common.git

# 7. 푸시
git push -u origin main --force
```

### 방법 2: 수동 복사 (간단)

```bash
# 1. common-kotlin-lib 디렉토리 복사
cd /Users/seeun/project/s-class
cp -r common-kotlin-lib /tmp/common-kotlin-lib-temp

# 2. 새 디렉토리로 이동
cd /tmp/common-kotlin-lib-temp

# 3. Git 초기화
git init
git branch -M main

# 4. 파일 추가 및 커밋
git add .
git commit -m "Initial commit: S-Class Common Kotlin Library"

# 5. 별도 레포지토리에 연결
git remote add origin https://github.com/passion-edu/s-class-common.git

# 6. 푸시
git push -u origin main

# 7. 원래 위치로 복사 (선택사항)
# cd /Users/seeun/project/s-class
# rm -rf common-kotlin-lib
# git clone https://github.com/passion-edu/s-class-common.git common-kotlin-lib
```

### 방법 3: GitHub에서 직접 생성 후 클론

```bash
# 1. GitHub에서 passion-edu/s-class-common 레포지토리 생성 (이미 생성됨)

# 2. 임시 디렉토리에 클론
cd /tmp
git clone https://github.com/passion-edu/s-class-common.git
cd s-class-common

# 3. common-kotlin-lib의 내용 복사
cp -r /Users/seeun/project/s-class/common-kotlin-lib/* .
cp -r /Users/seeun/project/s-class/common-kotlin-lib/.* . 2>/dev/null || true

# 4. 커밋 및 푸시
git add .
git commit -m "Initial commit: S-Class Common Kotlin Library"
git push -u origin main

# 5. 원래 위치 업데이트 (선택사항)
# cd /Users/seeun/project/s-class
# rm -rf common-kotlin-lib
# git clone https://github.com/passion-edu/s-class-common.git common-kotlin-lib
```

## 주의사항

1. **부모 레포지토리의 remote 확인**
   - 부모 레포지토리(`s-class`)의 remote가 `passion-edu/s-class-common`으로 설정되어 있다면 원래 remote로 변경 필요
   - 또는 부모 레포지토리를 별도 레포지토리로 관리

2. **`.gitignore` 확인**
   - `build/`, `.gradle/` 등은 이미 `.gitignore`에 포함되어 있음

3. **배포 후 확인**
   - GitHub Packages에서 패키지가 정상적으로 보이는지 확인
   - 각 서비스에서 의존성 다운로드 테스트

## 권장 방법

**방법 2 (수동 복사)**를 권장합니다:
- 가장 간단하고 안전함
- 부모 레포지토리에 영향 없음
- 실수로 부모 레포지토리를 건드릴 위험 없음
