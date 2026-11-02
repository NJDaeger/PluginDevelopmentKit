package com.njdaeger.pdk.serviceprovider;

import com.njdaeger.pdk.utils.Pair;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ServiceProvider implements IServiceProvider {

    private final Plugin plugin;
    private final Map<Class<?>, Object> singletons;
    private final Map<Class<?>, Function<IServiceProvider, ?>> generatorFunctions;
    private final String providerName;
    private final IServiceProvider parent;

    public ServiceProvider(Plugin plugin, String providerName, Stack<Pair<Class<?>, Function<IServiceProvider, ?>>> singletons, Map<Class<?>, Function<IServiceProvider, ?>> generatorFunctions, IServiceProvider parent) {
        this.singletons = new ConcurrentHashMap<>();
        this.generatorFunctions = new ConcurrentHashMap<>(generatorFunctions);
        this.plugin = plugin;
        this.providerName = providerName == null || providerName.isBlank() ? "" : providerName + " ";
        this.parent = parent;
        loadSingletons(singletons);
    }

    private void loadSingletons(Stack<Pair<Class<?>, Function<IServiceProvider, ?>>> singletons) {
        var retryStack = new Stack<Pair<Class<?>, Function<IServiceProvider, ?>>>();
        var failures = new LinkedHashMap<Class<?>, Exception>();
        singletons.forEach((pair) -> {
            var intf = pair.getFirst();
            var init = pair.getSecond();
            try {
                var obj = init.apply(this);
                if (obj == null) return; // we can have services that are registered but never actually register an implementation, in which case we hope the caller uses GetService if they want to avoid an error.
                this.singletons.put(intf, obj);
            } catch (Exception e) {
                retryStack.addFirst(pair);
                failures.put(intf, e);
            }
        });
        if (!retryStack.isEmpty()) {
            if (retryStack.size() == singletons.size()) {
                //nothing loaded this pass, so retrying won't help. Report why each singleton failed instead of hiding the cause.
                var reasons = failures.entrySet().stream()
                        .map(e -> e.getKey().getSimpleName() + ": " + rootMessage(e.getValue()))
                        .collect(Collectors.joining("; "));
                var ex = new RuntimeException("[" + providerName + "ServiceProvider] Failed to initialize singletons. " + reasons, failures.values().iterator().next());
                failures.values().stream().skip(1).forEach(ex::addSuppressed);
                throw ex;
            }
            loadSingletons(retryStack);
        }
    }

    private static String rootMessage(Throwable t) {
        var root = t;
        while (root.getCause() != null && root.getCause() != root) root = root.getCause();
        return root.getClass().getSimpleName() + (root.getMessage() == null ? "" : " - " + root.getMessage());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S> S getService(Class<S> serviceClass) {
        if (serviceClass == null) throw new IllegalArgumentException("[" + providerName + "ServiceProvider] Service class cannot be null.");
        if (serviceClass == IServiceProvider.class) return (S) this;
        if (serviceClass == Plugin.class) return (S) plugin;
        if (singletons.containsKey(serviceClass)) return (S) singletons.get(serviceClass);
        if (generatorFunctions.containsKey(serviceClass)) return (S) generatorFunctions.get(serviceClass).apply(this);
        if (parent != null) return parent.getService(serviceClass);
        return null;
    }

    @Override
    public <S> S getRequiredService(Class<S> serviceClass) {
        var service = getService(serviceClass);
        if (service == null) throw new RuntimeException("[" + providerName + "ServiceProvider] Unknown service requested. " + serviceClass.getSimpleName() + " is not registered.");
        return service;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C initialize(Class<C> clazz) {
        if (clazz == null) throw new IllegalArgumentException("[" + providerName + "ServiceProvider] Class cannot be null.");
        var constructor = Arrays.stream(clazz.getConstructors()).max(Comparator.comparingInt(Constructor::getParameterCount)).orElseThrow(() -> new RuntimeException("[" + providerName + "ServiceProvider] Cannot instantiate " + clazz.getSimpleName() + " because it has no public constructors."));
        var params = constructor.getParameters();
        var args = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            var param = params[i];
            var paramType = param.getType();
            var paramAnnotations = param.getAnnotations();
            //if it has a @Nullable annotation, we will try to get the service, but if it is not found, we will pass null.
            if (Arrays.stream(paramAnnotations).anyMatch(a -> a.annotationType().getSimpleName().equalsIgnoreCase("nullable")))
                args[i] = getService(paramType);
            else args[i] = getRequiredService(paramType);
        }
        try {
            return (C) constructor.newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException("[" + providerName + "ServiceProvider] Failed to instantiate " + clazz.getSimpleName() + " because of an exception.", e);
        }
    }

    @Override
    public Map<Class<?>, Object> getSingletons() {
        return Map.copyOf(singletons);
    }

    @Override
    public Map<Class<?>, Function<IServiceProvider, ?>> getTransients() {
        return Map.copyOf(generatorFunctions);
    }

}
