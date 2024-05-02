![header](https://capsule-render.vercel.app/api?type=waving&color=6994CDEE&text=&animation=twinkling&height=80)

![readme_mockup2](https://github.com/kyungmin1221/BaekJoon/assets/105621255/41dd9ef3-00ce-46a9-b4d8-52a7298108c3)
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
  - [팀원 소개](#백엔드-팀원-소개)
  - [서비스 아키텍쳐](#서비스-아키텍처)


## 배포 주소
> **개발 버전** : [https://dongnaebangnae.vercel.app/](https://dongnaebangnae.vercel.app/) <br>
> **서비스 서버** : [https://www.dongnaebangnae.com/](https://www.dongnaebangnae.com/)<br>
  

## 프로젝트 개요
> **🤩 프로젝트 이름 : 동네방네** <br/>
**📆 개발기간: 2024.04.26 ~ 2024.05.06** <br/>
**🛠️ 언어 : Spring Boot** <br />



## 백엔드 팀원 소개
| **박경민(팀장)** | **유재성** | **김주원** | **이은미** |
| :------: |  :------: | :------: | :------: |
| [<img src="https://github.com/kyungmin1221/BaekJoon/assets/105621255/1d1fd83d-ef01-4144-9d65-9b6056d40a43" height=150 width=150> <br/> @kyungmin](https://github.com/kyungmin1221) | [<img src="" height=150 width=150> <br/> @Peter-Yu](https://github.com/Peter-Yu-0402) | [<img src="" height=150 width=150> <br/> @Juwum12](https://github.com/juwum12) | [<img src="https://github.com/kyungmin1221/BaekJoon/assets/105621255/2373d997-73e3-47d7-84f9-fdc0c12cfa28" height=150 width=150> <br/> @minnieming](https://github.com/minnieming) |


| 이름    | 역할    | 깃허브    |
| ---    | ---    | ---    |
| 박경민    | 게시물 관련 기능(게시물 조회 / 무한스크롤, QueryDSL 동적 쿼리를 사용한 조회), 북마크 관련 기능, 이미지 최적화 처리, 이메일 인증 구현, ElasticSearch 를 사용한 검색 기능 |https://github.com/kyungmin1221 | 
| 유재성    |  | https://github.com/Peter-Yu-0402|
| 김주원    |  | https://github.com/DoKkangs|
| 이은미    | 댓글 CRUD, 좋아요 CRUD, 채팅 및 알림 기능 | https://github.com/minnieming |



## 🏗️ 서비스 아키텍처

### ✅ 전체 아키텍처
<img width="1231" alt="동네방네 전체 아키텍처" src="">

### ✅ Back-End
<img width="1060" alt="동네방네 아키텍처 백엔드" src="">


## 📌 기술적 의사결정
| 📌 사용 기술 | 📖 기술 설명 |
| --- | --- |
| GitHub Actions | GitHub와의 통합이 용이하며 비교적 설정이 간단하고, 빠른 배포와 프로젝트의 규모가 작은 경우 유리하기 때문에 해당 기술을 선택하였습니다. |
| Docker | 독립적인 환경을 구성하고, 개발 환경과 운영 환경 간의 일관성을 유지하며 컨테이너 기반의 배포로 가볍게 배포할 수 있기 때문에 해당 기술을 선택하였습니다. |
| Blue-Green | 사용자에게 영향을 주지 않으면서 신규 버전을 안전하게 테스트하고 점진적으로 전환할 수 있으며, blue-green 두 환경이 독립적이기 때문에 새 버전의 오류가 기존 시스템에 영향을 미치지 않는 이점으로 해당 기술을 선택하였습니다. |
| Nginx | 한정된 예산을 사용하는 상황에서 하나의 EC2 인스턴스로 서버를 구축하였기 때문에, nginx의 리버스 프록시 기능을 통해 한대의 서버로 무중단배포를 구현하였습니다. |
| SSE (Server-Sent Events) | 서버에서 클라이언트로의 메세지 전달만 필요했기 때문에 단방향 통신 기술인 SSE가 가장 적합한 기술이라 판단하여 선택하였습니다. |
| Social Login(Kakao) | 펀딩 후원에 참여하기 위해 다수가 접근할 수 있는 점을 고려하여, 사용자의 접근성에 중점을 둔 소셜 로그인(Kakao & Google) 기능 구현을 선택하였습니다.  |
| Spring Security | 인증되지 않은 불특정 다수가 접근할 수 있는 점을 고려하여, 개인정보 보안성에 중점을 둔 Spring Security 기반의 로그인 기능 구현을 선택하였습니다. |
| ElasticSearch | 원하는 펀딩에 후원을 진행하고, 후원 결제 내역을 수집하기 위해 Kakaopay 온라인 결제 기능 구현을 선택하였습니다. |
| QueryDSL | 원하는 펀딩에 후원을 진행하고, 후원 결제 내역을 수집하기 위해 Kakaopay 온라인 결제 기능 구현을 선택하였습니다. |
| Redis | 사용자들에게 빈번하게 보여지는 정보들은 캐시를 적용하여 처리하면 성능 개선을 할 수 있을 것이라고 생각하였고, 추후 사용자가 늘어남에 따라 동시성 문제도 발생할 수 있다고 생각하여 이를 제어할 수 있는 기능을 제공하는 해당 기술을 선택하였습니다. |




## 1. 개발 환경

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
<img src="https://img.shields.io/badge/Amazon AWS-232F3E?style=for-the-badge&logo=amazon aws&logoColor=white"> 
<img src="https://img.shields.io/badge/NGINX-009639?style=  &logo=nginx&logoColor=white"/>

### DataBase
![MYSQL](https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white) 

### Development Tools
<img src="https://img.shields.io/badge/IntelliJ IDEA-000000?style=flat-square&logo=intellij-idea&logoColor=white">


### Communication
![Slack](https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=Slack&logoColor=white)
![Notion](https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white)


### 브랜치 전략

- Git-flow 전략을 기반으로 main, develop 브랜치와 feature 보조 브랜치를 운용했습니다.
- main, develop, Feat 브랜치로 나누어 개발을 하였습니다.
    - **main** 브랜치는 배포 단계에서만 사용하는 브랜치입니다.
    - **develop** 브랜치는 개발 단계에서 git-flow의 master 역할을 하는 브랜치입니다.
    - **Feat** 브랜치는 기능 단위로 독립적인 개발 환경을 위하여 사용하고 merge 후 각 브랜치를 삭제해주었습니다.

<br>

## 🔎 주요기능

## ✅ 회원가입 / 로그인

### 📌 일반 회원가입 / 로그인(Spring Security)

|일반 회원가입|일반 로그인|
|:--:|:--:|
|![image]()|

- 유효성 검증과 약관 동의가 포함된 회원가입을 할 수 있습니다.
- Spring Security로 사용자의 개인정보 보안성에 중점을 둔 로그인을 할 수 있습니다.

### 📌 일반 회원가입 시 이메일 인증

![회원가입인증메일짤]()

- 실제 사용 중인 이메일인지 인증 메일을 발송하고, 인증 코드를 발급하여 메일을 인증할 수 있습니다.
    
### 📌 소셜 로그인(Kakao)
    
![image]()
    
- 사용자의 접근성에 중점을 둔 소셜 로그인(Kakao)을 할 수 있습니다.
    
## ✅ 지도에서 게시물 조회
    
![지도 조회]()
    
- 
    
## ✅ 리스트에서 게시물 조회
    
![리스트 조회]()
    
- 
## ✅ 게시물 작성
    
![게시물작성]()   
- 


## ✅ 동네별 활동 점수
    
![image]()

- 펀

## ✅ 동네 소식
    
![동네소식]()
|1|2|3|4|
|:--:|:--:|:--:|:--:|
|![image]()|![image]()|![image]()|![image]()|
    
- 동네소식 설명란


## ✅ 마이페이지
    
![회원정보변경짤]()

- 우

![본인게시물확인짤]()

- 우

![본인의 북마크짤]()

- ㄹ

    
## ✅ 실시간 알림 기능

![후원알림짤]()

- 알림 설명


<br>


## 3. 프로젝트 구조

```
├── README.md
├── .eslintrc.js
├── .gitignore
├── .prettierrc.json
├── package-lock.json
├── package.json
│
├── public
│    └── index.html
└── src
     ├── App.jsx
     ├── index.jsx
     ├── api
     │     └── mandarinAPI.js
     ├── asset
     │     ├── fonts
     │     ├── css_sprites.png
     │     ├── logo-404.svg
     │     └── logo-home.svg
     │          .
     │          .
     │          .
     ├── atoms
     │     ├── LoginData.js
     │     └── LoginState.js
     ├── common
     │     ├── alert
     │     │     ├── Alert.jsx
     │     │     └── Alert.Style.jsx
     │     ├── button
     │     ├── comment
     │     ├── inputBox
     │     ├── post
     │     ├── postModal
     │     ├── product
     │     ├── tabMenu
     │     ├── topBanner
     │     └── userBanner
     ├── pages
     │     ├── addProduct
     │     │     ├── AddProduct.jsx
     │     │     └── AddProduct.Style.jsx
     │     ├── chatList
     │     ├── chatRoom
     │     ├── emailLogin
     │     ├── followerList
     │     ├── followingList
     │     ├── home
     │     ├── join
     │     ├── page404
     │     ├── postDetail
     │     ├── postEdit
     │     ├── postUpload
     │     ├── productEdit
     │     ├── profile
     │     ├── profileEdit
     │     ├── profileSetting
     │     ├── search
     │     ├── snsLogin
     │     └── splash
     ├── routes
     │     ├── privateRoutes.jsx
     │     └── privateRoutesRev.jsx  
     └── styles
           └── Globalstyled.jsx
```
