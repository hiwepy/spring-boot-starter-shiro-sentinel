package org.apache.shiro.spring.boot;

import java.util.List;

import org.apache.shiro.spring.boot.sentinel.web.filter.SentinelOriginFilter;
import org.apache.shiro.spring.boot.sentinel.web.filter.SentinelTotalFilter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;

@Configuration
@ConditionalOnClass(com.alibaba.csp.sentinel.SphU.class)
@ConditionalOnProperty(prefix = ShiroSentinelProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties(ShiroSentinelProperties.class)
public class ShiroSentinelAutoConfiguration implements InitializingBean  {
	
	@Bean("origin-sentinel")
    protected FilterRegistrationBean<SentinelOriginFilter> commonFilter() throws Exception {
        FilterRegistrationBean<SentinelOriginFilter> registration = new FilterRegistrationBean<SentinelOriginFilter>();
        registration.setFilter(new SentinelOriginFilter());
        registration.setEnabled(false); 
        return registration;
    }
	
	@Bean("total-sentinel")
    protected FilterRegistrationBean<SentinelTotalFilter> commonTotalFilter() throws Exception {
        FilterRegistrationBean<SentinelTotalFilter> registration = new FilterRegistrationBean<SentinelTotalFilter>();
        registration.setFilter(new SentinelTotalFilter());
	    registration.setEnabled(false); 
	    return registration;
    }
	
	@Autowired(required = false)
	private List<AuthorityRule> authorityRules;
	@Autowired(required = false)
	private List<FlowRule> flowRules;
	@Autowired(required = false)
	private List<DegradeRule> degradeRules;
	@Autowired
	private ShiroSentinelProperties shiroSentinelProperties;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if(!CollectionUtils.isEmpty(authorityRules)) {
			authorityRules.addAll(shiroSentinelProperties.getAuthorityRules());
			AuthorityRuleManager.loadRules(authorityRules); // 修改鉴权规则
		}
		if(!CollectionUtils.isEmpty(flowRules)) {
			flowRules.addAll(shiroSentinelProperties.getFlowRules());
			FlowRuleManager.loadRules(flowRules);// 修改流控规则
		}
		if(!CollectionUtils.isEmpty(degradeRules)) {
			degradeRules.addAll(shiroSentinelProperties.getDegradeRules());
			DegradeRuleManager.loadRules(degradeRules); // 修改降级规则
		}
	}
	
}
 