package com.example.task_back.service;

import com.example.task_back.dto.PrayerDto;
import com.example.task_back.entity.Prayer;
import com.example.task_back.entity.User;
import com.example.task_back.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PrayerService {

    private final PrayerRepository prayerRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;
    private final AiService aiService;
    private final LikesRepository likesRepository;

    @Autowired
    public PrayerService(PrayerRepository prayerRepository, UserRepository userRepository,
                         GroupRepository groupRepository, UserGroupRepository userGroupRepository,
                         AiService aiService, LikesRepository likesRepository) {
        this.prayerRepository = prayerRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.userGroupRepository = userGroupRepository;
        this.aiService = aiService;
        this.likesRepository = likesRepository;
    }

    public List<PrayerDto> findPrayer(String username) {
        return prayerRepository.findByUserUsernameAndDeletedFalseOrderByIdDesc(username).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<PrayerDto> findPrayerById(Long id){
        return prayerRepository.findById(id)
                .map(this::convertToDTO);
    }

    public PrayerDto savePrayer(PrayerDto prayerDto){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        Prayer prayer = new Prayer();
        prayer.setTitle(prayerDto.getTitle());
        prayer.setContent(prayerDto.getContent());
        prayer.setTimeOfPrayer(LocalDateTime.now());
        prayer.setUser(user);
        prayer.setIsPublic(prayerDto.getIsPublic());
        String category = aiService.analysisPrayerCategory(prayerDto.getContent());
        prayer.setCategory(category);
        List<String> keyword = aiService.analysisPrayerKeywords(prayerDto.getContent());
        prayer.setKeywords(keyword);
        Prayer savedPrayer = prayerRepository.save(prayer);
        return convertToDTO(savedPrayer);
    }

    public String updatePrayer(Long id, PrayerDto prayerDto)throws IllegalArgumentException{
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        System.out.println(prayerDto.toString());

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();
        if(!username.equals(prayerDto.getUsername()) && !role.equals("ROLE_ADMIN")){
            System.out.println("다른 사용자의 게시글을 수정할 수 없습니다.");
            throw new IllegalArgumentException("다른 사용자의 게시글을 수정할 수 없습니다.");
        }
        User user = userRepository.findByUsername(prayerDto.getUsername());
        prayerRepository.findById(id).map(prayer -> {
            prayer.setTitle(prayerDto.getTitle());
            prayer.setContent(prayerDto.getContent());
            prayer.setIsPublic(prayerDto.getIsPublic());
            String category = aiService.analysisPrayerCategory(prayerDto.getContent());
            prayer.setCategory(category);
            List<String> keyword = aiService.analysisPrayerKeywords(prayerDto.getContent());
            prayer.setKeywords(keyword);
            Prayer updatedUser = prayerRepository.save(prayer);
            return convertToDTO(updatedUser);
        });
        return "정상적으로 수정되었습니다.";
    }

    public String deletePrayerById(Long id, PrayerDto prayerDto)throws IllegalArgumentException{
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();
        if(!username.equals(prayerDto.getUsername()) && !role.equals("ROLE_ADMIN")){
            System.out.println("다른 사용자의 게시글을 삭제할 수 없습니다.");
            throw new IllegalArgumentException("다른 사용자의 게시글을 삭제할 수 없습니다.");
        }
        Prayer prayer = prayerRepository.findById(id).orElseThrow(() -> new NullPointerException("Prayer not found"));
        prayer.setDeleted(true);
        Prayer updatedUser = prayerRepository.save(prayer);
        convertToDTO(updatedUser);
        return "정상적으로 삭제되었습니다.";
    }

    public List<PrayerDto> getGroupPrayers(String groupName) {
        List<Long> groupIdList = groupRepository.findIdByGroupName(groupName);
        List<Long> userIdList = userGroupRepository.findUserIdIn(groupIdList);
        return prayerRepository.findLatestPrayerForEachUser(userIdList).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PrayerDto convertToDTO(Prayer prayer) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        PrayerDto prayerDto = new PrayerDto();
        prayerDto.setId(prayer.getId());
        prayerDto.setTitle(prayer.getTitle());
        prayerDto.setContent(prayer.getContent());
        prayerDto.setTimeOfPrayer(prayer.getTimeOfPrayer());
        prayerDto.setTop(prayer.getTimeOfPrayer().format(formatter));
        prayerDto.setUsername(prayer.getUser().getUsername());
        prayerDto.setUserNickname(prayer.getUser().getNickname());
        prayerDto.setIsPublic(prayer.getIsPublic());
        prayerDto.setLikesCount(likesRepository.countByPrayer(prayer));
        return prayerDto;
    }

    public List<PrayerDto> getPrayerContainsKeyword(String keyword) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return prayerRepository.findByUser_UsernameAndDeletedFalseAndKeywordsContaining(username, keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PrayerDto> findAllUserPrayer(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return prayerRepository.findAllByOrderByTimeOfPrayerDesc(pageable).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Integer getAllUserPrayerCount() {
        return (int) prayerRepository.count();
    }

    public List<PrayerDto> findMyPrayerList(String id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return prayerRepository.findAllByUserUsernameAndDeletedFalseOrderByTimeOfPrayerDesc(id, pageable).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Integer getMyPrayerCount(String id) {
        return prayerRepository.countByUserUsernameAndDeletedFalse(id);
    }

    public Integer getGroupPrayerCount(String groupName) {
        List<Long> groupIdList = groupRepository.findIdByGroupName(groupName);
        List<Long> userIdList = userGroupRepository.findUserIdIn(groupIdList);
        return prayerRepository.countPrayerForEachUser(userIdList);
    }

    public List<PrayerDto> getGroupPrayerList(String groupName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<Long> groupIdList = groupRepository.findIdByGroupName(groupName);
        List<Long> userIdList = userGroupRepository.findUserIdIn(groupIdList);

        return prayerRepository.findPrayerForEachUser(userIdList, pageable).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public List<PrayerDto> getRecommendPrayer() {
        Map<String, Integer> myPrayerKeywords =  aiService.getPrayerKeywords();
        Set<String> keys = myPrayerKeywords.keySet();
        List<String> keyList = new ArrayList<>(keys);
        List<PrayerDto> prayers = new ArrayList<>(3);
        Collections.shuffle(keyList);

        Set<String> randomKeywords = new HashSet<>(keyList.subList(0, Math.min(3, keyList.size())));
        for(String s : randomKeywords){
            PrayerDto prayerDto = new PrayerDto();
            prayerDto.setTitle(s+" 관련 기도문");
            prayerDto.setContent(aiService.generatePrayer(s+"을(를) 위한 기도문 작성해줘"));
            prayers.add(prayerDto);
        }
        return prayers;
    }
}
