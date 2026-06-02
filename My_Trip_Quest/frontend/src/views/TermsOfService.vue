<template>
  <div class="terms-page">
    <h1>운영약관</h1>
    <div class="terms-content">
      <h2>제1조 (목적)</h2>
      <p>이 약관은 MyTripQuest (이하 "회사"라 함)가 제공하는 MyTripQuest 서비스 및 제반 서비스의 이용과 관련하여 회사와 회원과의 권리, 의무 및 책임사항, 기타 필요한 사항을 규정함을 목적으로 합니다.</p>

      <h2>제2조 (정의)</h2>
      <p>이 약관에서 사용하는 용어의 정의는 다음과 같습니다.</p>
      <ol>
        <li>"서비스"라 함은 구현되는 단말기(PC, TV, 휴대형단말기 등의 각종 유무선 장치를 포함)와 상관없이 "회원"이 이용할 수 있는 MyTripQuest 및 관련 제반 서비스를 의미합니다.</li>
        <li>"회원"이라 함은 회사의 "서비스"에 접속하여 이 약관에 따라 "회사"와 이용계약을 체결하고 "회사"가 제공하는 "서비스"를 이용하는 고객을 말합니다.</li>
        <li>"아이디(ID)"라 함은 "회원"의 식별과 "서비스" 이용을 위하여 "회원"이 정하고 "회사"가 승인하는 문자와 숫자의 조합을 의미합니다.</li>
        <li>"비밀번호"라 함은 "회원"이 부여 받은 "아이디와 일치되는 "회원"임을 확인하고 비밀보호를 위해 "회원" 자신이 정한 문자 또는 숫자의 조합을 의미합니다.</li>
      </ol>

      <h2>제3조 (약관의 게시와 개정)</h2>
      <p>"회사"는 이 약관의 내용을 "회원"이 쉽게 알 수 있도록 서비스 초기 화면에 게시합니다.</p>
      <p>"회사"는 "약관의규제에관한법률", "정보통신망이용촉진및정보보호등에관한법률(이하 "정보통신망법")" 등 관련법을 위배하지 않는 범위에서 이 약관을 개정할 수 있습니다.</p>
      
      <h2>제4조 (서비스의 중단)</h2>
      <p>회사는 컴퓨터 등 정보통신설비의 보수점검, 교체 및 고장, 통신두절 또는 운영상 상당한 이유가 있는 경우 서비스의 제공을 일시적으로 중단할 수 있습니다.</p>

      <p class="placeholder-text">[... 이하 약관 내용 (준비중) ...]</p>
    </div>
  </div>
</template>

<script setup>
// No script logic needed for this static page
</script>

<style scoped>
.terms-page {
  max-width: 800px;
  margin: 40px auto;
  padding: 20px;
  font-family: "Pretendard", sans-serif;
  color: #333;
}

h1 {
  font-size: 2.5rem;
  font-weight: 800;
  border-bottom: 2px solid #eee;
  padding-bottom: 10px;
  margin-bottom: 20px;
}

h2 {
  font-size: 1.5rem;
  font-weight: 700;
  margin-top: 40px;
  margin-bottom: 10px;
  border-left: 4px solid #3b82f6;
  padding-left: 10px;
}

p, ol {
  line-height: 1.8;
  font-size: 1rem;
  color: #555;
}

.terms-content {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
}

.placeholder-text {
  text-align: center;
  color: #999;
  margin-top: 50px;
  font-style: italic;
}
</style>
