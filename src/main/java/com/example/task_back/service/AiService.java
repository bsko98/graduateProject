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
import java.util.stream.Collectors;


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
        String prompt = message + " 사용자에게 기도문을 작성해주는 서비스를 제공할거니까 기도문으로만 대답해. 부정적인 내용,욕설,폭력적인 내용은 포함하지말고 기도문만 보여줘. 기도문은 5줄이내로 작성해";
        return chatModel.call(prompt).replaceAll("\\n","");
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


    public Integer getCountOfPrayerForMonth(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        return prayerRepository.countByUserUsernameAndDeletedFalseAndTimeOfPrayerBetween(username,startOfMonth,endOfMonth);
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
        StringBuilder promptBuilder = new StringBuilder("다음 기도에서 각 기도별로 적절한 키워드 3개만 명사 위주로 단어로만 대답해. 대답은 개행없이 한 줄로하고 각 기도도 콤마로 구분해서 대답해.\n");
        promptBuilder.append(String.format("%d 기도제목: \"%s\"\n", 1, content).replaceAll("\n",""));
        return promptBuilder.toString();
    }


    public Map<String, Integer> getPrayerCategory() {
        List<Prayer> prayerList = getPrayerInMonth();
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
        List<Prayer> prayerList = getPrayerInMonth();
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

    public List<Prayer> getPrayerInMonth(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("userName: "+username);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        return prayerRepository.findByUserUsernameAndDeletedFalseAndTimeOfPrayerBetween(username,startOfMonth,endOfMonth);
    }


    public String getCategoryComment() {
        List<Prayer> prayerList = getPrayerInMonth();
        Map<String,Integer> map = new HashMap<String,Integer>();
        for(Prayer prayer : prayerList){
            if(map.containsKey(prayer.getCategory())){
                map.put(prayer.getCategory(),map.get(prayer.getCategory())+1);
            }else{
                map.put(prayer.getCategory(),1);
            }
        }
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.<String, Integer>comparingByValue().reversed());
        Map.Entry<String, Integer> maxEntry = list.get(0);
        Map.Entry<String, Integer> minEntry = list.get(list.size() - 1);
        if(list.isEmpty()){
            return "아직 기도문이 없습니다.";
        }else if(Objects.equals(maxEntry.getValue(), minEntry.getValue())){
            return "카테고리들이 고르게 분포되어있습니다.";
        }
        return switch (maxEntry.getKey()) {
            case "감사" ->
                    "감사기도가 많다는건 좋은 일입니다. 감사 기도와 함께 알게 모르게 지은 죄들에 하나님의 자비를 구하며 회개의 기도를 드리는 시간을 가져보거나 가까운 이웃이나 가족을 위해 기도해도 좋을거같아요!";
            case "중보" ->
                    "자신뿐만 아니라 타인까지 생각하는 기도 너무 좋습니다! 이젠 나 자신을 위해 하나님께 자신의 소망과 목표를 간구하며 올려보세요. 아니면 오늘 하루 감사했던걸 기도해보는건 어떨까요?";
            case "회개" ->
                    "옳지않은 일들로 인해 마음이 많이 불편하셨나요? 앞으로는 옳은 선택을 할 수 있도록 주께서 인도해주시길 기도하시고 불편할 마음을 위해 치유의 기도를 해보세요";
            case "간구" ->
                    "간절히 구하면 주께서 주실것입니다. 원하는걸 얻는 과정을 위해 주께서 인도하심을 기도하고, 원하는걸 얻고나면 감사기도 하는것도 잊지마세요";
            case "인도" ->
                    "여러가지 일에 대한 선택 때문에 고민이 많으시겠어요. 원하는 것을 간구하면 그에 맞게 인도해주실거에요. ";
            case "치유" ->
                    "하나님께서 당신의 고통을 아십니다. 이 어려운 시기에 당신을 지키시고 함께하실 것입니다.건강의 온전한 회복을 위한 간구 기도와 하나님께서 치유하시는 과정에서 느낀 작은 변화와 회복에 대해 감사해 보세요.";
            default -> "";
        };
    }
}
