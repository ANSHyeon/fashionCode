# FashionCode

## 프로젝트 소개

추가 예정

## 📚 기술스택

| Category  | TechStack |
| ------------- | ------------- |
| Architecture  | <img src="https://img.shields.io/badge/MVVM-603B2C"> | 
| Jetpack | <img src="https://img.shields.io/badge/Compose-28456C"> <img src="https://img.shields.io/badge/Navigation-89632A">| 
| Network | <img src="https://img.shields.io/badge/Retrofit-603B2C"> <img src="https://img.shields.io/badge/OkHttp3-492F64">| 
| Asynchronous | <img src="https://img.shields.io/badge/Coroutine-69314C"> <img src="https://img.shields.io/badge/Flow-89632A"> | 
| DI | <img src="https://img.shields.io/badge/Hilt-2B593F"> | 
| Image | <img src="https://img.shields.io/badge/Coil-28456C"> | 
| Local DB |<img src="https://img.shields.io/badge/Jetpack Room-492F64"> | 
| Json | <img src="https://img.shields.io/badge/Kotlin%20Serialization-854C1D"> | 
| Open API | <img src="https://img.shields.io/badge/Adobe Photoshop API-69314C"> | 

## 💬 배운점

1. Compose 적용
   - 수정 예정

2. 공식문서만으로 기능 구현
   - 이미지 background remove 기능을 사용하기 위한 APi로 Remove.bg API와 Adobe Photoshop API 2개가 있었습니다.
   - Remove.bg API는 안드로이드 SDK가 제공되고 관련 가이드 자료도 풍부했지만 월 50회만 무료였습니다.
   - Adobe Photoshop API는 월 5,000회 무료라는 장점이 있지만 Android SDK가 제공되지 않고 REST API만 제공됐으며, 관련 자료도 공식문서 뿐이였습니다.
   - Remove.bg API를 한달에 5,000건 사용하기 위해서는 699,000원이 필요했기 때문에 사용이 더 어렵더라도 Adobe Photoshop API를 사용했습니다.
   - API를 사용하기 위해서는 Token이 필요했는데 Console에서 생성할 수 있는 Token은 24시간의 유효기간을 가져 Token을 갱신하는 요청과 Refresh Token을 발급받는 방법을 공식문서를 살펴보며 찾았습니다.
   - 공식문서는 문어체와 함께 대명사가 많아 이해하기 어려울 것이라 생각했지만 이번 경험을 통해 자신감을 얻을 수 있었습니다.
  
3. 만료된 AccessToken 갱신 구현
   - Adobe Photoshop API와 DropBox API를 사용하면서 Access Token이 만료되어 갱신해야하는 문제가 발생했습니다.
   - API 요청시 마다 새로운 Token을 발급 받을 수도 있었지만 불필요한 리소스가 소모된다고 생각했습니다.
   - Retrofit을 이용한 통신 시 Header를 변경해야 했기에 Interceptor에 관한 키워드로 내용을 찾은 결과 Authenticator를 사용해 해결할 수 있다는 내용을 확인했습니다.
   - Authenticator를 사용해 runBlocking으로 스레드를 잠시 막고, Token 갱신요청에 대한 응답이 오면 Interceptor로 Header를 추가해 이전 요청을 다시 하도록 구현해 해결했습니다.

4. Hilt AssistedInject - 런타임 주입
   - 게시물 상세 화면에서 getPost() 함수는 viewModel이 생성될 때 한번만 동작하도록 구현하고자 했습니다.
   - 그런데 이때 필요한 변수 ‘postId’는 이전 화면에서 navigate시 argument로 Composable에 전달됐습니다.
   - 그렇기에 ViewModel 생성 시 parameter를 전달할 필요가 있어 런타임 주입을 구현했습니다.
   - @AssistedInject 어노테이션에 대해 알게 되었고, @Inject, @Provides, @Binds의 차이점에 대해 살펴봤습니다.



## 📺︎ 작동화면

### 스타일
<div align="center">

| 스타일 상세 | 스타일 생성 |
| :---------------: | :---------------: |
| <img src="https://github.com/ANSHyeon/fashionCode/assets/127817240/5483af12-702d-4d90-a0dc-149d26cd111e" align="center" width="250px"/> | <img src="https://github.com/ANSHyeon/fashionCode/assets/127817240/c8a74bc5-51c8-47ab-ad0b-6d4f28913b82" align="center" width="250px"/> |

</div>


### 캘린더
<div align="center">

| 캘린더 | 옷장에 옷 추가 |
| :---------------: | :---------------: |
| <img src="https://github.com/ANSHyeon/fashionCode/assets/127817240/8aef4743-3b05-4eb6-b67f-7b04ddbb60e5" align="center" width="250px"/> | <img src="https://github.com/ANSHyeon/fashionCode/assets/127817240/510171fd-e1a4-4c45-8200-5be3039d3d8e" align="center" width="250px"/> |

</div>


### 커뮤니티
<div align="center">

| 게시물 작성 | 댓글 작성 | 대댓글 작성 |
| :---------------: | :---------------: | :---------------: |
| <img src="https://github.com/ANSHyeon/fashionCode/assets/127817240/e720cdd5-12f7-4f30-8420-c55931dfd616" align="center" width="250px"/> | <img src="https://github.com/ANSHyeon/fashionCode/assets/127817240/5418cfbe-e10a-4340-8b90-d59717f40f50" align="center" width="250px"/> | <img src="https://github.com/ANSHyeon/fashionCode/assets/127817240/1c9a4089-e98e-4faa-88fa-9aa58369c9f7" align="center" width="250px"/> |

</div>


### 유저
<div align="center">

| 마이페이지 | 팔로우 | 프로필 수정 |
| :---------------: | :---------------: | :---------------: |
| <img src="https://github.com/ANSHyeon/fashionCode/assets/127817240/1ce7a39a-4b25-4566-8a44-5cfeae3a2100" align="center" width="250px"/> | <img src="https://github.com/ANSHyeon/fashionCode/assets/127817240/e3858bb1-2dc7-4c48-b9d5-63ca6f5f0962" align="center" width="250px"/> | <img src="https://github.com/ANSHyeon/fashionCode/assets/127817240/26e32e3a-8be7-43d1-996d-29968d75869b" align="center" width="250px"/> |

</div>
