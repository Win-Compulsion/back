package com.lkkp.runwith.config.auth;

import com.lkkp.runwith.member.Member;
import com.lkkp.runwith.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ApiRestController {

    private final MemberRepository memberRepository;

    @Value("${google.auth.url}")
    private String googleAuthUrl;

    @Value("${google.login.url}")
    private String googleLoginUrl;

    @Value("${google.redirect.url}")
    private String googleRedirectUrl;

    @Value("${google.client.id}")
    private String googleClientId;

    @Value("${google.secret}")
    private String googleClientSecret;


    // 구글 로그인창 호출
    // http://localhost:8080/login/
    @GetMapping(value = "/login")
    public ResponseEntity<?> getGoogleAuthUrl(HttpServletRequest request) throws Exception {

        String reqUrl = googleLoginUrl + "/o/oauth2/v2/auth?client_id=" + googleClientId + "&redirect_uri=" + googleRedirectUrl
                + "&response_type=code&scope=email%20profile%20openid&access_type=offline";

        log.info("myLog-LoginUrl : {}",googleLoginUrl);
        log.info("myLog-ClientId : {}",googleClientId);
        log.info("myLog-RedirectUrl : {}",googleRedirectUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(reqUrl));

        //1.reqUrl 구글로그인 창을 띄우고, 로그인 후 /login/oauth_google_check 으로 리다이렉션하게 한다.
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

    // 구글에서 리다이렉션
    @GetMapping(value = "/login/oauth_google_check")
    public String oauth_google_check(HttpServletRequest request,
                                     @RequestParam(value = "code") String authCode,
                                     HttpServletResponse response) throws Exception{
        String googleUid = null;

        //2.구글에 등록된 설정정보를 보내어 약속된 토큰을 받위한 객체 생성
        GoogleOAuthRequest googleOAuthRequest = GoogleOAuthRequest
                .builder()
                .clientId(googleClientId)
                .clientSecret(googleClientSecret)
                .code(authCode)
                .redirectUri(googleRedirectUrl)
                .grantType("authorization_code")
                .build();

        RestTemplate restTemplate = new RestTemplate();

        //3.토큰요청을 한다.
        ResponseEntity<GoogleLoginResponse> apiResponse = restTemplate.postForEntity(googleAuthUrl + "/token", googleOAuthRequest, GoogleLoginResponse.class);
        //4.받은 토큰을 토큰객체에 저장
        GoogleLoginResponse googleLoginResponse = apiResponse.getBody();

        log.info("responseBody {}",googleLoginResponse.toString());


        String googleToken = googleLoginResponse.getId_token();

        //5.받은 토큰을 구글에 보내 유저정보를 얻는다.
        String requestUrl = UriComponentsBuilder.fromHttpUrl(googleAuthUrl + "/tokeninfo").queryParam("id_token",googleToken).toUriString();

        //6.허가된 토큰의 유저정보를 결과로 받는다.
        String resultJson = restTemplate.getForObject(requestUrl, String.class);

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(resultJson);

        String email = (String) jsonObject.get("email");
        String name = (String) jsonObject.get("name");
        String picture = (String) jsonObject.get("picture");


        Optional<Member> member = memberRepository.findByEmail(email);
        if(member.isPresent()) {
            // 이미 존재하는 회원, 따로 처리할 필요없고
            return "존재하는 회원";
        }
        else{
            return "존재하지 않는 회원";
            // 존재하지 않는 회원, 추가로 입력 받아야 함(성별, 이름)
            // member.builder() 나 memberService.memjoin(Dto) 로 해야될듯

        }

    }
}
