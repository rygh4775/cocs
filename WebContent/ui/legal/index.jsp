<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.cocs.common.Env"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../resources/jsp/variable.jsp" flush="false" />
<jsp:include page="../resources/jsp/header.jsp" flush="false" />
</head>
<body>
	<div class="navbar navbar-inner navbar-fixed-top" role="navigation">
      <div class="container">
          <a class="navbar-brand" href="<%=request.getContextPath()%>/">Cloud Of Coluds</a>
          <a class="navbar-brand pull-right pointer" href="<%=request.getContextPath()%>/signin">로그인</a>
      </div>
    </div>
	
	<div class="container">
         		<div class="row">
         			<pre>
1. 개인정보의 처리 목적 (‘http://cocs.no-ip.biz’이하 ‘COCS’) 은(는) 다음의 목적을 위하여 개인정보를 처리하고 있으며, 다음의 목적 이외의 용도로는 이용하지 않습니다.
- 고객 가입의사 확인, 고객에 대한 서비스 제공에 따른 본인 식별.인증, 회원자격 유지.관리, 물품 또는 서비스 공급에 따른 금액 결제, 물품 또는 서비스의 공급.배송 등


2. 개인정보처리 위탁

① ('COCS')은(는) 원활한 개인정보 업무처리를 위하여 다음과 같이 개인정보 처리업무를 위탁하고 있습니다.

② ('http://cocs.no-ip.biz'이하 'COCS')은(는) 위탁계약 체결시 개인정보 보호법 제25조에 따라 위탁업무 수행목적 외 개인정보 처리금지, 기술적․관리적 보호조치, 재위탁 제한, 수탁자에 대한 관리․감독, 손해배상 등 책임에 관한 사항을 계약서 등 문서에 명시하고, 수탁자가 개인정보를 안전하게 처리하는지를 감독하고 있습니다.

③ 위탁업무의 내용이나 수탁자가 변경될 경우에는 지체없이 본 개인정보 처리방침을 통하여 공개하도록 하겠습니다.


3. 정보주체의 권리,의무 및 그 행사방법 이용자는 개인정보주체로서 다음과 같은 권리를 행사할 수 있습니다.

① 정보주체는 <기관/회사명>(‘사이트URL’이하 ‘사이트명) 에 대해 언제든지 다음 각 호의 개인정보 보호 관련 권리를 행사할 수 있습니다.
1. 개인정보 열람요구
2. 오류 등이 있을 경우 정정 요구
3. 삭제요구
4. 처리정지 요구



4. 처리하는 개인정보의 항목 작성 

① ('http://cocs.no-ip.biz'이하 'COCS')은(는) 다음의 개인정보 항목을 처리하고 있습니다.



5. 개인정보의 파기('COCS')은(는) 원칙적으로 개인정보 처리목적이 달성된 경우에는 지체없이 해당 개인정보를 파기합니다. 파기의 절차, 기한 및 방법은 다음과 같습니다.

-파기절차
이용자가 입력한 정보는 목적 달성 후 별도의 DB에 옮겨져(종이의 경우 별도의 서류) 내부 방침 및 기타 관련 법령에 따라 일정기간 저장된 후 혹은 즉시 파기됩니다. 이 때, DB로 옮겨진 개인정보는 법률에 의한 경우가 아니고서는 다른 목적으로 이용되지 않습니다.
-파기기한
이용자의 개인정보는 개인정보의 보유기간이 경과된 경우에는 보유기간의 종료일로부터 5일 이내에, 개인정보의 처리 목적 달성, 해당 서비스의 폐지, 사업의 종료 등 그 개인정보가 불필요하게 되었을 때에는 개인정보의 처리가 불필요한 것으로 인정되는 날로부터 5일 이내에 그 개인정보를 파기합니다.



6. 개인정보의 안전성 확보 조치 ('COCS')은(는) 개인정보보호법 제29조에 따라 다음과 같이 안전성 확보에 필요한 기술적/관리적 및 물리적 조치를 하고 있습니다.

1. 개인정보 취급 직원의 최소화 및 교육
개인정보를 취급하는 직원을 지정하고 담당자에 한정시켜 최소화 하여 개인정보를 관리하는 대책을 시행하고 있습니다.

2. 정기적인 자체 감사 실시
개인정보 취급 관련 안정성 확보를 위해 정기적(분기 1회)으로 자체 감사를 실시하고 있습니다.

3. 내부관리계획의 수립 및 시행
개인정보의 안전한 처리를 위하여 내부관리계획을 수립하고 시행하고 있습니다.

4. 개인정보의 암호화
이용자의 개인정보는 비밀번호는 암호화 되어 저장 및 관리되고 있어, 본인만이 알 수 있으며 중요한 데이터는 파일 및 전송 데이터를 암호화 하거나 파일 잠금 기능을 사용하는 등의 별도 보안기능을 사용하고 있습니다.

5. 해킹 등에 대비한 기술적 대책
<COCS>('COCS')은 해킹이나 컴퓨터 바이러스 등에 의한 개인정보 유출 및 훼손을 막기 위하여 보안프로그램을 설치하고 주기적인 갱신·점검을 하며 외부로부터 접근이 통제된 구역에 시스템을 설치하고 기술적/물리적으로 감시 및 차단하고 있습니다.

6. 개인정보에 대한 접근 제한
개인정보를 처리하는 데이터베이스시스템에 대한 접근권한의 부여,변경,말소를 통하여 개인정보에 대한 접근통제를 위하여 필요한 조치를 하고 있으며 침입차단시스템을 이용하여 외부로부터의 무단 접근을 통제하고 있습니다.

7. 접속기록의 보관 및 위변조 방지
개인정보처리시스템에 접속한 기록을 최소 6개월 이상 보관, 관리하고 있으며, 접속 기록이 위변조 및 도난, 분실되지 않도록 보안기능 사용하고 있습니다.

8. 문서보안을 위한 잠금장치 사용
개인정보가 포함된 서류, 보조저장매체 등을 잠금장치가 있는 안전한 장소에 보관하고 있습니다.

9. 비인가자에 대한 출입 통제
개인정보를 보관하고 있는 물리적 보관 장소를 별도로 두고 이에 대해 출입통제 절차를 수립, 운영하고 있습니다.




7. 개인정보 보호책임자 작성


① COCS(‘http://cocs.no-ip.biz’이하 ‘COCS) 은(는) 개인정보 처리에 관한 업무를 총괄해서 책임지고, 개인정보 처리와 관련한 정보주체의 불만처리 및 피해구제 등을 위하여 아래와 같이 개인정보 보호책임자를 지정하고 있습니다.

▶ 개인정보 보호책임자 
성명 :조현수
직책 :팀장
직급 :1급
연락처 :cocs.cloudofclouds@gmail.com
※ 개인정보 보호 담당부서로 연결됩니다.

▶ 개인정보 보호 담당부서
부서명 :관리
담당자 :조현수
연락처 :cocs.cloudofclouds@gmail.com

② 정보주체께서는 COCS(‘http://cocs.no-ip.biz’이하 ‘COCS) 의 서비스(또는 사업)을 이용하시면서 발생한 모든 개인정보 보호 관련 문의, 불만처리, 피해구제 등에 관한 사항을 개인정보 보호책임자 및 담당부서로 문의하실 수 있습니다. COCS(‘http://cocs.no-ip.biz’이하 ‘COCS) 은(는) 정보주체의 문의에 대해 지체 없이 답변 및 처리해드릴 것입니다.



8. 개인정보 처리방침 변경

① 이 개인정보처리방침은 시행일로부터 적용되며, 법령 및 방침에 따른 변경내용의 추가, 삭제 및 정정이 있는 경우에는 변경사항의 시행 7일 전부터 공지사항을 통하여 고지할 것입니다.
					</pre>
         		</div>
         	</div>
          	
    <!-- bottom 섹션 -->
    <div class="footer">
	  <div class="container">
	    <div class="row">
		  <div class="col-xs-6 col-sm-3">Cocs 회사 내용</div>
		  <div class="col-xs-6 col-sm-3">information</div>
		  <div class="col-xs-6 col-sm-3">facebook</div>
		  <div class="col-xs-6 col-sm-3">twitter</div>
		</div>
	  </div>
	</div>
	</div>
</body>
</html>