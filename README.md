![header](https://capsule-render.vercel.app/api?type=waving&color=6994CDEE&text=&animation=twinkling&height=80)

![동네방네 썸네일 시안2](https://github.com/Nawabali-project/Nawabali-BE/assets/105621255/674e6c2c-ff01-4987-9356-72c0525ec8b1)
<br>
[![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2FNawabali-project%2FNawabali-BE&count_bg=%2379C83D&title_bg=%23555555&icon=&icon_color=%23E7E7E7&title=hits&edge_flat=false)](https://hits.seeyoufarm.com)


</div>

# 🏡 동네방네 🏡
"동네방네" 에서 우리의 동네를 소개해요!

**”동네 방네”는 줄어든 동네에 대한 애정, 주민들과의 유대감을 증대시키기 위한 서비스입니다.**

동네방네는 **"동네를 꾸며간다”** 에 중점을 두었습니다. 요즘은 인스타 피드, 싸이월드 같이 본인의 무언가를 꾸미고 싶어하는 트렌드입니다. 이를 이용하여 자신의 **동네에 주민** 들만 알 수 있는 히든 맛집, 카페, 사진 스팟 등을 지도에 드랍하여 본인 동네가 점점 꾸며지는 모습을 볼 수 있고 소통을 통해본인 동네의 애정, 주민들과의 유대감을 키울 수 있습니다. 다른 지역 동네들과 비교를 통해 경쟁할 수 도 있습니다. 

## 목차
  - [배포 주소](#배포-주소)
  - [프로젝트 개요](#프로젝트-개요)
  - [백엔드 팀원 소개](#백엔드-팀원-소개)
  - [서비스 아키텍처](#서비스-아키텍처)
  - [기술적 의사결정](#기술적-의사결정)
  - [주요 기능](#주요-기능)
  - [프로젝트 구조](#프로젝트-구조)



## 배포 주소
> **개발 버전** : [https://dongnaebangnae.vercel.app/](https://dongnaebangnae.vercel.app/) <br>
> **서비스 서버** : [https://www.dongnaebangnae.com/](https://www.dongnaebangnae.com/)<br>
  

## 프로젝트 개요
> **🤩 프로젝트 이름 : 동네방네** <br/>
**📆 개발기간: 2024.03.26 ~ 2024.05.06** <br/>
**🛠️ 언어 : Spring Boot** <br />



## 백엔드 팀원 소개
| **박경민(팀장)** | **유재성** | **김주원** | **이은미** |
| :------: |  :------: | :------: | :------: |
| [<img src="https://github.com/kyungmin1221/BaekJoon/assets/105621255/1d1fd83d-ef01-4144-9d65-9b6056d40a43" height=150 width=150> <br/> @kyungmin](https://github.com/kyungmin1221) | [<img src="https://github.com/Nawabali-project/Nawabali-BE/assets/105621255/dd34e52f-3038-470a-9aca-1833b42a0ace" height=150 width=150> <br/> @Peter-Yu](https://github.com/Peter-Yu-0402) | [<img src="https://github.com/Nawabali-project/Nawabali-BE/assets/105621255/87cd2bd0-62e3-4e01-b4c3-e1d06b29fde2" height=150 width=150> <br/> @Juwum12](https://github.com/juwum12) | [<img src="https://github.com/kyungmin1221/BaekJoon/assets/105621255/2373d997-73e3-47d7-84f9-fdc0c12cfa28" height=150 width=150> <br/> @minnieming](https://github.com/minnieming) |


| 이름    | 역할    | 깃허브    |
| -----    | -----    | -----    |
| 박경민    | 게시물 CRUD( 게시물 조회 무한스크롤),QueryDSL 동적 쿼리를 사용한 게시물 조회(동네소식), 북마크 생성/취소,댓글 조회 무한스크롤, AWS S3 다중이미지 업로드, 지도 이미지 최적화 처리(리사이즈하여 S3 저장 및 조회), 이메일 인증 구현(Redis),ElasticSearch 를 사용한 검색 기능, 예외처리|https://github.com/kyungmin1221 | 
| 유재성    | CI/CD 구축, AWS 서버 관리,Nginx HTTPS, 블루그린 무중단배포, CORS, 프로필이미지, 대댓글, 댓글 조회 무한스크롤, PPT 제작 및 최종 발표 | https://github.com/Peter-Yu-0402|
| 김주원    | 회원가입/ 로그인(SpringSecurity), 소셜 로그인(Kakao), 마이페이지 본인 게시물 조회, SpringSecurity Filter 예외처리, Redis refresh 토큰 관리, 회원 CRUD / 유효성검사 , 좋아요 CRUD, ElasticSearch 를 사용한 검색 기능 | https://github.com/juwum12 |
| 이은미    | 좋아요(생성, 삭제), 댓글 (작성, 수정, 삭제), 동네별 점수 조회, WebSocket, SockJs, Stomp, 채팅 CRUD, 페이지네이션, SSE 알림 연결 및 삭제 | https://github.com/minnieming |



## 서비스 아키텍처
### ✅ Back-End
![백아키텍처](https://github.com/Nawabali-project/Nawabali-BE/assets/105621255/dae757b2-b6cf-4b1a-9935-1a503ddb3ac4)


## 📌 기술적 의사결정
| 📌 사용 기술 | 📖 기술 설명 |
| --- | --- |
| GitHub Actions | GitHub와의 통합이 용이하며 비교적 설정이 간단하고, 빠른 배포와 프로젝트의 규모가 작은 경우 유리하기 때문에 해당 기술을 선택하였습니다. |
| Blue-Green | 사용자에게 영향을 주지 않으면서 신규 버전을 안전하게 테스트하고 점진적으로 전환할 수 있으며, blue-green 두 환경이 독립적이기 때문에 새 버전의 오류가 기존 시스템에 영향을 미치지 않는 이점으로 해당 기술을 선택하였습니다. |
| Nginx | AWS와 달리, 서버 1대로 블루그린 무중단배포를 구현할 수 있기 때문에 제한된 서버 자원을 최대화하기 위해 nginx 선택 |
| Social Login(Kakao) | 사용자의 접근성에 중점을 둔 소셜 로그인(Kakao) 기능 구현을 선택하였습니다.  |
| Spring Security | 인증되지 않은 불특정 다수가 접근할 수 있는 점을 고려하여, 개인정보 보안성에 중점을 둔 Spring Security 기반의 로그인 기능 구현을 선택하였습니다. |
| ElasticSearch | NoSQL 데이터베이스로 활용할 수도 있고 대량의 데이터를 빠르게 검색할 수 있기 때문에 선택 |
| QueryDSL | 무한 스크롤을 구현하는 경우, 사용자가 요청하는 페이지나 필터링 조건(예: 특정 지역, 카테고리 등)에 따라 결과가 달라져야 한다. 사용자의 요구에 맞춰 콘텐츠를 동적으로 로드해야 하므로, 이는 동적 쿼리가 필수적이라고 생각하였기 때문에 DSL 을 선택 |
| Redis | 사용자는 로그인시 accessToken과 refreshToken을 받는데 서버에서 refresh를 redis에 저장하여 accessToken을 갱신해줌. 로그아웃 시 블랙리스트를 저장하는데 활용. db를 활용하는 것 보다 속도가 빠르고, 간단한 데이터 구조로 저장이 가능하며 만료시간 설정으로 관리에 보다 효율적이기에 선택하였습니다.|
| WebSocket |HTTP는 단방향 통신으로 클라이언트가 요청을 보내는 경우만 응답할 수 있어 실시간으로 서로 원할때 데이터를 주고 받을 수 있는 WebSocket을 적용 |
| SSE (Server-Sent Events) | 단방향 통신만 필요한 알림의 경우, 웹소켓보다 더 경량화되어 쉽게 구현할 수 있고 서버 간의 데이터의 단순한 전달이 목적이므로 SSE 적용 |
| Stomp | STOMP는 메시지의 전송과 구독, 다양한 프로토콜과의 통합이 용이하여 메시지 전송과 관련된 복잡한 작업을 효과적으로 처리하고 설계, 유지보수 할 수 있기 때문에 WebSocket과 함께적용 |
| SockJS | WebSocket이 모든 브라우저에서 지원되지 않는 문제로 인해, 다양한 전송 프로토콜을 지원하고 웹서버와 브라우저 간에 최적의 프로토콜을 선택하여 통신할 수 있는 해당 기술을 선택 |
| AWS S3 | CodeDeploy와도 긴밀히 연결하여 배포할 수 있으며, AWS 콘솔에서 관리가 용이하며 이미지 업로드가 잦은 서비스에 맞게 서버 확장성이 뛰어나 선택함  |
| AWS RDS | 기존 AWS 서비스(EC2)와 연동 및 관리가 용이하고 자동백업 기능이 있어 선택함 |


## 개발 환경

## Stacks 🐈

### Environment
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=Git&logoColor=white)
![Github](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=GitHub&logoColor=white)             

### BackEnd FrameWork 
![SpringBoot](https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)   
![Soket](https://img.shields.io/badge/socket.io-010101?style=for-the-badge&logo=socket.io&logoColor=white)   
<img src="https://img.shields.io/badge/JPA-212121?style=  &logo=jpa&logoColor=white"/>
<img src="https://img.shields.io/badge/Querydsl-0285C9?style=  &logo=querydsl&logoColor=white"/>
  
### Server
<img src="https://img.shields.io/badge/GitHub%20Actions-232F3E?style=for-the-badge&logo=GitHubActions&logoColor=2088FF"/>
<img src="https://img.shields.io/badge/Amazon AWS-232F3E?style=for-the-badge&logo=amazon aws&logoColor=white"> 
<img src="https://img.shields.io/badge/NGINX-009639?style=  &logo=nginx&logoColor=white"/>

### DataBase
![MYSQL](https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white) 

### Development Tools
<img src="https://img.shields.io/badge/IntelliJ IDEA-000000?style=flat-square&logo=intellij-idea&logoColor=white">


### Communication
![Slack](https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=Slack&logoColor=white)
![Notion](https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white)


### GIT 브랜치 전략

- Git-flow 전략을 기반으로 main, develop 브랜치와 feature 보조 브랜치를 운용했습니다.
- main, develop, Feat 브랜치로 나누어 개발을 하였습니다.
    - **main** 브랜치는 배포 단계에서만 사용하는 브랜치입니다.
    - **develop** 브랜치는 개발 단계에서 git-flow의 master 역할을 하는 브랜치입니다.
    - **Feat** 브랜치는 기능 단위로 독립적인 개발 환경을 위하여 사용하고 merge 후 각 브랜치를 삭제해주었습니다.

<br>

## 주요 기능

## ✅ 회원가입 / 로그인

### 📌 일반 회원가입 / 로그인(Spring Security)

|일반 회원가입|일반 로그인|
|:--:|:--:|
|![image](https://github.com/Nawabali-project/Nawabali-BE/assets/105621255/91d96185-9181-4de0-a945-1dac97ec0987)|![image](https://github.com/Nawabali-project/Nawabali-BE/assets/105621255/62d0d4d3-e0ef-4c76-908f-ae412e52b1ef)|

- 유효성 검증과 약관 동의가 포함된 회원가입을 할 수 있습니다.
- Spring Security로 사용자의 개인정보 보안성에 중점을 둔 로그인을 할 수 있습니다.

### 📌 일반 회원가입 시 이메일 인증

![GIFMaker_me-6](https://github.com/Nawabali-project/Nawabali-BE/assets/157681548/3239cde3-db66-4806-835a-72e2b7f02585)
- 실제 사용 중인 이메일인지 인증 메일을 발송하고, 인증 코드를 발급하여 메일을 인증할 수 있습니다.
    
### 📌 소셜 로그인(Kakao)
![image](https://github.com/Nawabali-project/Nawabali-BE/assets/105621255/cc8abf0d-0769-4bac-9551-785fcc012839)
- 사용자의 접근성에 중점을 둔 소셜 로그인(Kakao)을 할 수 있습니다.
    
## ✅ 지도에서 게시물 조회    
![지도 조회](https://github.com/Nawabali-project/Nawabali-BE/assets/105621255/34bb5cd9-555c-46c4-b455-75c8d542b28a)
- 지도에서 게시물들을 한 눈에 볼 수 있습니다.
- 카테고리별로 게시물들을 필터링 할 수 있습니다.

  
## ✅ 리스트에서 게시물 조회    
![리스트 조회](https://github.com/Nawabali-project/Nawabali-BE/assets/105621255/e245888c-490a-4e03-b8d1-4986f64465af)
- 리스트에서 무한스크롤로 게시물들을 최신 등록 순으로 조회할 수 있습니다.
- 카테고리별로 게시물들을 필터링 할 수 있습니다.

### ✅ 카테고리 별 조회
**🍛 맛집 카테고리**
<img width="1396" alt="맛집 카테고리" src="https://github.com/Nawabali-project/Nawabali-BE/assets/105621255/a81ad662-f238-4d61-9449-eb8f03f01fe5">
**📸 사진스팟 카테고리**
<img width="1391" alt="사진카테고리" src="https://github.com/Nawabali-project/Nawabali-BE/assets/105621255/ed76c127-53e2-4d4b-82ca-7bc65948dd14">
**☕️ 카페 카테고리**
<img width="1395" alt="카페카테고리" src="https://github.com/Nawabali-project/Nawabali-BE/assets/105621255/3f5ca749-9e1d-45d4-8ced-04214659c9f4"> 

## ✅ 게시물 작성
![게시물작성](https://github.com/Nawabali-project/Nawabali-BE/assets/105621255/86481de8-47a7-4d7b-a601-4fff4ad49d00)   
- 자신의 동네에 있는 장소를 입력하여 게시물을 등록할 수 있습니다.
- 검색으로 나와있지 않은 장소라면 사용자가 직접 핀으로 지정하여 내가 알리고 싶은 장소를 게시할 수 있습니다.
- 사진은 최대 5장까지 등록할 수 있습니다.



## ✅ 동네별 활동 점수    
![image](https://github.com/Nawabali-project/Nawabali-BE/assets/105621255/95ed6af9-7c9c-4fae-af46-a558135c54ae)
- **동네의 총 게시물 수 + 주민추천 수**가 합산된 점수를 한 눈에 볼 수 있습니다.
- 활동점수가 높은 동네일 수록 지도에 색상이 진하게 표시됩니다.


## ✅ 동네 소식
![GIFMaker_me](https://github.com/Nawabali-project/Nawabali-BE/assets/157681548/09d8879b-91ed-410f-bc07-17bfd0b230b5)
|우리동네 인기글은?|여러동네의 인기들은?|카테고리별로 인기동네는?|
|:--:|:--:|:--:|
|![image](https://github.com/Nawabali-project/Nawabali-BE/assets/105621255/b7cff42b-ec6f-4379-a98b-a70a47e93859)|![image](https://github.com/Nawabali-project/Nawabali-BE/assets/105621255/4c64ec9f-d0ce-4795-abe4-447ecb868ca2)|![image](https://github.com/Nawabali-project/Nawabali-BE/assets/105621255/bf8f085b-a8f8-4695-a9bb-263742dc170e)|
    
- 동네소식에서 우리동네 인기글이 무엇일지 볼 수 있습니다.
- 동네소식에서 다른 여러동네들의 좋아요가 많은 인기글들을 볼 수 있습니다.
- 카테고리별로 인기있는 동네들을 볼 수 있습니다.



## ✅ 마이페이지

![GIFMaker_me-4](https://github.com/Nawabali-project/Nawabali-BE/assets/157681548/08fcdd56-da0e-4d2b-b0a0-db0712b84ffa)
![회원정보변경짤](https://github.com/Nawabali-project/Nawabali-BE/assets/105621255/760e16b3-b3dc-44f7-a9c5-9d1c176b2d33)

- 마이페이지에서 회원 정보를 변경할 수 있습니다.
- 프로필 사진, 닉네임, 나의 지역을 변경할 수 있습니다.

![GIFMaker_me-2](https://github.com/Nawabali-project/Nawabali-BE/assets/157681548/22597347-613f-49bd-9ba0-d158bc761306)
![본인게시물확인짤](https://github.com/Nawabali-project/Nawabali-BE/assets/105621255/f39b2b0b-85b2-4ca6-8e75-42d8b7ee871d)

- 마이페이지에서 내가 등록한 게시물들을 확인할 수 있습니다.

![GIFMaker_me-3](https://github.com/Nawabali-project/Nawabali-BE/assets/157681548/bca8d7b6-1ca1-439e-afe2-9c7c336e2cf1)
![본인의 북마크짤](https://github.com/Nawabali-project/Nawabali-BE/assets/105621255/032dd71f-a49d-421f-b9bb-d3cebce953df)
- 마이페이지에서 내가 등록한 북마크된 게시물들을 볼 수 있습니다.

![본인등급확인짤](https://github.com/Nawabali-project/Nawabali-BE/assets/105621255/570cd66e-0d58-4494-86f9-530dc2a0cfe1)
- 마이페이지에서 나의 등급을 확인할 수 있습니다.
- 등급은 좋아요를 받은 수와 게시글 수를 기준으로 **"주민,토박이,터줏대감"** 으로 구분됩니다.

## ✅ 실시간 채팅/알림 기능

![GIFMaker_me-5](https://github.com/Nawabali-project/Nawabali-BE/assets/157681548/e07ec5be-3391-4f4e-9554-e487d6c9ac3a)
![알림짤](https://github.com/Nawabali-project/Nawabali-BE/assets/105621255/f3495696-4fa7-485c-a9fe-4791c814ce10)
- 채팅방이나 채팅메세지를 통한 검색이 가능합니다.

![GIFMaker_me-7](https://github.com/Nawabali-project/Nawabali-BE/assets/157681548/161cae7e-381d-4cba-9fc6-958b018cb923)
- 상대의 이름을 검색하여 채팅방을 생성 할 수 있습니다.

![GIFMaker_me-8](https://github.com/Nawabali-project/Nawabali-BE/assets/157681548/aafcfc3e-3d54-46b6-8fcd-62979fc08230)
![알림짤](https://github.com/Nawabali-project/Nawabali-BE/assets/105621255/a20de69c-9bba-4a81-a7c7-01ac8ba4c115)
- 1:1 대화를 할 수 있습니다.

![GIFMaker_me-9](https://github.com/Nawabali-project/Nawabali-BE/assets/157681548/2d7b1a2a-87c0-4db8-85e0-61bde1719c7c)
![알림짤](https://github.com/Nawabali-project/Nawabali-BE/assets/105621255/f961b00c-b847-41a2-8d95-a8bcd19a6e8b)
- 메세지가 오면 상단에 알림으로 표시되어 어떤 페이지에 있어도 메세지가 왔음을 알 수 있습니다. 채팅을 확인하면 알림이 사라지게 됩니다.

<br>

