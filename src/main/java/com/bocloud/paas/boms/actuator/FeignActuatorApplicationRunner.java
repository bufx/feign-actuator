package com.bocloud.paas.boms.actuator;

import com.bocloud.paas.boms.qo.ApiRequestMappingSaveQo;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

/**
 * feignClient调用收集初始化类
 * @author: buubiu
 * @create: 2021/9/8 16:38
 */
@Component
public class FeignActuatorApplicationRunner implements ApplicationRunner {

	private final Logger logger = LoggerFactory.getLogger(FeignActuatorApplicationRunner.class);

	@Autowired
	private WebApplicationContext webApplicationConnect;

	@Value("${collect-server:}")
	private String apiManageIp;

	@Value("${spring.application.name}")
	private String applicationName;

	private static final String REPORT_TYPE = "SDK";

	@Override
	public void run(ApplicationArguments args) {
		loadFeignClient();
	}

	/**
	 * 搜集注解为@feignClient的所有类
	 * @author: buubiu
	 * @create: 2021/9/8 16:41
	 */
	private void loadFeignClient(){
		if (apiManageIp.isEmpty()) {
			logger.error("配置项[collect-server]为空，请确认！");
			return;
		}
		logger.info("collect-server: {}", apiManageIp);
		String url = apiManageIp + "/apirequestmapping";
		try {
			String[] beanNames = BeanFactoryUtils.beanNamesForAnnotationIncludingAncestors(webApplicationConnect,
				FeignClient.class);
			Class feignClientBean;
			List<ApiRequestMappingSaveQo> apiRequestMappingSaveQoList = new ArrayList<>();
			for (String beanName : beanNames) {
				feignClientBean = Class.forName(beanName);
				FeignClient feignClientAnnotation = (FeignClient) feignClientBean.getAnnotation(FeignClient.class);
				Method[] methods = feignClientBean.getMethods();
				for (Method method : methods) {
					ApiRequestMappingSaveQo apiRequestMappingSaveQo = new ApiRequestMappingSaveQo();
					setApiUrlAndApiUrlRequestType(method, apiRequestMappingSaveQo);
					apiRequestMappingSaveQo.setApiServiceEnName(getApiServiceEnName(feignClientAnnotation));
					apiRequestMappingSaveQo.setInvokeApiServiceEnName(applicationName);
					apiRequestMappingSaveQo.setReportType(REPORT_TYPE);
					apiRequestMappingSaveQoList.add(apiRequestMappingSaveQo);
				}
			}
			RestTemplate restTemplate = new RestTemplate();
			String result = restTemplate.postForObject(url, apiRequestMappingSaveQoList, String.class);
			logger.info("add ApiRequestMapping to api-doc: {}", result);
		} catch (Exception e) {
			logger.error("feign-actuator sdk上报信息失败! 请确认接口地址'{}'是否可用; 异常堆栈信息: \n{}", url, ExceptionUtils.getFullStackTrace(e));
		}
	}

	private String getApiServiceEnName(FeignClient feignClientAnnotation) {
		String classAnnotationName = feignClientAnnotation.name();
		String classAnnotationValue = feignClientAnnotation.value();
		String classAnnotationUrl = feignClientAnnotation.url();
		if (StringUtils.isEmpty(classAnnotationName)) {
			return StringUtils.isEmpty(classAnnotationValue) ? classAnnotationUrl : classAnnotationValue;
		} else {
			return classAnnotationName;
		}
	}

	private void setApiUrlAndApiUrlRequestType(Method method, ApiRequestMappingSaveQo apiRequestMappingSaveQo) {
		Annotation annotation = method.getAnnotations()[0];
		Class<? extends Annotation> aClass = annotation.annotationType();
		String mappingValue = "";
		String mappingType = "";
		if (RequestMapping.class.equals(aClass)) {
			RequestMapping methodAnnotation = method.getAnnotation(RequestMapping.class);
			mappingValue = ArrayUtils.isEmpty(methodAnnotation.value()) ? "" : methodAnnotation.value()[0];
			mappingType = ArrayUtils.isEmpty(methodAnnotation.method()) ? "" : methodAnnotation.method()[0].name();
		} else if (PostMapping.class.equals(aClass)) {
			PostMapping methodAnnotation = method.getAnnotation(PostMapping.class);
			mappingValue = ArrayUtils.isEmpty(methodAnnotation.value()) ? "" : methodAnnotation.value()[0];
			mappingType = RequestMethod.POST.name();
		} else if (GetMapping.class.equals(aClass)) {
			GetMapping methodAnnotation = method.getAnnotation(GetMapping.class);
			mappingValue = ArrayUtils.isEmpty(methodAnnotation.value()) ? "" : methodAnnotation.value()[0];
			mappingType = RequestMethod.GET.name();
		} else if (PutMapping.class.equals(aClass)) {
			PutMapping methodAnnotation = method.getAnnotation(PutMapping.class);
			mappingValue = ArrayUtils.isEmpty(methodAnnotation.value()) ? "" : methodAnnotation.value()[0];
			mappingType = RequestMethod.PUT.name();
		} else if (DeleteMapping.class.equals(aClass)) {
			DeleteMapping methodAnnotation = method.getAnnotation(DeleteMapping.class);
			mappingValue = ArrayUtils.isEmpty(methodAnnotation.value()) ? "" : methodAnnotation.value()[0];
			mappingType = RequestMethod.DELETE.name();
		} else if (PatchMapping.class.equals(aClass)) {
			PatchMapping methodAnnotation = method.getAnnotation(PatchMapping.class);
			mappingValue = ArrayUtils.isEmpty(methodAnnotation.value()) ? "" : methodAnnotation.value()[0];
			mappingType = RequestMethod.PATCH.name();
		}
		apiRequestMappingSaveQo.setApiUrl(mappingValue);
		apiRequestMappingSaveQo.setApiUrlRequestType(mappingType);
	}

}
