package com.andes.preat.service.user;

import com.andes.preat.domain.follow.FollowRepository;
import com.andes.preat.domain.review.Review;
import com.andes.preat.domain.review.ReviewRepository;
import com.andes.preat.domain.user.User;
import com.andes.preat.domain.user.UserRepository;
import com.andes.preat.dto.response.user.CategoryStaticsResponse;
import com.andes.preat.dto.response.user.FollowUserInfoResponse;
import com.andes.preat.dto.response.user.FollowsInfoResponse;
import com.andes.preat.dto.response.user.LoggedInUserInfoResponse;
import com.andes.preat.exception.badRequest.AlreadyFollowingException;
import com.andes.preat.exception.badRequest.DuplicateNicknameException;
import com.andes.preat.exception.badRequest.NotFoundUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final ReviewRepository reviewRepository;

    // TODO : 내정보 확인하기
    @Transactional
    public void saveUserForTest() {
        User newUser1 = User.newInstance("abc@email.com", "user1", "male", "20~29", "1111");
        userRepository.save(newUser1);
        System.out.println("newUser1.getId() = " + newUser1.getId());
        User newUser2 = User.newInstance("abc2@email.com", "user2", "male", "20~29", "1111");
        userRepository.save(newUser2);
        System.out.println("newUser2.getId() = " + newUser2.getId());
        User newUser3 = User.newInstance("abc3@email.com", "user3", "male", "20~29", "1111");
        userRepository.save(newUser3);
        System.out.println("newUser3.getId() = " + newUser3.getId());
    }

    public LoggedInUserInfoResponse getLoggedInUserInfo(final Long userId) {
        User foundUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundUserException());
//        calculateCategoryStatics(foundUser);
        return LoggedInUserInfoResponse.from(foundUser);
    }
//    TODO: 유저 통계
//    private void calculateCategoryStatics(User foundUser) {
//        List<CategoryStaticsResponse> mostVisitedCategory = reviewRepository.findMostVisitedCategory(foundUser.getId());
//        System.out.println("mostVisitedCategory = " + mostVisitedCategory);
//        mostVisitedCategory.stream().forEach(c -> System.out.println("c.getCount() = " + c.getCount()));
//    }

    @Transactional
    public LoggedInUserInfoResponse updateLoggedInUserNickname(Long userId, String nickname) {
        validateNicknameDuplicate(nickname);
        User foundUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundUserException());
        foundUser.updateNickname(nickname);
        return LoggedInUserInfoResponse.from(foundUser);
    }

    private void validateNicknameDuplicate(String nickname) {
        if (userRepository.existsUserByNickname(nickname)) {
            throw new DuplicateNicknameException();
        }
    }

    // TODO: 유저 닉네임으로 검색
    public LoggedInUserInfoResponse getUserByNickname(final String requestNickname) {
        User foundUser = userRepository.findByNickname(requestNickname).orElseThrow(() -> new NotFoundUserException());
        return LoggedInUserInfoResponse.from(foundUser);
    }
    // TODO: 내 팔로우 목록 가져오기
    public FollowsInfoResponse getUserFollows(final Long userId) {
        User foundUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundUserException());
        if (!followRepository.existsByFollower(foundUser)) {
            return FollowsInfoResponse.from(foundUser, Collections.emptyList());
        }
        List<FollowUserInfoResponse> followUserInfoResponses = followRepository.findAllByUserId(foundUser);
        return FollowsInfoResponse.from(foundUser, followUserInfoResponses);
    }
}
