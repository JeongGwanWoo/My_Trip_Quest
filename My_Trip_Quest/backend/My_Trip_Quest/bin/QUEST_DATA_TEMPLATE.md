# 신규 퀘스트 데이터 추가 가이드 (for AI Agents)

## 1. 목적

이 문서는 `My Trip Quest` 프로젝트에 새로운 관광지(`location`)와 관련 퀘스트(`quest`) 데이터를 추가하는 표준화된 절차를 정의합니다. AI 에이전트는 이 가이드를 숙지하고, 사용자의 요청에 따라 일관된 방식으로 데이터를 생성해야 합니다.

## 2. 작업 절차 (Agent's Workflow)

사용자가 "['관광지 이름']을(를) ['도시 이름']에 추가해줘" 와 같이 요청하면, 에이전트는 다음 절차를 따릅니다.

### STEP 1: 정보 수집 (Data Gathering)

1.  **관광지 이름과 도시 확인**: 사용자의 요청에서 핵심 정보를 추출합니다.
2.  **좌표 및 크기 검색**: 웹 검색을 통해 해당 관광지의 정확한 **위도(latitude)**, **경도(longitude)** 및 **실제 크기(가로/세로 길이 또는 면적)**를 찾습니다.
    - 검색어 예시: `[관광지 이름] 좌표`, `[관광지 이름] 크기`
    - 좌표는 '도분초' 형식일 경우 '십진수(Decimal Degrees)'로 변환해야 합니다. (예: 35°8′50.064″ N -> 35.14723)
3.  **지역 코드 확인**: 도시에 맞는 `area_code`를 할당합니다.
    - 서울: '1'
    - 광주: '5'
    - 부산: '6'
    - (필요시 다른 지역 코드 추가)

### STEP 2: 데이터 가공 (Data Processing)

1.  **`gps_verify_radius` 계산**: **STEP 1**에서 찾은 크기 정보를 바탕으로 GPS 인증 반경을 계산합니다. **이 값은 필수적으로 계산하여 적용해야 합니다.**
    - **계산식**: `반지름 = (√(가로² + 세로²)) / 2`
    - 계산된 반지름에 약간의 여유를 더해 최종 값을 결정합니다.
    - 만약 관광지의 가로/세로 길이를 찾기 어렵다면, 면적(Site Area)을 기준으로 원형이라 가정하고 `r = sqrt(면적 / π)` 로 계산 후 적절히 보정합니다. (예: 면적 135,000 m² -> 반지름 약 207m -> 최종 반경 280m)
    - **주의**: 학교 등 일반적인 시설은 150~200m 정도로 충분하나, 정확한 면적 검색이 불가능할 경우 사용자에게 고지 후 합의된 기본값을 사용합니다.
2.  **고유 ID 할당**: `src/main/resources/db/quest_dummy.sql` 파일을 읽어 현재 가장 마지막으로 사용된 `location_id`와 `quest_id`를 확인하고, 이와 겹치지 않는 새로운 ID를 할당합니다.
    - **ID 규칙**:
        - `area_code`가 '5'(광주)인 경우, `location_id`는 **5000번대부터 시작** (예: 5001, 5002...), `quest_id`는 **50000번대부터 시작** (예: 50001, 50002...)
        - 다른 `area_code`의 경우, 적절히 큰 번호대(예: 1000부터) 또는 해당 지역 코드에 맞는 범위(예: 서울 '1' -> 10000번대)를 사용하여 ID 충돌을 방지합니다.

### STEP 3: SQL 생성 (SQL Generation)

아래의 **SQL 템플릿**에 따라, 위에서 가공된 모든 정보를 채워넣어 최종 `INSERT` 문을 생성합니다.

### STEP 4: 사용자에게 결과 제시

생성된 SQL 문을 사용자에게 제시하고, 사용자가 이를 프로젝트에 적용할 수 있도록 안내합니다.

---

## 3. SQL 템플릿

```sql
-- =======================================================================
-- 관광지: [관광지 이름]
-- =======================================================================

-- STEP 1: locations 테이블에 관광지 추가
INSERT INTO `locations` (`location_id`, `title`, `latitude`, `longitude`, `area_code`, `gps_verify_radius`)
VALUES
([신규 location_id], '[관광지 이름]', [검색된 위도], [검색된 경도], '[지역 코드]', [계산된 gps_verify_radius]);

-- STEP 2: 해당 관광지에 대한 퀘스트 추가
-- 도착 퀘스트
INSERT INTO `quests` (`quest_id`, `location_id`, `quest_type_id`, `previous_quest_id`, `difficulty`, `title`, `description`, `reward_xp`, `reward_points`)
VALUES
([신규 quest_id_1], [신규 location_id], 1, NULL, 'EASY', '[관광지 이름] 도착', '[관광지 이름]에 도착하여 GPS를 인증하세요.', 50, 5);

-- 사진 퀘스트
INSERT INTO `quests` (`quest_id`, `location_id`, `quest_type_id`, `previous_quest_id`, `difficulty`, `title`, `description`, `reward_xp`, `reward_points`)
VALUES
([신규 quest_id_2], [신규 location_id], 2, [신규 quest_id_1], 'NORMAL', '[관광지 이름] 사진 찍기', '[관광지 특성을 반영한 사진 퀘스트 설명]', 150, 15);
```
