package com.example.task_back.service;

import com.example.task_back.entity.Prayer;
import com.example.task_back.repository.PrayerRepository;
import org.springframework.ai.moderation.*;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiModerationModel;
import org.springframework.ai.openai.OpenAiModerationOptions;
import org.springframework.ai.openai.api.OpenAiModerationApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;


@Service
public class AiService {
    private final PrayerRepository prayerRepository;
    private final OpenAiChatModel chatModel;
    private final OpenAiModerationOptions moderationOptions;
    private final OpenAiModerationApi openAiModerationApi;

    @Autowired
    public AiService(PrayerRepository prayerRepository, OpenAiChatModel chatModel) {
        this.prayerRepository = prayerRepository;
        this.chatModel = chatModel;
        this.moderationOptions = OpenAiModerationOptions.builder()
                .withModel("text-moderation-latest")
                .build();
        this.openAiModerationApi = new OpenAiModerationApi(System.getenv("OPENAI_API_KEY"));
    }

    public String generatePrayer(String message) throws IllegalArgumentException{

        OpenAiModerationModel openAiModerationModel = new OpenAiModerationModel(openAiModerationApi);
        ModerationPrompt moderationPrompt = new ModerationPrompt(message, moderationOptions);
        ModerationResponse response = openAiModerationModel.call(moderationPrompt);
        Moderation moderation = response.getResult().getOutput();

        for(ModerationResult result : moderation.getResults()){
            if(result.isFlagged()){
                System.out.println("isFlagged True");
                throw new IllegalArgumentException("부적절한 내용이 감지되었습니다.");
            }
        }
        String prompt = message + " 사용자에게 기도문을 작성해주는 서비스를 제공할거니까 기도문으로만 대답해. 부정적인 내용,욕설,폭력적인 내용은 포함하지말고 기도문만 보여줘. 기도문은 10줄이내로 작성해";
        return chatModel.call(prompt);
    }

    public String analysisPrayerCategory(String content){
        String prompt = createCategoryBatchPrompt(content);
        System.out.println(prompt);
        String ans = chatModel.call(prompt);
        System.out.println("카테고리 분석 결과: "+ans);
        return ans;
    }

    private String createCategoryBatchPrompt(String content) {
        StringBuilder promptBuilder = new StringBuilder("다음 기도제목을 적절한 카테고리로 분류해서 카테고리를 한 단어로만 개행없이 한 줄로 콤마로 구분해서 대답해.\n");
        promptBuilder.append("카테고리는 '감사', '중보', '회개', '간구', '인도', '치유' 중 하나여야해:\n\n");
        promptBuilder.append(String.format("%d 기도제목: \"%s\"\n", 1, content).replaceAll("\n",""));

        return promptBuilder.toString();
    }


    public Integer getPrayerForWeek(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        return prayerRepository.countByUserUsernameAndTimeOfPrayerBetween(username,startOfMonth,endOfMonth);

    }

    public List<String> analysisPrayerKeywords(String content) {
        String prompt = createKeywordBatchPrompt(content);
        String ans = chatModel.call(prompt);
        List<String> response = new ArrayList<>(Arrays.asList(ans.replaceAll(" ","").split(",")));
        for(String s : response){
            System.out.println("키워드 결과: "+s);
        }
        return response;
    }

    private String createKeywordBatchPrompt(String content) {
        StringBuilder promptBuilder = new StringBuilder("다음 기도에서 각 기도별로 적절한 키워드 기도당 3개를 명사 위주로 단어로만 대답해. 대답은 개행없이 한 줄로하고 각 기도도 콤마로 구분해서 대답해.\n");
            promptBuilder.append(String.format("%d 기도제목: \"%s\"\n", 1, content).replaceAll("\n",""));

        return promptBuilder.toString();
    }


    public Map<String, Integer> getPrayerCategory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        List<Prayer> prayerList = prayerRepository.findByUserUsernameAndTimeOfPrayerBetween(username,startOfMonth,endOfMonth);
        Map<String,Integer> map = new HashMap<String,Integer>();
        for(Prayer prayer : prayerList){
            if(map.containsKey(prayer.getCategory())){
                map.put(prayer.getCategory(),map.get(prayer.getCategory())+1);
            }else{
                map.put(prayer.getCategory(),1);
            }
        }
        return map;
    }

    public Map<String, Integer> getPrayerKeywords() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        List<Prayer> prayerList = prayerRepository.findByUserUsernameAndTimeOfPrayerBetween(username,startOfMonth,endOfMonth);
        Map<String,Integer> map = new HashMap<String,Integer>();
        for(Prayer prayer: prayerList){
            for(String keyword: prayer.getKeywords()){
                if(map.containsKey(keyword)){
                    map.put(keyword,map.get(keyword)+1);
                }else{
                    map.put(keyword,1);
                }
            }
        }
        return map;
    }
}
