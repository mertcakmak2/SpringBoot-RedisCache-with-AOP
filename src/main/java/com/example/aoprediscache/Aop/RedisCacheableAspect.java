package com.example.aoprediscache.Aop;

import com.example.aoprediscache.Model.Product;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RedisCacheableAspect {

    private final static Logger logger = LoggerFactory.getLogger(RedisCacheableAspect.class);
    private static final ExpressionParser expressionParser = new SpelExpressionParser();
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisCacheableAspect(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Pointcut("@annotation(redisCacheable)")
    public void RedisCacheablePointcut(RedisCacheable redisCacheable) {
    }

    private StandardEvaluationContext getContextContainingArguments(ProceedingJoinPoint joinPoint) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        String[] parameterNames = codeSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }
        return context;
    }

    private String getCacheKeyFromAnnotationKeyValue(StandardEvaluationContext context, String key) {
        Expression expression = expressionParser.parseExpression(key);
        return (String) expression.getValue(context);
    }

    // For Delete Method
    @After("execution(public * com.example.aoprediscache.Controller.Rest.ProductController.deleteProductById(..))")
    public void afterDelete(JoinPoint joinPoint) {

        int id = (int) joinPoint.getArgs()[0];
        String hash_key = "products_" + id;

        if (redisTemplate.hasKey(hash_key)) redisTemplate.delete(hash_key);

        System.out.println("After Delete Method");
    }

    // For Save Method
    @After("execution(public * com.example.aoprediscache.Controller.Rest.ProductController.saveProduct(..))")
    public void afterSave(JoinPoint joinPoint) {

        logger.info("args={}",joinPoint.getArgs());
        Product product = (Product) joinPoint.getArgs()[0];

        String hash_key = "products_"+product.getId();
        if(!redisTemplate.hasKey(hash_key)) redisTemplate.opsForValue().set(hash_key, product);

        System.out.println("After Save Method");
    }

    @Around("RedisCacheablePointcut(redisCacheable)")
    public Object cacheMethod(ProceedingJoinPoint joinPoint, RedisCacheable redisCacheable) throws Throwable {
        String key = redisCacheable.key();

        StandardEvaluationContext context = getContextContainingArguments(joinPoint);
        String cacheKey = getCacheKeyFromAnnotationKeyValue(context, key);
        logger.info("HASH_KEY: {}", cacheKey);

        Object result;
        if (redisTemplate.hasKey(cacheKey)) {
            result = redisTemplate.opsForValue().get(cacheKey);
            logger.info("Read to cache ..." + result.toString());
        } else {
            result = joinPoint.proceed();
            logger.info("Caching...");
            if(result != null) redisTemplate.opsForValue().set(cacheKey, result);
        }
        logger.info("Result: {}", result);
        return result;
    }
}
