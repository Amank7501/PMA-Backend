package com.rasp.app.config;

import java.util.HashMap;
import java.util.Map;

public class RoleStrategyRegistry {
    private  final Map<String, RoleStrategy> strategyMap = new HashMap<>();
    private final RoleStrategy defaultStrategy = new DefultRoleStrategy();
    private  final OwnerStrategy ownerStrategy= new OwnerStrategy();
    private  final RoleStrategyImplementation roleStrategyImplementation= new RoleStrategyImplementation();
    private  final SuperAdminStrategy superAdminStrategy= new SuperAdminStrategy();
    private final UserStrategy userStrategy=new UserStrategy();

    // Singleton instance
    private static RoleStrategyRegistry instance;

    public RoleStrategyRegistry() {
        strategyMap.put("SUPER_ADMIN",superAdminStrategy);
        strategyMap.put("DefultRoleStrategy", defaultStrategy);
        strategyMap.put("OWNER",ownerStrategy);
        strategyMap.put("TEAM_MEMBER",roleStrategyImplementation);
        strategyMap.put("VIEWER",roleStrategyImplementation);
        strategyMap.put("DEFAULT_USER",userStrategy);

    }
    public RoleStrategy getStrategy(String role) {
        if (role == null) return defaultStrategy;
        return strategyMap.getOrDefault(role.toUpperCase(), defaultStrategy);
    }
    // Public method to get singleton instance
    public static synchronized RoleStrategyRegistry getInstance() {
        if (instance == null) {
            instance = new RoleStrategyRegistry();
        }
        return instance;
    }
}
