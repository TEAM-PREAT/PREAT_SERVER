package com.andes.preat.service.auth;

import com.andes.preat.domain.hatefood.userhatefood.UserHateFoodRepository;
import com.andes.preat.domain.user.User;
import com.andes.preat.domain.user.UserRepository;
import com.andes.preat.domain.user.UserState;
import com.andes.preat.dto.request.auth.UserSignUpRequest;
import com.andes.preat.dto.request.auth.UserSignUpTastyInfoRequest;
import com.andes.preat.dto.response.auth.LoginResponse;
import com.andes.preat.dto.response.auth.NicknameCheckResponse;
import com.andes.preat.dto.response.auth.kakao.KakaoProfileResponse;
import com.andes.preat.exception.badRequest.NotFoundUserException;
import com.andes.preat.service.auth.jwt.JwtProvider;
import com.andes.preat.service.user.UserService;
import com.andes.preat.service.userhatefood.UserHateFoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final KakaoAuthClient kakaoAuthClient;
    private final UserRepository userRepository;
    private final UserHateFoodService userHateFoodService;
    private final JwtProvider jwtProvider;
    @Transactional
    public LoginResponse loginUser(final String code) {
        System.out.println("code = " + code);
        // kakao 조회
        String accessToken = "Bearer " + code;
        KakaoProfileResponse profileInfo = kakaoAuthClient.getProfileInfo(accessToken, "application/x-www/form-urlencoded");
        // 내 db 에서 확인
        boolean isNewUser = !checkUserExistAndRegistered(profileInfo);
        // 불린 값
        System.out.println("profileInfo = " + profileInfo.toString());
        final User loggedInUser = addOrUpdateMember(profileInfo);
        // jwt 발급
        String applicationAccessToken = jwtProvider.createAccessToken(loggedInUser.getId());
        return LoginResponse.from(isNewUser, applicationAccessToken);
    }
    @Transactional
    public void signUp(final Long userId, final UserSignUpRequest request) {
        User foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException());
        updateUserInfo(foundUser, request);
        userHateFoodService.saveUserHateFoods(userId, request.getHateFoods());
    }

    private void updateUserInfo(User foundUser, UserSignUpRequest request) {
        foundUser.updateNickname(request.getNickname());
        foundUser.updateTastyInfo(UserSignUpTastyInfoRequest.from(request));
        foundUser.updateUserToRegistered();
    }

    private User addOrUpdateMember(final KakaoProfileResponse kakaoProfileResponse) {
        final User requestedUser = kakaoProfileResponse.toUser();
        final User user = userRepository.findByEmail(kakaoProfileResponse.getKakaoAccount().getEmail())
                .orElseGet(() -> userRepository.save(requestedUser));
        user.update(requestedUser);
        return user;
    }
    public boolean checkUserExistAndRegistered(final KakaoProfileResponse kakaoProfileResponse) {
        if (!userRepository.existsUserByEmailAndStatus(kakaoProfileResponse
                .getKakaoAccount()
                .getEmail(), UserState.COMPLETE)) {
            return false;
        }
        return true;
    }
    public NicknameCheckResponse checkNicknameExist(final String requestNickname) {
        return NicknameCheckResponse.from(!checkUserExist(requestNickname));
    }
    public boolean checkUserExist(final String requestNickname) {
        if (!userRepository.existsUserByNickname(requestNickname)) {
            return false;
        }
        return true;
    }
}