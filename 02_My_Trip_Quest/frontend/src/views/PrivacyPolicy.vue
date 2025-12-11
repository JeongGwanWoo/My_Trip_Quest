<template>
  <div class="policy-page">
    <h1>개인정보처리방침</h1>
    <div class="policy-content">
      <h2>1. 개인정보의 처리 목적</h2>
      <p>MyTripQuest('www.mytripquest.com' 이하 'MyTripQuest')은(는) 다음의 목적을 위하여 개인정보를 처리합니다. 처리하고 있는 개인정보는 다음의 목적 이외의 용도로는 이용되지 않으며, 이용 목적이 변경되는 경우에는 개인정보 보호법 제18조에 따라 별도의 동의를 받는 등 필요한 조치를 이행할 예정입니다.</p>
      <ul>
        <li><strong>홈페이지 회원가입 및 관리:</strong> 회원 가입의사 확인, 회원제 서비스 제공에 따른 본인 식별·인증, 회원자격 유지·관리, 제한적 본인확인제 시행에 따른 본인확인, 서비스 부정이용 방지, 만 14세 미만 아동의 개인정보 처리 시 법정대리인의 동의여부 확인, 각종 고지·통지, 고충처리 등을 목적으로 개인정보를 처리합니다.</li>
        <li><strong>재화 또는 서비스 제공:</strong> 물품배송, 서비스 제공, 계약서·청구서 발송, 콘텐츠 제공, 맞춤서비스 제공, 본인인증, 연령인증, 요금결제·정산 등을 목적으로 개인정보를 처리합니다.</li>
      </ul>

      <h2>2. 처리하는 개인정보 항목</h2>
      <p>회사는 다음의 개인정보 항목을 처리하고 있습니다.</p>
      <ul>
        <li><strong>필수항목:</strong> 이메일, 비밀번호, 닉네임</li>
        <li><strong>선택항목:</strong> 프로필 사진</li>
      </ul>

      <h2>3. 개인정보의 처리 및 보유 기간</h2>
      <p>① 회사는 법령에 따른 개인정보 보유·이용기간 또는 정보주체로부터 개인정보를 수집 시에 동의 받은 개인정보 보유·이용기간 내에서 개인정보를 처리·보유합니다.</p>
      <p>② 각각의 개인정보 처리 및 보유 기간은 다음과 같습니다.</p>
      <ul>
        <li><strong>홈페이지 회원가입 및 관리:</strong> 사업자/단체 홈페이지 탈퇴 시까지. 다만, 다음의 사유에 해당하는 경우에는 해당 사유 종료 시까지</li>
        <li>1) 관계 법령 위반에 따른 수사·조사 등이 진행중인 경우에는 해당 수사·조사 종료 시까지</li>
        <li>2) 홈페이지 이용에 따른 채권·채무관계 잔존 시에는 해당 채권·채무관계 정산 시까지</li>
      </ul>

      <p class="placeholder-text">[... 이하 개인정보처리방침 내용 (준비중) ...]</p>
    </div>
  </div>
</template>

<script setup>
// No script logic needed for this static page
</script>

<style scoped>
.policy-page {
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

p, ul {
  line-height: 1.8;
  font-size: 1rem;
  color: #555;
}

.policy-content {
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
