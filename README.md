<div align="center">

# MyTripQuest

여행에서 또다른 즐거움을 챙겨보세요

</div>

---

## 📝 작품 소개

**MyTripQuest**는 단순한 여행 기록을 넘어, 게이미피케이션(Gamification) 요소를 접목한 위치 기반 체험형 여행 플랫폼입니다. 사용자는 전국의 관광지에서 **GPS 위치 인증(1차)**과 **AI 사진 인증(2차)**으로 이어지는 연계 퀘스트를 수행하며 여행지에서의 몰입감을 높일 수 있습니다. 퀘스트 완료 보상으로 획득한 포인트로 나만의 아바타를 커스터마이징하고, 실시간 랭킹 시스템을 통해 다른 여행자들과 선의의 경쟁을 즐겨보세요. 여행의 모든 순간이 기록이자 즐거운 모험이 됩니다.

---

## 🌁 프로젝트 배경

여행은 단순히 장소를 이동하는 행위를 넘어, 새로운 경험과 추억을 쌓는 과정입니다.
그러나 기존의 여행 서비스들은 정보 제공이나 기록 중심에 머무는 경우가 많아, 실제 여행지에서의 몰입감과 참여도를 충분히 끌어내지 못하는 한계가 있었습니다.

또한 여행 중 “무엇을 하면 좋을지”, “지금 이 장소에서만 할 수 있는 특별한 경험은 무엇인지”에 대한 가이드는 부족했고, 여행자 간의 상호작용 요소 역시 제한적이었습니다. 이로 인해 여행이 점점 소비형 콘텐츠로 변하고, 개인의 경험이 일회성으로 끝나는 경우가 많아지고 있습니다.

이러한 문제의식에서 출발한 MyTripQuest는
여행지에서의 행동 자체를 하나의 퀘스트로 설계하고,
위치 기반 인증과 AI 사진 인증을 결합하여 실제 현장 경험을 중심으로 한 참여형 여행을 목표로 합니다.

게임에서 퀘스트를 수행하며 성취감을 느끼듯,
여행자 또한 미션을 완료하고 보상을 얻는 과정에서 자연스럽게 여행에 몰입하고,
아바타 성장과 랭킹 시스템을 통해 지속적인 동기부여를 받을 수 있도록 설계했습니다.

MyTripQuest는 여행을 단순한 기록이 아닌 도전하고, 증명하고, 공유하는 하나의 경험으로 확장하고자 합니다.

---

## 🎞 Demo

[My Trip Quest 시연 영상](https://youtu.be/2jrZPjJJ3KY?si=9YHu5kjPaeJWw3Mf)

---

## ⭐ 주요 기능

-   **메인 페이지**
    지도 기반의 퀘스트 현황, 내 캐릭터 정보, 활동 로그 등을 확인할 수 있습니다.

    ![메인 페이지](images/mainpage.png)
    ![메뉴](images/menu.png)

-   **퀘스트맵**
    지도 위에 표시된 다양한 퀘스트를 확인하고 수락할 수 있습니다.

    ![퀘스트 맵](images/questmap.png)
    ![퀘스트 목록](images/quest1.png)

-   **퀘스트 수행**
    -   **도착 퀘스트**: GPS를 이용해 지정된 장소에 도착하면 완료됩니다.

        ![도착 퀘스트 예시](images/quest1-1.png)

    -   **사진 퀘스트**: 특정 장소에서 AI(Google Gemini Vision API)를 통한 사진 인증으로 퀘스트를 완료합니다.

        ![사진 퀘스트 예시](images/quest1-2.png)

-   **여행**
    지도 위에 표시된 다양한 퀘스트를 확인하고 수락할 수 있습니다.

    ![여행 맵](images/trip1.png)
    ![여행 목록](images/trip2.png)


-   **피팅룸 (캐릭터 커스터마이징)**
    퀘스트 보상으로 획득한 아이템으로 아바타를 꾸밀 수 있습니다.

    ![피팅 룸](images/fittingroom.png)

-   **상점**
    포인트를 사용하여 아바타가 착용할 수 있는 다양한 아이템을 구매할 수 있습니다.

    ![상점](images/shop.png)

-   **랭킹**
    다른 사용자들의 랭킹을 확인하며 선의의 경쟁을 즐길 수 있습니다.

    ![상점](images/rankings.png)

-   **마이페이지**
    내 정보, 진행 중인 퀘스트, 완료한 퀘스트 목록 등을 관리할 수 있습니다.

    ![마이페이지 상](images/mypage1.png)
    ![마이페이지 하](images/mypage2.png)

---

## 🔨 프로젝트 구조

![아키텍처 다이어그램](images/ArchitectureDiagram.png)

---

## 🔧 Stack

### Frontend

-   **Language**: JavaScript
-   **Library & Framework**: Vue.js 3, Vue Router, Pinia
-   **HTTP Client**: Axios
-   **Build Tool**: Vite

### Backend

-   **Language**: Java 17
-   **Framework**: Spring Boot 3.5.8
-   **Database**: MySQL
-   **ORM**: MyBatis
-   **Security**: Spring Security, JWT
-   **API Documentation**: SpringDoc (Swagger UI)
-   **Build Tool**: Maven

---

## 💡 기대 효과

-   **새로운 기술 학습**: Vue.js와 Spring Boot를 사용한 풀스택 웹 애플리케이션 개발 경험을 쌓을 수 있습니다.
-   **API 연동 능력**: 외부 AI API(Google Gemini)와 GPS 등 다양한 기술을 연동하고 활용하는 방법을 학습합니다.
-   **인증/인가 구현**: JWT를 이용한 토큰 기반 인증 시스템을 직접 설계하고 구현하며 보안에 대한 이해를 높일 수 있습니다.
-   **데이터베이스 설계**: MyBatis를 함께 사용하여 복잡한 서비스의 데이터 모델을 설계하고 최적화하는 경험을 할 수 있습니다.

---

## 🙋‍♂️ Developer

| Frontend / Backend | Frontend / Backend |
| :--------------------------------: | :--------------------------------: |
| [정관우](https://github.com/JeongGwanWoo) | [차지훈](https://github.com/hanjihun33) |

---

## 📋 프로젝트 관련 문서

| 구분 | 링크 |
| :--- | :--- |
| ERD | [ERD 바로가기](설계서/MyTripQuest_ERD.png) |
| 시연 시나리오 | [시연 시나리오 바로가기](docs/MyTripQuest_시연시나리오.pdf) |
| 발표자료 | [발표자료 바로가기](251226_14기_광주_5반_관통PJT_정관우_차지훈.pdf) |