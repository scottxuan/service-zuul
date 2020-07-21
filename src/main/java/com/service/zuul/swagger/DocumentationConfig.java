package com.service.zuul.swagger;

import com.google.common.collect.Lists;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.List;

/**
 * @author : pc
 * @date : 2020/7/21
 */
@Component
@Primary
public class DocumentationConfig implements SwaggerResourcesProvider {

    private final RouteLocator routeLocator;

    public DocumentationConfig(RouteLocator routeLocator) {
        this.routeLocator = routeLocator;
    }

    // 自动获取系统配置的路由资源集合
    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = Lists.newArrayList();
        List<Route> routes = routeLocator.getRoutes();
        routes.forEach(route -> {
            resources.add(swaggerResource(route.getId(),route.getFullPath().replace("**", "v2/api-docs"),"2.0"));
        });

        return resources;
    }

    // 获取对应的路由资源
    private SwaggerResource swaggerResource(String name, String location, String version) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion(version);
        return swaggerResource;
    }
}
