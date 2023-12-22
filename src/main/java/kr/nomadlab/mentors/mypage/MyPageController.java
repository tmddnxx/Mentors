package kr.nomadlab.mentors.mypage;



import kr.nomadlab.mentors.common.PageRequestDTO;
import kr.nomadlab.mentors.common.PageResponseDTO;
import kr.nomadlab.mentors.main.dto.MainDTO;
import kr.nomadlab.mentors.main.dto.MentorReviewDTO;
import kr.nomadlab.mentors.main.service.MainService;
import kr.nomadlab.mentors.main.service.MentorReviewService;
import kr.nomadlab.mentors.member.dto.MemberDTO;
import kr.nomadlab.mentors.member.dto.MemberSecurityDTO;
import kr.nomadlab.mentors.member.dto.MenteeDTO;
import kr.nomadlab.mentors.member.dto.MentorDTO;

import kr.nomadlab.mentors.member.service.MemberService;
import kr.nomadlab.mentors.member.service.MenteeService;
import kr.nomadlab.mentors.member.service.MentorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/mypage")
@Log4j2
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;
    private final MentorService mentorService;
    private final MenteeService menteeService;

    private final MentorReviewService mentorReviewService;
    private final MainService mainService;

    /*멘토 프로필 시작*/
    @GetMapping("/mentorProfile")
    public void mentorMyPageGET(@AuthenticationPrincipal MemberSecurityDTO memberSecurityDTO, Model model, String nickname) {
        log.info("mentorProfile GET...");

        MemberDTO memberDTO = memberService.getProfileNickname(nickname);
        log.info("memberDTO : " + memberDTO);
        model.addAttribute("memberDTO", memberDTO);

        int reviewCnt = mentorReviewService.mentorReviewCount(memberDTO.getMno());
        model.addAttribute("reviewCnt", reviewCnt);

        int memberRole = memberService.getMemberRole(memberDTO.getMemberId());
        model.addAttribute("memberRole", memberRole);


        if (memberSecurityDTO != null) { // 로그인시 가져올 값
            if (memberSecurityDTO.getNickname().equals(nickname)) { // 로그인하고 해당 프로필의 닉네임이 같으면
                MentorDTO mentorDTO = mentorService.getOne(memberSecurityDTO.getMemberId());
                log.info("mentorDTO : " + mentorDTO);
                model.addAttribute("mentorDTO", mentorDTO);
            } else { // 로그인하고 해당 프로필의 닉네임이 다르면
                MentorDTO mentorDTO = mentorService.getOne(memberDTO.getMemberId());
                model.addAttribute("mentorDTO", mentorDTO);
            }
        } else if (memberSecurityDTO == null) { // 비로그인시 가져올 값
          MentorDTO mentorDTO = mentorService.getOne(memberDTO.getMemberId());
          model.addAttribute("mentorDTO", mentorDTO);
        }


    }
    /*멘토 프로필 끝*/
    /*멘티 프로필 시작*/
    @GetMapping("/menteeProfile")
    public void menteeMyPageGET(@AuthenticationPrincipal MemberSecurityDTO memberSecurityDTO, Model model, String nickname) { // 멘티 마이페이지 GET
        log.info("mentorProfile GET...");

        MemberDTO memberDTO = memberService.getProfileNickname(nickname);
        log.info("memberDTO : " + memberDTO);
        model.addAttribute("memberDTO", memberDTO);

        int reviewCnt = mentorReviewService.menteeReviewCount(memberDTO.getMno());
        model.addAttribute("reviewCnt", reviewCnt);

        int memberRole = memberService.getMemberRole(memberDTO.getMemberId());
        model.addAttribute("memberRole", memberRole);

        if(memberSecurityDTO != null) {
            if (memberSecurityDTO.getNickname().equals(nickname)) {
                MenteeDTO menteeDTO = menteeService.getOne(memberSecurityDTO.getMemberId());
                log.info("menteeDTO : " + menteeDTO);
                model.addAttribute("menteeDTO", menteeDTO);
            } else {
                MenteeDTO menteeDTO = menteeService.getOne(memberDTO.getMemberId());
                model.addAttribute("menteeDTO", menteeDTO);
            }
        } else if (memberSecurityDTO == null) {
            MenteeDTO menteeDTO = menteeService.getOne(memberDTO.getMemberId());
            model.addAttribute("menteeDTO", menteeDTO);
        }

    }

    /*멘티 프로필 끝*/

    /* 멘토 메인 영역*/
    @GetMapping("/mainListTor")// 내가 작성한 멘토링 목록 보기
    public void mainList(Model model, PageRequestDTO pageRequestDTO, @AuthenticationPrincipal MemberSecurityDTO memberSecurityDTO){
        pageRequestDTO.setSize(12);
        PageResponseDTO<MainDTO> mainList = mainService.myPageList(pageRequestDTO, memberSecurityDTO.getMno());
        log.info("end는 ?"+mainList.getEnd());
        model.addAttribute("mainList", mainList);

        MemberDTO memberDTO = memberService.getProfileNickname(memberSecurityDTO.getNickname());
        log.info("memberDTO : " + memberDTO);
        model.addAttribute("memberDTO", memberDTO);

        MentorDTO mentorDTO = mentorService.getOne(memberSecurityDTO.getMemberId());
        log.info("mentorDTO : " + mentorDTO);
        model.addAttribute("mentorDTO", mentorDTO);
    }

    @GetMapping("/mainList/remove") // 내가 작성한 멘토링 삭제
    public String removeMain(Long mbNo){
        mainService.removeOne(mbNo);

        return "redirect:/mypage/mainList";
    }

    @GetMapping("/mainList/modify") // 내가 작성한 멘토링 수정페이지
    public String modifyMain(Model model, Long mbNo, @AuthenticationPrincipal MemberSecurityDTO memberSecurityDTO){
        Long mno = memberSecurityDTO.getMno();
        MainDTO mainDTO = mainService.getBoard(mbNo);

        int mentoringCnt = mainService.mentoringCnt(mno);

        model.addAttribute("mentoringCnt", mentoringCnt);
        model.addAttribute("mainDTO", mainDTO);
        return "/mypage/mainModify";
    }

    @PostMapping("/mainList/modify") // 멘토의 수정하기
    public String modifyMain(MainDTO mainDTO){
        log.info("수정 DTO ? " + mainDTO);
        mainService.modifyBoard(mainDTO);
        return "redirect:/mypage/mainList";
    }
    /* 멘토 메인 영역 끝 */

    /* 멘티 메인 영역*/

    @GetMapping("/mainListTee") // 멘토링 목록
    public void mainListTee(Model model, PageRequestDTO pageRequestDTO, @AuthenticationPrincipal MemberSecurityDTO memberSecurityDTO){
        pageRequestDTO.setSize(12);
        PageResponseDTO<MainDTO> mainListTee = mainService.mainListTee(pageRequestDTO, memberSecurityDTO.getMno());
        log.info("서비스 sort ?"+pageRequestDTO.getSort());
        model.addAttribute("mainList", mainListTee);
        model.addAttribute("sort", pageRequestDTO.getSort());

        MemberDTO memberDTO = memberService.getProfileNickname(memberSecurityDTO.getNickname());
        model.addAttribute("memberDTO", memberDTO);
        MenteeDTO menteeDTO = menteeService.getOne(memberSecurityDTO.getMemberId());
        model.addAttribute("menteeDTO", menteeDTO);
    }

    @PostMapping("/review/write")
    public String write(MentorReviewDTO mentorReviewDTO, PageRequestDTO pageRequestDTO){
        mentorReviewService.register(mentorReviewDTO);
        int page = pageRequestDTO.getPage();
        String sort = pageRequestDTO.getSort();
        return "redirect:/mypage/mainListTee?page="+page+"&size=12&sort="+sort;
    }
    /* 멘티 메인 영역 끝*/
    @GetMapping("/paymentsHistory")
    public String paymentHistory(){
        return "/mypage/paymentsHistory";
    }
}
