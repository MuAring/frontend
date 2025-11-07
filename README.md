# 🎵 Muaring frontend

# 📁 Project Structure

```text
muaring/
├─ app/                     # 실행 모듈 (런처, 네비게이션, DI 루트)
│  ├─ src/main/
│  │  ├─ AndroidManifest.xml
│  │  ├─ java/org/maru/muaring/ (App.java, MainActivity 등)
│  │  └─ res/ (activity_main.xml 등)
│  └─ build.gradle
│
├─ core/                    # 전역 공통 기능(서비스/유틸) 묶음
│  ├─ common/               # Result, Error, Constants 등 베이스 타입
│  ├─ ui/                   # BaseFragment/Adapter, 공통 UI 헬퍼
│  ├─ network/              # Retrofit/OkHttp, Interceptor, 네트워크 유틸
│  └─ (core-database)/      # 선택: Room, DataStore (필요 시)
│
├─ data/                    # 데이터 접근(원천) 레이어: API/DB/Repo 구현
│  ├─ remote/               # Retrofit API
│  ├─ local/                # DAO / DataStore
│  ├─ model/                # DTO/Response (domain과 분리)
│  ├─ repository/           # RepositoryImpl (+ mapper)
│  └─ di/                   # Hilt Module (Network/Repo 제공)
│
├─ design/                  # 디자인 시스템 (테마/색/타이포/컴포넌트 스타일)
│  ├─ src/main/res/values/  # themes.xml, colors.xml, styles.xml, dimens.xml
│  └─ (components/, utils/) # 선택: 커스텀 뷰/리소스 헬퍼
│
├─ feature/                 # 화면(기능) 단위 모듈들
│  ├─ search/
│  │  ├─ ui/                # Fragment, Adapter 등 뷰 계층
│  │  ├─ domain/            # UseCase, Domain model(필요 시)
│  │  ├─ data/              # 해당 피처 한정 RepoImpl(선택)
│  │  └─ di/                # 피처 내부 의존성 제공(Hilt)
│  │  └─ src/main/res/layout/fragment_search.xml
│  └─ featureName           # 예) feature-auth, feature-home 등
│
├─ gradle/
│  └─ libs.versions.toml    # 버전 카탈로그(의존성 버전 한 곳에서 관리)
│
├─ settings.gradle          # 모듈 등록(include)
└─ build.gradle             # 루트 빌드 스크립트
```
