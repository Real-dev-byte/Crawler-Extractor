package com.crawlerAndExtractor.project.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


import java.sql.Timestamp;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetProdDetailResponse extends BaseResponse{

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String title;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String offerPrice;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String description;

    @Getter
    @Setter
    private ratingsMap ratingsMap;


    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class  ratingsMap{
        private String overallCount;
        private String star_5;
        private String star_4;
        private String star_3;
        private String star_2;
        private String star_1;
    }

    @Setter
    @Getter
    private List<prices> prices;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class  prices{
        private Timestamp timestamp;
        private String price;
    }
}
