# 수상 내역
* 2024 컴퓨터공학 심화 캡스톤 디자인 경진대회 은상 🥈
* 2024 한국 정보 기술 대학생 논문 경진 대회 은상 🥈

| 2024 한국정보기술학회                                                   |
|----------------------------------------------------------|
| <img src="https://github.com/user-attachments/assets/b7f94f7f-1ce7-4bf7-b8bf-540cab34b95a" width="300" height="500"> |

# 프로젝트 배경 및 소개

행정안전부 자료에 따르면, 농기계 사고는 연평균 1,273건 발생하며, 이로 인해 매년 93명이 사망하고 1,009명이 다치는 것으로 나타났습니다. 특히 농기계는 안전장치가 부족하기 때문에 사고 발생 시 치사율이 일반 사고보다 7배나 높은 것으로 조사되었습니다.

소방청의 자료에 따르면, 농촌 지역에서의 사고 유형은 끼임 사고, 전도 및 전복 사고, 그리고 교통 사고 순으로 발생하고 있습니다. 

그리고, 최근 3년간의 구조 출동 분석 자료를 보면, 농기계와 관련된 구조활동 건수가 매년 증가하고 있으며, 2021년 510건에서 2023년에는 632건으로 늘어났습니다. 이로 인해 인명피해도 연평균 159명에 달하고 있습니다.

또한 구급 차량 이송 소요시간은 농업 지역의 특성상 작업 지역이 주로 병원시설과 멀리 떨어져 있어 구급차가 병원에 도착하기까지 걸리는 시간이 30분 이상인 경우가 43%로, 전국 평균인 11.9%에 비해 상대적으로 높게 나타나고 있습니다.
| 2022년 구급차량 이송 소요시간(현장→병원도착) |
|----------------------------------------------------------|
| <p align="center"><img src="https://github.com/user-attachments/assets/c2b19a49-afcb-440f-ac68-8adab8ea15e6" width="450" height="300"></p> |
| <p align="center">소방청 자료 제공</p>|

<br> 농기계 사고는 60대 이상의 노령층에서 주로 발생하며, 단독으로 작업하는 경우에 많이 발생했습니다. 

| 사고 발생 원인 | 사고 발생 연령대|
|----------------------------------------------------------| ----------------------------------------------------------|
| <p align="center"><img src="https://github.com/user-attachments/assets/a90a847a-3a92-4d86-b084-5ced492044f9" width="250" height="250"></p> | <p align="center"><img src="https://github.com/user-attachments/assets/2aad6870-ade7-4ddd-aea5-f98694f991c1" width="400" height="250"></p> |

<br> 따라서 본 프로젝트는 농기계 사용 중 안전사고가 발생했을 때, 응급 센터와 등록된 비상 연락망에 신속하게 구조 알림을 보내어 사고 발생 지역에서 병원까지의 이송 시간을 단축시키는 것을 목적으로 하고 있습니다.

# 프로젝트 동작 순서
<img src="https://github.com/user-attachments/assets/94ceceab-659c-46dc-9e27-5d2948a3b477" width="850" height="750">

# 스크린샷 

## 🛠 HW 구성 

| 차량 전복 모습 | 전복 감지 센서 |
|----------------------|----------------------|
| <p align="center"><img src="https://github.com/user-attachments/assets/ad2a422c-b880-4f43-8f7e-4d480fd70133" width="150" height="150"></p>|<img src="https://github.com/user-attachments/assets/c1878f2f-f2b2-4d53-96b2-b01e489f1a07" width="150" height="250"></p> |



### 센서 상태 
| 1. Safe | 2. Warning  | 3. Ready | 4. Ready → Safe |
|----------------------|----------------------|----------------------| ----------------------|
| <p align="center"><img src="https://github.com/user-attachments/assets/92aab309-43b8-4e2d-9a8b-afbe455504a5" width="300" height="300"></p>|<p align="center"><img src="https://github.com/user-attachments/assets/cb1a3a78-e5f0-4c9c-9688-748d96a9dc78" width="300" height="280"></p> |<p align="center"><img src="https://github.com/user-attachments/assets/6483c390-3c0d-444e-a0e7-bf0c7d5eb20e" width="250" height="300"></p> |<p align="center"><img src="https://github.com/user-attachments/assets/dac49593-e517-41e3-8c6f-6dfca3cba105" width="250" height="300"></p> |
| 기울기 30도 넘어가면 4초 동안 전복 상황인지 확인  | 센서가 전복 상황으로 판단하면 위도, 경도 데이터를 앱으로 전송| 앱에서 'E' 신호를 수신하면 데이터 전송을 멈춤 | Ready 상태에서 원상태로 복귀하면 센서를 재가동 |

<br>

## 💻 App

| 페어링 기기 리스트 | 센서 연결 상태  | 포그라운드 실행 | 센서 연결 확인 |
|----------------------|----------------------|----------------------| ----------------------|
| <p align="center"><img src="https://github.com/user-attachments/assets/8bfb939c-157a-4d73-a5ed-5a15dc69b02b" width="200" height="450"></p>|<p align="center"><img src="https://github.com/user-attachments/assets/e686ccd5-6376-493f-96fd-c779e21bf559" width="200" height="450"></p> |<p align="center"><img src="https://github.com/user-attachments/assets/617aa4a1-00af-4101-b37f-b1fdc505e091" width="200" height="450"></p> |<p align="center"><img src="https://github.com/user-attachments/assets/ded1c099-ece0-4034-a448-b39c53456858" width="200" height="450"></p> |

<br>

| 사용자 응답 요청 | 비상 연락망 알림 전송 | 
|----------------------|----------------------|
| <p align="center"><img src="https://github.com/user-attachments/assets/db75f412-73cf-4728-a748-94dfd389df64" width="200" height="450"></p> | <p align="center"><img src="https://github.com/user-attachments/assets/c8e286ef-5835-49d1-b7d7-95a4d7428296" width="200" height="350"></p> |
| 전복사고 발생시 <br>운전자의 상태를 확인 | 운전자가 제한 시간 안에<br> 응답하지 못할 경우|

