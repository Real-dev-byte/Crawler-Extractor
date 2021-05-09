package com.crawlerAndExtractor.project.Response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseResponse {
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String displayMessge;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String htmlDocument;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private List<GetProdDetailResponse> getProdDetailResponseList;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private long responseTime;

}
